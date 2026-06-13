package com.community.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.community.app.ui.components.CommunityBottomBar
import com.community.app.ui.screens.HomeScreen
import com.community.app.ui.screens.PlaceholderScreen
import com.community.app.ui.screens.ProfileScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Center : Screen("center")
    object Search : Screen("search")
    object Settings : Screen("settings")
}

@Composable
fun CommunityNavGraph(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Screen.Home.route

    fun navigate(route: String) {
        if (currentRoute != route) {
            navController.navigate(route) {
                popUpTo(Screen.Home.route) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    val bottomBar: @Composable () -> Unit = {
        CommunityBottomBar(
            currentRoute = currentRoute,
            onNavigate = ::navigate,
        )
    }

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                currentRoute = Screen.Home.route,
                onNavigate = ::navigate,
                bottomBar = bottomBar,
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                currentRoute = Screen.Profile.route,
                onNavigate = ::navigate,
                bottomBar = bottomBar,
            )
        }
        composable(Screen.Center.route) {
            PlaceholderScreen(
                title = "Center",
                onNavigate = ::navigate,
                bottomBar = bottomBar,
            )
        }
        composable(Screen.Search.route) {
            PlaceholderScreen(
                title = "Search",
                subtitle = "검색·히스토리 화면 플레이스홀더입니다.",
                onNavigate = ::navigate,
                bottomBar = bottomBar,
            )
        }
        composable(Screen.Settings.route) {
            PlaceholderScreen(
                title = "Settings",
                subtitle = "앱 설정 화면 플레이스홀더입니다.",
                onNavigate = ::navigate,
                bottomBar = bottomBar,
            )
        }
    }
}
