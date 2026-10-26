package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.LearningCurriculum
import com.example.data.LearningLesson
import com.example.model.LanguageRegistry
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningScreen(
  onBack: () -> Unit,
  onOpenLessonInEditor: (LearningLesson) -> Unit,
  modifier: Modifier = Modifier
) {
  var expandedLessonId by remember { mutableStateOf<String?>(LearningCurriculum.lessons.first().id) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = BgDarkest,
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Learning Center", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text("Interactive coding modules & concepts", color = AccentCyan, fontSize = 11.sp)
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("learning_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDark)
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = BgSurface),
          shape = RoundedCornerShape(12.dp),
          border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.School, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("From Basics to Mastery", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Each interactive lesson includes complete runnable source code, explanations, and key takeaways. Tap 'Open in Editor & Run' to experiment freely.",
              color = TextSecondary,
              fontSize = 12.sp,
              lineHeight = 18.sp
            )
          }
        }
      }

      items(LearningCurriculum.lessons, key = { it.id }) { lesson ->
        val isExpanded = lesson.id == expandedLessonId
        val lang = LanguageRegistry.getById(lesson.languageId)

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("lesson_card_${lesson.id}")
            .clickable {
              expandedLessonId = if (isExpanded) null else lesson.id
            },
          colors = CardDefaults.cardColors(containerColor = BgSurface),
          shape = RoundedCornerShape(12.dp),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (isExpanded) AccentPrimary else BorderSubtle)
          )
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(lang.iconEmoji, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(lesson.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(lang.name, color = AccentCyan, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Text(" • ", color = TextMuted, fontSize = 11.sp)
                    Text(lesson.difficulty, color = StatusSuccess, fontSize = 11.sp)
                  }
                }
              }

              Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = TextSecondary
              )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(lesson.summary, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)

            // Expanded body
            AnimatedVisibility(visible = isExpanded) {
              Column(modifier = Modifier.padding(top = 12.dp)) {
                // Explanation
                Text(
                  text = "Concept Breakdown:",
                  color = AccentCyan,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = lesson.explanation,
                  color = TextPrimary,
                  fontSize = 12.sp,
                  lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Key Takeaways
                Text(
                  text = "Key Takeaways:",
                  color = AccentCyan,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                lesson.keyTakeaways.forEach { takeaway ->
                  Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                    Text("•", color = AccentPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(takeaway, color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Starter Code Box Preview
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(BgDarkest)
                    .padding(10.dp)
                ) {
                  Text(
                    text = lesson.starterCode,
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                  )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action to Open in Editor
                Button(
                  onClick = { onOpenLessonInEditor(lesson) },
                  colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("open_lesson_editor_button")
                ) {
                  Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Open in Editor & Run", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
              }
            }
          }
        }
      }
    }
  }
}
