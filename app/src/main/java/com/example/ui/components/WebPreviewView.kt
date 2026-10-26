package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.*

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPreviewView(
  htmlContent: String,
  onConsoleMessage: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var webViewInstance by remember { mutableStateOf<WebView?>(null) }
  var isLoading by remember { mutableStateOf(false) }

  Column(modifier = modifier.fillMaxSize().background(BgDarkest)) {
    // Web Preview Controls Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(36.dp)
        .background(BgSurface)
        .padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (isLoading) StatusWarning else StatusSuccess)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isLoading) "Rendering DOM..." else "Live Web View (Sandboxed)",
          fontSize = 11.sp,
          color = TextSecondary
        )
      }

      IconButton(
        onClick = {
          webViewInstance?.loadDataWithBaseURL("https://codeverse.local/", htmlContent, "text/html", "UTF-8", null)
        },
        modifier = Modifier.size(28.dp).testTag("reload_web_preview")
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Reload",
          tint = AccentCyan,
          modifier = Modifier.size(16.dp)
        )
      }
    }

    AndroidView(
      modifier = Modifier.fillMaxSize().weight(1f),
      factory = { ctx ->
        WebView(ctx).apply {
          settings.javaScriptEnabled = true
          settings.domStorageEnabled = true
          settings.loadWithOverviewMode = true
          settings.useWideViewPort = true
          settings.builtInZoomControls = true
          settings.displayZoomControls = false

          webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
              if (consoleMessage != null) {
                val formatted = "[${consoleMessage.messageLevel()}] Line ${consoleMessage.lineNumber()}: ${consoleMessage.message()}"
                onConsoleMessage(formatted)
              }
              return super.onConsoleMessage(consoleMessage)
            }
          }

          webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
              super.onPageStarted(view, url, favicon)
              isLoading = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
              super.onPageFinished(view, url)
              isLoading = false
            }
          }

          webViewInstance = this
          loadDataWithBaseURL("https://codeverse.local/", htmlContent, "text/html", "UTF-8", null)
        }
      },
      update = { view ->
        view.loadDataWithBaseURL("https://codeverse.local/", htmlContent, "text/html", "UTF-8", null)
      }
    )
  }
}
