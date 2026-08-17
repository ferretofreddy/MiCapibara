package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = CapyOrangePrimary,
  onPrimary = Color.White,
  primaryContainer = CapyOrangeLight,
  onPrimaryContainer = CapyBrownDark,
  secondary = CapyPinkAccent,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFFFD1DC),
  onSecondaryContainer = Color(0xFF4A1020),
  tertiary = CapyBlueAccent,
  onTertiary = Color.White,
  background = CapyBackgroundCream,
  onBackground = CapyBrownDark,
  surface = CapySurfaceYellow,
  onSurface = CapyBrownDark,
  surfaceVariant = Color(0xFFFFF0C2),
  onSurfaceVariant = CapyBrownMedium
)

private val DarkColorScheme = lightColorScheme(
  primary = CapyOrangePrimary,
  onPrimary = Color.White,
  secondary = CapyPinkAccent,
  onSecondary = Color.White,
  background = Color(0xFF2C1810),
  surface = Color(0xFF3E2723),
  onBackground = Color(0xFFFFF9E6),
  onSurface = Color(0xFFFFF9E6)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
