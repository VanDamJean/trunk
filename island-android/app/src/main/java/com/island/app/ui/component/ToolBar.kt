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

data class ToolItem(val icon: String, val count: Int)

@Composable
fun ToolBar(
    tools: List<ToolItem> = listOf(
        ToolItem("🔨", 4),
        ToolItem("⚗️", 1),
        ToolItem("💉", 6),
        ToolItem("🎲", 6)
    ),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF5C3A20), WoodDark)))
            .border(2.dp, WoodBorder, RoundedCornerShape(0.dp))
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tools.forEach { tool ->
            ToolItemButton(tool)
        }
    }
}

@Composable
fun ToolItemButton(tool: ToolItem) {
    Box(contentAlignment = Alignment.BottomEnd) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(4.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.verticalGradient(listOf(GoldLight, Gold, GoldDark)))
                .border(2.dp, GoldBorder, RoundedCornerShape(10.dp))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Text(text = tool.icon, fontSize = 26.sp)
        }
        // 수량 뱃지
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Gold)
                .border(1.5.dp, GoldBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = tool.count.toString(),
                color = WoodDark,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}
