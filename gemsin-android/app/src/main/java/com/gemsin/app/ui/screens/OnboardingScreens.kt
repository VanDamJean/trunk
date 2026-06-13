package com.gemsin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import com.gemsin.app.data.MockData

@Composable
private fun OnboardingLayout(
    pageIndex: Int,
    accentColor: Color,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    nextLabel: String = "❯"
) {
    val item = MockData.onboarding[pageIndex]
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFF0F3460))))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .padding(2.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(accentColor.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.badge,
                    color = accentColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color(0xFF16213E))
                    .padding(horizontal = 28.dp, vertical = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.title,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = item.description,
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onSkip) {
                        Text("Skip", color = Color.White.copy(alpha = 0.5f))
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(3) { i ->
                            Box(
                                modifier = Modifier
                                    .size(if (i == pageIndex) 20.dp else 8.dp, 8.dp)
                                    .clip(CircleShape)
                                    .background(if (i == pageIndex) accentColor else Color.White.copy(alpha = 0.3f))
                            )
                        }
                    }

                    FilledIconButton(
                        onClick = onNext,
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = accentColor)
                    ) {
                        Text(nextLabel, color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingScreen1(onNext: () -> Unit, onSkip: () -> Unit) {
    OnboardingLayout(0, Color(0xFF7C4DFF), onNext, onSkip)
}

@Composable
fun OnboardingScreen2(onNext: () -> Unit, onSkip: () -> Unit) {
    OnboardingLayout(1, Color(0xFF00BCD4), onNext, onSkip)
}

@Composable
fun OnboardingScreen3(onDone: () -> Unit) {
    OnboardingLayout(2, Color(0xFF4CAF50), onDone, onDone, nextLabel = "✓")
}
