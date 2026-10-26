package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.SyntaxHighlighter
import com.example.model.LanguageId
import com.example.ui.theme.*

@Composable
fun CodeEditorArea(
  value: TextFieldValue,
  onValueChange: (TextFieldValue) -> Unit,
  languageId: LanguageId,
  fontSize: Int,
  showLineNumbers: Boolean,
  wordWrap: Boolean,
  modifier: Modifier = Modifier
) {
  val lines = remember(value.text) { value.text.lines() }
  val lineCount = lines.size.coerceAtLeast(1)

  // Calculate active line from cursor position
  val activeLine = remember(value.selection, value.text) {
    val cursor = value.selection.min
    var count = 1
    for (i in 0 until cursor.coerceAtMost(value.text.length)) {
      if (value.text[i] == '\n') count++
    }
    count
  }

  val textStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontSize = fontSize.sp,
    lineHeight = (fontSize * 1.5).sp,
    color = TextPrimary
  )

  val verticalScroll = rememberScrollState()
  val horizontalScroll = rememberScrollState()

  Row(
    modifier = modifier
      .fillMaxSize()
      .background(BgEditor)
      .verticalScroll(verticalScroll)
  ) {
    // Line Numbers Gutter
    if (showLineNumbers) {
      Column(
        modifier = Modifier
          .width((fontSize * 2.8).dp)
          .background(BgDark)
          .padding(vertical = 12.dp, horizontal = 4.dp)
      ) {
        for (i in 1..lineCount) {
          val isActive = i == activeLine
          Text(
            text = i.toString().padStart(3, ' '),
            style = textStyle.copy(
              color = if (isActive) AccentCyan else TextMuted,
              fontSize = (fontSize - 1).sp
            )
          )
        }
      }

      // Gutter separator
      Box(
        modifier = Modifier
          .width(1.dp)
          .fillMaxHeight()
          .background(BorderSubtle)
      )
    }

    // Code Editor Field
    Box(
      modifier = Modifier
        .weight(1f)
        .padding(12.dp)
        .then(if (!wordWrap) Modifier.horizontalScroll(horizontalScroll) else Modifier)
    ) {
      BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        cursorBrush = SolidColor(AccentCyan),
        modifier = Modifier
          .fillMaxWidth()
          .defaultMinSize(minHeight = 400.dp)
          .testTag("code_editor_text_field"),
        visualTransformation = VisualTransformation { original ->
          val highlighted = SyntaxHighlighter.highlight(original.text, languageId)
          TransformedText(highlighted, OffsetMapping.Identity)
        }
      )
    }
  }
}
