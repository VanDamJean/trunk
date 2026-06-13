package com.island.app.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object PlayMap : Screen("play_map")
    object Level : Screen("level")
    object Game : Screen("game/{levelId}") {
        fun createRoute(levelId: Int) = "game/$levelId"
    }
}
