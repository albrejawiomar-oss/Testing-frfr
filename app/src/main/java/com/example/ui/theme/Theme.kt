package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkNeonColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = NeonBackground,
  primaryContainer = NeonCyanGlow,
  onPrimaryContainer = NeonCyan,
  secondary = NeonMagenta,
  onSecondary = NeonBackground,
  secondaryContainer = NeonMagentaGlow,
  onSecondaryContainer = NeonMagenta,
  tertiary = NeonGreen,
  onTertiary = NeonBackground,
  tertiaryContainer = NeonGreenGlow,
  onTertiaryContainer = NeonGreen,
  background = NeonBackground,
  onBackground = TextPrimary,
  surface = NeonSurface,
  onSurface = TextPrimary,
  surfaceVariant = NeonSurfaceCard,
  onSurfaceVariant = TextSecondary,
  outline = NeonSurfaceCardBorder,
  error = DangerNeon,
  onError = NeonBackground
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkNeonColorScheme,
    typography = Typography,
    content = content
  )
}

