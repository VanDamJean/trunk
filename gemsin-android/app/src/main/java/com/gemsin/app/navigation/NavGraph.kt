package com.gemsin.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gemsin.app.ui.screens.*

sealed class Screen(val route: String) {
    object Onboarding1 : Screen("onboarding_1")
    object Onboarding2 : Screen("onboarding_2")
    object Onboarding3 : Screen("onboarding_3")
    object Home : Screen("home")
    object Leaderboard : Screen("leaderboard")
    object Ticket : Screen("ticket")
    object Friends : Screen("friends")
    object History : Screen("history")
    object Profile : Screen("profile")
    object DailyCheckin : Screen("daily_checkin")
    object EditProfile : Screen("edit_profile")
    object JumpMan : Screen("game_jump_man")
    object FlappyBird : Screen("game_flappy_bird")
    object UlarAngka : Screen("game_ular_angka")
    object Tetris : Screen("game_tetris")
}

@Composable
fun GemsinNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Onboarding1.route) {
        composable(Screen.Onboarding1.route) {
            OnboardingScreen1(
                onNext = { navController.navigate(Screen.Onboarding2.route) },
                onSkip = { navController.navigate(Screen.Home.route) { popUpTo(0) } }
            )
        }
        composable(Screen.Onboarding2.route) {
            OnboardingScreen2(
                onNext = { navController.navigate(Screen.Onboarding3.route) },
                onSkip = { navController.navigate(Screen.Home.route) { popUpTo(0) } }
            )
        }
        composable(Screen.Onboarding3.route) {
            OnboardingScreen3(
                onDone = { navController.navigate(Screen.Home.route) { popUpTo(0) } }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(navController = navController)
        }
        composable(Screen.Ticket.route) {
            TicketScreen(navController = navController)
        }
        composable(Screen.Friends.route) {
            FriendsScreen(navController = navController)
        }
        composable(Screen.History.route) {
            HistoryScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Screen.DailyCheckin.route) {
            DailyCheckinScreen(navController = navController)
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController = navController)
        }
        composable(Screen.JumpMan.route) {
            JumpManScreen(navController = navController)
        }
        composable(Screen.FlappyBird.route) {
            FlappyBirdScreen(navController = navController)
        }
        composable(Screen.UlarAngka.route) {
            UlarAngkaScreen(navController = navController)
        }
        composable(Screen.Tetris.route) {
            TetrisScreen(navController = navController)
        }
    }
}
