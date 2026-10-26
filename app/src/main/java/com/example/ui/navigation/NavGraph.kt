package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.PreferencesManager
import com.example.model.LanguageRegistry
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearningScreen
import com.example.ui.screens.SettingsScreen
import com.example.viewmodel.EditorViewModel
import com.example.viewmodel.HomeViewModel

sealed class Screen(val route: String) {
  object Home : Screen("home")
  object Editor : Screen("editor/{projectId}") {
    fun createRoute(projectId: Long): String = "editor/$projectId"
  }
  object Learning : Screen("learning")
  object Settings : Screen("settings")
}

@Composable
fun CodeVerseNavGraph(
  prefs: PreferencesManager,
  homeViewModel: HomeViewModel = viewModel(),
  editorViewModel: EditorViewModel = viewModel()
) {
  val navController = rememberNavController()

  NavHost(
    navController = navController,
    startDestination = Screen.Home.route
  ) {
    composable(Screen.Home.route) {
      HomeScreen(
        viewModel = homeViewModel,
        onOpenProject = { projectId ->
          editorViewModel.loadProject(projectId)
          navController.navigate(Screen.Editor.createRoute(projectId))
        },
        onNavigateToLearning = {
          navController.navigate(Screen.Learning.route)
        },
        onNavigateToSettings = {
          navController.navigate(Screen.Settings.route)
        }
      )
    }

    composable(
      route = Screen.Editor.route,
      arguments = listOf(navArgument("projectId") { type = NavType.LongType })
    ) { backStackEntry ->
      val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
      // Ensure project is loaded if not already loaded
      androidx.compose.runtime.LaunchedEffect(projectId) {
        editorViewModel.loadProject(projectId)
      }

      EditorScreen(
        viewModel = editorViewModel,
        onBack = {
          navController.popBackStack()
        },
        onNavigateToSettings = {
          navController.navigate(Screen.Settings.route)
        }
      )
    }

    composable(Screen.Learning.route) {
      LearningScreen(
        onBack = {
          navController.popBackStack()
        },
        onOpenLessonInEditor = { lesson ->
          val lang = LanguageRegistry.getById(lesson.languageId)
          homeViewModel.createProject(
            name = lesson.title,
            languageId = lesson.languageId,
            description = lesson.summary
          ) { newProjId ->
            editorViewModel.loadProject(newProjId)
            navController.navigate(Screen.Editor.createRoute(newProjId))
          }
        }
      )
    }

    composable(Screen.Settings.route) {
      SettingsScreen(
        prefs = prefs,
        onBack = {
          navController.popBackStack()
        }
      )
    }
  }
}
