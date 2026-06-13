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
import com.gemsin.app.data.MockData
import com.gemsin.app.ui.components.GemsinBottomBar
import com.gemsin.app.ui.theme.*

@Composable
fun HistoryScreen(navController: NavController) {
    var filter by remember { mutableStateOf("all") }

    val filtered = MockData.history.filter { filter == "all" || it.range == filter }

    Scaffold(bottomBar = { GemsinBottomBar(navController) }, containerColor = GemsinBlue) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).background(GemsinBlue)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("History", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Surface(shape = RoundedCornerShape(20.dp), color = GemsinAccent.copy(alpha = 0.15f)) {
                    Text("This Week", color = GemsinAccent, modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp), fontSize = 12.sp)
                }
            }

            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("all" to "All", "today" to "Today", "yesterday" to "Yesterday").forEach { (key, label) ->
                    val selected = filter == key
                    Button(
                        onClick = { filter = key },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected) GemsinAccent else GemsinAccent.copy(alpha = 0.12f)
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(label, color = if (selected) Color.White else GemsinAccent, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filtered.isEmpty()) {
                Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No history yet", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("No records for this filter range.", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered) { entry ->
                        Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFF16213E)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(GemsinAccent.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(entry.icon, fontSize = 22.sp)
                                    }
                                    Column {
                                        Text(entry.game, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(entry.time, color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp)
                                    }
                                }
                                Text("Score ${entry.score}", color = GemsinAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
