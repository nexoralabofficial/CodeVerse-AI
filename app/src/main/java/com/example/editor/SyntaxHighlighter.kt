package com.example.editor

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.model.LanguageId
import com.example.ui.theme.*

object SyntaxHighlighter {

  // Common keyword sets
  private val pyKeywords = setOf(
    "and", "as", "assert", "async", "await", "break", "class", "continue", "def",
    "del", "elif", "else", "except", "finally", "for", "from", "global", "if",
    "import", "in", "is", "lambda", "nonlocal", "not", "or", "pass", "raise",
    "return", "try", "while", "with", "yield", "True", "False", "None"
  )

  private val cppKeywords = setOf(
    "alignas", "alignof", "and", "auto", "bool", "break", "case", "catch", "char",
    "class", "const", "constexpr", "continue", "default", "delete", "do", "double",
    "else", "enum", "explicit", "export", "extern", "false", "float", "for", "friend",
    "goto", "if", "inline", "int", "long", "mutable", "namespace", "new", "noexcept",
    "not", "nullptr", "operator", "or", "private", "protected", "public", "register",
    "return", "short", "signed", "sizeof", "static", "struct", "switch", "template",
    "this", "throw", "true", "try", "typedef", "typeid", "typename", "union",
    "unsigned", "using", "virtual", "void", "volatile", "while"
  )

  private val jsKeywords = setOf(
    "abstract", "arguments", "await", "boolean", "break", "byte", "case", "catch",
    "char", "class", "const", "continue", "debugger", "default", "delete", "do",
    "double", "else", "enum", "eval", "export", "extends", "false", "final", "finally",
    "float", "for", "function", "goto", "if", "implements", "import", "in",
    "instanceof", "int", "interface", "let", "long", "native", "new", "null",
    "package", "private", "protected", "public", "return", "short", "static",
    "super", "switch", "synchronized", "this", "throw", "throws", "transient",
    "true", "try", "typeof", "var", "void", "volatile", "while", "with", "yield"
  )

  fun highlight(text: String, languageId: LanguageId): AnnotatedString {
    return buildAnnotatedString {
      append(text)

      if (text.isEmpty()) return@buildAnnotatedString

      val keywords = when (languageId) {
        LanguageId.PYTHON -> pyKeywords
        LanguageId.CPP, LanguageId.C, LanguageId.RUST, LanguageId.GO, LanguageId.CSHARP -> cppKeywords
        else -> jsKeywords
      }

      // 1. Strings
      val stringRegex = Regex("(\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\"|'[^'\\\\]*(?:\\\\.[^'\\\\]*)*'|`[^`\\\\]*(?:\\\\.[^`\\\\]*)*`)")
      for (match in stringRegex.findAll(text)) {
        addStyle(SpanStyle(color = SyntaxString), match.range.first, match.range.last + 1)
      }

      // 2. Numbers
      val numberRegex = Regex("""\b\d+(\.\d+)?([eE][+-]?\d+)?\b|0x[0-9a-fA-F]+\b""")
      for (match in numberRegex.findAll(text)) {
        addStyle(SpanStyle(color = SyntaxNumber), match.range.first, match.range.last + 1)
      }

      // 3. Keywords
      val wordRegex = Regex("""\b[a-zA-Z_][a-zA-Z0-9_]*\b""")
      for (match in wordRegex.findAll(text)) {
        val word = match.value
        if (word in keywords) {
          addStyle(
            SpanStyle(color = SyntaxKeyword, fontWeight = FontWeight.SemiBold),
            match.range.first,
            match.range.last + 1
          )
        } else if (word.first().isUpperCase()) {
          // Types or Classes
          addStyle(
            SpanStyle(color = SyntaxType, fontWeight = FontWeight.Medium),
            match.range.first,
            match.range.last + 1
          )
        }
      }

      // 4. HTML / XML Tags
      if (languageId == LanguageId.HTML || languageId == LanguageId.XML) {
        val tagRegex = Regex("""(</?[a-zA-Z0-9\-]+)|(/?>)""")
        for (match in tagRegex.findAll(text)) {
          addStyle(SpanStyle(color = SyntaxTag, fontWeight = FontWeight.Bold), match.range.first, match.range.last + 1)
        }
        val attrRegex = Regex("""\b([a-zA-Z\-:]+)(?==)""")
        for (match in attrRegex.findAll(text)) {
          addStyle(SpanStyle(color = SyntaxAttribute), match.range.first, match.range.last + 1)
        }
      }

      // 5. Operators & Punctuation
      val opRegex = Regex("""[{}()\[\];:,=+\-*/<>!&|^~%]""")
      for (match in opRegex.findAll(text)) {
        addStyle(SpanStyle(color = SyntaxOperator), match.range.first, match.range.last + 1)
      }

      // 6. Comments (single-line //, #, and multi-line /* */)
      val commentRegex = when (languageId) {
        LanguageId.PYTHON, LanguageId.BASH, LanguageId.RUBY -> Regex("""#.*$""", RegexOption.MULTILINE)
        LanguageId.HTML, LanguageId.XML -> Regex("""<!--[\s\S]*?-->""")
        LanguageId.SQL -> Regex("""--.*$""", RegexOption.MULTILINE)
        else -> Regex("""(//.*$)|(/\*[\s\S]*?\*/)""", RegexOption.MULTILINE)
      }

      for (match in commentRegex.findAll(text)) {
        addStyle(
          SpanStyle(color = SyntaxComment, fontStyle = FontStyle.Italic),
          match.range.first,
          match.range.last + 1
        )
      }
    }
  }
}
