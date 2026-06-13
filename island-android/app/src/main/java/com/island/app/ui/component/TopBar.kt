package com.island.app.ui.component

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
fun TopBar(
    coins: Int = 100,
    keys: Int = 54,
    showGem: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                Brush.verticalGradient(listOf(WoodLight, WoodDark))
            )
            .border(
                width = 2.dp,
                color = WoodBorder,
                shape = RoundedCornerShape(0.dp)
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 녹색 젬 아이콘 (좌)
        if (showGem) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(GreenDark)
                    .border(2.dp, GreenBorder, RoundedCornerShape(6.dp))
            )
            Spacer(Modifier.width(8.dp))
        }

        // 코인 영역
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(GoldLight, Gold, GoldDark))
                    )
                    .border(1.5.dp, GoldBorder, CircleShape)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = coins.toString(),
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp
            )
            Spacer(Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(GreenBright)
                    .border(1.dp, GreenBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(Modifier.weight(1f))

        // 열쇠 영역
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(RedKey)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = keys.toString(),
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp
            )
        }
    }
}
