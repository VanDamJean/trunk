package com.island.app.ui.modal

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
import androidx.compose.ui.window.Dialog
import com.island.app.ui.theme.*

@Composable
fun ModalContainer(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .width(300.dp)
                .shadow(12.dp, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .background(Cream)
                .border(3.dp, CreamBorder, RoundedCornerShape(14.dp))
        ) {
            // 리본 헤더
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF4CAF50), GreenDark))
                    )
                    .border(
                        width = 3.dp,
                        color = GreenBorder,
                        shape = RoundedCornerShape(0.dp)
                    )
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
                // X 닫기 버튼
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp)
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(GoldLight, Gold, GoldDark)))
                        .border(2.dp, GoldBorder, CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("✕", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            // 내용
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    }
}

@Composable
fun GreenButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(listOf(Color(0xFF66BB6A), Color(0xFF43A047), GreenDark))
            )
            .border(3.dp, GreenBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
    }
}

@Composable
fun StarRow(stars: Int, size: Int = 32) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) { i ->
            Text(
                text = if (i < stars) "★" else "☆",
                color = if (i < stars) StarFilled else StarEmpty,
                fontSize = size.sp
            )
        }
    }
}

@Composable
fun GoldCircleButton(
    icon: String,
    size: Int = 48,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(GoldLight, Gold, GoldDark)))
            .border(3.dp, GoldBorder, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(icon, fontSize = (size * 0.45).sp)
    }
}
