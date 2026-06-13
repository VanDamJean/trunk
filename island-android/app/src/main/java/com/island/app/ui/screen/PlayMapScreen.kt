package com.island.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.island.app.ui.component.BottomNav
import com.island.app.ui.component.TopBar
import com.island.app.ui.modal.GoldenKeyModal
import com.island.app.ui.modal.SettingsModal
import com.island.app.ui.modal.TasksModal
import com.island.app.ui.theme.*

@Composable
fun PlayMapScreen(
    onLevel: () -> Unit,
    onHome: () -> Unit
) {
    var showSettings by remember { mutableStateOf(false) }
    var showTasks by remember { mutableStateOf(false) }
    var showGoldenKey by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to SkyBlue,
                        0.45f to Color(0xFF6BB8E8),
                        0.7f to SeaBlue,
                        0.85f to Sand,
                        1f to Color(0xFFC49A5A)
                    )
                )
            )
    ) {
        // 상단 바
        TopBar(coins = 100, keys = 54, modifier = Modifier.align(Alignment.TopCenter))

        // 섬 씬
        IslandScene(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .align(Alignment.TopCenter)
                .padding(top = 56.dp)
        )

        // 하단 버튼 영역 (Level + Zone)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 70.dp, start = 16.dp, end = 16.dp)
                .height(44.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Level 버튼
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(Brush.verticalGradient(listOf(GreenLight, GreenMedium, GreenDark)))
                    .border(3.dp, GreenBorder, RoundedCornerShape(8.dp))
                    .clickable { onLevel() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Level 1",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }

            // Zone 칩
            Box(contentAlignment = Alignment.TopEnd) {
                Box(
                    modifier = Modifier
                        .width(128.dp)
                        .fillMaxHeight()
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.verticalGradient(listOf(GreenMedium, GreenDark)))
                        .border(3.dp, GreenBorder, RoundedCornerShape(8.dp))
                        .clickable { showGoldenKey = true },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "zone 1",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            "2/10",
                            color = Color.White.copy(0.85f),
                            fontSize = 12.sp
                        )
                    }
                }
                // 뱃지
                Box(
                    modifier = Modifier
                        .offset(x = 8.dp, y = (-8).dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Gold)
                        .border(1.5.dp, GoldBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("2", color = WoodDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        // 하단 내비게이션
        BottomNav(
            onSettings = { showSettings = true },
            onTasks = { showTasks = true },
            onHome = onHome,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // 모달들
        if (showSettings) {
            SettingsModal(onDismiss = { showSettings = false })
        }
        if (showTasks) {
            TasksModal(onDismiss = { showTasks = false })
        }
        if (showGoldenKey) {
            GoldenKeyModal(onDismiss = { showGoldenKey = false })
        }
    }
}

@Composable
fun IslandScene(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        // 야자수 1
        Column(
            modifier = Modifier
                .offset(x = 40.dp, y = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🌴", fontSize = 64.sp)
        }

        // 야자수 2
        Column(
            modifier = Modifier
                .offset(x = 220.dp, y = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🌴", fontSize = 72.sp)
        }

        // 집
        Column(
            modifier = Modifier
                .offset(x = 110.dp, y = 130.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 지붕
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(Color(0xFFE57373))
                    .border(2.dp, Color(0xFFB71C1C), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )
            // 몸체
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .height(70.dp)
                    .background(Color(0xFFF5F5DC))
                    .border(2.dp, Color(0xFFBCAAA4)),
                contentAlignment = Alignment.BottomCenter
            ) {
                // 문
                Box(
                    modifier = Modifier
                        .offset(y = 0.dp)
                        .width(24.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .background(Color(0xFF7B4F15))
                        .border(1.dp, WoodBorder, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )
            }
        }

        // 바위들
        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 280.dp)
                .size(55.dp, 35.dp)
                .clip(RoundedCornerShape(50))
                .background(Brush.verticalGradient(listOf(Color(0xFF8C7B6A), Color(0xFF5A4A3A))))
        )
        Box(
            modifier = Modifier
                .offset(x = 290.dp, y = 260.dp)
                .size(45.dp, 30.dp)
                .clip(RoundedCornerShape(50))
                .background(Brush.verticalGradient(listOf(Color(0xFF8C7B6A), Color(0xFF5A4A3A))))
        )
    }
}
