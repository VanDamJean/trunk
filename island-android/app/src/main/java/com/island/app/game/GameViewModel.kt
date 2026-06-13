package com.island.app.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.island.app.data.model.GemColor
import com.island.app.game.engine.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy

    private val _showWin = MutableStateFlow(false)
    val showWin: StateFlow<Boolean> = _showWin

    fun startGame(target: Int, moves: Int) {
        val grid = SwapValidator.initGrid()
        _state.value = GameState(grid = grid, target = target, moves = moves)
        _showWin.value = false
        _busy.value = false
    }

    fun onSwap(r1: Int, c1: Int, r2: Int, c2: Int) {
        if (_busy.value) return
        val st = _state.value
        if (!SwapValidator.isAdjacent(r1, c1, r2, c2)) return

        viewModelScope.launch {
            _busy.value = true
            val swapped = SwapValidator.swap(st.grid, r1, c1, r2, c2)
            val matches = MatchEngine.findMatches(swapped)

            if (matches.isEmpty()) {
                // 매치 없음: 즉시 되돌림
                _state.value = st.copy(grid = swapped)
                delay(350)
                _state.value = st
                _busy.value = false
                return@launch
            }

            // 매치 있음: 스왑 반영 후 처리
            _state.value = st.copy(grid = swapped, moves = st.moves - 1)
            delay(150)
            processMatches(matches)
        }
    }

    private suspend fun processMatches(matches: Set<Pair<Int, Int>>) {
        val st = _state.value
        val grid = st.copyGrid()

        // 매치 셀 플래시 표시
        _state.value = st.copy(matchedCells = matches)
        delay(600)

        // 점수 계산 (파란 젬 target 차감)
        var newTarget = st.target
        var newScore = st.score
        for ((r, c) in matches) {
            newScore += 100
            if (grid[r][c] == GemColor.BLUE) newTarget--
            grid[r][c] = null
        }
        newTarget = maxOf(0, newTarget)

        // 낙하 적용
        val (newGrid, newCells, fallDistances) = GravitySystem.apply(grid)

        _state.value = _state.value.copy(
            grid = newGrid,
            score = newScore,
            target = newTarget,
            matchedCells = emptySet(),
            newCells = newCells,
            fallDistances = fallDistances,
            fallGeneration = _state.value.fallGeneration + 1
        )
        delay(500)  // 낙하 애니메이션 완료 대기 (350ms tween + 여유)

        _state.value = _state.value.copy(newCells = emptySet(), fallDistances = emptyMap())

        // 연쇄 체크
        val cascade = MatchEngine.findMatches(newGrid)
        if (cascade.isNotEmpty()) {
            processMatches(cascade)
            return
        }

        // 종료 조건 체크
        val final = _state.value
        if (final.target <= 0 || final.moves <= 0) {
            _showWin.value = true
        }
        _busy.value = false
    }

    fun dismissWin() {
        _showWin.value = false
    }
}
