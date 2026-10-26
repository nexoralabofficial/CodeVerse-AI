package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.PreferencesManager
import com.example.ui.navigation.CodeVerseNavGraph
import com.example.ui.theme.BgDarkest
import com.example.ui.theme.CodeVerseTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val prefs = PreferencesManager.getInstance(applicationContext)

    setContent {
      CodeVerseTheme(darkTheme = true) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = BgDarkest
        ) {
          CodeVerseNavGraph(prefs = prefs)
        }
      }
    }
  }
}
