package com.silentmoon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silentmoon.app.ui.theme.SMAccent
import com.silentmoon.app.ui.theme.SMBackgroundLight
import com.silentmoon.app.ui.theme.SMPrimary
import com.silentmoon.app.ui.theme.SMSecondary

val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

@Composable
fun ReminderSetupScreen(onDone: () -> Unit) {
    var selectedHour by remember { mutableIntStateOf(7) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var isAm by remember { mutableStateOf(true) }
    val selectedDays = remember { mutableStateListOf("Mon", "Tue", "Wed", "Thu", "Fri") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SMBackgroundLight)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            "Set Your Reminder",
            color = SMSecondary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "We'll remind you to meditate at your chosen time each day.",
            color = Color(0xFF6B7280),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Time display
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFF3F0FF)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("⏰", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(12.dp))

                // Time picker row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Hour
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { selectedHour = if (selectedHour >= 12) 1 else selectedHour + 1 }) {
                            Text("▲", fontSize = 18.sp, color = SMPrimary)
                        }
                        Text(
                            selectedHour.toString().padStart(2, '0'),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = SMSecondary
                        )
                        IconButton(onClick = { selectedHour = if (selectedHour <= 1) 12 else selectedHour - 1 }) {
                            Text("▼", fontSize = 18.sp, color = SMPrimary)
                        }
                    }

                    Text(
                        ":",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = SMSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Minute
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { selectedMinute = if (selectedMinute >= 55) 0 else selectedMinute + 5 }) {
                            Text("▲", fontSize = 18.sp, color = SMPrimary)
                        }
                        Text(
                            selectedMinute.toString().padStart(2, '0'),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = SMSecondary
                        )
                        IconButton(onClick = { selectedMinute = if (selectedMinute <= 0) 55 else selectedMinute - 5 }) {
                            Text("▼", fontSize = 18.sp, color = SMPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // AM/PM toggle
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        listOf(true to "AM", false to "PM").forEach { (amValue, label) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isAm == amValue) SMPrimary else Color.Transparent,
                                modifier = Modifier
                                    .width(52.dp)
                                    .clickable { isAm = amValue }
                                    .border(
                                        1.dp,
                                        if (isAm == amValue) Color.Transparent else Color(0xFFD1D5DB),
                                        RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Text(
                                    label,
                                    color = if (isAm == amValue) Color.White else Color(0xFF6B7280),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            "Repeat On",
            color = SMSecondary,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Days of week
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            daysOfWeek.forEach { day ->
                val isSelected = selectedDays.contains(day)
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) SMPrimary else Color(0xFFF3F0FF))
                        .border(
                            1.dp,
                            if (isSelected) Color.Transparent else Color(0xFFDDD6FE),
                            CircleShape
                        )
                        .clickable {
                            if (isSelected) selectedDays.remove(day)
                            else selectedDays.add(day)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        day.first().toString(),
                        color = if (isSelected) Color.White else SMSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Day labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    day,
                    color = Color(0xFF9CA3AF),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(42.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SMPrimary)
        ) {
            Text(
                "Save Reminder",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("No Thanks", color = Color(0xFF9CA3AF), fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
