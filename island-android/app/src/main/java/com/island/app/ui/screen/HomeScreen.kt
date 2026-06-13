package com.island.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.island.app.ui.theme.*

@Composable
fun HomeScreen(onPlay: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to SkyBlue,
                        0.55f to SeaBlue,
                        0.75f to Sand,
                        1f to Color(0xFFC49A5A)
                    )
                )
            )
    ) {
        // 구름들
        CloudShape(x = 0.45f, y = 0.10f, width = 120.dp, height = 36.dp)
        CloudShape(x = -0.05f, y = 0.15f, width = 90.dp, height = 28.dp)
        CloudShape(x = 0.65f, y = 0.06f, width = 100.dp, height = 32.dp)
        CloudShape(x = 0.1f, y = 0.08f, width = 80.dp, height = 24.dp)

        // ISLAND 로고
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 190.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ISLAND",
                fontSize = 66.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = Gold,
                textAlign = TextAlign.Center,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color(0xFF6A0DAD),
                        blurRadius = 8f,
                        offset = androidx.compose.ui.geometry.Offset(2f, 4f)
                    )
                )
            )
        }

        // Play 버튼
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 390.dp),
            contentAlignment = Alignment.Center
        ) {
            // 나무 외곽 프레임
            Box(
                modifier = Modifier
                    .width(141.dp)
                    .height(59.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.verticalGradient(listOf(WoodMedium, WoodDark)))
                    .border(3.dp, WoodBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(123.dp)
                        .height(39.dp)
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.verticalGradient(listOf(GoldLight, Gold, GoldDark)))
                        .border(2.dp, GoldBorder, RoundedCornerShape(8.dp))
                        .clickable { onPlay() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Play",
                        color = WoodDark,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        fontSize = 22.sp
                    )
                }
            }
        }

        // 보물상자
        TreasureChest(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 180.dp)
        )

        // 바위들
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 20.dp, y = (-140).dp)
                .size(60.dp, 40.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF8C7B6A), Color(0xFF5A4A3A)))
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-20).dp, y = (-140).dp)
                .size(50.dp, 35.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF8C7B6A), Color(0xFF5A4A3A)))
                )
        )
    }
}

@Composable
fun CloudShape(x: Float, y: Float, width: androidx.compose.ui.unit.Dp, height: androidx.compose.ui.unit.Dp) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenW = maxWidth
        val screenH = maxHeight
        Box(
            modifier = Modifier
                .offset(
                    x = screenW * x,
                    y = screenH * y
                )
                .size(width, height)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.85f))
        )
    }
}

@Composable
fun TreasureChest(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 뚜껑
        Box(
            modifier = Modifier
                .width(130.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF8B5E1A), Color(0xFF5C3A0A)))
                )
                .border(3.dp, WoodBorder, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("💎", fontSize = 20.sp)
        }
        // 몸체
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(70.dp)
                .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF7B4F15), Color(0xFF4A2D08)))
                )
                .border(3.dp, WoodBorder, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("🪙✨🪙", fontSize = 18.sp, letterSpacing = 2.sp)
        }
    }
}
