package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
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
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantBottomSheet(
  isAiLoading: Boolean,
  aiResponse: String?,
  onAskAi: (String) -> Unit,
  onDismiss: () -> Unit,
  onInsertCodeToEditor: (String) -> Unit,
  selectedLanguage: String,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var promptInput by remember { mutableStateOf("") }

  val quickActions = listOf(
    "Explain code line by line",
    "Explain in Hindi (हिंदी)",
    "Explain in Hinglish",
    "Find potential bugs & security",
    "Optimize this function",
    "Write unit test suite",
    "Convert to Python",
    "Generate code comments"
  )

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = BgDark,
    dragHandle = { BottomSheetDefaults.DragHandle(color = TextMuted) },
    modifier = modifier.testTag("ai_assistant_bottom_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .padding(bottom = 32.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(AccentPrimary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "AI",
              tint = AccentCyan,
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "CodeVerse AI Assistant",
              color = TextPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Context: $selectedLanguage",
              color = AccentCyan,
              fontSize = 11.sp
            )
          }
        }

        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = TextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Quick Action Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        quickActions.forEach { action ->
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = BgSurface,
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle)),
            modifier = Modifier.clickable { onAskAi(action) }
          ) {
            Text(
              text = action,
              color = TextSecondary,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // AI Response Content Area
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 120.dp, max = 340.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(BgDarkest)
          .padding(12.dp)
      ) {
        if (isAiLoading) {
          Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            CircularProgressIndicator(
              color = AccentCyan,
              strokeWidth = 2.5.dp,
              modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "CodeVerse AI is thinking...",
              color = TextSecondary,
              fontSize = 13.sp
            )
          }
        } else if (aiResponse.isNullOrBlank()) {
          Text(
            text = "Ask CodeVerse AI to explain, optimize, debug, generate tests, or convert code in English, Hindi, or Hinglish.",
            color = TextMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp
          )
        } else {
          SelectionContainer {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
              item {
                Text(
                  text = aiResponse,
                  color = TextPrimary,
                  fontSize = 13.sp,
                  lineHeight = 20.sp,
                  fontFamily = FontFamily.SansSerif
                )
              }

              item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.End,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  // Copy Response Button
                  OutlinedButton(
                    onClick = {
                      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                      clipboard.setPrimaryClip(ClipData.newPlainText("CodeVerse AI", aiResponse))
                    },
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                  ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", fontSize = 11.sp)
                  }

                  Spacer(modifier = Modifier.width(8.dp))

                  // Extract code if present and insert
                  Button(
                    onClick = {
                      val extracted = extractCodeFromMarkdown(aiResponse)
                      onInsertCodeToEditor(extracted)
                      onDismiss()
                    },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                  ) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = "Insert", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Insert Code", fontSize = 11.sp)
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Prompt Input Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = promptInput,
          onValueChange = { promptInput = it },
          placeholder = { Text("Ask anything about your code...", color = TextMuted, fontSize = 13.sp) },
          modifier = Modifier
            .weight(1f)
            .testTag("ai_prompt_input"),
          maxLines = 3,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentCyan,
            unfocusedBorderColor = BorderSubtle,
            focusedContainerColor = BgSurface,
            unfocusedContainerColor = BgSurface
          ),
          shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = {
            if (promptInput.isNotBlank()) {
              onAskAi(promptInput)
              promptInput = ""
            }
          },
          enabled = promptInput.isNotBlank() && !isAiLoading,
          modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (promptInput.isNotBlank()) AccentPrimary else BgSurfaceElevated)
            .testTag("ai_send_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send",
            tint = if (promptInput.isNotBlank()) Color.White else TextMuted
          )
        }
      }
    }
  }
}

private fun extractCodeFromMarkdown(text: String): String {
  val match = Regex("""```(?:[a-zA-Z0-9_\-]+)?\n([\s\S]*?)```""").find(text)
  return match?.groupValues?.get(1)?.trim() ?: text
}
