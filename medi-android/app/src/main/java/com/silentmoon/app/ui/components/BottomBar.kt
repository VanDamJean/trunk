package com.silentmoon.app.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.silentmoon.app.navigation.Screen
import com.silentmoon.app.ui.theme.SMAccent
import com.silentmoon.app.ui.theme.SMPrimary
import com.silentmoon.app.ui.theme.SMSecondary

data class BottomNavItem(val label: String, val icon: String, val route: String)

val bottomNavItems = listOf(
    BottomNavItem("Home", "🏠", Screen.Home.route),
    BottomNavItem("Sleep", "🌙", Screen.Sleep.route),
    BottomNavItem("Profile", "👤", Screen.Profile.route),
)

@Composable
fun SilentMoonBottomBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(containerColor = Color(0xFFF8F7FF)) {
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
                    Text(item.icon, fontSize = 20.sp)
                },
                label = { Text(item.label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SMPrimary,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = SMPrimary,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFFEDE9FE),
                )
            )
        }
    }
}
