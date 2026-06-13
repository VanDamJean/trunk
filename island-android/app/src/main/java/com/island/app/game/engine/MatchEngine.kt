package com.island.app.game.engine

import com.island.app.data.model.GemColor

object MatchEngine {

    fun findMatches(grid: Array<Array<GemColor?>>): Set<Pair<Int, Int>> {
        val matched = mutableSetOf<Pair<Int, Int>>()

        // 가로 매치
        for (r in 0 until ROWS) {
            var c = 0
            while (c < COLS) {
                val color = grid[r][c] ?: run { c++; continue }
                var len = 1
                while (c + len < COLS && grid[r][c + len] == color) len++
                if (len >= 3) {
                    for (k in 0 until len) matched.add(Pair(r, c + k))
                }
                c += len
            }
        }

        // 세로 매치
        for (c in 0 until COLS) {
            var r = 0
            while (r < ROWS) {
                val color = grid[r][c] ?: run { r++; continue }
                var len = 1
                while (r + len < ROWS && grid[r + len][c] == color) len++
                if (len >= 3) {
                    for (k in 0 until len) matched.add(Pair(r + k, c))
                }
                r += len
            }
        }

        return matched
    }

    fun hasAnyMatch(grid: Array<Array<GemColor?>>): Boolean =
        findMatches(grid).isNotEmpty()
}
