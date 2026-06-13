package com.island.app.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.island.app.data.model.GemColor
import com.island.app.game.engine.COLS
import com.island.app.game.engine.ROWS
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

private val GravityEasing = Easing { it * it }

@Composable
fun GemGrid(
    grid: Array<Array<GemColor?>>,
    matchedCells: Set<Pair<Int, Int>>,
    newCells: Set<Pair<Int, Int>>,
    fallDistances: Map<Pair<Int, Int>, Int>,
    fallGeneration: Int,
    busy: Boolean,
    onSwap: (Int, Int, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var dragStartX by remember { mutableStateOf(0f) }
    var dragStartY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val cellSize = 50.dp
    val gap = 2.dp
    val cellPx = with(LocalDensity.current) { (cellSize + gap).toPx() }

    Column(
        modifier = modifier
            .clipToBounds()
            .pointerInput(busy) {
                if (busy) return@pointerInput
                detectDragGestures(
                    onDragStart = { offset ->
                        val col = (offset.x / cellPx).toInt().coerceIn(0, COLS - 1)
                        val row = (offset.y / cellPx).toInt().coerceIn(0, ROWS - 1)
                        selectedCell = Pair(row, col)
                        dragStartX = offset.x
                        dragStartY = offset.y
                        isDragging = true
                    },
                    onDragEnd = {
                        selectedCell = null
                        isDragging = false
                    },
                    onDragCancel = {
                        selectedCell = null
                        isDragging = false
                    },
                    onDrag = { change, _ ->
                        if (!isDragging) return@detectDragGestures
                        val dx = change.position.x - dragStartX
                        val dy = change.position.y - dragStartY
                        val dist = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
                        if (dist < 40f) return@detectDragGestures

                        val sel = selectedCell ?: return@detectDragGestures
                        val row = sel.first
                        val col = sel.second
                        val toRow: Int
                        val toCol: Int
                        if (abs(dx) > abs(dy)) {
                            toRow = row
                            toCol = if (dx > 0f) col + 1 else col - 1
                        } else {
                            toRow = if (dy > 0f) row + 1 else row - 1
                            toCol = col
                        }
                        if (toRow in 0 until ROWS && toCol in 0 until COLS) {
                            onSwap(row, col, toRow, toCol)
                        }
                        selectedCell = null
                        isDragging = false
                    }
                )
            },
        verticalArrangement = Arrangement.spacedBy(gap)
    ) {
        for (r in 0 until ROWS) {
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                for (c in 0 until COLS) {
                    val fallDist = fallDistances[Pair(r, c)] ?: 0

                    // animKey가 바뀌면 새 Animatable을 올바른 시작 오프셋으로 즉시 생성
                    // → 첫 프레임부터 그리드 위에 위치해 플리커 없음
                    val animKey = if (fallDist > 0) fallGeneration else 0
                    val yAnim = remember(r, c, animKey) {
                        Animatable(if (fallDist > 0) -fallDist * cellPx else 0f)
                    }

                    LaunchedEffect(animKey) {
                        if (fallDist > 0) {
                            yAnim.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(
                                    durationMillis = 300 + fallDist * 25,
                                    easing = GravityEasing
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .offset { IntOffset(0, yAnim.value.roundToInt()) }
                    ) {
                        GemCell(
                            color = grid[r][c],
                            isSelected = selectedCell == Pair(r, c),
                            isMatched = matchedCells.contains(Pair(r, c)),
                            modifier = Modifier.matchParentSize()
                        )
                    }
                }
            }
        }
    }
}
