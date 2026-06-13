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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val FLAP_GRAVITY = 0.55f
private const val FLAP_JUMP = -13f
private const val FLAP_PIPE_W = 70f
private const val FLAP_GAP = 230f
private const val FLAP_FRAME_MS = 16L

data class FlappyPipe(val x: Float, val gapTop: Float)
enum class FlappyState { IDLE, RUNNING, DEAD }

@Composable
fun FlappyBirdScreen(navController: NavController) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val sw = with(density) { maxWidth.toPx() }
        val sh = with(density) { maxHeight.toPx() }
        val birdX = sw * 0.25f
        val groundY = sh - 60f

        var state by remember { mutableStateOf(FlappyState.IDLE) }
        var birdY by remember { mutableStateOf(sh * 0.4f) }
        var vy by remember { mutableStateOf(0f) }
        var pipes by remember { mutableStateOf(emptyList<FlappyPipe>()) }
        var score by remember { mutableStateOf(0) }
        var best by remember { mutableStateOf(0) }
        var frame by remember { mutableStateOf(0) }
        var speed by remember { mutableStateOf(5f) }

        fun doFlap() {
            if (state == FlappyState.IDLE) state = FlappyState.RUNNING
            vy = FLAP_JUMP
        }

        fun reset() {
            if (score > best) best = score
            score = 0; birdY = sh * 0.4f; vy = 0f
            pipes = emptyList(); frame = 0; speed = 5f
            state = FlappyState.RUNNING
        }

        LaunchedEffect(state) {
            if (state != FlappyState.RUNNING) return@LaunchedEffect
            while (state == FlappyState.RUNNING) {
                delay(FLAP_FRAME_MS)
                frame++
                speed = 5f + frame / 400f

                vy += FLAP_GRAVITY
                birdY += vy

                if (birdY >= groundY - 22f || birdY <= 10f) {
                    if (score > best) best = score
                    state = FlappyState.DEAD
                    return@LaunchedEffect
                }

                pipes = pipes
                    .map { it.copy(x = it.x - speed) }
                    .filter { it.x + FLAP_PIPE_W > 0f }

                if (frame % 120 == 0) {
                    val gapTop = Random.nextFloat() * (groundY * 0.38f) + groundY * 0.12f
                    pipes = pipes + FlappyPipe(sw, gapTop)
                }

                // 파이프 통과 = 점수
                pipes.forEach { p ->
                    if (p.x + FLAP_PIPE_W < birdX && p.x + FLAP_PIPE_W + speed >= birdX) score++
                }

                // 충돌
                val birdR = 20f
                val hit = pipes.any { p ->
                    val inX = birdX + birdR > p.x + 5f && birdX - birdR < p.x + FLAP_PIPE_W - 5f
                    val inGap = birdY - birdR > p.gapTop && birdY + birdR < p.gapTop + FLAP_GAP
                    inX && !inGap
                }
                if (hit) {
                    if (score > best) best = score
                    state = FlappyState.DEAD
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF64B5F6))
                .pointerInput(Unit) { detectTapGestures { doFlap() } }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 구름
                listOf(0.15f to 0.12f, 0.55f to 0.07f, 0.80f to 0.18f).forEach { (rx, ry) ->
                    drawCircle(Color.White.copy(alpha = 0.7f), 40f, Offset(sw * rx, sh * ry))
                    drawCircle(Color.White.copy(alpha = 0.7f), 30f, Offset(sw * rx + 35f, sh * ry))
                    drawCircle(Color.White.copy(alpha = 0.7f), 30f, Offset(sw * rx - 30f, sh * ry))
                }

                // 파이프
                pipes.forEach { p ->
                    drawRect(Color(0xFF388E3C), topLeft = Offset(p.x, 0f), size = Size(FLAP_PIPE_W, p.gapTop))
                    drawRect(Color(0xFF2E7D32), topLeft = Offset(p.x - 6f, p.gapTop - 22f), size = Size(FLAP_PIPE_W + 12f, 24f))
                    val bot = p.gapTop + FLAP_GAP
                    drawRect(Color(0xFF388E3C), topLeft = Offset(p.x, bot), size = Size(FLAP_PIPE_W, groundY - bot))
                    drawRect(Color(0xFF2E7D32), topLeft = Offset(p.x - 6f, bot), size = Size(FLAP_PIPE_W + 12f, 24f))
                }

                // 땅
                drawRect(Color(0xFF8D6E63), topLeft = Offset(0f, groundY), size = Size(sw, sh - groundY))
                drawRect(Color(0xFF4CAF50), topLeft = Offset(0f, groundY), size = Size(sw, 18f))

                // 새
                val rotation = (vy * 3f).coerceIn(-30f, 60f)
                val wingY = if (vy < -4f) birdY - 10f else birdY + 4f
                drawCircle(Color(0xFFFFEB3B), 22f, Offset(birdX, birdY))
                drawCircle(Color(0xFFFF9800), 12f, Offset(birdX + 6f, wingY))
                drawCircle(Color(0xFFFF5722), 7f, Offset(birdX + 20f, birdY + 2f))
                drawCircle(Color.White, 7f, Offset(birdX + 8f, birdY - 7f))
                drawCircle(Color(0xFF1A1A2E), 4f, Offset(birdX + 10f, birdY - 7f))
            }

            Text(
                "$score",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                style = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.4f), Offset(2f, 2f), 4f)),
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 52.dp)
            )
            if (best > 0) Text(
                "BEST  $best",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 58.dp, end = 16.dp)
            )

            if (state == FlappyState.IDLE) Column(
                modifier = Modifier.align(Alignment.Center).offset(y = (-80).dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🐦 FLAPPY BIRD", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold,
                    style = TextStyle(shadow = Shadow(Color.Black.copy(0.4f), Offset(2f,2f), 4f)))
                Text("TAP TO FLAP", color = Color(0xFF1565C0), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            if (state == FlappyState.DEAD) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
                Surface(shape = RoundedCornerShape(24.dp), color = Color(0xFF1A1A2E), modifier = Modifier.align(Alignment.Center)) {
                    Column(modifier = Modifier.padding(horizontal = 40.dp, vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("GAME OVER", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                        Text("$score", color = Color(0xFFFFD600), fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)
                        Text("SCORE", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                        if (best > 0) Text("BEST  $best", color = Color(0xFFFFD600).copy(alpha = 0.7f), fontSize = 14.sp)
                        Spacer(Modifier.height(4.dp))
                        Button(onClick = ::reset, colors = ButtonDefaults.buttonColors(Color(0xFF2E7D32)),
                            modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp)) {
                            Text("RETRY", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                        TextButton(onClick = { navController.popBackStack() }) {
                            Text("Back to Home", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
            }

            if (state != FlappyState.DEAD) IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
            ) { Text("‹", color = Color.White.copy(alpha = 0.7f), fontSize = 30.sp) }
        }
    }
}
