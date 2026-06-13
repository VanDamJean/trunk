package com.gemsin.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650A4)
val PurpleGrey40 = Color(0xFF625B71)
val Pink40 = Color(0xFF7D5260)

val GemsinBlue = Color(0xFF1A1A2E)
val GemsinAccent = Color(0xFF7C4DFF)
val GemsinYellow = Color(0xFFFFD600)
val GemsinGold = Color(0xFFFFB300)
val GemsinSilver = Color(0xFF9E9E9E)
val GemsinBronze = Color(0xFF8D6E63)
val GemsinCard1 = Color(0xFF1565C0)
val GemsinCard2 = Color(0xFF2E7D32)
val GemsinCard3 = Color(0xFF4A148C)
val GemsinCard4 = Color(0xFFBF360C)
val GemsinSurface = Color(0xFF16213E)
val GemsinBackground = Color(0xFF0F3460)

private val DarkColorScheme = darkColorScheme(
    primary = GemsinAccent,
    secondary = GemsinYellow,
    tertiary = Pink80,
    background = GemsinBlue,
    surface = GemsinSurface,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun GemsinTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
