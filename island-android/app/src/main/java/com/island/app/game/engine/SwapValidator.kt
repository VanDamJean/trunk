package com.island.app.game.engine

import com.island.app.data.model.GemColor

object SwapValidator {

    fun isAdjacent(r1: Int, c1: Int, r2: Int, c2: Int): Boolean {
        val dr = kotlin.math.abs(r1 - r2)
        val dc = kotlin.math.abs(c1 - c2)
        return (dr == 1 && dc == 0) || (dr == 0 && dc == 1)
    }

    fun wouldMatch(grid: Array<Array<GemColor?>>, r1: Int, c1: Int, r2: Int, c2: Int): Boolean {
        val swapped = swap(grid, r1, c1, r2, c2)
        return MatchEngine.hasAnyMatch(swapped)
    }

    fun swap(grid: Array<Array<GemColor?>>, r1: Int, c1: Int, r2: Int, c2: Int): Array<Array<GemColor?>> {
        val newGrid = Array(ROWS) { r -> Array(COLS) { c -> grid[r][c] } }
        val tmp = newGrid[r1][c1]
        newGrid[r1][c1] = newGrid[r2][c2]
        newGrid[r2][c2] = tmp
        return newGrid
    }

    fun initGrid(): Array<Array<GemColor?>> {
        val grid = Array(ROWS) { Array<GemColor?>(COLS) { null } }
        for (r in 0 until ROWS) {
            for (c in 0 until COLS) {
                var color: GemColor
                do {
                    color = GemColor.random()
                } while (
                    (c >= 2 && grid[r][c - 1] == color && grid[r][c - 2] == color) ||
                    (r >= 2 && grid[r - 1][c] == color && grid[r - 2][c] == color)
                )
                grid[r][c] = color
            }
        }
        return grid
    }
}
