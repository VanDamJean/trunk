package com.silentmoon.app.ui.screens

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silentmoon.app.data.MockData
import com.silentmoon.app.ui.theme.SMAccent
import com.silentmoon.app.ui.theme.SMBackgroundLight
import com.silentmoon.app.ui.theme.SMPrimary
import com.silentmoon.app.ui.theme.SMSecondary
import kotlinx.coroutines.delay

@Composable
fun MeditationPlayerScreen(sessionId: Int, onBack: () -> Unit) {
    val session = MockData.getSessionById(sessionId) ?: MockData.meditationSessions.first()

    val totalSeconds = remember(session.duration) {
        (session.duration.split(" ").firstOrNull()?.toIntOrNull() ?: 10) * 60
    }

    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }

    // MediaPlayer — 세션별 오디오 재생, 컴포저블 라이프사이클과 연동
    val context = LocalContext.current
    val mediaPlayer = remember(session.id) {
        MediaPlayer.create(context, session.audioRes).apply {
            isLooping = true
        }
    }

    DisposableEffect(session.id) {
        onDispose {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    // 재생 상태에 따라 오디오 시작/정지
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            mediaPlayer.start()
            while (isPlaying && elapsedSeconds < totalSeconds) {
                delay(1000L)
                if (isPlaying) elapsedSeconds++
            }
            if (elapsedSeconds >= totalSeconds) {
                isPlaying = false
                mediaPlayer.pause()
            }
        } else {
            if (mediaPlayer.isPlaying) mediaPlayer.pause()
        }
    }

    val progress = if (totalSeconds > 0) elapsedSeconds.toFloat() / totalSeconds else 0f
    val remainSec = totalSeconds - elapsedSeconds

    val isSleepSession = session.category == "evening"
    val bgBrush = if (isSleepSession)
        Brush.verticalGradient(listOf(Color(0xFF0D0B1E), Color(0xFF1A1740)))
    else
        Brush.verticalGradient(listOf(Color(0xFFF3F0FF), SMBackgroundLight))
    val onBg = if (isSleepSession) Color.White else SMSecondary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),  // 1:1 비율에서도 재생 버튼 접근 가능
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            // 상단 바
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Text("←", fontSize = 22.sp, color = onBg)
                }
                Text(
                    session.category.replaceFirstChar { it.uppercase() },
                    color = onBg.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                IconButton(onClick = { isFavorite = !isFavorite }) {
                    Text(if (isFavorite) "❤️" else "🤍", fontSize = 22.sp)
                }
            }

            Spacer(Modifier.height(36.dp))

            // 아이콘 원형
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(SMPrimary.copy(alpha = 0.25f), SMAccent.copy(alpha = 0.08f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(SMPrimary.copy(alpha = 0.35f), SMPrimary.copy(alpha = 0.12f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(session.icon, fontSize = 60.sp, textAlign = TextAlign.Center)
                }
            }

            Spacer(Modifier.height(28.dp))

            // 세션 정보
            Text(
                session.title,
                color = onBg,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                session.description,
                color = onBg.copy(alpha = 0.6f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(32.dp))

            // 타이머
            Text(
                "%02d:%02d".format(remainSec / 60, remainSec % 60),
                color = SMPrimary,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(16.dp))

            // 프로그레스 바
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = SMPrimary,
                    trackColor = SMPrimary.copy(alpha = 0.2f)
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "%02d:%02d".format(elapsedSeconds / 60, elapsedSeconds % 60),
                        color = onBg.copy(alpha = 0.4f), fontSize = 11.sp
                    )
                    Text(session.duration, color = onBg.copy(alpha = 0.4f), fontSize = 11.sp)
                }
            }

            Spacer(Modifier.height(36.dp))

            // 재생 컨트롤
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // -15초
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(SMPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { elapsedSeconds = maxOf(0, elapsedSeconds - 15) }) {
                        Text("⏪", fontSize = 20.sp)
                    }
                }

                // 재생/일시정지
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(SMPrimary, SMAccent))),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        if (elapsedSeconds >= totalSeconds) elapsedSeconds = 0
                        isPlaying = !isPlaying
                    }) {
                        Text(if (isPlaying) "⏸" else "▶", fontSize = 28.sp, color = Color.White)
                    }
                }

                // +15초
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(SMPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { elapsedSeconds = minOf(totalSeconds, elapsedSeconds + 15) }) {
                        Text("⏩", fontSize = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            TextButton(onClick = { elapsedSeconds = 0; isPlaying = false }) {
                Text("Reset Session", color = onBg.copy(alpha = 0.4f), fontSize = 13.sp)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
