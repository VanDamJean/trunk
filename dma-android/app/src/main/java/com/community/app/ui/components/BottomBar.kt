package com.community.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import com.community.app.navigation.Screen
import com.community.app.ui.theme.CommunityPurple

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

val communityNavItems = listOf(
    BottomNavItem(Screen.Home.route, "Home", Icons.Filled.Home),
    BottomNavItem(Screen.Profile.route, "Profile", Icons.Filled.Person),
    BottomNavItem(Screen.Center.route, "Center", Icons.Filled.Star),
    BottomNavItem(Screen.Search.route, "Search", Icons.Filled.Search),
    BottomNavItem(Screen.Settings.route, "Settings", Icons.Filled.Settings),
)

@Composable
fun CommunityBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
) {
    NavigationBar(containerColor = Color.White) {
        communityNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.label)
                },
                label = {
                    Text(item.label, fontSize = 10.sp)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CommunityPurple,
                    selectedTextColor = CommunityPurple,
                    unselectedIconColor = Color(0xFF6B7280),
                    unselectedTextColor = Color(0xFF6B7280),
                    indicatorColor = Color(0xFFEDE9F8),
                ),
            )
        }
    }
}
