package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.*
import com.example.data.PreferencesManager
import com.example.data.local.AppDatabase
import com.example.data.local.ProjectEntity
import com.example.data.local.ProjectFileEntity
import com.example.editor.EditorActions
import com.example.editor.EditorHistoryManager
import com.example.execution.*
import com.example.model.Language
import com.example.model.LanguageId
import com.example.model.LanguageRegistry
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EditorViewModel(application: Application) : AndroidViewModel(application) {
  private val dao = AppDatabase.getDatabase(application).projectDao()
  val prefs = PreferencesManager.getInstance(application)

  private val _project = MutableStateFlow<ProjectEntity?>(null)
  val project: StateFlow<ProjectEntity?> = _project.asStateFlow()

  private val _files = MutableStateFlow<List<ProjectFileEntity>>(emptyList())
  val files: StateFlow<List<ProjectFileEntity>> = _files.asStateFlow()

  private val _activeFile = MutableStateFlow<ProjectFileEntity?>(null)
  val activeFile: StateFlow<ProjectFileEntity?> = _activeFile.asStateFlow()

  private val _editorValue = MutableStateFlow(TextFieldValue(""))
  val editorValue: StateFlow<TextFieldValue> = _editorValue.asStateFlow()

  private val _hasUnsavedChanges = MutableStateFlow(false)
  val hasUnsavedChanges: StateFlow<Boolean> = _hasUnsavedChanges.asStateFlow()

  private val historyManager = EditorHistoryManager()
  private val _canUndo = MutableStateFlow(false)
  val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()
  private val _canRedo = MutableStateFlow(false)
  val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

  // Console & Execution State
  private val _executionStatus = MutableStateFlow(ExecutionStatus.READY)
  val executionStatus: StateFlow<ExecutionStatus> = _executionStatus.asStateFlow()

  private val _executionResult = MutableStateFlow<ExecutionResult?>(null)
  val executionResult: StateFlow<ExecutionResult?> = _executionResult.asStateFlow()

  private val _standardInput = MutableStateFlow("")
  val standardInput: StateFlow<String> = _standardInput.asStateFlow()

  private val _webConsoleLogs = MutableStateFlow<List<String>>(emptyList())
  val webConsoleLogs: StateFlow<List<String>> = _webConsoleLogs.asStateFlow()

  // CodeSense AI State
  private val _detectedIssues = MutableStateFlow<List<CodeIssue>>(emptyList())
  val detectedIssues: StateFlow<List<CodeIssue>> = _detectedIssues.asStateFlow()

  private val _fixAppliedMessage = MutableStateFlow<String?>(null)
  val fixAppliedMessage: StateFlow<String?> = _fixAppliedMessage.asStateFlow()

  // AI Assistant Drawer / Bottom Sheet State
  private val _aiResponse = MutableStateFlow<String?>(null)
  val aiResponse: StateFlow<String?> = _aiResponse.asStateFlow()
  private val _isAiLoading = MutableStateFlow(false)
  val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

  // Search & Replace State
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
  private val _replaceQuery = MutableStateFlow("")
  val replaceQuery: StateFlow<String> = _replaceQuery.asStateFlow()
  private val _isSearchVisible = MutableStateFlow(false)
  val isSearchVisible: StateFlow<Boolean> = _isSearchVisible.asStateFlow()

  private var executionJob: Job? = null

  fun loadProject(projectId: Long) {
    viewModelScope.launch {
      val proj = dao.getProjectById(projectId)
      _project.value = proj
      dao.getFilesForProject(projectId).collect { fileList ->
        _files.value = fileList
        if (_activeFile.value == null && fileList.isNotEmpty()) {
          val mainFile = fileList.firstOrNull { it.isMain } ?: fileList.first()
          selectFile(mainFile)
        }
      }
    }
  }

  fun selectFile(file: ProjectFileEntity) {
    // If active file has unsaved changes and autoSave is true, save it first
    if (_hasUnsavedChanges.value && prefs.autoSave.value) {
      saveActiveFile()
    }
    _activeFile.value = file
    val tfv = TextFieldValue(file.content, TextRange(0))
    _editorValue.value = tfv
    _hasUnsavedChanges.value = false
    historyManager.clear()
    historyManager.record(tfv)
    updateUndoRedoStates()
  }

  fun onEditorTextChanged(newValue: TextFieldValue) {
    _editorValue.value = newValue
    _hasUnsavedChanges.value = true
    historyManager.record(newValue)
    updateUndoRedoStates()
  }

  fun undo() {
    val previous = historyManager.undo(_editorValue.value)
    if (previous != null) {
      _editorValue.value = previous
      _hasUnsavedChanges.value = true
      updateUndoRedoStates()
    }
  }

  fun redo() {
    val next = historyManager.redo()
    if (next != null) {
      _editorValue.value = next
      _hasUnsavedChanges.value = true
      updateUndoRedoStates()
    }
  }

  private fun updateUndoRedoStates() {
    _canUndo.value = historyManager.canUndo()
    _canRedo.value = historyManager.canRedo()
  }

  fun insertSymbol(symbol: String) {
    val updated = EditorActions.insertSymbol(_editorValue.value, symbol)
    onEditorTextChanged(updated)
  }

  fun indent() {
    val updated = EditorActions.indent(_editorValue.value, 4)
    onEditorTextChanged(updated)
  }

  fun toggleComment() {
    val currentLang = getCurrentLanguage()
    val commentPrefix = when (currentLang.id) {
      LanguageId.PYTHON, LanguageId.BASH, LanguageId.RUBY -> "#"
      LanguageId.SQL -> "--"
      else -> "//"
    }
    val updated = EditorActions.toggleComment(_editorValue.value, commentPrefix)
    onEditorTextChanged(updated)
  }

  fun duplicateLine() {
    val updated = EditorActions.duplicateLine(_editorValue.value)
    onEditorTextChanged(updated)
  }

  fun saveActiveFile() {
    val file = _activeFile.value ?: return
    val content = _editorValue.value.text
    viewModelScope.launch {
      dao.updateFileContent(file.id, content)
      _project.value?.let { p ->
        dao.updateProject(p.copy(updatedAt = System.currentTimeMillis()))
      }
      _hasUnsavedChanges.value = false
    }
  }

  fun createNewFile(name: String) {
    val proj = _project.value ?: return
    viewModelScope.launch {
      val newId = dao.insertFile(
        ProjectFileEntity(
          projectId = proj.id,
          name = name,
          content = "",
          isMain = false
        )
      )
      val newFile = dao.getFileById(newId)
      if (newFile != null) {
        selectFile(newFile)
      }
    }
  }

  fun deleteFile(file: ProjectFileEntity) {
    viewModelScope.launch {
      dao.deleteFile(file)
      if (_activeFile.value?.id == file.id) {
        val remaining = _files.value.filter { it.id != file.id }
        if (remaining.isNotEmpty()) {
          selectFile(remaining.first())
        }
      }
    }
  }

  fun onStandardInputChanged(input: String) {
    _standardInput.value = input
  }

  fun addWebConsoleLog(log: String) {
    _webConsoleLogs.value = _webConsoleLogs.value + log
  }

  fun clearConsole() {
    _executionResult.value = null
    _webConsoleLogs.value = emptyList()
    _detectedIssues.value = emptyList()
    _executionStatus.value = ExecutionStatus.READY
  }

  fun cancelExecution() {
    executionJob?.cancel()
    _executionStatus.value = ExecutionStatus.READY
  }

  fun runCode() {
    val proj = _project.value ?: return
    saveActiveFile()

    val lang = getCurrentLanguage()

    // If language is HTML or web preview
    if (lang.isWebPreviewable) {
      _executionStatus.value = ExecutionStatus.COMPLETED
      _executionResult.value = ExecutionResult(
        status = ExecutionStatus.COMPLETED,
        stdout = "HTML/Web Preview refreshed and rendering in live canvas.",
        durationMs = 12
      )
      return
    }

    if (!lang.executionSupport || lang.pistonLanguage == null) {
      _executionStatus.value = ExecutionStatus.ERROR
      _executionResult.value = ExecutionResult(
        status = ExecutionStatus.ERROR,
        errorMessage = "${lang.name} runtime execution is not configured for remote compilation. Editor and syntax highlighting are fully supported.",
        hasErrors = true
      )
      return
    }

    executionJob?.cancel()
    executionJob = viewModelScope.launch {
      _executionStatus.value = ExecutionStatus.RUNNING
      _detectedIssues.value = emptyList()
      _fixAppliedMessage.value = null

      val execFiles = _files.value.map { file ->
        ExecutionFile(
          name = file.name,
          content = if (file.id == _activeFile.value?.id) _editorValue.value.text else file.content
        )
      }

      val request = ExecutionRequest(
        language = lang.pistonLanguage,
        version = lang.pistonVersion ?: "",
        files = execFiles,
        stdin = _standardInput.value
      )

      val result = CodeExecutionEngine.execute(request)
      _executionResult.value = result
      _executionStatus.value = result.status

      if (result.hasErrors) {
        val currentCode = _editorValue.value.text
        val currentFileName = _activeFile.value?.name ?: "main"
        val issues = CodeSenseParser.parseDiagnostics(
          sourceCode = currentCode,
          fileName = currentFileName,
          compilerStderr = result.compilerDiagnostics,
          runtimeStderr = result.stderr
        )
        _detectedIssues.value = issues
      }
    }
  }

  fun applyFix(issue: CodeIssue, andRerun: Boolean = false) {
    if (issue.correctedSnippet.isBlank() || issue.line == null) return
    val currentText = _editorValue.value.text
    val lines = currentText.lines().toMutableList()
    val lineIdx = issue.line - 1

    if (lineIdx in lines.indices) {
      lines[lineIdx] = issue.correctedSnippet
      val newCode = lines.joinToString("\n")
      onEditorTextChanged(TextFieldValue(newCode, TextRange(newCode.length)))
      saveActiveFile()
      _fixAppliedMessage.value = "Fix applied to line ${issue.line}: ${issue.category}"

      if (andRerun) {
        runCode()
      }
    }
  }

  fun dismissFixMessage() {
    _fixAppliedMessage.value = null
  }

  fun askAi(userPrompt: String) {
    viewModelScope.launch {
      _isAiLoading.value = true
      _aiResponse.value = null

      val currentCode = _editorValue.value.text
      val lang = getCurrentLanguage().name
      val langMode = prefs.aiLanguageMode.value

      val answer = GeminiAiService.promptAi(
        userPrompt = userPrompt,
        codeContext = currentCode,
        languageName = lang,
        preferredLang = langMode
      )

      _aiResponse.value = answer
      _isAiLoading.value = false
    }
  }

  fun clearAiResponse() {
    _aiResponse.value = null
  }

  // Search & Replace logic
  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setReplaceQuery(query: String) {
    _replaceQuery.value = query
  }

  fun toggleSearch() {
    _isSearchVisible.value = !_isSearchVisible.value
  }

  fun replaceCurrent() {
    val q = _searchQuery.value
    if (q.isEmpty()) return
    val text = _editorValue.value.text
    val idx = text.indexOf(q)
    if (idx != -1) {
      val newText = text.replaceRange(idx, idx + q.length, _replaceQuery.value)
      onEditorTextChanged(TextFieldValue(newText, TextRange(idx + _replaceQuery.value.length)))
    }
  }

  fun replaceAll() {
    val q = _searchQuery.value
    if (q.isEmpty()) return
    val text = _editorValue.value.text
    val newText = text.replace(q, _replaceQuery.value)
    onEditorTextChanged(TextFieldValue(newText, TextRange(newText.length)))
  }

  fun getCurrentLanguage(): Language {
    val activeFileName = _activeFile.value?.name ?: ""
    val ext = activeFileName.substringAfterLast('.', "")
    if (ext.isNotEmpty()) {
      return LanguageRegistry.getByExtension(ext)
    }
    val projLang = _project.value?.languageId ?: LanguageId.PYTHON.name
    return try {
      LanguageRegistry.getById(LanguageId.valueOf(projLang))
    } catch (e: Throwable) {
      LanguageRegistry.getById(LanguageId.PYTHON)
    }
  }
}
