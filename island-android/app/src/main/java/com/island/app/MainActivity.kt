package com.island.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.island.app.game.GameViewModel
import com.island.app.navigation.Screen
import com.island.app.ui.screen.GameScreen
import com.island.app.ui.screen.HomeScreen
import com.island.app.ui.screen.LevelScreen
import com.island.app.ui.screen.PlayMapScreen
import com.island.app.ui.theme.IslandTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IslandTheme {
                IslandApp()
            }
        }
    }
}

@Composable
fun IslandApp() {
    val navController = rememberNavController()
    val gameViewModel: GameViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.Home.route) {
            HomeScreen(onPlay = { navController.navigate(Screen.PlayMap.route) })
        }
        composable(Screen.PlayMap.route) {
            PlayMapScreen(
                onLevel = { navController.navigate(Screen.Level.route) },
                onHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } }
            )
        }
        composable(Screen.Level.route) {
            LevelScreen(
                onLevelSelect = { levelId ->
                    navController.navigate(Screen.Game.createRoute(levelId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Game.route,
            arguments = listOf(navArgument("levelId") { type = NavType.IntType })
        ) { backStack ->
            val levelId = backStack.arguments?.getInt("levelId") ?: 1
            GameScreen(
                levelId = levelId,
                viewModel = gameViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
