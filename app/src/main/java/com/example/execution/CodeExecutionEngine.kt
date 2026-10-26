package com.example.execution

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException

object CodeExecutionEngine {

  suspend fun execute(request: ExecutionRequest): ExecutionResult = withContext(Dispatchers.IO) {
    val startTime = System.currentTimeMillis()
    try {
      val payload = PistonPayload(
        language = request.language,
        version = request.version,
        files = request.files,
        stdin = request.stdin,
        compileTimeout = request.timeoutMs,
        runTimeout = request.timeoutMs
      )

      val response = PistonClient.api.executeCode(payload)
      val duration = System.currentTimeMillis() - startTime

      val compileResult = response.compile
      val runResult = response.run

      val compileStderr = compileResult?.stderr?.trim() ?: ""
      val runStderr = runResult?.stderr?.trim() ?: ""
      val runStdout = runResult?.stdout ?: (runResult?.output ?: "")

      val isCompileError = compileResult != null && (compileResult.code != 0 || compileStderr.isNotEmpty())
      val isRunError = runResult != null && (runResult.code != 0 || runStderr.isNotEmpty())
      val hasError = isCompileError || isRunError

      val exitCode = if (isCompileError) compileResult.code else runResult?.code ?: 0

      val status = when {
        exitCode == 0 && !hasError -> ExecutionStatus.COMPLETED
        else -> ExecutionStatus.FAILED
      }

      ExecutionResult(
        status = status,
        stdout = runStdout,
        stderr = runStderr,
        compilerDiagnostics = compileStderr,
        exitCode = exitCode,
        durationMs = duration,
        hasErrors = hasError
      )
    } catch (e: SocketTimeoutException) {
      val duration = System.currentTimeMillis() - startTime
      ExecutionResult(
        status = ExecutionStatus.TIMED_OUT,
        durationMs = duration,
        errorMessage = "Execution timed out (exceeded time limit). Check for infinite loops or heavy operations.",
        hasErrors = true
      )
    } catch (e: IOException) {
      val duration = System.currentTimeMillis() - startTime
      ExecutionResult(
        status = ExecutionStatus.ERROR,
        durationMs = duration,
        errorMessage = "Network or execution service unavailable: ${e.localizedMessage ?: "Connection failed"}. Please verify your internet connection.",
        hasErrors = true
      )
    } catch (e: Exception) {
      val duration = System.currentTimeMillis() - startTime
      ExecutionResult(
        status = ExecutionStatus.ERROR,
        durationMs = duration,
        errorMessage = "Execution failed: ${e.localizedMessage ?: "Unexpected error"}",
        hasErrors = true
      )
    }
  }
}
