package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = AccentPrimary,
  onPrimary = Color.White,
  primaryContainer = Color(0xFF312E81),
  onPrimaryContainer = Color(0xFFE0E7FF),
  secondary = AccentSecondary,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFF4C1D95),
  onSecondaryContainer = Color(0xFFEDE9FE),
  tertiary = AccentCyan,
  onTertiary = Color(0xFF083344),
  tertiaryContainer = Color(0xFF164E63),
  onTertiaryContainer = Color(0xFFCFFAFE),
  background = BgDarkest,
  onBackground = TextPrimary,
  surface = BgSurface,
  onSurface = TextPrimary,
  surfaceVariant = BgDark,
  onSurfaceVariant = TextSecondary,
  outline = BorderSubtle,
  outlineVariant = Color(0xFF1F2937),
  error = StatusError,
  onError = Color.White
)

private val LightColorScheme = lightColorScheme(
  primary = AccentPrimary,
  onPrimary = Color.White,
  secondary = AccentSecondary,
  onSecondary = Color.White,
  tertiary = Color(0xFF0284C7),
  onTertiary = Color.White,
  background = Color(0xFFF8FAFC),
  onBackground = Color(0xFF0F172A),
  surface = Color(0xFFFFFFFF),
  onSurface = Color(0xFF0F172A),
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = Color(0xFF475569),
  outline = Color(0xFFCBD5E1),
  error = StatusError,
  onError = Color.White
)

@Composable
fun CodeVerseTheme(
  darkTheme: Boolean = true, // Default to primary developer dark theme
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
