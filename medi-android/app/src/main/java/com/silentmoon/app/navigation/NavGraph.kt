package com.silentmoon.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.silentmoon.app.ui.screens.*

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Sleep : Screen("sleep")
    object Profile : Screen("profile")
    object TopicSelection : Screen("topic_selection")
    object ReminderSetup : Screen("reminder_setup")
    object Player : Screen("player/{sessionId}") {
        fun createRoute(sessionId: Int) = "player/$sessionId"
    }
}

@Composable
fun SilentMoonNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Onboarding.route) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Home.route) { popUpTo(0) }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Sleep.route) {
            SleepSectionScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfilePlaceholderScreen(navController = navController)
        }
        composable(Screen.TopicSelection.route) {
            TopicSelectionScreen(
                onDone = { navController.popBackStack() }
            )
        }
        composable(Screen.ReminderSetup.route) {
            ReminderSetupScreen(
                onDone = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Player.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: 1
            MeditationPlayerScreen(
                sessionId = sessionId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
