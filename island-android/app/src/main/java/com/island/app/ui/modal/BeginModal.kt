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
fun BeginModal(
    target: Int,
    onStart: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalContainer(title = "Target", onDismiss = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 타겟 젬 아이콘 (파란 젬)
            Text("🔵", fontSize = 80.sp)

            Spacer(Modifier.height(8.dp))

            Text(
                text = target.toString(),
                color = WoodDark,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp
            )

            Spacer(Modifier.height(24.dp))

            GreenButton(text = "Start!", onClick = onStart)

            Spacer(Modifier.height(8.dp))
        }
    }
}
