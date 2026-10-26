package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AiLanguageMode
import com.example.ai.CodeIssue
import com.example.ai.IssueSeverity
import com.example.execution.ExecutionResult
import com.example.execution.ExecutionStatus
import com.example.ui.theme.*

enum class ConsoleHeightMode {
  COLLAPSED,
  HALF,
  EXPANDED
}

enum class ConsoleTab {
  OUTPUT,
  ERRORS,
  INPUT,
  WEB_PREVIEW
}

@Composable
fun ConsolePanel(
  executionStatus: ExecutionStatus,
  executionResult: ExecutionResult?,
  detectedIssues: List<CodeIssue>,
  standardInput: String,
  webConsoleLogs: List<String>,
  htmlContent: String,
  isWebProject: Boolean,
  aiLanguageMode: AiLanguageMode,
  heightMode: ConsoleHeightMode,
  onHeightModeChange: (ConsoleHeightMode) -> Unit,
  onClear: () -> Unit,
  onStop: () -> Unit,
  onStandardInputChange: (String) -> Unit,
  onApplyFix: (CodeIssue, Boolean) -> Unit,
  onWebConsoleMessage: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedTab by remember(isWebProject) {
    mutableStateOf(if (isWebProject) ConsoleTab.WEB_PREVIEW else ConsoleTab.OUTPUT)
  }
  var localAiLangMode by remember { mutableStateOf(aiLanguageMode) }

  // Automatically switch to ERRORS tab when issues detected
  LaunchedEffect(detectedIssues) {
    if (detectedIssues.isNotEmpty()) {
      selectedTab = ConsoleTab.ERRORS
    }
  }

  val panelHeight = when (heightMode) {
    ConsoleHeightMode.COLLAPSED -> 44.dp
    ConsoleHeightMode.HALF -> 260.dp
    ConsoleHeightMode.EXPANDED -> 480.dp
  }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .height(panelHeight)
      .animateContentSize(),
    color = BgDark,
    shadowElevation = 8.dp,
    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Console Header Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp)
          .background(BgSurface)
          .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Status & Tabs Row
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          // Status Indicator
          StatusBadge(executionStatus = executionStatus)

          Spacer(modifier = Modifier.width(8.dp))

          // Tab buttons
          ConsoleTabButton(
            title = "Output",
            isSelected = selectedTab == ConsoleTab.OUTPUT,
            onClick = {
              selectedTab = ConsoleTab.OUTPUT
              if (heightMode == ConsoleHeightMode.COLLAPSED) onHeightModeChange(ConsoleHeightMode.HALF)
            }
          )

          ConsoleTabButton(
            title = "Errors",
            badgeCount = detectedIssues.size,
            isSelected = selectedTab == ConsoleTab.ERRORS,
            onClick = {
              selectedTab = ConsoleTab.ERRORS
              if (heightMode == ConsoleHeightMode.COLLAPSED) onHeightModeChange(ConsoleHeightMode.HALF)
            }
          )

          ConsoleTabButton(
            title = "Input",
            isSelected = selectedTab == ConsoleTab.INPUT,
            onClick = {
              selectedTab = ConsoleTab.INPUT
              if (heightMode == ConsoleHeightMode.COLLAPSED) onHeightModeChange(ConsoleHeightMode.HALF)
            }
          )

          if (isWebProject) {
            ConsoleTabButton(
              title = "Web Preview",
              isSelected = selectedTab == ConsoleTab.WEB_PREVIEW,
              onClick = {
                selectedTab = ConsoleTab.WEB_PREVIEW
                if (heightMode == ConsoleHeightMode.COLLAPSED) onHeightModeChange(ConsoleHeightMode.HALF)
              }
            )
          }
        }

        // Action icons (Copy, Clear, Expand/Collapse)
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (executionStatus == ExecutionStatus.RUNNING) {
            IconButton(
              onClick = onStop,
              modifier = Modifier.size(28.dp).testTag("console_stop_button")
            ) {
              Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "Stop",
                tint = StatusError,
                modifier = Modifier.size(16.dp)
              )
            }
          }

          IconButton(
            onClick = {
              val textToCopy = when (selectedTab) {
                ConsoleTab.OUTPUT -> executionResult?.stdout ?: ""
                ConsoleTab.ERRORS -> executionResult?.compilerDiagnostics ?: executionResult?.stderr ?: ""
                ConsoleTab.INPUT -> standardInput
                ConsoleTab.WEB_PREVIEW -> webConsoleLogs.joinToString("\n")
              }
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              clipboard.setPrimaryClip(ClipData.newPlainText("CodeVerse Console", textToCopy))
            },
            modifier = Modifier.size(28.dp).testTag("console_copy_button")
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy Console",
              tint = TextSecondary,
              modifier = Modifier.size(16.dp)
            )
          }

          IconButton(
            onClick = onClear,
            modifier = Modifier.size(28.dp).testTag("console_clear_button")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Clear",
              tint = TextSecondary,
              modifier = Modifier.size(16.dp)
            )
          }

          IconButton(
            onClick = {
              val nextMode = when (heightMode) {
                ConsoleHeightMode.COLLAPSED -> ConsoleHeightMode.HALF
                ConsoleHeightMode.HALF -> ConsoleHeightMode.EXPANDED
                ConsoleHeightMode.EXPANDED -> ConsoleHeightMode.COLLAPSED
              }
              onHeightModeChange(nextMode)
            },
            modifier = Modifier.size(28.dp).testTag("console_resize_button")
          ) {
            Icon(
              imageVector = when (heightMode) {
                ConsoleHeightMode.COLLAPSED -> Icons.Default.KeyboardArrowUp
                ConsoleHeightMode.HALF -> Icons.Default.Fullscreen
                ConsoleHeightMode.EXPANDED -> Icons.Default.KeyboardArrowDown
              },
              contentDescription = "Resize Console",
              tint = AccentCyan,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // Console Body (Only visible if not collapsed)
      if (heightMode != ConsoleHeightMode.COLLAPSED) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .background(BgDarkest)
        ) {
          when (selectedTab) {
            ConsoleTab.OUTPUT -> OutputTabContent(executionResult, webConsoleLogs)
            ConsoleTab.ERRORS -> ErrorsTabContent(
              executionResult = executionResult,
              detectedIssues = detectedIssues,
              aiLanguageMode = localAiLangMode,
              onLanguageModeChanged = { localAiLangMode = it },
              onApplyFix = onApplyFix
            )
            ConsoleTab.INPUT -> InputTabContent(
              standardInput = standardInput,
              onStandardInputChange = onStandardInputChange
            )
            ConsoleTab.WEB_PREVIEW -> WebPreviewView(
              htmlContent = htmlContent,
              onConsoleMessage = onWebConsoleMessage
            )
          }
        }
      }
    }
  }
}

@Composable
private fun StatusBadge(executionStatus: ExecutionStatus) {
  val (color, text) = when (executionStatus) {
    ExecutionStatus.READY -> TextMuted to "Ready"
    ExecutionStatus.RUNNING -> AccentCyan to "Running..."
    ExecutionStatus.COMPLETED -> StatusSuccess to "Exit 0"
    ExecutionStatus.FAILED -> StatusError to "Failed"
    ExecutionStatus.TIMED_OUT -> StatusWarning to "Timeout"
    ExecutionStatus.ERROR -> StatusError to "Error"
  }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .background(color.copy(alpha = 0.15f))
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {
    if (executionStatus == ExecutionStatus.RUNNING) {
      CircularProgressIndicator(
        modifier = Modifier.size(8.dp),
        color = AccentCyan,
        strokeWidth = 1.5.dp
      )
      Spacer(modifier = Modifier.width(4.dp))
    } else {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(color)
      )
      Spacer(modifier = Modifier.width(4.dp))
    }
    Text(
      text = text,
      color = color,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold,
      fontFamily = FontFamily.Monospace
    )
  }
}

@Composable
private fun ConsoleTabButton(
  title: String,
  isSelected: Boolean,
  badgeCount: Int = 0,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .padding(horizontal = 2.dp)
      .clip(RoundedCornerShape(6.dp))
      .background(if (isSelected) BgSurfaceElevated else Color.Transparent)
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      fontSize = 12.sp,
      color = if (isSelected) AccentCyan else TextSecondary,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
    )

    if (badgeCount > 0) {
      Spacer(modifier = Modifier.width(4.dp))
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(StatusError)
          .padding(horizontal = 5.dp, vertical = 1.dp)
      ) {
        Text(
          text = badgeCount.toString(),
          color = Color.White,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
private fun OutputTabContent(
  result: ExecutionResult?,
  webConsoleLogs: List<String>
) {
  SelectionContainer {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)
    ) {
      if (result == null && webConsoleLogs.isEmpty()) {
        item {
          Text(
            text = "Console ready. Tap 'Run' above to execute code.",
            color = TextMuted,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
          )
        }
      } else {
        if (result != null) {
          item {
            // Execution metadata banner
            Row(
              modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Time: ${result.durationMs}ms | Exit: ${result.exitCode ?: 0}",
                color = TextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }

          if (result.stdout.isNotEmpty()) {
            item {
              Text(
                text = result.stdout,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 20.sp
              )
            }
          }

          if (result.errorMessage != null) {
            item {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = result.errorMessage,
                color = StatusError,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
              )
            }
          }
        }

        if (webConsoleLogs.isNotEmpty()) {
          item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "=== Browser Console Output ===",
              color = AccentCyan,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }
          items(webConsoleLogs) { log ->
            Text(
              text = log,
              color = TextSecondary,
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ErrorsTabContent(
  executionResult: ExecutionResult?,
  detectedIssues: List<CodeIssue>,
  aiLanguageMode: AiLanguageMode,
  onLanguageModeChanged: (AiLanguageMode) -> Unit,
  onApplyFix: (CodeIssue, Boolean) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(12.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    if (detectedIssues.isEmpty() && executionResult?.compilerDiagnostics.isNullOrBlank() && executionResult?.stderr.isNullOrBlank()) {
      item {
        Column(
          modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "No errors",
            tint = StatusSuccess,
            modifier = Modifier.size(36.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Zero diagnostics or compiler errors detected.",
            color = TextSecondary,
            fontSize = 13.sp
          )
        }
      }
    } else {
      // Language Mode Selector for CodeSense
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "CodeSense AI Diagnostics (${detectedIssues.size} issues)",
            color = AccentCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )

          // Language toggles
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(BgSurface)
              .padding(2.dp)
          ) {
            AiLanguageMode.values().forEach { mode ->
              val isSel = mode == aiLanguageMode
              Text(
                text = when (mode) {
                  AiLanguageMode.ENGLISH -> "EN"
                  AiLanguageMode.HINDI -> "हिंदी"
                  AiLanguageMode.HINGLISH -> "Hinglish"
                },
                fontSize = 10.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                color = if (isSel) AccentCyan else TextSecondary,
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(if (isSel) BgSurfaceElevated else Color.Transparent)
                  .clickable { onLanguageModeChanged(mode) }
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }

      // Structured issues
      items(detectedIssues) { issue ->
        CodeIssueCard(
          issue = issue,
          langMode = aiLanguageMode,
          onApplyFix = onApplyFix
        )
      }

      // Raw compiler diagnostics preview
      val rawDiag = executionResult?.compilerDiagnostics?.ifBlank { executionResult.stderr } ?: ""
      if (rawDiag.isNotBlank()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BgSurface),
            shape = RoundedCornerShape(8.dp)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "Raw Compiler / Interpreter Output:",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
              Spacer(modifier = Modifier.height(6.dp))
              SelectionContainer {
                Text(
                  text = rawDiag,
                  color = StatusError,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  lineHeight = 16.sp
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CodeIssueCard(
  issue: CodeIssue,
  langMode: AiLanguageMode,
  onApplyFix: (CodeIssue, Boolean) -> Unit
) {
  val explanation = when (langMode) {
    AiLanguageMode.ENGLISH -> issue.explanationEn
    AiLanguageMode.HINDI -> issue.explanationHi
    AiLanguageMode.HINGLISH -> issue.explanationHinglish
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = BgSurface),
    shape = RoundedCornerShape(10.dp),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Header: Category, Severity, Location
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(if (issue.severity == IssueSeverity.CRITICAL) StatusError else StatusWarning)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = issue.category,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Text(
          text = if (issue.line != null) "${issue.fileName}:${issue.line}" else issue.fileName,
          color = AccentCyan,
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Diagnostic raw message
      Text(
        text = issue.rawMessage,
        color = StatusError,
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Multi-language friendly explanation
      Text(
        text = explanation,
        color = TextSecondary,
        fontSize = 12.sp,
        lineHeight = 18.sp
      )

      // Diff preview if corrected snippet exists
      if (issue.correctedSnippet.isNotBlank() && issue.originalSnippet.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(BgDarkest)
            .padding(8.dp)
        ) {
          Text(
            text = "- " + issue.originalSnippet,
            color = StatusError,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp
          )
          Text(
            text = "+ " + issue.correctedSnippet,
            color = StatusSuccess,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (issue.correctedSnippet.isNotBlank()) {
          Button(
            onClick = { onApplyFix(issue, false) },
            modifier = Modifier.weight(1f).height(34.dp).testTag("apply_fix_button"),
            colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AutoFixHigh,
              contentDescription = "Apply",
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Apply Fix", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          FilledTonalButton(
            onClick = { onApplyFix(issue, true) },
            modifier = Modifier.weight(1f).height(34.dp).testTag("apply_and_run_button"),
            colors = ButtonDefaults.filledTonalButtonColors(containerColor = BgSurfaceElevated, contentColor = AccentCyan),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Run",
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Apply & Rerun", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun InputTabContent(
  standardInput: String,
  onStandardInputChange: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(12.dp)
  ) {
    Text(
      text = "Standard Input (stdin):",
      color = TextSecondary,
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = "Values entered here are passed to cin, input(), or Scanner when running.",
      color = TextMuted,
      fontSize = 11.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
      value = standardInput,
      onValueChange = onStandardInputChange,
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .testTag("stdin_input_field"),
      placeholder = { Text("Enter inputs separated by newlines...", color = TextMuted, fontSize = 12.sp) },
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AccentCyan,
        unfocusedBorderColor = BorderSubtle,
        focusedContainerColor = BgDarkest,
        unfocusedContainerColor = BgDarkest
      ),
      textStyle = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        color = TextPrimary
      )
    )
  }
}
