package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProjectEntity
import com.example.model.Language
import com.example.model.LanguageId
import com.example.model.LanguageRegistry
import com.example.ui.theme.*
import com.example.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  viewModel: HomeViewModel,
  onOpenProject: (Long) -> Unit,
  onNavigateToLearning: () -> Unit,
  onNavigateToSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  val projects by viewModel.projects.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  var showNewProjectDialog by remember { mutableStateOf(false) }
  var selectedLanguageForNewProject by remember { mutableStateOf(LanguageId.PYTHON) }
  var newProjectName by remember { mutableStateOf("") }
  var newProjectDesc by remember { mutableStateOf("") }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = BgDarkest,
    topBar = {
      HomeTopBar(
        onSettingsClick = onNavigateToSettings,
        onLearningClick = onNavigateToLearning
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showNewProjectDialog = true },
        containerColor = AccentPrimary,
        contentColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("fab_new_project")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Add, contentDescription = "New Project")
          Spacer(modifier = Modifier.width(6.dp))
          Text("New Project", fontWeight = FontWeight.Bold)
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Hero Branding & Tagline Card
      item {
        HeroBrandingCard(
          onNewProjectClick = { showNewProjectDialog = true },
          onLearningClick = onNavigateToLearning
        )
      }

      // 2. Search Field
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = viewModel::onSearchQueryChanged,
          placeholder = { Text("Search your projects or languages...", color = TextMuted, fontSize = 13.sp) },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
              }
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_projects_field"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentCyan,
            unfocusedBorderColor = BorderSubtle,
            focusedContainerColor = BgDark,
            unfocusedContainerColor = BgDark
          ),
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )
      }

      // 3. Supported Languages Horizontal Showcase
      item {
        SupportedLanguagesSection(
          onLanguageSelected = { lang ->
            selectedLanguageForNewProject = lang.id
            showNewProjectDialog = true
          }
        )
      }

      // 4. Quick Code Templates
      item {
        CodeTemplatesSection(
          onTemplateSelected = { langId, title, desc ->
            viewModel.createProject(title, langId, desc) { id ->
              onOpenProject(id)
            }
          }
        )
      }

      // 5. Recent Projects Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Your Projects (${projects.size})",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // 6. Project Cards or Empty State
      if (projects.isEmpty()) {
        item {
          EmptyProjectsCard(
            hasSearch = searchQuery.isNotEmpty(),
            onCreateFirst = { showNewProjectDialog = true }
          )
        }
      } else {
        items(projects, key = { it.id }) { project ->
          ProjectCard(
            project = project,
            onClick = { onOpenProject(project.id) },
            onDuplicate = { viewModel.duplicateProject(project, onOpenProject) },
            onDelete = { viewModel.deleteProject(project) }
          )
        }
      }

      // Bottom Spacer for FAB
      item {
        Spacer(modifier = Modifier.height(72.dp))
      }
    }
  }

  // New Project Dialog
  if (showNewProjectDialog) {
    AlertDialog(
      onDismissRequest = { showNewProjectDialog = false },
      containerColor = BgDark,
      title = {
        Text("Create New Project", color = TextPrimary, fontWeight = FontWeight.Bold)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = newProjectName,
            onValueChange = { newProjectName = it },
            label = { Text("Project Name", color = TextSecondary) },
            placeholder = { Text("e.g. MyAlgorithm", color = TextMuted) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("new_project_name_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = AccentCyan,
              unfocusedBorderColor = BorderSubtle
            )
          )

          OutlinedTextField(
            value = newProjectDesc,
            onValueChange = { newProjectDesc = it },
            label = { Text("Description (Optional)", color = TextSecondary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = AccentCyan,
              unfocusedBorderColor = BorderSubtle
            )
          )

          Text("Select Language:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            LanguageRegistry.languages.take(10).forEach { lang ->
              val isSel = lang.id == selectedLanguageForNewProject
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSel) AccentPrimary else BgSurface,
                border = if (isSel) null else ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle)),
                modifier = Modifier.clickable { selectedLanguageForNewProject = lang.id }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(lang.iconEmoji, fontSize = 13.sp)
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    lang.name,
                    color = if (isSel) Color.White else TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                  )
                }
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showNewProjectDialog = false
            viewModel.createProject(
              name = newProjectName,
              languageId = selectedLanguageForNewProject,
              description = newProjectDesc
            ) { createdId ->
              newProjectName = ""
              newProjectDesc = ""
              onOpenProject(createdId)
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
          modifier = Modifier.testTag("confirm_create_project_button")
        ) {
          Text("Create & Open")
        }
      },
      dismissButton = {
        TextButton(onClick = { showNewProjectDialog = false }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
  onSettingsClick: () -> Unit,
  onLearningClick: () -> Unit
) {
  TopAppBar(
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        androidx.compose.foundation.Image(
          painter = androidx.compose.ui.res.painterResource(com.example.R.drawable.app_logo),
          contentDescription = "CodeVerse Logo",
          modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "CodeVerse AI",
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Code. Run. Debug. Learn.",
            color = AccentCyan,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    },
    actions = {
      IconButton(onClick = onLearningClick, modifier = Modifier.testTag("nav_learning_button")) {
        Icon(Icons.Default.School, contentDescription = "Learning", tint = AccentCyan)
      }
      IconButton(onClick = onSettingsClick, modifier = Modifier.testTag("nav_settings_button")) {
        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextSecondary)
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDarkest)
  )
}

@Composable
private fun HeroBrandingCard(
  onNewProjectClick: () -> Unit,
  onLearningClick: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = BgSurface),
    shape = RoundedCornerShape(16.dp),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Autonomous Mobile IDE",
            color = AccentCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Compile, Execute & Debug on Your Phone",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 24.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Zero computer required. Real GCC/Clang, Python, Java runtimes and CodeSense AI auto-debugging.",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 18.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = onNewProjectClick,
          colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f).height(38.dp)
        ) {
          Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Start Coding", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        FilledTonalButton(
          onClick = onLearningClick,
          colors = ButtonDefaults.filledTonalButtonColors(containerColor = BgSurfaceElevated, contentColor = AccentCyan),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f).height(38.dp)
        ) {
          Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Learn Code", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun SupportedLanguagesSection(
  onLanguageSelected: (Language) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Supported Runtimes & Languages",
      color = TextPrimary,
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      LanguageRegistry.languages.forEach { lang ->
        Card(
          modifier = Modifier
            .width(130.dp)
            .clickable { onLanguageSelected(lang) },
          colors = CardDefaults.cardColors(containerColor = BgSurface),
          shape = RoundedCornerShape(10.dp),
          border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(lang.iconEmoji, fontSize = 20.sp)
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(if (lang.executionSupport) StatusSuccess.copy(alpha = 0.15f) else BgSurfaceElevated)
                  .padding(horizontal = 4.dp, vertical = 2.dp)
              ) {
                Text(
                  text = if (lang.isWebPreviewable) "PREVIEW" else if (lang.executionSupport) "EXEC" else "SYNTAX",
                  color = if (lang.executionSupport) StatusSuccess else TextMuted,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace
                )
              }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = lang.name,
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
            Text(
              text = ".${lang.extension}",
              color = AccentCyan,
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun CodeTemplatesSection(
  onTemplateSelected: (LanguageId, String, String) -> Unit
) {
  val templates = listOf(
    Triple(LanguageId.PYTHON, "Data Analysis & Math", "Fibonacci calculation and math routines"),
    Triple(LanguageId.HTML, "Interactive Neon Web Card", "HTML5 + CSS3 Flexbox with JS Counter"),
    Triple(LanguageId.CPP, "C++ Vector Summation", "High-performance data vector aggregation"),
    Triple(LanguageId.JAVA, "Java OOP Blueprint", "Class methods and list collection iteration")
  )

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Instant Starter Templates",
      color = TextPrimary,
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      templates.forEach { (langId, title, desc) ->
        val lang = LanguageRegistry.getById(langId)
        Card(
          modifier = Modifier
            .width(200.dp)
            .clickable { onTemplateSelected(langId, title, desc) },
          colors = CardDefaults.cardColors(containerColor = BgSurface),
          shape = RoundedCornerShape(10.dp),
          border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(lang.iconEmoji, fontSize = 16.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(lang.name, color = AccentCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, color = TextSecondary, fontSize = 11.sp, maxLines = 2)
          }
        }
      }
    }
  }
}

@Composable
private fun ProjectCard(
  project: ProjectEntity,
  onClick: () -> Unit,
  onDuplicate: () -> Unit,
  onDelete: () -> Unit
) {
  var showMenu by remember { mutableStateOf(false) }
  val langId = try {
    LanguageId.valueOf(project.languageId)
  } catch (e: Throwable) {
    LanguageId.PYTHON
  }
  val lang = LanguageRegistry.getById(langId)
  val dateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("project_card_${project.name}"),
    colors = CardDefaults.cardColors(containerColor = BgSurface),
    shape = RoundedCornerShape(12.dp),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Language Icon Box
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(BgSurfaceElevated),
        contentAlignment = Alignment.Center
      ) {
        Text(lang.iconEmoji, fontSize = 22.sp)
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Title & metadata
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = project.name,
          color = TextPrimary,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = lang.name,
            color = AccentCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
          Text(text = " • ", color = TextMuted, fontSize = 11.sp)
          Text(
            text = dateFormat.format(Date(project.updatedAt)),
            color = TextSecondary,
            fontSize = 11.sp
          )
        }
        if (project.description.isNotBlank()) {
          Text(
            text = project.description,
            color = TextMuted,
            fontSize = 11.sp,
            maxLines = 1
          )
        }
      }

      // Action menu
      Box {
        IconButton(onClick = { showMenu = true }) {
          Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = TextSecondary)
        }

        DropdownMenu(
          expanded = showMenu,
          onDismissRequest = { showMenu = false },
          modifier = Modifier.background(BgDark)
        ) {
          DropdownMenuItem(
            text = { Text("Duplicate", color = TextPrimary) },
            leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null, tint = AccentCyan) },
            onClick = {
              showMenu = false
              onDuplicate()
            }
          )
          DropdownMenuItem(
            text = { Text("Delete", color = StatusError) },
            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = StatusError) },
            onClick = {
              showMenu = false
              onDelete()
            }
          )
        }
      }
    }
  }
}

@Composable
private fun EmptyProjectsCard(
  hasSearch: Boolean,
  onCreateFirst: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = BgSurface),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = Icons.Default.FolderOpen,
        contentDescription = "No projects",
        tint = TextMuted,
        modifier = Modifier.size(48.dp)
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = if (hasSearch) "No matching projects found" else "No projects yet",
        color = TextPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = if (hasSearch) "Try searching another language or clear query." else "Create your first project or pick a template above.",
        color = TextSecondary,
        fontSize = 12.sp,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )

      if (!hasSearch) {
        Spacer(modifier = Modifier.height(16.dp))
        Button(
          onClick = onCreateFirst,
          colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Create New Project")
        }
      }
    }
  }
}
