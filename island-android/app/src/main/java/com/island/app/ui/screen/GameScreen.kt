package com.island.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.island.app.data.repository.LevelRepository
import com.island.app.game.GameViewModel
import com.island.app.ui.component.*
import com.island.app.ui.modal.BeginModal
import com.island.app.ui.modal.YouWinModal
import com.island.app.ui.theme.*

@Composable
fun GameScreen(
    levelId: Int,
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val showWin by viewModel.showWin.collectAsState()
    val busy by viewModel.busy.collectAsState()
    val level = remember { LevelRepository.getLevel(levelId) }

    var showBegin by rememberSaveable { mutableStateOf(true) }
    var gameStarted by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to SkyBlue,
                        0.35f to Color(0xFF6BB8E8),
                        0.55f to SeaBlue,
                        0.75f to Sand,
                        1f to Color(0xFFC49A5A)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 스코어 바
            ScoreBar(
                score = state.score,
                target = state.target,
                moves = state.moves,
                onBack = onBack
            )

            // 진행 바
            Spacer(Modifier.height(6.dp))
            GameProgressBar(
                score = state.score,
                targetScore = level.target * 100
            )

            // 배경 씬
            GameScene(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            // 젬 그리드
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (gameStarted) {
                    GemGrid(
                        grid = state.grid,
                        matchedCells = state.matchedCells,
                        newCells = state.newCells,
                        fallDistances = state.fallDistances,
                        fallGeneration = state.fallGeneration,
                        busy = busy,
                        onSwap = { r1, c1, r2, c2 -> viewModel.onSwap(r1, c1, r2, c2) }
                    )
                }
            }
        }

        // 툴바 (하단)
        ToolBar(modifier = Modifier.align(Alignment.BottomCenter))
    }

    // Begin 팝업
    if (showBegin) {
        BeginModal(
            target = level.target,
            onStart = {
                showBegin = false
                gameStarted = true
                viewModel.startGame(target = level.target, moves = level.moves)
            },
            onDismiss = {
                showBegin = false
                onBack()
            }
        )
    }

    // You Win 팝업
    if (showWin) {
        YouWinModal(
            score = state.score,
            target = level.target,
            stars = when {
                state.target <= 0 && state.moves > level.moves / 2 -> 3
                state.target <= 0 -> 2
                else -> 1
            },
            onRestart = {
                viewModel.dismissWin()
                showBegin = true
                gameStarted = false
            },
            onBack = {
                viewModel.dismissWin()
                onBack()
            }
        )
    }
}

@Composable
fun GameScene(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🌴", fontSize = 48.sp)
            Text("🏴‍☠️", fontSize = 36.sp)
            Text("⛵", fontSize = 32.sp)
            Text("🌴", fontSize = 40.sp)
        }
    }
}
