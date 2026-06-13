package com.gemsin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.gemsin.app.data.Game
import com.gemsin.app.data.MockData
import com.gemsin.app.navigation.Screen
import com.gemsin.app.ui.components.GemsinBottomBar
import com.gemsin.app.ui.theme.*

val gameColors = listOf(GemsinCard1, GemsinCard2, GemsinCard3, GemsinCard4)

@Composable
fun HomeScreen(navController: NavController) {
    var selectedGame by remember { mutableStateOf<Game?>(null) }

    Scaffold(
        bottomBar = { GemsinBottomBar(navController) },
        containerColor = GemsinBlue
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(GemsinBlue, GemsinSurface)))
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF2A2A4E)
                ) {
                    Text("🎟 270", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = Color.White, fontSize = 13.sp)
                }
                Text("GEMSIN", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                    Text("🕶️", fontSize = 22.sp)
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF7C4DFF).copy(alpha = 0.25f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Daily Check In", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Dapatkan 5 kredit tiap harinya!", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.1f },
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                        color = GemsinAccent,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Day 1", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("623" to "Total Diamond", "982" to "Total Kredit").forEachIndexed { i, (val_, label) ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = if (i == 0) GemsinAccent.copy(alpha = 0.3f) else Color(0xFF2A2A4E)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(val_, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text(label, color = Color.White.copy(alpha = 0.65f), fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Games",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MockData.games.chunked(2).forEach { rowGames ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowGames.forEach { game ->
                            val color = gameColors[MockData.games.indexOf(game) % gameColors.size]
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(140.dp)
                                    .clickable { selectedGame = game },
                                shape = RoundedCornerShape(16.dp),
                                color = color
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp).fillMaxHeight(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(game.emoji, fontSize = 28.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(game.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(game.mode, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = { selectedGame = game },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Start", color = Color.White, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                        if (rowGames.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    selectedGame?.let { game ->
        Dialog(onDismissRequest = { selectedGame = null }) {
            Surface(shape = RoundedCornerShape(20.dp), color = GemsinSurface) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(game.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        TextButton(onClick = { selectedGame = null }) { Text("Close", color = Color.Gray) }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(game.desc, color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(shape = RoundedCornerShape(20.dp), color = GemsinAccent.copy(alpha = 0.2f)) {
                            Text(game.mode, color = GemsinAccent, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 12.sp)
                        }
                        Surface(shape = RoundedCornerShape(20.dp), color = GemsinYellow.copy(alpha = 0.2f)) {
                            Text("Difficulty: ${game.difficulty}", color = GemsinYellow, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            selectedGame = null
                            when (game.id) {
                                "jump-man" -> navController.navigate(Screen.JumpMan.route)
                                "flappy-bird" -> navController.navigate(Screen.FlappyBird.route)
                                "ular-angka" -> navController.navigate(Screen.UlarAngka.route)
                                "tetris" -> navController.navigate(Screen.Tetris.route)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GemsinAccent)
                    ) {
                        Text("Start Game")
                    }
                }
            }
        }
    }
}
