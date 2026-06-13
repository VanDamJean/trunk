package com.island.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
fun BottomNav(
    onSettings: () -> Unit = {},
    onTasks: () -> Unit = {},
    onHome: () -> Unit = {},
    showTasks: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(
                Brush.verticalGradient(listOf(Color(0xFF5C3A20), WoodDark))
            )
            .border(
                width = 2.dp,
                color = WoodBorder,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavButton(label = "⚙", onClick = onSettings)
        if (showTasks) NavButton(label = "≡", onClick = onTasks)
        NavButton(label = "⌂", onClick = onHome)
    }
}

@Composable
fun NavButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(listOf(GoldLight, Gold, GoldDark))
            )
            .border(3.dp, GoldBorder, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
    }
}
