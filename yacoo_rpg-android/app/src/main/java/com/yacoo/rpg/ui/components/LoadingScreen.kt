package com.yacoo.rpg.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.R
import com.yacoo.rpg.game.AppLanguage
import com.yacoo.rpg.ui.theme.*

@Composable
fun LoadingScreen(
    language: AppLanguage = AppLanguage.ENGLISH,
    modifier: Modifier = Modifier
) {
    // Infinite loading progress simulation
    val infiniteTransition = rememberInfiniteTransition(label = "loadingProgress")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progressVal"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B18)),
        contentAlignment = Alignment.Center
    ) {
        // Collage Loading Image Wallpaper
        Image(
            painter = painterResource(id = R.drawable.bg_loading_title),
            contentDescription = "Loading Wallpaper",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Title and Progress layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "YACOO RPG",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = ColorTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = if (language == AppLanguage.KOREAN) "모험장에 진입하는 중..." else "Entering adventure...",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Chunky progress bar
            val progressShape = RoundedCornerShape(10.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(16.dp)
                    .cartoonBorder(2.dp, ColorInk, progressShape)
                    .clip(progressShape)
                    .background(Color(0xFF090612))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(
                            Brush.horizontalGradient(
                                listOf(ColorSecondaryTop, ColorSecondaryBottom)
                            )
                        )
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun LoadingScreenPreview() {
    LoadingScreen()
}
