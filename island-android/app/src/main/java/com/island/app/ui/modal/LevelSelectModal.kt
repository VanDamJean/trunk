package com.island.app.ui.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.island.app.ui.theme.*

@Composable
fun LevelSelectModal(
    levelId: Int,
    onPlay: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalContainer(title = "Level $levelId", onDismiss = onDismiss) {
        // 별 3개
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            StarRow(stars = 0, size = 32)
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Select boosts",
            color = WoodDark,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )

        Spacer(Modifier.height(12.dp))

        // 부스트 아이템 3개
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("💣", "⚡", "🌀").forEach { icon ->
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.verticalGradient(listOf(GoldLight, GoldDark)))
                            .border(2.dp, GoldBorder, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(icon, fontSize = 28.sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Gold)
                            .border(1.dp, GoldBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("3", color = WoodDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Play 골드 원형 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            GoldCircleButton(icon = "▶", size = 60, onClick = onPlay)
        }

        Spacer(Modifier.height(8.dp))
    }
}
