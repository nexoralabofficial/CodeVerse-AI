package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun MobileProgrammingToolbar(
  onInsertSymbol: (String) -> Unit,
  onIndent: () -> Unit,
  onToggleComment: () -> Unit,
  onDuplicateLine: () -> Unit,
  modifier: Modifier = Modifier
) {
  val symbols = listOf(
    "{", "}", "(", ")", "[", "]", ";", ":", "\"", "'",
    "=", "+", "-", "/", "*", "<", ">", "!", "?", "$",
    "_", "\\", "#", "@", "&", "|", "~", "%", "."
  )

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = BgSurfaceElevated,
    shadowElevation = 4.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(44.dp)
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      // Indent (Tab 4 spaces)
      ToolbarActionButton(
        label = "TAB",
        testTag = "toolbar_tab",
        onClick = onIndent
      )

      // Comment Toggle
      ToolbarActionButton(
        label = "//",
        testTag = "toolbar_comment",
        onClick = onToggleComment
      )

      // Duplicate Line
      ToolbarActionButton(
        label = "DUP",
        testTag = "toolbar_duplicate",
        onClick = onDuplicateLine
      )

      Box(
        modifier = Modifier
          .width(1.dp)
          .height(24.dp)
          .background(BorderSubtle)
      )

      // Symbol buttons
      symbols.forEach { sym ->
        SymbolButton(
          symbol = sym,
          onClick = { onInsertSymbol(sym) }
        )
      }
    }
  }
}

@Composable
private fun ToolbarActionButton(
  label: String,
  testTag: String,
  onClick: () -> Unit
) {
  FilledTonalButton(
    onClick = onClick,
    modifier = Modifier
      .testTag(testTag)
      .height(34.dp),
    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
    colors = ButtonDefaults.filledTonalButtonColors(
      containerColor = BgSurface,
      contentColor = AccentCyan
    ),
    shape = RoundedCornerShape(6.dp)
  ) {
    Text(
      text = label,
      fontFamily = FontFamily.Monospace,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp
    )
  }
}

@Composable
private fun SymbolButton(
  symbol: String,
  onClick: () -> Unit
) {
  FilledTonalButton(
    onClick = onClick,
    modifier = Modifier
      .testTag("symbol_$symbol")
      .height(34.dp)
      .defaultMinSize(minWidth = 36.dp),
    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
    colors = ButtonDefaults.filledTonalButtonColors(
      containerColor = BgDark,
      contentColor = TextPrimary
    ),
    shape = RoundedCornerShape(6.dp)
  ) {
    Text(
      text = symbol,
      fontFamily = FontFamily.Monospace,
      fontWeight = FontWeight.SemiBold,
      fontSize = 14.sp
    )
  }
}
