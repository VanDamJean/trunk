package com.island.app.data.repository

import com.island.app.data.model.Level

object LevelRepository {
    val levels: List<Level> = (1..16).map { id ->
        Level(
            id = id,
            target = 30 + id * 3,
            moves = 30 - id / 2,
            theme = if (id <= 5) "pirate" else "desert",
            stars = when {
                id <= 2 -> 3
                id <= 4 -> 2
                id <= 6 -> 1
                id <= 10 -> 0
                else -> 0
            },
            isUnlocked = id <= 10
        )
    }

    fun getLevel(id: Int): Level = levels.first { it.id == id }
}
