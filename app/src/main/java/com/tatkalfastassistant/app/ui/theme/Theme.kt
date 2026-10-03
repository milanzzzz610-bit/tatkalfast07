package com.tatkalfastassistant.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AmberPrimary = Color(0xFFF59E0B)
val AmberVariant = Color(0xFFD97706)
val NavyDarkBackground = Color(0xFF090D16)
val NavySurface = Color(0xFF0F172A)
val NavySurfaceVariant = Color(0xFF1E293B)
val TextLight = Color(0xFFF8FAFC)
val EmeraldAccent = Color(0xFF10B981)

private val DarkColorScheme = darkColorScheme(
    primary = AmberPrimary,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = AmberVariant,
    background = NavyDarkBackground,
    surface = NavySurface,
    surfaceVariant = NavySurfaceVariant,
    onBackground = TextLight,
    onSurface = TextLight
)

private val LightColorScheme = lightColorScheme(
    primary = AmberVariant,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    surfaceVariant = Color(0xFFE2E8F0),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A)
)

@Composable
fun TatkalFastAssistantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
