package com.island.app.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.island.app.data.model.GemColor

@Composable
fun GemCell(
    color: GemColor?,
    isSelected: Boolean = false,
    isMatched: Boolean = false,
    modifier: Modifier = Modifier
) {
    // 매치 플래시: 1f → 0f
    val matchScale by animateFloatAsState(
        targetValue = if (isMatched) 0f else 1f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "matchScale"
    )

    val scale = if (isMatched) matchScale else if (isSelected) 1.12f else 1f

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(9.dp))
            .then(
                if (color != null) Modifier.background(
                    Brush.verticalGradient(listOf(color.light, color.dark))
                ) else Modifier.background(Color.Transparent)
            )
            .border(
                width = if (isSelected) 3.dp else 1.5.dp,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(9.dp)
            )
    )
}
