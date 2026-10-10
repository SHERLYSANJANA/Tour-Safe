package com.toursafe.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = Color.Black,
    primaryContainer = SurfaceVariantNavy,
    onPrimaryContainer = TextPrimary,
    secondary = AccentBlue,
    onSecondary = Color.White,
    background = BackgroundNavy,
    onBackground = TextPrimary,
    surface = SurfaceNavy,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantNavy,
    onSurfaceVariant = TextSecondary,
    error = AlertRed,
    onError = Color.White
)

@Composable
fun TourSafeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
