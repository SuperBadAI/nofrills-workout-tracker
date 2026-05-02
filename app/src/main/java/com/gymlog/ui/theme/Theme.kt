package com.gymlog.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF1A1A2E),
    secondary = Color(0xFFE94560),
    background = Color(0xFF0F0F0F),
    surface = Color(0xFF1C1C1C),
    onSurface = Color(0xFFE0E0E0)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF1A1A2E),
    secondary = Color(0xFFE94560),
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A)
)

/** Applies GymLog Material 3 color palette. */
@Composable
fun GymLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
