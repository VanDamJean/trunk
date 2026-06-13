package com.yacoo.rpg.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.game.MetaSave
import com.yacoo.rpg.game.*
import com.yacoo.rpg.ui.components.*
import com.yacoo.rpg.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RunResultScreen(
    meta: MetaSave,
    language: AppLanguage = AppLanguage.KOREAN,
    onReturnHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val labels = runResultLabels(language)
    
    DarkOverlayPanel(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            // Defeat Icon with bounce
            Column(
                modifier = Modifier.floatBobbing(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SunburstBackground(modifier = Modifier.size(220.dp))
                GameIcon(GameIconRole.DEFEAT, fontSize = 92f)
                Text(
                    labels.title,
                    style      = GameTypography.screenTitle,
                    color      = ColorDangerTop,
                    fontSize   = 48.sp,
                    modifier   = Modifier.pulseGlow()
                )
            }
            
            Spacer(Modifier.height(40.dp))

            // Stat Board
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp, end = 6.dp)
                    .cartoonShadow(shadowOffset = 6.dp, color = ColorInk, shape = RoundedCornerShape(24.dp))
                    .cartoonBorder(strokeWidth = 3.dp, color = ColorInk, shape = RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(ColorSurfacePanel)
                    .padding(24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RewardBanner(title = if (language == AppLanguage.KOREAN) "모험 기록" else "Run Summary")
                    
                    RunStatRow(labels.bestChapter, "${labels.chapter} ${meta.bestChapter}")
                    RunStatRow(labels.totalRuns,  "${meta.totalRuns}${labels.runSuffix}")
                    RunStatRow(labels.coins, "${meta.coins} ${GameIconRole.GOLD.fallback}")
                }
            }

            Spacer(Modifier.height(40.dp))

            GameButton(
                text    = "${GameIconRole.HOME.fallback} ${labels.returnHome}",
                onClick = onReturnHome,
                variant = GameButtonVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth().height(64.dp)
            )
        }
    }
}

private data class RunResultLabels(val title: String, val bestChapter: String, val totalRuns: String, val runSuffix: String, val coins: String, val returnHome: String, val chapter: String)

private fun runResultLabels(language: AppLanguage): RunResultLabels = when (language) {
    AppLanguage.KOREAN -> RunResultLabels("런 종료", "최고 챕터", "총 런 수", "회", "보유 코인", "홈으로 돌아가기", "챕터")
    AppLanguage.ENGLISH -> RunResultLabels("Run Ended", "Best Chapter", "Total Runs", "", "Coins", "Return Home", "Chapter")
}

@Composable
private fun RunStatRow(label: String, value: String) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .cartoonBorder(2.dp, ColorPanelBrownLight, shape)
            .clip(shape)
            .background(Color(0xFF22172E))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label, 
            color = ColorSecondaryTop, 
            style = GameTypography.bodyText,
            fontWeight = FontWeight.Black
        )
        Text(
            text = value, 
            fontWeight = FontWeight.Black, 
            color = ColorTextOnDark, 
            style = GameTypography.statValue
        )
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun RunResultScreenPreview() {
    RunResultScreen(
        meta = createDefaultMeta().copy(bestChapter = 4, totalRuns = 12, coins = 540),
        onReturnHome = {}
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812, name = "Run Result English")
@Composable
private fun RunResultScreenEnglishPreview() {
    RunResultScreen(
        meta = createDefaultMeta().copy(bestChapter = 17, totalRuns = 32, coins = 13_500),
        language = AppLanguage.ENGLISH,
        onReturnHome = {}
    )
}
