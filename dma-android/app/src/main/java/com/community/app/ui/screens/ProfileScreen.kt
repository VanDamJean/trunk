package com.community.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.community.app.ui.components.AppHeader
import com.community.app.ui.components.ReportCard
import com.community.app.ui.components.TagBadge
import com.community.app.ui.components.TagColor

private data class Report(val icon: ImageVector, val title: String, val description: String)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    bottomBar: @Composable () -> Unit,
) {
    val reports = listOf(
        Report(Icons.Filled.Person, "Astro psychological report", "Some short description of this type of report"),
        Report(Icons.Filled.Flag, "Monthly prediction report", "Some short description of this type of report"),
        Report(Icons.Filled.Flag, "Daily Prediction", "Some short description of this type of report"),
        Report(Icons.Filled.Favorite, "Love report", "Some short description of this type of report"),
    )

    Scaffold(
        topBar = {
            AppHeader(title = "Profile")
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
            // Profile avatar + name
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF3B82F6), Color(0xFF9333EA))
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("👤", fontSize = 40.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Angelica Jackson",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Analyzer",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280),
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = {}) {
                    Text(
                        text = "Change profile",
                        fontSize = 13.sp,
                        color = Color(0xFF5B3FA0),
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            // Strong side
            Column {
                Text(
                    text = "Strong side:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937),
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TagBadge(label = "Analytics", color = TagColor.TEAL)
                    TagBadge(label = "Perfectionism", color = TagColor.TEAL)
                    TagBadge(label = "Analytics", color = TagColor.TEAL)
                }
            }

            // Weak side
            Column {
                Text(
                    text = "Weak side:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937),
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TagBadge(label = "Perfectionism", color = TagColor.PINK)
                    TagBadge(label = "Analytics", color = TagColor.PINK)
                }
            }

            // My Reports
            Column {
                Text(
                    text = "My Reports:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937),
                )
                Spacer(modifier = Modifier.height(8.dp))
                val rows = reports.chunked(2)
                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        row.forEach { report ->
                            ReportCard(
                                icon = report.icon,
                                title = report.title,
                                description = report.description,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (row.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    if (rows.last() != row) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
