package com.island.app.data.model

data class Level(
    val id: Int,
    val target: Int,
    val moves: Int,
    val theme: String = "pirate",
    val stars: Int = 0,
    val isUnlocked: Boolean = false
)
