package com.gemsin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.gemsin.app.ui.theme.*

@Composable
fun DailyCheckinScreen(navController: NavController) {
    val claimedDays = setOf(1, 2, 3)
    val totalDays = 28

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(GemsinBlue, GemsinSurface)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Text("‹", color = Color.White, fontSize = 28.sp)
                }
                Text("Daily Checkin", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    "275".forEach { c ->
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GemsinAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(c.toString(), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Kredit", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF16213E)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            repeat(7) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(GemsinAccent.copy(alpha = 0.6f))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val rows = (1..totalDays).chunked(7)
                        rows.forEach { week ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                week.forEach { day ->
                                    val claimed = day in claimedDays
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when {
                                                    claimed -> GemsinAccent
                                                    else -> Color.White.copy(alpha = 0.06f)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            if (claimed) "✓" else "$day",
                                            color = if (claimed) Color.White else Color.White.copy(alpha = 0.5f),
                                            fontSize = 13.sp,
                                            fontWeight = if (claimed) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                                repeat(7 - week.size) { Spacer(modifier = Modifier.size(36.dp)) }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}
