package com.community.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class TagColor { TEAL, PINK, PURPLE }

@Composable
fun TagBadge(
    label: String,
    color: TagColor,
    onClick: () -> Unit = {},
) {
    val (bg, fg) = when (color) {
        TagColor.TEAL -> Color(0xFFCCFBF1) to Color(0xFF0F766E)
        TagColor.PINK -> Color(0xFFFCE7F3) to Color(0xFFBE185D)
        TagColor.PURPLE -> Color(0xFFEDE9F8) to Color(0xFF5B3FA0)
    }

    Text(
        text = label,
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        color = fg,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
    )
}
