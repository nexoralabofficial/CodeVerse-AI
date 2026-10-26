package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.ai.AiLanguageMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("codeverse_prefs", Context.MODE_PRIVATE)

  private val _fontSize = MutableStateFlow(prefs.getInt(KEY_FONT_SIZE, 14))
  val fontSize: StateFlow<Int> = _fontSize.asStateFlow()

  private val _showLineNumbers = MutableStateFlow(prefs.getBoolean(KEY_SHOW_LINE_NUMBERS, true))
  val showLineNumbers: StateFlow<Boolean> = _showLineNumbers.asStateFlow()

  private val _wordWrap = MutableStateFlow(prefs.getBoolean(KEY_WORD_WRAP, false))
  val wordWrap: StateFlow<Boolean> = _wordWrap.asStateFlow()

  private val _autoSave = MutableStateFlow(prefs.getBoolean(KEY_AUTO_SAVE, true))
  val autoSave: StateFlow<Boolean> = _autoSave.asStateFlow()

  private val _aiLanguageMode = MutableStateFlow(
    AiLanguageMode.valueOf(prefs.getString(KEY_AI_LANG_MODE, AiLanguageMode.ENGLISH.name) ?: AiLanguageMode.ENGLISH.name)
  )
  val aiLanguageMode: StateFlow<AiLanguageMode> = _aiLanguageMode.asStateFlow()

  private val _customApiKey = MutableStateFlow(prefs.getString(KEY_CUSTOM_API_KEY, "") ?: "")
  val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

  fun setFontSize(size: Int) {
    _fontSize.value = size
    prefs.edit().putInt(KEY_FONT_SIZE, size).apply()
  }

  fun setShowLineNumbers(enabled: Boolean) {
    _showLineNumbers.value = enabled
    prefs.edit().putBoolean(KEY_SHOW_LINE_NUMBERS, enabled).apply()
  }

  fun setWordWrap(enabled: Boolean) {
    _wordWrap.value = enabled
    prefs.edit().putBoolean(KEY_WORD_WRAP, enabled).apply()
  }

  fun setAutoSave(enabled: Boolean) {
    _autoSave.value = enabled
    prefs.edit().putBoolean(KEY_AUTO_SAVE, enabled).apply()
  }

  fun setAiLanguageMode(mode: AiLanguageMode) {
    _aiLanguageMode.value = mode
    prefs.edit().putString(KEY_AI_LANG_MODE, mode.name).apply()
  }

  fun setCustomApiKey(key: String) {
    _customApiKey.value = key
    prefs.edit().putString(KEY_CUSTOM_API_KEY, key).apply()
  }

  companion object {
    private const val KEY_FONT_SIZE = "editor_font_size"
    private const val KEY_SHOW_LINE_NUMBERS = "editor_show_line_numbers"
    private const val KEY_WORD_WRAP = "editor_word_wrap"
    private const val KEY_AUTO_SAVE = "editor_auto_save"
    private const val KEY_AI_LANG_MODE = "ai_language_mode"
    private const val KEY_CUSTOM_API_KEY = "custom_gemini_api_key"

    @Volatile
    private var INSTANCE: PreferencesManager? = null

    fun getInstance(context: Context): PreferencesManager {
      return INSTANCE ?: synchronized(this) {
        val instance = PreferencesManager(context.applicationContext)
        INSTANCE = instance
        instance
      }
    }
  }
}
