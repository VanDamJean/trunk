package com.island.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val IslandColorScheme = darkColorScheme(
    primary = GreenMedium,
    onPrimary = Color.White,
    secondary = Gold,
    onSecondary = WoodDark,
    background = SkyBlue,
    onBackground = Color.White,
    surface = WoodDark,
    onSurface = Color.White,
    surfaceVariant = Cream,
    onSurfaceVariant = WoodDark
)

@Composable
fun IslandTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = IslandColorScheme,
        typography = IslandTypography,
        content = content
    )
}
