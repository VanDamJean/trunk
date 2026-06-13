package com.community.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.community.app.ui.components.AppHeader
import com.community.app.ui.components.CommunityListItem
import com.community.app.ui.components.DailyTaskCard
import com.community.app.ui.components.NewsCard
import com.community.app.ui.components.ProgressSection
import com.community.app.ui.theme.CommunityGreen
import com.community.app.ui.theme.CommunityRed
import com.community.app.ui.theme.NewsGradient1End
import com.community.app.ui.theme.NewsGradient1Start
import com.community.app.ui.theme.NewsGradient2End
import com.community.app.ui.theme.NewsGradient2Start
import com.community.app.ui.theme.NewsGradient3End
import com.community.app.ui.theme.NewsGradient3Start

@Composable
fun HomeScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    bottomBar: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            AppHeader(title = "Home")
        },
        bottomBar = bottomBar,
        containerColor = Color.White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // News Section
            Column {
                Text(
                    text = "News",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B7280),
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    NewsCard(
                        title = "Short news title will be here",
                        gradientColors = listOf(NewsGradient1Start, NewsGradient1End),
                    )
                    NewsCard(
                        title = "Short news title will be here",
                        gradientColors = listOf(NewsGradient2Start, NewsGradient2End),
                    )
                    NewsCard(
                        title = "Short news title will be here",
                        gradientColors = listOf(NewsGradient3Start, NewsGradient3End),
                    )
                }
            }

            // Daily Tasks Section
            Column {
                Text(
                    text = "Daily Tasks:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B7280),
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    DailyTaskCard(
                        label = "Daily",
                        count = 3,
                        accentColor = CommunityRed,
                        modifier = Modifier.weight(1f),
                    )
                    DailyTaskCard(
                        label = "Daily deep",
                        count = 1,
                        accentColor = CommunityRed,
                        modifier = Modifier.weight(1f),
                    )
                    DailyTaskCard(
                        label = "Daily mantra",
                        count = 2,
                        accentColor = CommunityGreen,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Progress Section
            ProgressSection(progress = 60f)

            // List Items
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CommunityListItem(
                    icon = Icons.Filled.AccessTime,
                    title = "How was your day?",
                    description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut sed odio in urna ultrices.",
                )
                CommunityListItem(
                    icon = Icons.Filled.NotificationsActive,
                    iconTint = CommunityRed,
                    title = "Current Transit 3rd House",
                    description = "This is demonstrate siblings, hobbies, efforts, confidence, friends and short tr...",
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
