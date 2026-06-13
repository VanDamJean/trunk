package com.silentmoon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.silentmoon.app.ui.components.SilentMoonBottomBar
import com.silentmoon.app.ui.theme.SMBackgroundLight
import com.silentmoon.app.ui.theme.SMPrimary
import com.silentmoon.app.ui.theme.SMSecondary

@Composable
fun ProfilePlaceholderScreen(navController: NavController) {
    Scaffold(
        bottomBar = { SilentMoonBottomBar(navController) },
        containerColor = SMBackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(SMPrimary, Color(0xFF6D28D9)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🧘", fontSize = 44.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Mindful User",
                color = SMSecondary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "7 day streak 🔥",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(
                    Triple("12", "Sessions", "🧘"),
                    Triple("3.5h", "Total Time", "⏱️"),
                    Triple("7", "Day Streak", "🔥")
                ).forEach { (value, label, icon) ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF3F0FF)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(icon, fontSize = 22.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                value,
                                color = SMPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                label,
                                color = Color(0xFF9CA3AF),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Profile features coming soon",
                color = Color(0xFF9CA3AF),
                fontSize = 14.sp
            )
        }
    }
}
