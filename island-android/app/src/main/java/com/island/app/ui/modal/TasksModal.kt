package com.island.app.ui.modal

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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

data class Task(val icon: String, val name: String, val done: Boolean = false)

@Composable
fun TasksModal(onDismiss: () -> Unit) {
    var worldProgress by remember { mutableStateOf(2) }
    val tasks = remember {
        mutableStateListOf(
            Task("🪔", "Lamp"),
            Task("🪣", "Well")
        )
    }

    ModalContainer(title = "Tasks", onDismiss = onDismiss) {
        Text("World Progress", color = WoodMedium, fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))

        val progressFraction by animateFloatAsState(
            targetValue = worldProgress / 10f,
            animationSpec = tween(300),
            label = "worldProgress"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CreamBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressFraction)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Gold)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                "$worldProgress/10",
                color = WoodDark,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(Modifier.width(4.dp))
            Text("📦", fontSize = 24.sp)
        }

        Spacer(Modifier.height(12.dp))

        tasks.forEachIndexed { index, task ->
            if (index > 0) HorizontalDivider(color = CreamBorder, thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(task.icon, fontSize = 32.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    task.name,
                    color = WoodDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (task.done) Color.Gray else GreenMedium)
                        .border(2.dp, if (task.done) Color.DarkGray else GreenBorder, RoundedCornerShape(6.dp))
                        .clickable(enabled = !task.done) {
                            tasks[index] = task.copy(done = true)
                            if (worldProgress < 10) worldProgress++
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (task.done) "Done ✓" else "Do it",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}
