package com.island.app.data.model

import androidx.compose.ui.graphics.Color
import com.island.app.ui.theme.*

enum class GemColor(val light: Color, val dark: Color) {
    BLUE(GemBlue, GemBlueDark),
    PINK(GemPink, GemPinkDark),
    YELLOW(GemYellow, GemYellowDark),
    GREEN(GemGreen, GemGreenDark);

    companion object {
        fun random(): GemColor = entries.random()
    }
}
