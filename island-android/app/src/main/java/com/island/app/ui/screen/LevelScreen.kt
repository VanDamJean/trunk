package com.island.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.island.app.data.repository.LevelRepository
import com.island.app.ui.component.BottomNav
import com.island.app.ui.component.TopBar
import com.island.app.ui.modal.LevelSelectModal
import com.island.app.ui.theme.*

@Composable
fun LevelScreen(
    onLevelSelect: (Int) -> Unit,
    onBack: () -> Unit
) {
    var selectedLevelId by remember { mutableStateOf<Int?>(null) }
    val levels = LevelRepository.levels

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to SkyBlue,
                        0.5f to SeaBlue,
                        0.75f to Sand,
                        1f to Color(0xFFC49A5A)
                    )
                )
            )
    ) {
        // 해적선 장식 (배경)
        Text(
            text = "🏴‍☠️",
            fontSize = 180.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-30).dp),
            color = Color.Black.copy(alpha = 0.1f)
        )

        // 상단 바 (코인만)
        TopBar(
            coins = 100,
            keys = 54,
            showGem = false,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // 레벨 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp, bottom = 150.dp, start = 20.dp, end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            items(levels) { level ->
                LevelCell(
                    levelId = level.id,
                    stars = level.stars,
                    isUnlocked = level.isUnlocked,
                    onClick = {
                        if (level.isUnlocked) selectedLevelId = level.id
                    }
                )
            }
        }

        // 하단 버튼 (Back + < >)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 70.dp, start = 16.dp, end = 16.dp)
                .height(44.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(Brush.verticalGradient(listOf(GreenLight, GreenMedium, GreenDark)))
                    .border(3.dp, GreenBorder, RoundedCornerShape(8.dp))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text("Back", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
            listOf("<", ">").forEach { arrow ->
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .fillMaxHeight()
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.verticalGradient(listOf(GreenLight, GreenMedium, GreenDark)))
                        .border(3.dp, GreenBorder, RoundedCornerShape(8.dp))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Text(arrow, color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }
        }

        // 하단 내비 (⚙️ + 🏠만)
        BottomNav(
            onHome = onBack,
            showTasks = false,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Level Select 팝업
    selectedLevelId?.let { levelId ->
        LevelSelectModal(
            levelId = levelId,
            onPlay = {
                selectedLevelId = null
                onLevelSelect(levelId)
            },
            onDismiss = { selectedLevelId = null }
        )
    }
}

@Composable
fun LevelCell(
    levelId: Int,
    stars: Int,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .size(74.dp)
            .shadow(4.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isUnlocked)
                    Brush.verticalGradient(listOf(GoldLight, GoldDark))
                else
                    Brush.verticalGradient(listOf(LevelLocked, LevelLockedDark))
            )
            .border(
                3.dp,
                if (isUnlocked) GoldBorder else Color(0xFF3A1010),
                RoundedCornerShape(10.dp)
            )
            .clickable(enabled = isUnlocked) { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 별 3개
        Row {
            repeat(3) { i ->
                Text(
                    text = if (i < stars) "★" else "☆",
                    color = if (i < stars) StarFilled else StarEmpty,
                    fontSize = 12.sp
                )
            }
        }
        // 숫자 또는 자물쇠
        Text(
            text = if (isUnlocked) levelId.toString() else "🔒",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = if (isUnlocked) 22.sp else 20.sp
        )
        Spacer(Modifier.height(4.dp))
    }
}
