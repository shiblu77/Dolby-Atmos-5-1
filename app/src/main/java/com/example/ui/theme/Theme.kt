package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DolbyDarkColorScheme = darkColorScheme(
    primary = DolbyGold,
    onPrimary = Color(0xFF140E00),
    primaryContainer = Color(0xFF332700),
    onPrimaryContainer = DolbyGold,
    secondary = SpatialCyan,
    onSecondary = Color(0xFF001F26),
    secondaryContainer = Color(0xFF003642),
    onSecondaryContainer = SpatialCyan,
    tertiary = SpatialElectricBlue,
    onTertiary = Color.White,
    background = DolbyObsidian,
    onBackground = TextPrimary,
    surface = DolbySurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = DolbyCardDark,
    onSurfaceVariant = TextSecondary,
    outline = DolbyBorder,
    error = StatusRed,
    onError = Color.White
)

@Composable
fun DolbyByShibluTheme(
    content: @Composable () -> Unit
) {
    // Cinematic theme always stays in dark mode for all menus as requested
    MaterialTheme(
        colorScheme = DolbyDarkColorScheme,
        typography = Typography,
        content = content
    )
}
