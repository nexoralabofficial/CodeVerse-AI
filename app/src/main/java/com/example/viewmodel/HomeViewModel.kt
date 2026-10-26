package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ProjectEntity
import com.example.data.local.ProjectFileEntity
import com.example.model.LanguageId
import com.example.model.LanguageRegistry
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
  private val dao = AppDatabase.getDatabase(application).projectDao()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery = _searchQuery.asStateFlow()

  val projects: StateFlow<List<ProjectEntity>> = combine(
    dao.getAllProjects(),
    _searchQuery
  ) { list, query ->
    if (query.isBlank()) {
      list
    } else {
      list.filter {
        it.name.contains(query, ignoreCase = true) ||
          it.languageId.contains(query, ignoreCase = true) ||
          it.description.contains(query, ignoreCase = true)
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun onSearchQueryChanged(query: String) {
    _searchQuery.value = query
  }

  fun createProject(
    name: String,
    languageId: LanguageId,
    description: String,
    onCreated: (Long) -> Unit
  ) {
    viewModelScope.launch {
      val lang = LanguageRegistry.getById(languageId)
      val projectId = dao.insertProject(
        ProjectEntity(
          name = name.ifBlank { "Untitled ${lang.name}" },
          languageId = languageId.name,
          description = description.ifBlank { "A modern ${lang.name} project" }
        )
      )

      val ext = lang.extension
      val mainFileName = when (languageId) {
        LanguageId.HTML -> "index.html"
        LanguageId.JAVA -> "Main.java"
        LanguageId.GO -> "main.go"
        LanguageId.CPP -> "main.cpp"
        LanguageId.C -> "main.c"
        LanguageId.RUST -> "main.rs"
        else -> "main.$ext"
      }

      dao.insertFile(
        ProjectFileEntity(
          projectId = projectId,
          name = mainFileName,
          content = lang.defaultTemplate,
          isMain = true
        )
      )

      onCreated(projectId)
    }
  }

  fun deleteProject(project: ProjectEntity) {
    viewModelScope.launch {
      dao.deleteFilesForProject(project.id)
      dao.deleteProject(project)
    }
  }

  fun duplicateProject(project: ProjectEntity, onDuplicated: (Long) -> Unit) {
    viewModelScope.launch {
      val files = dao.getFilesForProjectOnce(project.id)
      val newProjectId = dao.insertProject(
        project.copy(
          id = 0,
          name = "${project.name} (Copy)",
          createdAt = System.currentTimeMillis(),
          updatedAt = System.currentTimeMillis()
        )
      )
      for (f in files) {
        dao.insertFile(f.copy(id = 0, projectId = newProjectId))
      }
      onDuplicated(newProjectId)
    }
  }
}
