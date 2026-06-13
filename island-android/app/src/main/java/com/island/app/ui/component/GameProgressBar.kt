package com.island.app.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.island.app.ui.theme.*

@Composable
fun GameProgressBar(
    score: Int,
    targetScore: Int = 5000,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = (score.toFloat() / targetScore.toFloat()).coerceIn(0f, 1f),
        animationSpec = tween(300),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(16.dp)
    ) {
        // 배경 바
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(ProgressBg)
                .border(1.dp, WoodBorder, RoundedCornerShape(8.dp))
        )
        // 채움 바
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .clip(RoundedCornerShape(8.dp))
                .background(ProgressFill)
        )
        // 별 3개 마커
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(0.33f, 0.66f, 1.0f).forEach { threshold ->
                val reached = progress >= threshold
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(if (reached) StarFilled else StarEmpty)
                        .border(1.dp, WoodBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "★",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
