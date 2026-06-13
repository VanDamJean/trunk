package com.yacoo.rpg.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary          = ColorPrimaryBottom,
    onPrimary        = ColorCream,
    primaryContainer = ColorPrimaryTop,
    secondary        = ColorSecondaryBottom,
    onSecondary      = ColorCream,
    background       = ColorCream,
    onBackground     = ColorInk,
    surface          = ColorCardSolid,
    onSurface        = ColorInk,
    error            = ColorDangerBottom,
    onError          = ColorCream,
)

private val DarkColorScheme = darkColorScheme(
    primary          = ColorPrimaryTop,
    onPrimary        = ColorInk,
    primaryContainer = ColorPrimaryBottom,
    secondary        = ColorSecondaryTop,
    onSecondary      = ColorInk,
    background       = ColorScreenBg,
    onBackground     = ColorCream,
    surface          = ColorChrome,
    onSurface        = ColorCream,
    error            = ColorDangerTop,
    onError          = ColorInk,
)

@Composable
fun YacooTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography  = YacooTypography,
        content     = content
    )
}
