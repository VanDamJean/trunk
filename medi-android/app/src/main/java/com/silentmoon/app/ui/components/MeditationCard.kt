package com.silentmoon.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
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
import com.silentmoon.app.data.MeditationSession

val cardGradients = listOf(
    listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
    listOf(Color(0xFFA78BFA), Color(0xFF7C3AED)),
    listOf(Color(0xFF6366F1), Color(0xFF4338CA)),
    listOf(Color(0xFF7C3AED), Color(0xFF4C1D95)),
    listOf(Color(0xFF8B5CF6), Color(0xFF5B21B6)),
)

@Composable
fun MeditationCard(
    session: MeditationSession,
    gradientIndex: Int = 0,
    onClick: () -> Unit = {}
) {
    val gradient = cardGradients[gradientIndex % cardGradients.size]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(gradient))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    session.title,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    session.description,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.25f)
                ) {
                    Text(
                        session.duration,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Text(session.icon, fontSize = 44.sp)
        }
    }
}

@Composable
fun MeditationCardCompact(
    session: MeditationSession,
    gradientIndex: Int = 0,
    onClick: () -> Unit = {}
) {
    val gradient = cardGradients[gradientIndex % cardGradients.size]

    Box(
        modifier = Modifier
            .width(160.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(gradient))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(session.icon, fontSize = 36.sp)
            Column {
                Text(
                    session.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    session.duration,
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 11.sp
                )
            }
        }
    }
}
