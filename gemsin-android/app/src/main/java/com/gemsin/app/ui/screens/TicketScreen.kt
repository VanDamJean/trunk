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
fun TicketScreen(navController: NavController) {
    var credit by remember { mutableStateOf(982) }
    var toastMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(bottomBar = { GemsinBottomBar(navController) }, containerColor = GemsinBlue) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(GemsinBlue),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Credit & Ticket", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Surface(shape = RoundedCornerShape(20.dp), color = GemsinAccent.copy(alpha = 0.2f)) {
                        Text("Wallet", color = GemsinAccent, modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp), fontSize = 12.sp)
                    }
                }
            }

            item {
                Surface(shape = RoundedCornerShape(20.dp), color = GemsinAccent) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Current Credit", color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp)
                            Text("$credit", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Button(
                            onClick = {
                                credit += 5
                                toastMsg = "Daily reward claimed: +5 credit"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text("Claim Daily +5", color = GemsinAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Text("Top Up Packs", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            items(MockData.packs) { pack ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF16213E)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(pack.name, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("+${pack.credit} Credit", color = GemsinAccent, fontSize = 13.sp)
                        }
                        Button(
                            onClick = {
                                credit += pack.credit
                                toastMsg = "${pack.name} purchased: +${pack.credit} credit"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GemsinAccent)
                        ) {
                            Text(pack.price, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    toastMsg?.let { msg ->
        LaunchedEffect(msg) {
            kotlinx.coroutines.delay(2000)
            toastMsg = null
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Surface(
                modifier = Modifier.padding(bottom = 100.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF333355)
            ) {
                Text(msg, color = Color.White, modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), fontSize = 13.sp)
            }
        }
    }
}
