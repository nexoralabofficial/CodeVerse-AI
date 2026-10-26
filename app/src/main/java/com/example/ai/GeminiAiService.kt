package com.example.ai

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiAiService {
  private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

  private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  var customApiKey: String? = null

  private fun getEffectiveApiKey(): String {
    val custom = customApiKey?.trim()
    if (!custom.isNullOrEmpty()) return custom
    val buildKey = try {
      BuildConfig.GEMINI_API_KEY
    } catch (e: Throwable) {
      ""
    }
    return if (buildKey.isNotEmpty() && buildKey != "MY_GEMINI_API_KEY") buildKey else ""
  }

  fun hasApiKey(): Boolean = getEffectiveApiKey().isNotEmpty()

  suspend fun promptAi(
    userPrompt: String,
    codeContext: String = "",
    languageName: String = "",
    preferredLang: AiLanguageMode = AiLanguageMode.ENGLISH
  ): String = withContext(Dispatchers.IO) {
    val apiKey = getEffectiveApiKey()
    if (apiKey.isEmpty()) {
      return@withContext generateLocalAiResponse(userPrompt, codeContext, languageName, preferredLang)
    }

    try {
      val systemLangInstruction = when (preferredLang) {
        AiLanguageMode.ENGLISH -> "Respond clearly and concisely in professional English."
        AiLanguageMode.HINDI -> "Respond in natural, clear Hindi (हिंदी) with code blocks in English."
        AiLanguageMode.HINGLISH -> "Respond in modern conversational Hinglish (Hindi written in English alphabet) with code snippets."
      }

      val fullPrompt = buildString {
        appendLine("You are CodeVerse AI, an expert mobile programming companion and senior architect.")
        appendLine(systemLangInstruction)
        if (languageName.isNotEmpty()) {
          appendLine("Active Language: $languageName")
        }
        if (codeContext.isNotBlank()) {
          appendLine("Current Source Code:\n```\n$codeContext\n```")
        }
        appendLine("User Request: $userPrompt")
      }

      val jsonBody = JSONObject().apply {
        val contentsArray = JSONArray()
        val contentObj = JSONObject()
        val partsArray = JSONArray()
        partsArray.put(JSONObject().put("text", fullPrompt))
        contentObj.put("parts", partsArray)
        contentsArray.put(contentObj)
        put("contents", contentsArray)

        val genConfig = JSONObject().apply {
          put("temperature", 0.4)
          put("maxOutputTokens", 2048)
        }
        put("generationConfig", genConfig)
      }

      val request = Request.Builder()
        .url("$BASE_URL?key=$apiKey")
        .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
        .build()

      val response = client.newCall(request).execute()
      val bodyStr = response.body?.string() ?: ""

      if (!response.isSuccessful) {
        return@withContext "AI service notice (${response.code}): ${JSONObject(bodyStr).optJSONObject("error")?.optString("message") ?: "Could not complete request"}\n\nFalling back to local CodeVerse engine:\n\n" +
          generateLocalAiResponse(userPrompt, codeContext, languageName, preferredLang)
      }

      val responseJson = JSONObject(bodyStr)
      val candidates = responseJson.optJSONArray("candidates")
      val text = candidates?.optJSONObject(0)
        ?.optJSONObject("content")
        ?.optJSONArray("parts")
        ?.optJSONObject(0)
        ?.optString("text")

      text ?: generateLocalAiResponse(userPrompt, codeContext, languageName, preferredLang)
    } catch (e: Exception) {
      "Offline AI Response (Network unavailable):\n\n" +
        generateLocalAiResponse(userPrompt, codeContext, languageName, preferredLang)
    }
  }

  private fun generateLocalAiResponse(
    prompt: String,
    code: String,
    language: String,
    langMode: AiLanguageMode
  ): String {
    val p = prompt.lowercase()
    return when {
      p.contains("explain") || p.contains("samjhao") || p.contains("samjha") -> {
        when (langMode) {
          AiLanguageMode.HINDI -> "### कोड विवरण (CodeVerse AI Local):\n\nयह कोड $language में लिखा गया है। यह डेटा को प्रोसेस करता है और परिणाम कंसोल पर प्रिंट करता है।\n\n1. आवश्यक लाइब्रेरीज को इम्पोर्ट करता है।\n2. मुख्य लॉजिक को निष्पादित करता है।\n3. आउटपुट प्रदर्शित करता है।"
          AiLanguageMode.HINGLISH -> "### Code Explanation (CodeVerse AI):\n\nYeh program $language me likha gaya hai. Iska main flow yeh hai:\n\n1. Required modules / headers import karta hai.\n2. Functions aur data structures define karta hai.\n3. Calculated results console me print karta hai. Execution check karne ke liye 'Run' button tap karein!"
          else -> "### Code Analysis ($language):\n\nThis source code defines executable routines in $language. It demonstrates standard initialization, variable assignments, computational loops, and console logging. Tap 'Run' in the editor toolbar to execute it live."
        }
      }
      p.contains("optimize") -> {
        "### Optimization Recommendations:\n\n1. **Time Complexity**: Ensure loops avoid repeated calculations; cache static lengths or values outside loops.\n2. **Memory Allocation**: Use pre-allocated buffers/vectors where collection size is known in advance.\n3. **Modern Idioms**: Use standard library algorithms or list comprehensions for cleaner execution."
      }
      p.contains("test") -> {
        "### Suggested Unit Test Suite:\n\n```$language\n// Test 1: Standard input\n// Input: [10, 20, 30] -> Expected Result: Success\n\n// Test 2: Edge cases\n// Input: [] (Empty set) -> Handles gracefully\n// Input: Boundary values -> No overflow\n```"
      }
      p.contains("convert") -> {
        "### Language Porting:\nTo convert between $language and another language, preserve the algorithmic steps and map the collection data types and print statements to the target language idioms."
      }
      else -> {
        when (langMode) {
          AiLanguageMode.HINDI -> "CodeVerse AI सहायक तैयार है। आप कोड को एक्सप्लेन करने, एरर फिक्स करने या नए फंक्शन बनाने के लिए कह सकते हैं।"
          AiLanguageMode.HINGLISH -> "CodeVerse AI Assistant ready hai! Aap code explain karne, bugs find karne, ya optimization ke liye prompts de sakte hain."
          else -> "CodeVerse AI Assistant is ready. You can ask for line-by-line code explanation, bug detection, automated test cases, or language conversions."
        }
      }
    }
  }
}
