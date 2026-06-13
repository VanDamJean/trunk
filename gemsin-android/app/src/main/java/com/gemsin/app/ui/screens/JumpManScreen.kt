package com.gemsin.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val GRAVITY = 0.7f
private const val JUMP_FORCE = -26f
private const val FRAME_MS = 16L
private const val CHAR_SIZE = 52f
private const val OBS_WIDTH = 46f
private const val GROUND_RATIO = 0.78f
private const val CHAR_X_RATIO = 0.18f

enum class JumpGameState { IDLE, RUNNING, DEAD }

data class JumpObstacle(val x: Float, val height: Float)

@Composable
fun JumpManScreen(navController: NavController) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val sw = with(density) { maxWidth.toPx() }
        val sh = with(density) { maxHeight.toPx() }
        val groundY = sh * GROUND_RATIO
        val charX = sw * CHAR_X_RATIO

        var state by remember { mutableStateOf(JumpGameState.IDLE) }
        var score by remember { mutableStateOf(0) }
        var charY by remember { mutableStateOf(groundY) }
        var vy by remember { mutableStateOf(0f) }
        var obstacles by remember { mutableStateOf(emptyList<JumpObstacle>()) }
        var frame by remember { mutableStateOf(0) }
        var speed by remember { mutableStateOf(7f) }
        var bestScore by remember { mutableStateOf(0) }

        fun doJump() {
            if (state == JumpGameState.IDLE) state = JumpGameState.RUNNING
            if (charY >= groundY - 2f) vy = JUMP_FORCE
        }

        fun reset() {
            if (score > bestScore) bestScore = score / 10
            score = 0; charY = groundY; vy = 0f
            obstacles = emptyList(); frame = 0; speed = 7f
            state = JumpGameState.RUNNING
        }

        // 게임 루프
        LaunchedEffect(state) {
            if (state != JumpGameState.RUNNING) return@LaunchedEffect
            while (state == JumpGameState.RUNNING) {
                delay(FRAME_MS)
                frame++
                score++
                speed = 7f + frame / 350f

                // 물리
                vy += GRAVITY
                charY = (charY + vy).coerceAtMost(groundY)
                if (charY == groundY) vy = 0f

                // 장애물 이동 + 제거
                obstacles = obstacles
                    .map { it.copy(x = it.x - speed) }
                    .filter { it.x + OBS_WIDTH > 0f }

                // 장애물 스폰 (처음엔 60프레임 후, 이후 90~130프레임 간격)
                val spawnInterval = Random.nextInt(90, 130)
                if (frame == 60 || (frame > 60 && frame % spawnInterval == 0)) {
                    val h = Random.nextFloat() * (groundY * 0.18f) + groundY * 0.1f
                    obstacles = obstacles + JumpObstacle(sw, h)
                }

                // 충돌 감지 (마진 살짝 줌)
                val cx1 = charX - CHAR_SIZE * 0.35f
                val cx2 = charX + CHAR_SIZE * 0.35f
                val cy2 = charY
                val hit = obstacles.any { o ->
                    cx2 > o.x + 5f && cx1 < o.x + OBS_WIDTH - 5f &&
                    cy2 > groundY - o.height + 6f
                }
                if (hit) {
                    if (score / 10 > bestScore) bestScore = score / 10
                    state = JumpGameState.DEAD
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A1E))
                .pointerInput(Unit) { detectTapGestures { doJump() } }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 별 배경
                repeat(25) { i ->
                    val starAlpha = if (i % 3 == 0) 0.6f else 0.25f
                    drawCircle(
                        Color.White.copy(alpha = starAlpha), 2f,
                        Offset((i * 113f + 30f) % sw, (i * 73f + 15f) % (groundY * 0.85f))
                    )
                }

                // 땅
                drawRect(
                    Color(0xFF7C4DFF).copy(alpha = 0.3f),
                    topLeft = Offset(0f, groundY),
                    size = Size(sw, sh - groundY)
                )
                drawLine(Color(0xFF7C4DFF), Offset(0f, groundY), Offset(sw, groundY), 4f)

                // 땅 줄무늬
                var lineX = (frame * speed) % 80f
                while (lineX < sw) {
                    drawLine(
                        Color(0xFF7C4DFF).copy(alpha = 0.2f),
                        Offset(lineX, groundY),
                        Offset(lineX, groundY + (sh - groundY)),
                        1f
                    )
                    lineX += 80f
                }

                // 장애물 (빨간 기둥 + 위 모자)
                obstacles.forEach { obs ->
                    drawRect(
                        Color(0xFFD32F2F),
                        topLeft = Offset(obs.x, groundY - obs.height),
                        size = Size(OBS_WIDTH, obs.height)
                    )
                    // 기둥 하이라이트
                    drawRect(
                        Color(0xFFEF5350),
                        topLeft = Offset(obs.x, groundY - obs.height),
                        size = Size(OBS_WIDTH * 0.25f, obs.height)
                    )
                    // 위 모자
                    drawRect(
                        Color(0xFFB71C1C),
                        topLeft = Offset(obs.x - 5f, groundY - obs.height - 10f),
                        size = Size(OBS_WIDTH + 10f, 14f)
                    )
                }

                // 캐릭터
                val headR = CHAR_SIZE * 0.28f
                val headCy = charY - CHAR_SIZE + headR
                val bodyTop = headCy + headR
                val bodyH = CHAR_SIZE * 0.42f

                // 몸통
                drawRect(
                    Color(0xFFFF9800),
                    topLeft = Offset(charX - CHAR_SIZE * 0.2f, bodyTop),
                    size = Size(CHAR_SIZE * 0.4f, bodyH)
                )
                // 머리
                drawCircle(Color(0xFFFFD600), headR, Offset(charX, headCy))
                // 눈
                drawCircle(Color(0xFF1A1A2E), headR * 0.22f, Offset(charX + headR * 0.38f, headCy - headR * 0.1f))
                // 다리 (점프 중 벌림)
                val legSpread = if (charY < groundY - 5f) 10f else 4f
                drawLine(Color(0xFF795548), Offset(charX - legSpread, bodyTop + bodyH), Offset(charX - legSpread - 4f, charY), 5f)
                drawLine(Color(0xFF795548), Offset(charX + legSpread, bodyTop + bodyH), Offset(charX + legSpread + 4f, charY), 5f)
            }

            // 스코어
            Text(
                text = "${score / 10}",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 50.dp)
            )

            // 최고점
            if (bestScore > 0) {
                Text(
                    text = "BEST  $bestScore",
                    color = Color(0xFFFFD600).copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.TopEnd).padding(top = 56.dp, end = 16.dp)
                )
            }

            // IDLE
            if (state == JumpGameState.IDLE) {
                Column(
                    modifier = Modifier.align(Alignment.Center).offset(y = (-60).dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🏃 JUMP MAN", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                    Text("TAP TO START", color = Color(0xFF7C4DFF), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // 게임 오버
            if (state == JumpGameState.DEAD) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f)))
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF16213E),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 40.dp, vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("GAME OVER", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                        Text("${score / 10}", color = Color(0xFFFFD600), fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)
                        Text("SCORE", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                        if (bestScore > 0) {
                            Text("BEST  $bestScore", color = Color(0xFFFFD600).copy(alpha = 0.7f), fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = ::reset,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("RETRY", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                        TextButton(onClick = { navController.popBackStack() }) {
                            Text("Back to Home", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
            }

            // 뒤로가기
            if (state != JumpGameState.DEAD) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                ) {
                    Text("‹", color = Color.White.copy(alpha = 0.5f), fontSize = 30.sp)
                }
            }
        }
    }
}
