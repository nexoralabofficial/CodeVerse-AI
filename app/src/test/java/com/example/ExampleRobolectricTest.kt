package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ai.CodeSenseParser
import com.example.model.LanguageId
import com.example.model.LanguageRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CodeVerse AI", appName)
  }

  @Test
  fun `language registry has required targets`() {
    val python = LanguageRegistry.getById(LanguageId.PYTHON)
    assertNotNull(python)
    assertTrue(python.executionSupport)

    val cpp = LanguageRegistry.getById(LanguageId.CPP)
    assertNotNull(cpp)
    assertTrue(cpp.compilationSupport)

    val html = LanguageRegistry.getById(LanguageId.HTML)
    assertNotNull(html)
    assertTrue(html.isWebPreviewable)
  }

  @Test
  fun `codesense parser analyzes gcc error accurately`() {
    val stderr = "main.cpp:12:5: error: expected ';' before '}' token"
    val code = "int main() {\n    int a = 5\n}"
    val issues = CodeSenseParser.parseDiagnostics(code, "main.cpp", stderr, "")

    assertEquals(1, issues.size)
    assertEquals("Missing Semicolon", issues.first().category)
    assertEquals(12, issues.first().line)
  }
}
