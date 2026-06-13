package com.island.app.game.engine

import com.island.app.data.model.GemColor

const val ROWS = 6
const val COLS = 6

data class GameState(
    val grid: Array<Array<GemColor?>> = Array(ROWS) { Array(COLS) { null } },
    val score: Int = 0,
    val target: Int = 42,
    val moves: Int = 24,
    val isGameOver: Boolean = false,
    val matchedCells: Set<Pair<Int, Int>> = emptySet(),
    val newCells: Set<Pair<Int, Int>> = emptySet(),
    // 각 셀의 낙하 거리 (행 단위). 애니메이션 후 emptyMap으로 클리어.
    val fallDistances: Map<Pair<Int, Int>, Int> = emptyMap(),
    // 중력 적용 횟수. GemGrid에서 Animatable 재생성 키로 사용.
    val fallGeneration: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GameState) return false
        return score == other.score &&
            target == other.target &&
            moves == other.moves &&
            isGameOver == other.isGameOver &&
            matchedCells == other.matchedCells &&
            newCells == other.newCells &&
            fallDistances == other.fallDistances &&
            grid.contentDeepEquals(other.grid)
    }

    override fun hashCode(): Int {
        var result = grid.contentDeepHashCode()
        result = 31 * result + score
        result = 31 * result + target
        result = 31 * result + moves
        result = 31 * result + isGameOver.hashCode()
        return result
    }

    fun copyGrid(): Array<Array<GemColor?>> =
        Array(ROWS) { r -> Array(COLS) { c -> grid[r][c] } }
}
