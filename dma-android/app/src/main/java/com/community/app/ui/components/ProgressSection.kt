package com.community.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.community.app.ui.theme.CommunityPink
import com.community.app.ui.theme.ProgressEnd
import com.community.app.ui.theme.ProgressStart

@Composable
fun ProgressSection(progress: Float) {
    Column {
        Text(
            text = buildAnnotatedString {
                append("Your overall progress is ")
                withStyle(SpanStyle(color = CommunityPink, fontWeight = FontWeight.Bold)) {
                    append("${progress.toInt()}%")
                }
            },
            fontSize = 14.sp,
            color = Color(0xFF374151),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFE5E7EB)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress / 100f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Brush.horizontalGradient(listOf(ProgressStart, ProgressEnd))),
            )
        }
    }
}
