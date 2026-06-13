package com.island.app.game.engine

import com.island.app.data.model.GemColor

object GravitySystem {

    // 빈 칸을 아래로 채우고 상단에 랜덤 젬 보충.
    // fallDistances: 각 셀이 몇 행 낙하하는지 (새 젬 + 기존 젬이 밀려 내려간 경우 모두 포함)
    fun apply(
        grid: Array<Array<GemColor?>>
    ): Triple<Array<Array<GemColor?>>, Set<Pair<Int, Int>>, Map<Pair<Int, Int>, Int>> {
        val newGrid = Array(ROWS) { r -> Array(COLS) { c -> grid[r][c] } }
        val newCells = mutableSetOf<Pair<Int, Int>>()
        val fallDistances = mutableMapOf<Pair<Int, Int>, Int>()

        for (c in 0 until COLS) {
            // 기존 젬의 원래 행 번호 수집
            val existingRows = mutableListOf<Int>()
            for (r in 0 until ROWS) {
                if (grid[r][c] != null) existingRows.add(r)
            }
            val filled = ROWS - existingRows.size  // 새로 채울 행 수
            if (filled == 0) continue

            // 새 랜덤 젬 prepend
            val gems = mutableListOf<GemColor?>()
            repeat(filled) { gems.add(GemColor.random()) }
            for (r in existingRows) gems.add(grid[r][c])

            for (r in 0 until ROWS) {
                newGrid[r][c] = gems[r]
                if (r < filled) {
                    // 새 젬: 그리드 위에서 (r+1)칸 낙하
                    // → 절대 Y 기준으로 모두 row -1에서 출발 (= 그리드 상단 바로 위)
                    fallDistances[Pair(r, c)] = r + 1
                    newCells.add(Pair(r, c))
                } else {
                    // 기존 젬이 밀려 내려간 경우
                    val originalRow = existingRows[r - filled]
                    val drop = r - originalRow
                    if (drop > 0) fallDistances[Pair(r, c)] = drop
                }
            }
        }

        return Triple(newGrid, newCells, fallDistances)
    }
}
