package com.island.app.ui.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.island.app.ui.theme.*

val FLAGS = listOf("🇹🇲", "🇦🇪", "🇩🇪", "🇨🇳", "🇨🇦", "🇧🇷", "🇦🇫", "🇦🇲", "🇫🇷", "🇹🇷")

@Composable
fun EditProfileModal(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("Jeyhun Kelov") }
    var selectedFlag by remember { mutableStateOf(0) }

    ModalContainer(title = "Edit Profile", onDismiss = onDismiss) {
        // 이름 변경
        Text("Change name", color = WoodMedium, fontSize = 14.sp)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CreamBorder,
                unfocusedBorderColor = CreamBorder,
                focusedTextColor = WoodDark,
                unfocusedTextColor = WoodDark,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
        )

        Spacer(Modifier.height(16.dp))

        // 프로필 사진 (국기)
        Text("Change profile picture", color = WoodMedium, fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(160.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(FLAGS) { flag ->
                val idx = FLAGS.indexOf(flag)
                Box(
                    modifier = Modifier
                        .size(56.dp, 40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (selectedFlag == idx) Gold.copy(0.3f) else Color.Transparent)
                        .border(
                            width = if (selectedFlag == idx) 2.dp else 1.dp,
                            color = if (selectedFlag == idx) Gold else CreamBorder,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { selectedFlag = idx },
                    contentAlignment = Alignment.Center
                ) {
                    Text(flag, fontSize = 24.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        GreenButton(
            text = "Accept",
            onClick = onDismiss,
            modifier = Modifier.width(200.dp).align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(8.dp))
    }
}
