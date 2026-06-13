package com.island.app.ui.modal

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.island.app.ui.theme.*

@Composable
fun SettingsModal(onDismiss: () -> Unit) {
    var showEditProfile by remember { mutableStateOf(false) }
    var soundOn by remember { mutableStateOf(true) }
    var musicOn by remember { mutableStateOf(true) }

    ModalContainer(title = "Settings", onDismiss = onDismiss) {
        // Sound + Music 토글
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                GoldCircleButton(
                    icon = if (soundOn) "🔊" else "🔇",
                    size = 64,
                    onClick = { soundOn = !soundOn }
                )
                Spacer(Modifier.height(6.dp))
                Text("Sound", color = WoodMedium, fontSize = 13.sp, fontWeight = FontWeight.Normal)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                GoldCircleButton(
                    icon = if (musicOn) "🎵" else "🎵",
                    size = 64,
                    onClick = { musicOn = !musicOn }
                )
                Spacer(Modifier.height(6.dp))
                Text("Music", color = WoodMedium, fontSize = 13.sp, fontWeight = FontWeight.Normal)
            }
        }

        Spacer(Modifier.height(24.dp))

        GreenButton(text = "Edit Profile", onClick = { showEditProfile = true })

        Spacer(Modifier.height(8.dp))
    }

    if (showEditProfile) {
        EditProfileModal(onDismiss = { showEditProfile = false })
    }
}
