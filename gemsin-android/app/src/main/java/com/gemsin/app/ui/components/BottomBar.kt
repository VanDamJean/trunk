package com.gemsin.app.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gemsin.app.navigation.Screen

data class BottomNavItem(val label: String, val icon: String, val route: String)

val bottomNavItems = listOf(
    BottomNavItem("Home", "🏠", Screen.Home.route),
    BottomNavItem("Rank", "📊", Screen.Leaderboard.route),
    BottomNavItem("🎟", "🎟", Screen.Ticket.route),
    BottomNavItem("Teman", "👥", Screen.Friends.route),
    BottomNavItem("History", "📋", Screen.History.route),
)

@Composable
fun GemsinBottomBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(containerColor = Color(0xFF1A1A2E)) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Text(item.icon, fontSize = if (item.route == Screen.Ticket.route) 24.sp else 18.sp)
                },
                label = if (item.route != Screen.Ticket.route) {
                    { Text(item.label, fontSize = 10.sp) }
                } else null,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF7C4DFF),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color(0xFF2A2A4E),
                )
            )
        }
    }
}
