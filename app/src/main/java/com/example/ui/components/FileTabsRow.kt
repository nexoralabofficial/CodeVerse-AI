package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProjectFileEntity
import com.example.ui.theme.*

@Composable
fun FileTabsRow(
  files: List<ProjectFileEntity>,
  activeFile: ProjectFileEntity?,
  hasUnsavedChanges: Boolean,
  onSelectFile: (ProjectFileEntity) -> Unit,
  onCloseFile: (ProjectFileEntity) -> Unit,
  onNewFileClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(40.dp)
      .background(BgDark)
      .horizontalScroll(rememberScrollState())
      .padding(horizontal = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    files.forEach { file ->
      val isActive = file.id == activeFile?.id
      val isDirty = isActive && hasUnsavedChanges

      Row(
        modifier = Modifier
          .padding(horizontal = 2.dp, vertical = 4.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(if (isActive) BgSurface else BgDarkest)
          .clickable { onSelectFile(file) }
          .padding(start = 10.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
          .testTag("file_tab_${file.name}"),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Unsaved changes indicator
        if (isDirty) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(AccentCyan)
          )
          Spacer(modifier = Modifier.width(6.dp))
        }

        Text(
          text = file.name,
          color = if (isActive) AccentCyan else TextSecondary,
          fontFamily = FontFamily.Monospace,
          fontSize = 12.sp,
          fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
        )

        Spacer(modifier = Modifier.width(6.dp))

        if (files.size > 1) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close file",
            tint = TextMuted,
            modifier = Modifier
              .size(14.dp)
              .clickable { onCloseFile(file) }
          )
        }
      }
    }

    // Add new file button
    IconButton(
      onClick = onNewFileClick,
      modifier = Modifier
        .size(32.dp)
        .testTag("add_file_button")
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "New file",
        tint = TextSecondary,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}
