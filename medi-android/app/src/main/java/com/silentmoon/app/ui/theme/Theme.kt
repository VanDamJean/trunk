package com.silentmoon.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Silent Moon brand colors
val SMPrimary = Color(0xFF8B5CF6)       // Purple
val SMAccent = Color(0xFFA78BFA)        // Soft Purple
val SMSecondary = Color(0xFF1E1B4B)     // Navy
val SMBackgroundLight = Color(0xFFFAFAFF)
val SMBackgroundDark = Color(0xFF0D0B1E)
val SMSurface = Color(0xFF1A1740)
val SMSurfaceLight = Color(0xFFF3F0FF)
val SMOnPrimary = Color.White
val SMCardGrad1 = Color(0xFF7C3AED)
val SMCardGrad2 = Color(0xFF6D28D9)
val SMCardGrad3 = Color(0xFF4C1D95)
val SMCardGrad4 = Color(0xFF5B21B6)

private val LightColorScheme = lightColorScheme(
    primary = SMPrimary,
    secondary = SMAccent,
    tertiary = SMSecondary,
    background = SMBackgroundLight,
    surface = SMSurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1C1B2E),
    onSurface = Color(0xFF1C1B2E),
)

private val DarkColorScheme = darkColorScheme(
    primary = SMPrimary,
    secondary = SMAccent,
    tertiary = SMSecondary,
    background = SMBackgroundDark,
    surface = SMSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun SilentMoonTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun SilentMoonDarkTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
