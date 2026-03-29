package com.devlearn.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF7C5CBF)
val PrimaryVariant = Color(0xFF9B7DE8)
val Secondary = Color(0xFF00C896)
val Background = Color(0xFF0F0F1A)
val Surface = Color(0xFF1A1A2E)
val SurfaceVariant = Color(0xFF16213E)
val OnPrimary = Color(0xFFFFFFFF)
val OnBackground = Color(0xFFE8E8F0)
val OnSurface = Color(0xFFE8E8F0)
val Error = Color(0xFFFF6B6B)
val Success = Color(0xFF00C896)

private val DarkColorScheme = darkColorScheme(
    primary = Primary, secondary = Secondary,
    background = Background, surface = Surface,
    surfaceVariant = SurfaceVariant, onPrimary = OnPrimary,
    onBackground = OnBackground, onSurface = OnSurface, error = Error
)

@Composable
fun DevLearnTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColorScheme, content = content)
}
