package com.example.execution

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class ExecutionStatus {
  READY,
  RUNNING,
  COMPLETED,
  FAILED,
  TIMED_OUT,
  ERROR
}

data class ExecutionRequest(
  val language: String,
  val version: String,
  val files: List<ExecutionFile>,
  val stdin: String = "",
  val timeoutMs: Long = 10000L
)

@JsonClass(generateAdapter = true)
data class ExecutionFile(
  val name: String,
  val content: String
)

@JsonClass(generateAdapter = true)
data class PistonPayload(
  val language: String,
  val version: String,
  val files: List<ExecutionFile>,
  val stdin: String = "",
  @property:Json(name = "compile_timeout")
  val compileTimeout: Long = 10000L,
  @property:Json(name = "run_timeout")
  val runTimeout: Long = 6000L
)

@JsonClass(generateAdapter = true)
data class PistonResponse(
  val language: String?,
  val version: String?,
  val run: PistonStageResult?,
  val compile: PistonStageResult?
)

@JsonClass(generateAdapter = true)
data class PistonStageResult(
  val stdout: String? = "",
  val stderr: String? = "",
  val output: String? = "",
  val code: Int? = 0,
  val signal: String? = null
)

data class ExecutionResult(
  val status: ExecutionStatus,
  val stdout: String = "",
  val stderr: String = "",
  val compilerDiagnostics: String = "",
  val exitCode: Int? = 0,
  val durationMs: Long = 0,
  val errorMessage: String? = null,
  val hasErrors: Boolean = false
)
