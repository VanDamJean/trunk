package com.island.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.island.app.ui.theme.*

@Composable
fun ScoreBar(
    score: Int,
    target: Int,
    moves: Int,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(78.dp)
            .background(Brush.verticalGradient(listOf(WoodLight, WoodDark)))
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 뒤로가기 버튼 (게임 화면에서만 표시)
        if (onBack != null) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .shadow(3.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(GoldLight, Gold, GoldDark)))
                    .border(2.dp, GoldBorder, CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text("←", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
        ScoreCell(label = "Score", value = score.toString())
        ScoreCell(
            label = "Target",
            value = target.toString(),
            showBlueGem = true
        )
        ScoreCell(label = "Moves", value = moves.toString())
    }
}

@Composable
fun ScoreCell(
    label: String,
    value: String,
    showBlueGem: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(100.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.verticalGradient(listOf(GoldLight, Gold)))
            .border(2.dp, GoldBorder, RoundedCornerShape(8.dp))
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            color = WoodDark,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showBlueGem) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(GemBlue)
                        .border(1.dp, GemBlueDark, RoundedCornerShape(4.dp))
                )
                Spacer(Modifier.width(4.dp))
            }
            Text(
                text = value,
                color = WoodDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
