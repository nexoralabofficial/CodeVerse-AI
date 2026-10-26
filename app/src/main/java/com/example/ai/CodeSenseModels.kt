package com.example.ai

data class CodeIssue(
  val title: String,
  val category: String,
  val severity: IssueSeverity,
  val fileName: String,
  val line: Int?,
  val column: Int?,
  val rawMessage: String,
  val explanationEn: String,
  val explanationHi: String,
  val explanationHinglish: String,
  val likelyCause: String,
  val suggestedCorrection: String,
  val originalSnippet: String = "",
  val correctedSnippet: String = ""
)

enum class IssueSeverity {
  CRITICAL,
  HIGH,
  MEDIUM,
  WARNING
}

enum class AiLanguageMode(val displayName: String) {
  ENGLISH("English"),
  HINDI("हिंदी (Hindi)"),
  HINGLISH("Hinglish")
}
