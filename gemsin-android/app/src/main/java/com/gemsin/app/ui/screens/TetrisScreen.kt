package com.gemsin.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.random.Random

private val PIECE_COLORS = listOf(
    Color(0xFF00BCD4), Color(0xFFFFEB3B), Color(0xFF9C27B0),
    Color(0xFF4CAF50), Color(0xFFF44336), Color(0xFFFF9800), Color(0xFF2196F3)
)

// 각 피스의 4가지 회전 상태 (r, c) offset 목록
private val PIECES: List<List<List<Pair<Int, Int>>>> = listOf(
    // I
    listOf(listOf(1 to 0, 1 to 1, 1 to 2, 1 to 3), listOf(0 to 2, 1 to 2, 2 to 2, 3 to 2),
        listOf(2 to 0, 2 to 1, 2 to 2, 2 to 3), listOf(0 to 1, 1 to 1, 2 to 1, 3 to 1)),
    // O
    listOf(listOf(0 to 1, 0 to 2, 1 to 1, 1 to 2), listOf(0 to 1, 0 to 2, 1 to 1, 1 to 2),
        listOf(0 to 1, 0 to 2, 1 to 1, 1 to 2), listOf(0 to 1, 0 to 2, 1 to 1, 1 to 2)),
    // T
    listOf(listOf(0 to 1, 1 to 0, 1 to 1, 1 to 2), listOf(0 to 1, 1 to 1, 1 to 2, 2 to 1),
        listOf(1 to 0, 1 to 1, 1 to 2, 2 to 1), listOf(0 to 1, 1 to 0, 1 to 1, 2 to 1)),
    // S
    listOf(listOf(0 to 1, 0 to 2, 1 to 0, 1 to 1), listOf(0 to 1, 1 to 1, 1 to 2, 2 to 2),
        listOf(1 to 1, 1 to 2, 2 to 0, 2 to 1), listOf(0 to 0, 1 to 0, 1 to 1, 2 to 1)),
    // Z
    listOf(listOf(0 to 0, 0 to 1, 1 to 1, 1 to 2), listOf(0 to 2, 1 to 1, 1 to 2, 2 to 1),
        listOf(1 to 0, 1 to 1, 2 to 1, 2 to 2), listOf(0 to 1, 1 to 0, 1 to 1, 2 to 0)),
    // L
    listOf(listOf(0 to 2, 1 to 0, 1 to 1, 1 to 2), listOf(0 to 1, 1 to 1, 2 to 1, 2 to 2),
        listOf(1 to 0, 1 to 1, 1 to 2, 2 to 0), listOf(0 to 0, 0 to 1, 1 to 1, 2 to 1)),
    // J
    listOf(listOf(0 to 0, 1 to 0, 1 to 1, 1 to 2), listOf(0 to 1, 0 to 2, 1 to 1, 2 to 1),
        listOf(1 to 0, 1 to 1, 1 to 2, 2 to 2), listOf(0 to 1, 1 to 1, 2 to 0, 2 to 1)),
)

private const val BOARD_W = 10
private const val BOARD_H = 20

data class TetrisPiece(val type: Int, val rot: Int, val row: Int, val col: Int) {
    fun cells() = PIECES[type][rot].map { (dr, dc) -> (row + dr) to (col + dc) }
    fun rotated() = copy(rot = (rot + 1) % 4)
    fun moved(dr: Int, dc: Int) = copy(row = row + dr, col = col + dc)
}

enum class TetrisGameState { IDLE, RUNNING, PAUSED, DEAD }

@Composable
fun TetrisScreen(navController: NavController) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val sw = with(density) { maxWidth.toPx() }
        val sh = with(density) { maxHeight.toPx() }
        val cellPx = minOf(sw * 0.62f / BOARD_W, sh / BOARD_H.toFloat())

        // board: 0 = empty, 1~7 = color index
        var board by remember { mutableStateOf(Array(BOARD_H) { IntArray(BOARD_W) }) }
        var current by remember { mutableStateOf(TetrisPiece(Random.nextInt(7), 0, 0, 3)) }
        var next by remember { mutableStateOf(TetrisPiece(Random.nextInt(7), 0, 0, 3)) }
        var state by remember { mutableStateOf(TetrisGameState.IDLE) }
        var score by remember { mutableStateOf(0) }
        var lines by remember { mutableStateOf(0) }
        var best by remember { mutableStateOf(0) }
        var level by remember { mutableStateOf(1) }
        var dropMs by remember { mutableStateOf(500L) }

        fun newPiece() = TetrisPiece(Random.nextInt(7), 0, 0, 3)

        fun isValid(piece: TetrisPiece, b: Array<IntArray>): Boolean {
            return piece.cells().all { (r, c) ->
                r >= 0 && r < BOARD_H && c >= 0 && c < BOARD_W && b[r][c] == 0
            }
        }

        fun lockPiece(piece: TetrisPiece, b: Array<IntArray>): Array<IntArray> {
            val newBoard = b.map { it.copyOf() }.toTypedArray()
            piece.cells().forEach { (r, c) -> if (r >= 0) newBoard[r][c] = piece.type + 1 }
            return newBoard
        }

        fun clearLines(b: Array<IntArray>): Pair<Array<IntArray>, Int> {
            val kept = b.filter { row -> row.any { it == 0 } }
            val cleared = BOARD_H - kept.size
            val newBoard = Array(cleared) { IntArray(BOARD_W) } + kept.toTypedArray()
            return newBoard to cleared
        }

        fun reset() {
            if (score > best) best = score
            board = Array(BOARD_H) { IntArray(BOARD_W) }
            current = newPiece(); next = newPiece()
            score = 0; lines = 0; level = 1; dropMs = 500L
            state = TetrisGameState.RUNNING
        }

        fun tryMove(dc: Int) {
            val moved = current.moved(0, dc)
            if (isValid(moved, board)) current = moved
        }

        fun tryRotate() {
            val rotated = current.rotated()
            if (isValid(rotated, board)) current = rotated
            else {
                // wall kick
                for (kick in listOf(1, -1, 2, -2)) {
                    val kicked = rotated.moved(0, kick)
                    if (isValid(kicked, board)) { current = kicked; return }
                }
            }
        }

        fun hardDrop() {
            var dropped = current
            while (isValid(dropped.moved(1, 0), board)) dropped = dropped.moved(1, 0)
            val locked = lockPiece(dropped, board)
            val (newBoard, cleared) = clearLines(locked)
            board = newBoard
            lines += cleared
            score += when (cleared) { 1 -> 100; 2 -> 300; 3 -> 500; 4 -> 800; else -> 0 } * level
            level = lines / 10 + 1
            dropMs = (500L - (level - 1) * 40L).coerceAtLeast(80L)
            val nextPiece = next
            next = newPiece()
            current = nextPiece
            if (!isValid(current, board)) {
                if (score > best) best = score
                state = TetrisGameState.DEAD
            }
        }

        // 자동 낙하
        LaunchedEffect(state, dropMs) {
            if (state != TetrisGameState.RUNNING) return@LaunchedEffect
            while (state == TetrisGameState.RUNNING) {
                delay(dropMs)
                if (state != TetrisGameState.RUNNING) break
                val down = current.moved(1, 0)
                if (isValid(down, board)) {
                    current = down
                } else {
                    val locked = lockPiece(current, board)
                    val (newBoard, cleared) = clearLines(locked)
                    board = newBoard
                    lines += cleared
                    score += when (cleared) { 1 -> 100; 2 -> 300; 3 -> 500; 4 -> 800; else -> 0 } * level
                    level = lines / 10 + 1
                    dropMs = (500L - (level - 1) * 40L).coerceAtLeast(80L)
                    val nextPiece = next
                    next = newPiece()
                    current = nextPiece
                    if (!isValid(current, board)) {
                        if (score > best) best = score
                        state = TetrisGameState.DEAD
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A1E)),
            horizontalArrangement = Arrangement.Center
        ) {
            // 게임 보드
            Box(
                modifier = Modifier
                    .width((cellPx * BOARD_W / density.density).dp)
                    .fillMaxHeight()
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cs = size.width / BOARD_W

                    // 배경 격자
                    for (r in 0 until BOARD_H) for (c in 0 until BOARD_W) {
                        drawRect(Color.White.copy(alpha = if ((r + c) % 2 == 0) 0.04f else 0.02f),
                            topLeft = Offset(c * cs, r * cs), size = Size(cs, cs))
                    }

                    // 고정된 블록
                    for (r in 0 until BOARD_H) for (c in 0 until BOARD_W) {
                        if (board[r][c] != 0) {
                            val color = PIECE_COLORS[board[r][c] - 1]
                            drawRoundRect(color, topLeft = Offset(c * cs + 2f, r * cs + 2f),
                                size = Size(cs - 4f, cs - 4f), cornerRadius = CornerRadius(4f))
                            drawRoundRect(color.copy(alpha = 0.5f), topLeft = Offset(c * cs + 2f, r * cs + 2f),
                                size = Size(cs * 0.4f, cs - 4f), cornerRadius = CornerRadius(4f))
                        }
                    }

                    // 고스트 피스 (낙하 예측)
                    if (state == TetrisGameState.RUNNING) {
                        var ghost = current
                        while (isValid(ghost.moved(1, 0), board)) ghost = ghost.moved(1, 0)
                        ghost.cells().forEach { (r, c) ->
                            if (r >= 0) drawRoundRect(PIECE_COLORS[current.type].copy(alpha = 0.2f),
                                topLeft = Offset(c * cs + 2f, r * cs + 2f),
                                size = Size(cs - 4f, cs - 4f), cornerRadius = CornerRadius(4f))
                        }
                    }

                    // 현재 피스
                    if (state == TetrisGameState.RUNNING || state == TetrisGameState.PAUSED) {
                        current.cells().forEach { (r, c) ->
                            if (r >= 0) {
                                val color = PIECE_COLORS[current.type]
                                drawRoundRect(color, topLeft = Offset(c * cs + 2f, r * cs + 2f),
                                    size = Size(cs - 4f, cs - 4f), cornerRadius = CornerRadius(4f))
                                drawRoundRect(color.copy(alpha = 0.5f), topLeft = Offset(c * cs + 2f, r * cs + 2f),
                                    size = Size(cs * 0.4f, cs - 4f), cornerRadius = CornerRadius(4f))
                            }
                        }
                    }
                }

                // IDLE
                if (state == TetrisGameState.IDLE) Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🧱 TETRIS", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    Button(onClick = ::reset, colors = ButtonDefaults.buttonColors(Color(0xFF7C4DFF)),
                        shape = RoundedCornerShape(10.dp)) { Text("START", fontWeight = FontWeight.ExtraBold) }
                }

                // DEAD
                if (state == TetrisGameState.DEAD) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)))
                    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFF16213E),
                        modifier = Modifier.align(Alignment.Center)) {
                        Column(modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("GAME OVER", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                            Text("$score", color = Color(0xFFFFD600), fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                            if (best > 0) Text("BEST  $best", color = Color(0xFFFFD600).copy(0.7f), fontSize = 12.sp)
                            Button(onClick = ::reset, colors = ButtonDefaults.buttonColors(Color(0xFF7C4DFF)),
                                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) { Text("RETRY") }
                            TextButton(onClick = { navController.popBackStack() }) {
                                Text("Home", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 사이드 패널
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 6.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(36.dp)) {
                    Text("‹", color = Color.White.copy(0.5f), fontSize = 22.sp)
                }
                InfoCard("SCORE", "$score")
                InfoCard("LINES", "$lines")
                InfoCard("LV", "$level")
                if (best > 0) InfoCard("BEST", "$best")

                Text("NEXT", color = Color.White.copy(0.6f), fontSize = 10.sp)
                Canvas(modifier = Modifier.size(56.dp).background(Color(0xFF16213E), RoundedCornerShape(8.dp))) {
                    val cs = size.width / 4f
                    next.cells().forEach { (r, c) ->
                        drawRoundRect(PIECE_COLORS[next.type],
                            topLeft = Offset(c * cs + 1f, r * cs + 1f),
                            size = Size(cs - 2f, cs - 2f), cornerRadius = CornerRadius(3f))
                    }
                }

                Spacer(Modifier.height(4.dp))
                TetrisBtn("⟳") { if (state == TetrisGameState.RUNNING) tryRotate() }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TetrisBtn("◀") { if (state == TetrisGameState.RUNNING) tryMove(-1) }
                    TetrisBtn("▶") { if (state == TetrisGameState.RUNNING) tryMove(1) }
                }
                TetrisBtn("▼▼") { if (state == TetrisGameState.RUNNING) hardDrop() }
            }
        }
    }
}

@Composable
private fun InfoCard(label: String, value: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF16213E), modifier = Modifier.width(72.dp)) {
        Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = Color.White.copy(0.5f), fontSize = 9.sp)
            Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TetrisBtn(label: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.size(52.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(Color(0xFF16213E)),
        contentPadding = PaddingValues(0.dp)) {
        Text(label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
