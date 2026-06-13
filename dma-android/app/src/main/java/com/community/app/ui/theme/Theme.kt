package com.community.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = CommunityPurple,
    onPrimary = CommunityWhite,
    primaryContainer = Color(0xFFEDE9F8),
    onPrimaryContainer = CommunityPurple,
    secondary = CommunityPink,
    onSecondary = CommunityWhite,
    tertiary = CommunityTeal,
    onTertiary = CommunityWhite,
    background = CommunityWhite,
    onBackground = CommunityTextDark,
    surface = CommunityWhite,
    onSurface = CommunityTextDark,
    surfaceVariant = CommunityLightGray,
    onSurfaceVariant = Color(0xFF666666),
    outline = CommunityGray,
    error = CommunityRed,
    onError = CommunityWhite,
)

@Composable
fun CommunityTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
