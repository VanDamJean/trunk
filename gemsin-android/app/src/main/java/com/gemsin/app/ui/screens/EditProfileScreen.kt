package com.gemsin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gemsin.app.ui.theme.*

@Composable
fun EditProfileScreen(navController: NavController) {
    var name by remember { mutableStateOf("Wan Sabrina Mayzura") }
    var username by remember { mutableStateOf("@wansabrina") }
    var phone by remember { mutableStateOf("08147029597") }
    var address by remember { mutableStateOf("Wan Sabrina Mayzura | 08147029597\nJalan Bukit Merah Brava, Jawa Maju, 3rd Kepulau") }
    var saved by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(GemsinBlue)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Text("‹", color = Color.White, fontSize = 28.sp)
                }
                Text("Profile", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
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
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = {}) {
                    Text("Ubah Foto Profile", color = GemsinAccent, fontSize = 13.sp)
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(
                    "Nama Lengkap" to name,
                    "Username" to username,
                    "No Telepon" to phone,
                ).forEachIndexed { i, (label, value) ->
                    Column {
                        Text(label, color = Color.White.copy(alpha = 0.65f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = value,
                            onValueChange = { v ->
                                when (i) { 0 -> name = v; 1 -> username = v; 2 -> phone = v }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = GemsinAccent,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                                cursorColor = GemsinAccent,
                                unfocusedContainerColor = Color(0xFF16213E),
                                focusedContainerColor = Color(0xFF16213E),
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }

                Column {
                    Text("Alamat Pengiriman", color = Color.White.copy(alpha = 0.65f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = GemsinAccent,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                            cursorColor = GemsinAccent,
                            unfocusedContainerColor = Color(0xFF16213E),
                            focusedContainerColor = Color(0xFF16213E),
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        saved = true
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GemsinAccent),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Simpan", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
