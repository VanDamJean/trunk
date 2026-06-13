package com.island.app.ui.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.island.app.data.model.GemColor
import com.island.app.ui.theme.*

@Composable
fun GoldenKeyModal(onDismiss: () -> Unit) {
    ModalContainer(title = "Golden key", onDismiss = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 젬 2×2 + 화살표 + 열쇠 아이콘
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // 2×2 젬 그리드
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(GemColor.PINK, GemColor.YELLOW).forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Brush.verticalGradient(listOf(color.light, color.dark)))
                                    .border(1.dp, Color.White.copy(0.3f), RoundedCornerShape(5.dp))
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(GemColor.BLUE, GemColor.GREEN).forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Brush.verticalGradient(listOf(color.light, color.dark)))
                                    .border(1.dp, Color.White.copy(0.3f), RoundedCornerShape(5.dp))
                            )
                        }
                    }
                }

                Spacer(Modifier.width(16.dp))
                Text("→", fontSize = 28.sp, color = WoodDark, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(16.dp))

                // 골든 키
                Text("🗝️", fontSize = 48.sp)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Beat more levels to earn\ngolden key !",
                color = WoodMedium,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            Box(modifier = Modifier.width(160.dp)) {
                GreenButton(text = "Continue", onClick = onDismiss)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
