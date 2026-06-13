package com.silentmoon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silentmoon.app.ui.theme.SMAccent
import com.silentmoon.app.ui.theme.SMBackgroundLight
import com.silentmoon.app.ui.theme.SMPrimary
import com.silentmoon.app.ui.theme.SMSecondary

data class OnboardingPage(
    val icon: String,
    val title: String,
    val description: String
)

val onboardingPages = listOf(
    OnboardingPage(
        icon = "🧘",
        title = "Find Your Peace",
        description = "Discover guided meditations and mindfulness practices designed to help you reduce stress and find inner calm."
    ),
    OnboardingPage(
        icon = "🌙",
        title = "Sleep Better",
        description = "Drift into restful sleep with soothing sleep stories, calming music, and bedtime meditations crafted for deep rest."
    ),
    OnboardingPage(
        icon = "🌱",
        title = "Grow Every Day",
        description = "Build a daily meditation habit and track your progress as you develop focus, happiness, and personal growth."
    )
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) }
    val page = onboardingPages[currentPage]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(SMBackgroundLight, Color(0xFFEDE9FE))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Icon area
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(SMPrimary.copy(alpha = 0.15f), SMAccent.copy(alpha = 0.05f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(page.icon, fontSize = 72.sp)
            }

            // Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // App name
                Text(
                    "Silent Moon",
                    color = SMPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp
                )

                Text(
                    page.title,
                    color = SMSecondary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )

                Text(
                    page.description,
                    color = Color(0xFF6B7280),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            // Page indicator dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                onboardingPages.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentPage) 10.dp else 7.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == currentPage) SMPrimary
                                else SMPrimary.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            // Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (currentPage < onboardingPages.size - 1) {
                            currentPage++
                        } else {
                            onFinish()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SMPrimary)
                ) {
                    Text(
                        if (currentPage < onboardingPages.size - 1) "Next" else "Get Started",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                if (currentPage > 0) {
                    TextButton(
                        onClick = { currentPage-- },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Back",
                            color = SMPrimary.copy(alpha = 0.7f),
                            fontSize = 15.sp
                        )
                    }
                } else {
                    TextButton(
                        onClick = onFinish,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Skip",
                            color = Color(0xFF9CA3AF),
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
