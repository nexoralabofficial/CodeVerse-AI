package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ai.AiLanguageMode
import com.example.ai.GeminiAiService
import com.example.data.PreferencesManager
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  prefs: PreferencesManager,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val fontSize by prefs.fontSize.collectAsState()
  val showLineNumbers by prefs.showLineNumbers.collectAsState()
  val wordWrap by prefs.wordWrap.collectAsState()
  val autoSave by prefs.autoSave.collectAsState()
  val aiLanguageMode by prefs.aiLanguageMode.collectAsState()
  val customApiKey by prefs.customApiKey.collectAsState()

  var apiKeyInput by remember(customApiKey) { mutableStateOf(customApiKey) }
  var isKeySaved by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = BgDarkest,
    topBar = {
      TopAppBar(
        title = {
          Text("IDE Settings", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_button")) {
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
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Editor Visuals Section
      item {
        SettingsSectionHeader(title = "Editor Appearance & Layout")
      }

      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = BgSurface),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            // Font Size Slider
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Editor Font Size", color = TextPrimary, fontSize = 14.sp)
              Text("${fontSize}sp", color = AccentCyan, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }
            Slider(
              value = fontSize.toFloat(),
              onValueChange = { prefs.setFontSize(it.toInt()) },
              valueRange = 11f..22f,
              steps = 11,
              colors = SliderDefaults.colors(
                thumbColor = AccentCyan,
                activeTrackColor = AccentPrimary
              ),
              modifier = Modifier.testTag("font_size_slider")
            )

            // Preview Box
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(BgDarkest)
                .padding(8.dp)
            ) {
              Text(
                text = "val code = \"Hello CodeVerse!\"; // Preview",
                fontFamily = FontFamily.Monospace,
                fontSize = fontSize.sp,
                color = AccentCyan
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Show Line Numbers Switch
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("Show Line Numbers", color = TextPrimary, fontSize = 14.sp)
                Text("Displays code line numbers in gutter", color = TextSecondary, fontSize = 11.sp)
              }
              Switch(
                checked = showLineNumbers,
                onCheckedChange = { prefs.setShowLineNumbers(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = AccentCyan, checkedTrackColor = AccentPrimary),
                modifier = Modifier.testTag("line_numbers_switch")
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Word Wrap Switch
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("Soft Word Wrap", color = TextPrimary, fontSize = 14.sp)
                Text("Wrap long code lines instead of horizontal scroll", color = TextSecondary, fontSize = 11.sp)
              }
              Switch(
                checked = wordWrap,
                onCheckedChange = { prefs.setWordWrap(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = AccentCyan, checkedTrackColor = AccentPrimary)
              )
            }
          }
        }
      }

      // 2. Saving & Behavior
      item {
        SettingsSectionHeader(title = "Persistence & Autosave")
      }

      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = BgSurface),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("Automatic File Saving", color = TextPrimary, fontSize = 14.sp)
                Text("Saves files before compiling or switching tabs", color = TextSecondary, fontSize = 11.sp)
              }
              Switch(
                checked = autoSave,
                onCheckedChange = { prefs.setAutoSave(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = AccentCyan, checkedTrackColor = AccentPrimary)
              )
            }
          }
        }
      }

      // 3. AI Assistant & CodeSense Preferences
      item {
        SettingsSectionHeader(title = "CodeSense AI & Language")
      }

      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = BgSurface),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text("AI Explanation Language", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text("Select preferred language for compiler diagnostics and explanations:", color = TextSecondary, fontSize = 11.sp)

            Spacer(modifier = Modifier.height(10.dp))

            AiLanguageMode.values().forEach { mode ->
              val isSelected = mode == aiLanguageMode
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) BgSurfaceElevated else Color.Transparent)
                  .clickable { prefs.setAiLanguageMode(mode) }
                  .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                RadioButton(
                  selected = isSelected,
                  onClick = { prefs.setAiLanguageMode(mode) },
                  colors = RadioButtonDefaults.colors(selectedColor = AccentCyan)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = mode.displayName,
                  color = if (isSelected) AccentCyan else TextPrimary,
                  fontSize = 13.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text("Custom Gemini API Key (Optional)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(
              text = "Leave empty to use automatic injected key or offline rule-based diagnostics.",
              color = TextMuted,
              fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
              value = apiKeyInput,
              onValueChange = {
                apiKeyInput = it
                isKeySaved = false
              },
              placeholder = { Text("AIzaSy...", color = TextMuted, fontSize = 12.sp) },
              modifier = Modifier.fillMaxWidth().testTag("custom_api_key_field"),
              singleLine = true,
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

            Spacer(modifier = Modifier.height(8.dp))

            Button(
              onClick = {
                prefs.setCustomApiKey(apiKeyInput.trim())
                GeminiAiService.customApiKey = apiKeyInput.trim()
                isKeySaved = true
              },
              colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.align(Alignment.End).height(34.dp)
            ) {
              Text(if (isKeySaved) "Saved ✓" else "Save Key", fontSize = 12.sp)
            }
          }
        }
      }

      // 4. Execution Sandbox Information
      item {
        SettingsSectionHeader(title = "Execution Architecture & Sandbox")
      }

      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = BgSurface),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "• Web Projects: Rendered in isolated Android WebView canvas with DOM console capture.",
              color = TextSecondary,
              fontSize = 11.sp,
              lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "• C, C++, Python, Java, Rust: Remote high-performance containerized execution with stdout/stderr diagnostics and process timeouts.",
              color = TextSecondary,
              fontSize = 11.sp,
              lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "• CodeSense AI: Real-time static & diagnostic analyzer with 1-click patch application.",
              color = TextSecondary,
              fontSize = 11.sp,
              lineHeight = 16.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SettingsSectionHeader(title: String) {
  Text(
    text = title,
    color = AccentCyan,
    fontSize = 13.sp,
    fontWeight = FontWeight.Bold,
    fontFamily = FontFamily.Monospace
  )
}
