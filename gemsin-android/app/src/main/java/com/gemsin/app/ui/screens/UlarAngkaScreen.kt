package com.gemsin.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val COLS = 16
private const val ROWS = 22
private const val TICK_MS = 160L

enum class Dir { UP, DOWN, LEFT, RIGHT }
enum class SnakeState { IDLE, RUNNING, DEAD }
data class Cell(val r: Int, val c: Int)

@Composable
fun UlarAngkaScreen(navController: NavController) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current

        var state by remember { mutableStateOf(SnakeState.IDLE) }
        var snake by remember { mutableStateOf(listOf(Cell(ROWS / 2, COLS / 2))) }
        var dir by remember { mutableStateOf(Dir.RIGHT) }
        var nextDir by remember { mutableStateOf(Dir.RIGHT) }
        var food by remember { mutableStateOf(Cell(ROWS / 2, COLS / 2 + 4)) }
        var foodNum by remember { mutableStateOf(1) }
        var score by remember { mutableStateOf(0) }
        var best by remember { mutableStateOf(0) }

        fun spawnFood(body: List<Cell>): Cell {
            var c: Cell
            do { c = Cell(Random.nextInt(ROWS), Random.nextInt(COLS)) } while (body.contains(c))
            return c
        }

        fun reset() {
            if (score > best) best = score
            val s = listOf(Cell(ROWS / 2, COLS / 2))
            snake = s; dir = Dir.RIGHT; nextDir = Dir.RIGHT
            food = spawnFood(s); foodNum = 1; score = 0
            state = SnakeState.RUNNING
        }

        LaunchedEffect(state) {
            if (state != SnakeState.RUNNING) return@LaunchedEffect
            while (state == SnakeState.RUNNING) {
                delay(TICK_MS)
                dir = nextDir
                val head = snake.first()
                val newHead = when (dir) {
                    Dir.UP -> Cell(head.r - 1, head.c)
                    Dir.DOWN -> Cell(head.r + 1, head.c)
                    Dir.LEFT -> Cell(head.r, head.c - 1)
                    Dir.RIGHT -> Cell(head.r, head.c + 1)
                }

                // 벽 충돌 또는 자기 충돌
                if (newHead.r < 0 || newHead.r >= ROWS || newHead.c < 0 || newHead.c >= COLS || snake.contains(newHead)) {
                    if (score > best) best = score
                    state = SnakeState.DEAD
                    return@LaunchedEffect
                }

                val ate = newHead == food
                val newSnake = if (ate) listOf(newHead) + snake else listOf(newHead) + snake.dropLast(1)
                snake = newSnake

                if (ate) {
                    score += foodNum * 10
                    foodNum = if (foodNum >= 9) 1 else foodNum + 1
                    food = spawnFood(newSnake)
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A1E)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 정보
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Text("‹", color = Color.White.copy(alpha = 0.6f), fontSize = 30.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🐍 ULAR ANGKA", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    Text("SCORE  $score", color = Color(0xFFFFD600), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("BEST", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                    Text("$best", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            // 게임 보드
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF0F1923))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cs = minOf(size.width / COLS, size.height / ROWS)

                    // 격자
                    for (r in 0 until ROWS) for (c in 0 until COLS) {
                        drawRect(
                            Color.White.copy(alpha = if ((r + c) % 2 == 0) 0.03f else 0.01f),
                            topLeft = Offset(c * cs, r * cs),
                            size = Size(cs, cs)
                        )
                    }

                    // 음식
                    val fc = food.c * cs + cs / 2
                    val fr = food.r * cs + cs / 2
                    drawCircle(Color(0xFFFFD600), cs * 0.4f, Offset(fc, fr))
                    drawCircle(Color(0xFF1A1A2E), cs * 0.2f, Offset(fc, fr))
                    // 숫자는 Canvas text로 대신 숫자 위에 작은 원 개수로 표현
                    // (drawText는 TextMeasurer 필요 - 간단히 점으로 대체)
                    repeat(foodNum) { i ->
                        drawCircle(Color(0xFFFF9800), cs * 0.07f,
                            Offset(fc + (i - foodNum / 2f) * cs * 0.18f, fr + cs * 0.18f))
                    }

                    // 뱀 몸통
                    snake.forEachIndexed { i, cell ->
                        val x = cell.c * cs
                        val y = cell.r * cs
                        val isHead = i == 0
                        val color = if (isHead) Color(0xFF4CAF50) else Color(0xFF2E7D32).copy(alpha = 1f - i * 0.015f)
                        val pad = if (isHead) cs * 0.08f else cs * 0.12f
                        drawRoundRect(color, topLeft = Offset(x + pad, y + pad),
                            size = Size(cs - pad * 2, cs - pad * 2),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cs * 0.25f))
                        if (isHead) {
                            // 눈
                            drawCircle(Color.White, cs * 0.12f, Offset(x + cs * 0.65f, y + cs * 0.3f))
                            drawCircle(Color(0xFF1A1A2E), cs * 0.07f, Offset(x + cs * 0.68f, y + cs * 0.3f))
                        }
                    }
                }

                // IDLE 오버레이
                if (state == SnakeState.IDLE) Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("TAP ▶ TO START", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Text("금색 원을 잡아라!", color = Color(0xFFFFD600), fontSize = 14.sp)
                }

                // DEAD 오버레이
                if (state == SnakeState.DEAD) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f)))
                    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFF16213E),
                        modifier = Modifier.align(Alignment.Center)) {
                        Column(modifier = Modifier.padding(horizontal = 36.dp, vertical = 28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("GAME OVER", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                            Text("$score", color = Color(0xFFFFD600), fontSize = 44.sp, fontWeight = FontWeight.ExtraBold)
                            Text("SCORE", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            if (best > 0) Text("BEST  $best", color = Color(0xFFFFD600).copy(0.7f), fontSize = 13.sp)
                            Spacer(Modifier.height(4.dp))
                            Button(onClick = ::reset, colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50)),
                                modifier = Modifier.fillMaxWidth().height(44.dp), shape = RoundedCornerShape(10.dp)) {
                                Text("RETRY", fontWeight = FontWeight.ExtraBold)
                            }
                            TextButton(onClick = { navController.popBackStack() }) {
                                Text("Back to Home", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // D-패드 컨트롤
            Column(
                modifier = Modifier.padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DPadButton("▲") {
                    if (state == SnakeState.IDLE) state = SnakeState.RUNNING
                    if (dir != Dir.DOWN) nextDir = Dir.UP
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    DPadButton("◀") {
                        if (state == SnakeState.IDLE) state = SnakeState.RUNNING
                        if (dir != Dir.RIGHT) nextDir = Dir.LEFT
                    }
                    Spacer(modifier = Modifier.size(60.dp))
                    DPadButton("▶") {
                        if (state == SnakeState.IDLE) state = SnakeState.RUNNING
                        if (dir != Dir.LEFT) nextDir = Dir.RIGHT
                    }
                }
                DPadButton("▼") {
                    if (state == SnakeState.IDLE) state = SnakeState.RUNNING
                    if (dir != Dir.UP) nextDir = Dir.DOWN
                }
            }
        }
    }
}

@Composable
private fun DPadButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16213E)),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(label, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
