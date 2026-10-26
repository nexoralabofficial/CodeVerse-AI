package com.example.editor

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

class EditorHistoryManager(
  private val maxHistory: Int = 50
) {
  private val undoStack = ArrayDeque<TextFieldValue>()
  private val redoStack = ArrayDeque<TextFieldValue>()

  fun record(state: TextFieldValue) {
    if (undoStack.isNotEmpty() && undoStack.last().text == state.text) {
      return
    }
    undoStack.addLast(state)
    if (undoStack.size > maxHistory) {
      undoStack.removeFirst()
    }
    redoStack.clear()
  }

  fun canUndo(): Boolean = undoStack.size > 1
  fun canRedo(): Boolean = redoStack.isNotEmpty()

  fun undo(current: TextFieldValue): TextFieldValue? {
    if (!canUndo()) return null
    redoStack.addLast(undoStack.removeLast())
    return undoStack.lastOrNull()
  }

  fun redo(): TextFieldValue? {
    if (!canRedo()) return null
    val state = redoStack.removeLast()
    undoStack.addLast(state)
    return state
  }

  fun clear() {
    undoStack.clear()
    redoStack.clear()
  }
}

object EditorActions {

  fun insertSymbol(current: TextFieldValue, symbol: String): TextFieldValue {
    val text = current.text
    val selection = current.selection

    // Auto-pairing brackets
    val (insertText, cursorOffset) = when (symbol) {
      "{" -> "{}" to 1
      "(" -> "()" to 1
      "[" -> "[]" to 1
      "\"" -> "\"\"" to 1
      "'" -> "''" to 1
      else -> symbol to symbol.length
    }

    val newText = text.replaceRange(selection.min, selection.max, insertText)
    val newCursor = selection.min + cursorOffset
    return TextFieldValue(newText, TextRange(newCursor))
  }

  fun indent(current: TextFieldValue, spaces: Int = 4): TextFieldValue {
    val indentStr = " ".repeat(spaces)
    return insertSymbol(current, indentStr)
  }

  fun toggleComment(current: TextFieldValue, commentPrefix: String = "//"): TextFieldValue {
    val text = current.text
    val sel = current.selection
    val start = sel.min

    val lineStart = text.lastIndexOf('\n', (start - 1).coerceAtLeast(0)).let { if (it == -1) 0 else it + 1 }
    val lineEnd = text.indexOf('\n', start).let { if (it == -1) text.length else it }
    val line = text.substring(lineStart, lineEnd)

    val newLine = if (line.trimStart().startsWith(commentPrefix)) {
      val idx = line.indexOf(commentPrefix)
      line.removeRange(idx, (idx + commentPrefix.length + 1).coerceAtMost(line.length))
    } else {
      "$commentPrefix $line"
    }

    val newText = text.replaceRange(lineStart, lineEnd, newLine)
    return TextFieldValue(newText, TextRange((start + 3).coerceAtMost(newText.length)))
  }

  fun duplicateLine(current: TextFieldValue): TextFieldValue {
    val text = current.text
    val start = current.selection.min

    val lineStart = text.lastIndexOf('\n', (start - 1).coerceAtLeast(0)).let { if (it == -1) 0 else it + 1 }
    val lineEnd = text.indexOf('\n', start).let { if (it == -1) text.length else it }
    val line = text.substring(lineStart, lineEnd)

    val newText = StringBuilder(text)
      .insert(lineEnd, "\n$line")
      .toString()

    return TextFieldValue(newText, TextRange(lineEnd + 1 + line.length))
  }
}
