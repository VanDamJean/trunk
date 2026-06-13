package com.gemsin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.gemsin.app.data.Friend
import com.gemsin.app.data.MockData
import com.gemsin.app.ui.components.AvatarFallback
import com.gemsin.app.ui.components.GemsinBottomBar
import com.gemsin.app.ui.theme.*

@Composable
fun FriendsScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var friends by remember {
        mutableStateOf(MockData.friends)
    }

    val filtered = friends.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Scaffold(bottomBar = { GemsinBottomBar(navController) }, containerColor = GemsinBlue) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).background(GemsinBlue)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Teman", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = { navController.navigate("profile") }) {
                    Text("My Profile", color = GemsinAccent, fontSize = 12.sp)
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search friends...", fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(48.dp),
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

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(filtered) { _, friend ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF16213E)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AvatarFallback(friend.initials, size = 42.dp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(friend.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(friend.status, color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp)
                            }
                            Button(
                                onClick = {
                                    friends = friends.map {
                                        if (it.name == friend.name) it.copy(following = !it.following) else it
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (friend.following) GemsinAccent.copy(alpha = 0.2f) else GemsinAccent
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    if (friend.following) "Following" else "Follow",
                                    color = if (friend.following) GemsinAccent else Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
