package com.community.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.community.app.ui.components.AppHeader

@Composable
fun PlaceholderScreen(
    title: String,
    subtitle: String = "이 화면은 데모용 플레이스홀더입니다.",
    onNavigate: (String) -> Unit = {},
    bottomBar: @Composable () -> Unit,
) {
    Scaffold(
        topBar = { AppHeader(title = title) },
        bottomBar = bottomBar,
        containerColor = Color.White,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            contentAlignment = Alignment.TopStart,
        ) {
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                lineHeight = 22.sp,
                textAlign = TextAlign.Start,
            )
        }
    }
}
