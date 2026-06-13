package com.silentmoon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.silentmoon.app.data.MockData
import com.silentmoon.app.data.MeditationSession
import com.silentmoon.app.navigation.Screen
import com.silentmoon.app.ui.theme.SMAccent
import com.silentmoon.app.ui.theme.SMBackgroundDark
import com.silentmoon.app.ui.theme.SMPrimary
import com.silentmoon.app.ui.theme.SilentMoonDarkTheme

val sleepCardGradients = listOf(
    listOf(Color(0xFF1E1B4B), Color(0xFF312E81)),
    listOf(Color(0xFF0F172A), Color(0xFF1E1B4B)),
    listOf(Color(0xFF1E1B4B), Color(0xFF4C1D95)),
    listOf(Color(0xFF0D0B1E), Color(0xFF312E81)),
)

@Composable
fun SleepSectionScreen(navController: NavController) {
    SilentMoonDarkTheme {
        Scaffold(
            bottomBar = {
                // Dark-themed bottom bar for sleep section
                NavigationBar(containerColor = Color(0xFF0D0B1E)) {
                    val items = listOf(
                        Triple("Home", "🏠", Screen.Home.route),
                        Triple("Sleep", "🌙", Screen.Sleep.route),
                        Triple("Profile", "👤", Screen.Profile.route),
                    )
                    items.forEach { (label, icon, route) ->
                        val selected = route == Screen.Sleep.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (route != Screen.Sleep.route) {
                                    navController.navigate(route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Text(icon, fontSize = 20.sp) },
                            label = { Text(label, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SMAccent,
                                unselectedIconColor = Color(0xFF6B7280),
                                selectedTextColor = SMAccent,
                                unselectedTextColor = Color(0xFF6B7280),
                                indicatorColor = Color(0xFF1E1B4B),
                            )
                        )
                    }
                }
            },
            containerColor = SMBackgroundDark
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with starry background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF0D0B1E), Color(0xFF1E1B4B))
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 32.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Good Night 🌙",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Sleep Well Tonight",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text("✨", fontSize = 32.sp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Tonight's recommendation
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF5B21B6), Color(0xFF4C1D95))
                                    )
                                )
                                .clickable {
                                    navController.navigate(Screen.Player.createRoute(10))
                                }
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.White.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            "TONIGHT'S PICK",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Night Island",
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "45 MIN • Sleep Story",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 13.sp
                                    )
                                }
                                Text("🏝️", fontSize = 48.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sleep Stories Section
                Text(
                    "Sleep Stories",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Soothing tales to drift off to",
                    color = Color(0xFF6B7280),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    itemsIndexed(MockData.sleepSessions.take(3)) { index, session ->
                        SleepCard(
                            session = session,
                            gradientIndex = index,
                            onClick = { navController.navigate(Screen.Player.createRoute(session.id)) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Sleep Music Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Sleep Music",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("🎵", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MockData.sleepSessions.forEach { session ->
                        SleepListItem(
                            session = session,
                            onClick = { navController.navigate(Screen.Player.createRoute(session.id)) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Wind Down Timer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E1B4B))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Wind Down Timer",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Auto-stop music after 30 min",
                                color = Color(0xFF9CA3AF),
                                fontSize = 13.sp
                            )
                        }
                        Switch(
                            checked = true,
                            onCheckedChange = {},
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SMAccent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SleepCard(
    session: MeditationSession,
    gradientIndex: Int,
    onClick: () -> Unit
) {
    val gradient = sleepCardGradients[gradientIndex % sleepCardGradients.size]

    Box(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(gradient))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(session.icon, fontSize = 40.sp)

            Column {
                Text(
                    session.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    session.duration,
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Text(
                        "▶ Play",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SleepListItem(
    session: MeditationSession,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1A1740))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF5B21B6), Color(0xFF4C1D95))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(session.icon, fontSize = 24.sp)
            }

            Column {
                Text(
                    session.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    session.duration,
                    color = Color(0xFF6B7280),
                    fontSize = 12.sp
                )
            }
        }

        Text("▶", color = SMAccent, fontSize = 18.sp)
    }
}
