package com.nofrills.workouttracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF6FAE95),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDF4E9),
    onPrimaryContainer = Color(0xFF587167),
    secondary = Color(0xFFA5BFE8),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEAF2FF),
    onSecondaryContainer = Color(0xFF61718B),
    background = Color(0xFFFFFBF4),
    onBackground = Color(0xFF66736D),
    surface = Color(0xFFFFFEFA),
    onSurface = Color(0xFF66736D),
    surfaceVariant = Color(0xFFF1F7EF),
    onSurfaceVariant = Color(0xFF718078),
    outline = Color(0xFFB8C7BE)
)

/** Applies a soft, always-light Material 3 palette to keep the tracker bright and low-friction. */
@Composable
fun NoFrillsWorkoutTrackerTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
