package com.silentmoon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silentmoon.app.data.MockData
import com.silentmoon.app.ui.theme.SMAccent
import com.silentmoon.app.ui.theme.SMBackgroundLight
import com.silentmoon.app.ui.theme.SMPrimary
import com.silentmoon.app.ui.theme.SMSecondary

val topicIcons = listOf("😌", "😰", "😴", "🎯", "😊", "🌱")

val topicGradients = listOf(
    listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
    listOf(Color(0xFF6366F1), Color(0xFF4338CA)),
    listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
    listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED)),
    listOf(Color(0xFFA78BFA), Color(0xFF7C3AED)),
    listOf(Color(0xFF34D399), Color(0xFF059669)),
)

@Composable
fun TopicSelectionScreen(onDone: () -> Unit) {
    val selectedTopics = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SMBackgroundLight)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Text(
                "What brings you to Silent Moon?",
                color = SMSecondary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Choose a topic to focus on. You can always change this later.",
                color = Color(0xFF6B7280),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }

        // Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(MockData.topics) { index, topic ->
                val isSelected = selectedTopics.contains(topic)
                val gradient = topicGradients[index % topicGradients.size]
                val icon = topicIcons[index % topicIcons.size]

                Box(
                    modifier = Modifier
                        .height(110.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) Brush.verticalGradient(gradient)
                            else Brush.verticalGradient(
                                listOf(
                                    Color(0xFFF3F0FF),
                                    Color(0xFFEDE9FE)
                                )
                            )
                        )
                        .border(
                            width = if (isSelected) 0.dp else 1.dp,
                            color = if (isSelected) Color.Transparent else Color(0xFFDDD6FE),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            if (isSelected) selectedTopics.remove(topic)
                            else selectedTopics.add(topic)
                        }
                        .padding(14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(icon, fontSize = 28.sp)
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(RoundedCornerShape(11.dp))
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("✓", color = SMPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(
                            topic,
                            color = if (isSelected) Color.White else SMSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Bottom area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            if (selectedTopics.isNotEmpty()) {
                Text(
                    "${selectedTopics.size} topic${if (selectedTopics.size > 1) "s" else ""} selected",
                    color = SMPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTopics.isNotEmpty()) SMPrimary
                    else Color(0xFFD1D5DB)
                )
            ) {
                Text(
                    "Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Maybe Later", color = Color(0xFF9CA3AF), fontSize = 14.sp)
            }
        }
    }
}
