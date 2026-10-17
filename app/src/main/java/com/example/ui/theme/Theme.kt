package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = PrimaryPurple,
  onPrimary = Color.White,
  primaryContainer = PrimaryPurpleContainer,
  onPrimaryContainer = OnPrimaryPurpleContainer,
  secondary = AccentCoral,
  onSecondary = Color.White,
  secondaryContainer = AccentCoralContainer,
  onSecondaryContainer = OnAccentCoralContainer,
  tertiary = PrimaryPurpleDark,
  onTertiary = Color.White,
  background = AppBackground,
  onBackground = TextPrimary,
  surface = CardSurface,
  onSurface = TextPrimary,
  surfaceVariant = SurfaceSubtle,
  onSurfaceVariant = TextSecondary,
  outline = BorderStroke,
  outlineVariant = BorderLight,
  error = StatusError,
  onError = Color.White,
  errorContainer = StatusErrorBg,
  onErrorContainer = StatusError
)

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryPurple,
  onPrimary = Color.White,
  primaryContainer = PrimaryPurpleDeep,
  onPrimaryContainer = Color.White,
  secondary = AccentCoral,
  onSecondary = Color.White,
  secondaryContainer = OnAccentCoralContainer,
  onSecondaryContainer = Color.White,
  background = DarkBackground,
  onBackground = DarkTextPrimary,
  surface = DarkSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = DarkSurfaceSubtle,
  onSurfaceVariant = DarkTextSecondary,
  outline = Color(0xFF475569),
  error = StatusError,
  onError = Color.White
)

@Composable
fun EventHubTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Set to false by default so the requested Vibrant Purple and Accent Coral palette shines consistently
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
