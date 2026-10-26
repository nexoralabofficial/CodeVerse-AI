package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.execution.ExecutionStatus
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.EditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
  viewModel: EditorViewModel,
  onBack: () -> Unit,
  onNavigateToSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  val project by viewModel.project.collectAsState()
  val files by viewModel.files.collectAsState()
  val activeFile by viewModel.activeFile.collectAsState()
  val editorValue by viewModel.editorValue.collectAsState()
  val hasUnsavedChanges by viewModel.hasUnsavedChanges.collectAsState()
  val canUndo by viewModel.canUndo.collectAsState()
  val canRedo by viewModel.canRedo.collectAsState()

  val executionStatus by viewModel.executionStatus.collectAsState()
  val executionResult by viewModel.executionResult.collectAsState()
  val standardInput by viewModel.standardInput.collectAsState()
  val webConsoleLogs by viewModel.webConsoleLogs.collectAsState()
  val detectedIssues by viewModel.detectedIssues.collectAsState()
  val fixAppliedMessage by viewModel.fixAppliedMessage.collectAsState()

  val aiResponse by viewModel.aiResponse.collectAsState()
  val isAiLoading by viewModel.isAiLoading.collectAsState()

  val searchQuery by viewModel.searchQuery.collectAsState()
  val replaceQuery by viewModel.replaceQuery.collectAsState()
  val isSearchVisible by viewModel.isSearchVisible.collectAsState()

  // Settings
  val fontSize by viewModel.prefs.fontSize.collectAsState()
  val showLineNumbers by viewModel.prefs.showLineNumbers.collectAsState()
  val wordWrap by viewModel.prefs.wordWrap.collectAsState()
  val aiLanguageMode by viewModel.prefs.aiLanguageMode.collectAsState()

  var consoleHeightMode by remember { mutableStateOf(ConsoleHeightMode.COLLAPSED) }
  var showAiSheet by remember { mutableStateOf(false) }
  var showNewFileDialog by remember { mutableStateOf(false) }
  var newFileNameInput by remember { mutableStateOf("") }

  val snackbarHostState = remember { SnackbarHostState() }

  // Auto-expand console when running or when errors are detected
  LaunchedEffect(executionStatus) {
    if (executionStatus == ExecutionStatus.RUNNING || executionStatus == ExecutionStatus.FAILED) {
      if (consoleHeightMode == ConsoleHeightMode.COLLAPSED) {
        consoleHeightMode = ConsoleHeightMode.HALF
      }
    }
  }

  LaunchedEffect(fixAppliedMessage) {
    fixAppliedMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.dismissFixMessage()
    }
  }

  val currentLang = viewModel.getCurrentLanguage()

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = BgDarkest,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = project?.name ?: "Editor",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
              )
              if (hasUnsavedChanges) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(AccentCyan)
                )
              }
            }
            Text(
              text = "${currentLang.name} (${currentLang.iconEmoji})",
              color = AccentCyan,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("editor_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
          }
        },
        actions = {
          // Undo & Redo
          IconButton(
            onClick = viewModel::undo,
            enabled = canUndo,
            modifier = Modifier.size(36.dp).testTag("editor_undo_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", tint = if (canUndo) TextPrimary else TextMuted)
          }

          IconButton(
            onClick = viewModel::redo,
            enabled = canRedo,
            modifier = Modifier.size(36.dp).testTag("editor_redo_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo", tint = if (canRedo) TextPrimary else TextMuted)
          }

          // Search toggle
          IconButton(
            onClick = viewModel::toggleSearch,
            modifier = Modifier.size(36.dp).testTag("editor_search_toggle")
          ) {
            Icon(Icons.Default.Search, contentDescription = "Find", tint = if (isSearchVisible) AccentCyan else TextSecondary)
          }

          // AI Assistant Button
          IconButton(
            onClick = { showAiSheet = true },
            modifier = Modifier.size(36.dp).testTag("editor_ai_button")
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = "AI Assistant", tint = AccentCyan)
          }

          // Run Code Button
          FilledTonalButton(
            onClick = viewModel::runCode,
            enabled = executionStatus != ExecutionStatus.RUNNING,
            modifier = Modifier
              .height(34.dp)
              .padding(start = 4.dp, end = 8.dp)
              .testTag("run_code_button"),
            colors = ButtonDefaults.filledTonalButtonColors(
              containerColor = AccentPrimary,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
          ) {
            if (executionStatus == ExecutionStatus.RUNNING) {
              CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                color = Color.White,
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Running", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            } else {
              Icon(Icons.Default.PlayArrow, contentDescription = "Run", modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Run", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDark)
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // File Tabs Row
      FileTabsRow(
        files = files,
        activeFile = activeFile,
        hasUnsavedChanges = hasUnsavedChanges,
        onSelectFile = viewModel::selectFile,
        onCloseFile = viewModel::deleteFile,
        onNewFileClick = { showNewFileDialog = true }
      )

      // Search & Replace bar if active
      AnimatedVisibility(visible = isSearchVisible) {
        SearchReplaceBar(
          searchQuery = searchQuery,
          replaceQuery = replaceQuery,
          onSearchChange = viewModel::setSearchQuery,
          onReplaceChange = viewModel::setReplaceQuery,
          onReplaceCurrent = viewModel::replaceCurrent,
          onReplaceAll = viewModel::replaceAll,
          onClose = viewModel::toggleSearch
        )
      }

      // Code Editor Canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
      ) {
        CodeEditorArea(
          value = editorValue,
          onValueChange = viewModel::onEditorTextChanged,
          languageId = currentLang.id,
          fontSize = fontSize,
          showLineNumbers = showLineNumbers,
          wordWrap = wordWrap
        )
      }

      // Mobile Programming Toolbar
      MobileProgrammingToolbar(
        onInsertSymbol = viewModel::insertSymbol,
        onIndent = viewModel::indent,
        onToggleComment = viewModel::toggleComment,
        onDuplicateLine = viewModel::duplicateLine
      )

      // Expandable Output & Debugging Console
      ConsolePanel(
        executionStatus = executionStatus,
        executionResult = executionResult,
        detectedIssues = detectedIssues,
        standardInput = standardInput,
        webConsoleLogs = webConsoleLogs,
        htmlContent = editorValue.text,
        isWebProject = currentLang.isWebPreviewable,
        aiLanguageMode = aiLanguageMode,
        heightMode = consoleHeightMode,
        onHeightModeChange = { consoleHeightMode = it },
        onClear = viewModel::clearConsole,
        onStop = viewModel::cancelExecution,
        onStandardInputChange = viewModel::onStandardInputChanged,
        onApplyFix = viewModel::applyFix,
        onWebConsoleMessage = viewModel::addWebConsoleLog
      )
    }
  }

  // AI Assistant Bottom Sheet
  if (showAiSheet) {
    AiAssistantBottomSheet(
      isAiLoading = isAiLoading,
      aiResponse = aiResponse,
      onAskAi = viewModel::askAi,
      onDismiss = { showAiSheet = false },
      onInsertCodeToEditor = { snippet ->
        viewModel.insertSymbol(snippet)
      },
      selectedLanguage = currentLang.name
    )
  }

  // Create New File Dialog
  if (showNewFileDialog) {
    AlertDialog(
      onDismissRequest = { showNewFileDialog = false },
      containerColor = BgDark,
      title = { Text("New File in Project", color = TextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        OutlinedTextField(
          value = newFileNameInput,
          onValueChange = { newFileNameInput = it },
          placeholder = { Text("e.g. utils.py, style.css", color = TextMuted) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("new_file_name_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentCyan,
            unfocusedBorderColor = BorderSubtle
          )
        )
      },
      confirmButton = {
        Button(
          onClick = {
            if (newFileNameInput.isNotBlank()) {
              viewModel.createNewFile(newFileNameInput.trim())
              newFileNameInput = ""
              showNewFileDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
        ) {
          Text("Create")
        }
      },
      dismissButton = {
        TextButton(onClick = { showNewFileDialog = false }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }
}
