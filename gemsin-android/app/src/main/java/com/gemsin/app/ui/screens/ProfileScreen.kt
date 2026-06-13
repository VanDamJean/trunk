package com.gemsin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gemsin.app.navigation.Screen
import com.gemsin.app.ui.components.GemsinBottomBar
import com.gemsin.app.ui.theme.*

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(bottomBar = { GemsinBottomBar(navController) }, containerColor = GemsinBlue) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(GemsinBlue)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(GemsinAccent.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🕶️", fontSize = 32.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("Wan Sabrina Mayzura", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("@wansabrina | +6281247029597", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp)
            }

            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = GemsinAccent.copy(alpha = 0.2f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Daily Check In", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Dapatkan 5 kredit tiap harinya!", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.3f },
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                        color = GemsinAccent,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Day 3", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        TextButton(
                            onClick = { navController.navigate(Screen.DailyCheckin.route) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Klaim disini", color = GemsinAccent, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            listOf(
                listOf("Edit Profile" to Screen.EditProfile.route, "Notifications" to null, "Bahasa" to null),
                listOf("Help & Support" to null, "Contact Us" to null, "Privacy Policy" to null)
            ).forEach { group ->
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF16213E)
                ) {
                    Column {
                        group.forEachIndexed { i, (label, route) ->
                            TextButton(
                                onClick = { route?.let { navController.navigate(it) } },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(label, color = Color.White, fontSize = 14.sp)
                                    Text("›", color = Color.White.copy(alpha = 0.4f), fontSize = 18.sp)
                                }
                            }
                            if (i < group.size - 1) HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
