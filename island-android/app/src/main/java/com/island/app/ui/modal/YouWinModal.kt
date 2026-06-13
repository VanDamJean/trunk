package com.island.app.ui.modal

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.island.app.ui.theme.*

@Composable
fun YouWinModal(
    score: Int,
    target: Int,
    stars: Int,
    onRestart: () -> Unit,
    onBack: () -> Unit
) {
    ModalContainer(title = "You Win", onDismiss = onBack) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StarRow(stars = stars, size = 48)

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Target", color = WoodMedium, fontSize = 14.sp)
                    Text(
                        target.toString(),
                        color = WoodDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Column {
                    Text("Your Score", color = WoodMedium, fontSize = 14.sp)
                    Text(
                        score.toString(),
                        color = WoodDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // 코인 보상
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("🪙", fontSize = 24.sp)
                Text(
                    " +50",
                    color = WoodDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // 버튼 3개
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GoldCircleButton(icon = "≡", size = 48, onClick = {})
                GoldCircleButton(icon = "▶", size = 48, onClick = onRestart)
                GoldCircleButton(icon = "↺", size = 48, onClick = onRestart)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
