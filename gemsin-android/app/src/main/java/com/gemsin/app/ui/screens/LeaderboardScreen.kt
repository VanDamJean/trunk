package com.gemsin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gemsin.app.data.LeaderboardEntry
import com.gemsin.app.data.MockData
import com.gemsin.app.ui.components.AvatarFallback
import com.gemsin.app.ui.components.GemsinBottomBar
import com.gemsin.app.ui.theme.*

@Composable
fun LeaderboardScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var sortHigh by remember { mutableStateOf(true) }
    var rangeLabel by remember { mutableStateOf("Weekly") }
    var selectedRegion by remember { mutableStateOf("nasional") }

    val sorted = MockData.leaderboard
        .filter { it.name.contains(searchQuery, ignoreCase = true) }
        .sortedWith(if (sortHigh) compareByDescending { it.score } else compareBy { it.score })

    val podium = sorted.take(3)
    val rest = sorted.drop(3)

    Scaffold(bottomBar = { GemsinBottomBar(navController) }, containerColor = GemsinBlue) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(GemsinBlue),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Leaderboard", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { rangeLabel = if (rangeLabel == "Weekly") "Monthly" else "Weekly" },
                        colors = ButtonDefaults.buttonColors(containerColor = GemsinAccent.copy(alpha = 0.2f)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(rangeLabel, color = GemsinAccent, fontSize = 12.sp)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("nasional" to "Nasional", "provinsi" to "Provinsi", "kota" to "Kota").forEach { (key, label) ->
                        val selected = selectedRegion == key
                        Button(
                            onClick = { selectedRegion = key },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) GemsinAccent else GemsinAccent.copy(alpha = 0.15f)
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(label, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search player...", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = GemsinAccent,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            cursorColor = GemsinAccent,
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Button(
                        onClick = { sortHigh = !sortHigh },
                        colors = ButtonDefaults.buttonColors(containerColor = GemsinAccent.copy(alpha = 0.2f)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(if (sortHigh) "Sort: High" else "Sort: Low", color = GemsinAccent, fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text("Hall of Fame", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val ordered = listOfNotNull(podium.getOrNull(1), podium.getOrNull(0), podium.getOrNull(2))
                    ordered.forEachIndexed { idx, entry ->
                        val actualRank = podium.indexOf(entry) + 1
                        val toneColor = when (actualRank) { 1 -> GemsinGold; 2 -> GemsinSilver; else -> GemsinBronze }
                        val height = when (actualRank) { 1 -> 110.dp; 2 -> 85.dp; else -> 70.dp }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(90.dp)
                        ) {
                            if (actualRank == 1) Text("👑", fontSize = 20.sp)
                            AvatarFallback(entry.initials, size = if (actualRank == 1) 52.dp else 44.dp, backgroundColor = toneColor)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("@${entry.name}", color = Color.White, fontSize = 10.sp, maxLines = 1)
                            Text("${entry.score.format()} tiket", color = toneColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(height)
                                    .background(toneColor.copy(alpha = 0.3f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("$actualRank", color = toneColor, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(rest) { entry ->
                val rank = sorted.indexOf(entry) + 1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(
                            if (entry.isMe) GemsinAccent.copy(alpha = 0.2f) else Color(0xFF16213E),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(rank.toString().padStart(2, '0'), color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                    AvatarFallback(entry.initials, size = 34.dp)
                    Text(entry.name, color = Color.White, modifier = Modifier.weight(1f), fontSize = 13.sp)
                    Text(entry.score.format(), color = if (entry.isMe) GemsinAccent else Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            if (sorted.isEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No leaderboard data", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Try another keyword", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

private fun Int.format() = "%,d".format(this)
