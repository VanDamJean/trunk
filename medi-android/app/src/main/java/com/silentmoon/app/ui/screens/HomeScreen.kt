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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.silentmoon.app.data.MockData
import com.silentmoon.app.navigation.Screen
import com.silentmoon.app.ui.components.MeditationCard
import com.silentmoon.app.ui.components.MeditationCardCompact
import com.silentmoon.app.ui.components.SilentMoonBottomBar
import com.silentmoon.app.ui.theme.SMAccent
import com.silentmoon.app.ui.theme.SMBackgroundLight
import com.silentmoon.app.ui.theme.SMPrimary

@Composable
fun HomeScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = { SilentMoonBottomBar(navController) },
        containerColor = SMBackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF3F0FF))
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    "Good Morning ☀️",
                    color = Color(0xFF6B7280),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "How are you feeling today?",
                    color = Color(0xFF1C1B2E),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("Search meditations...", color = Color(0xFF9CA3AF), fontSize = 14.sp)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SMPrimary,
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    leadingIcon = {
                        Text("🔍", fontSize = 16.sp, modifier = Modifier.padding(start = 4.dp))
                    },
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Daily Reminder Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SMPrimary)
                    .clickable { navController.navigate(Screen.ReminderSetup.route) }
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Daily Reminder",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            "Tap to set your meditation time",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                    Text("⏰", fontSize = 32.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recommended Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recommended for You",
                    color = Color(0xFF1C1B2E),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { navController.navigate(Screen.TopicSelection.route) }) {
                    Text("See All", color = SMPrimary, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val filteredSessions = if (searchQuery.isBlank()) MockData.meditationSessions
            else MockData.meditationSessions.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                filteredSessions.forEachIndexed { index, session ->
                    MeditationCard(
                        session = session,
                        gradientIndex = index,
                        onClick = {
                            navController.navigate(Screen.Player.createRoute(session.id))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Topics
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Browse Topics",
                    color = Color(0xFF1C1B2E),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { navController.navigate(Screen.TopicSelection.route) }) {
                    Text("All Topics", color = SMPrimary, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(MockData.topics) { _, topic ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SMPrimary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            topic,
                            color = SMPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Popular This Week
            Text(
                "Popular This Week",
                color = Color(0xFF1C1B2E),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(MockData.meditationSessions) { index, session ->
                    MeditationCardCompact(
                        session = session,
                        gradientIndex = index + 2,
                        onClick = {
                            navController.navigate(Screen.Player.createRoute(session.id))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
