package com.yacoo.rpg.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.game.CombatOutcome
import com.yacoo.rpg.game.CombatResult
import com.yacoo.rpg.game.*
import com.yacoo.rpg.ui.components.*
import com.yacoo.rpg.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ResultScreen(
    result: CombatResult?,
    language: AppLanguage = AppLanguage.ENGLISH,
    onClaimReward: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (result == null) {
        Column(
            modifier = modifier.fillMaxSize().background(ColorScreenBg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("No result yet", color = ColorTextSecondary, style = GameTypography.bodyText)
        }
        return
    }

    val labels = resultLabels(language)
    val isWin   = result.outcome == CombatOutcome.WIN
    val headline = if (isWin) labels.victory else labels.defeat
    
    // We use DarkOverlayPanel to darken the background
    DarkOverlayPanel(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            
            // Header with Sunburst + Floating text
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isWin) {
                    SunburstBackground(modifier = Modifier.size(300.dp))
                }
                
                Column(
                    modifier = Modifier.floatBobbing(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GameIcon(
                        icon = if (isWin) GameIconRole.VICTORY else GameIconRole.DEFEAT, 
                        fontSize = 100f
                    )
                    Text(
                        text       = headline,
                        style      = GameTypography.screenTitle,
                        color      = if (isWin) ColorPrimaryTop else ColorDangerTop,
                        fontSize   = 48.sp,
                        modifier   = Modifier.pulseGlow()
                    )
                }
            }

            // Central Reward Board
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp, end = 6.dp)
                    .bounceIn()
                    .cartoonShadow(shadowOffset = 6.dp, color = ColorInk, shape = RoundedCornerShape(24.dp))
                    .cartoonBorder(strokeWidth = 3.dp, color = ColorInk, shape = RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(ColorSurfacePanel)
                    .padding(20.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RewardBanner(title = labels.stageTitle(result.stage, isWin))
                    
                    // Glowing Central Score
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .staggerSlideIn(delayMs = 100)
                            .pulseGlow()
                    ) {
                        GameIcon(icon = GameIconRole.GOLD, fontSize = 48f)
                        Text(
                            text = "+${result.coinsEarned}",
                            color = ColorPrimaryTop,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    
                    result.handUsed?.let { ResultRow(labels.hand, it.label, modifier = Modifier.staggerSlideIn(delayMs = 200)) }
                    
                    result.duplicateItemName?.let {
                        ResultRow(labels.bonus, "+20 (${labels.duplicate} $it)", modifier = Modifier.staggerSlideIn(delayMs = 300))
                    }
                }
            }

            GameButton(
                text    = labels.claim,
                onClick = onClaimReward,
                variant = GameButtonVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth().height(64.dp)
            )
        }
    }
}

private data class ResultLabels(val victory: String, val defeat: String, val stage: String, val coins: String, val hand: String, val bonus: String, val duplicate: String, val claim: String, val stageTitle: (Int, Boolean) -> String)

private fun resultLabels(language: AppLanguage): ResultLabels = when (language) {
    AppLanguage.KOREAN -> ResultLabels("승리!", "패배...", "스테이지", "코인", "족보", "보너스", "중복", "보상 받기") { stage, win -> if (win) "스테이지 $stage 클리어" else "스테이지 $stage" }
    AppLanguage.ENGLISH -> ResultLabels("Victory!", "Defeat...", "Stage", "Coins", "Hand", "Bonus", "duplicate", "Claim Reward") { stage, win -> if (win) "Stage $stage Clear" else "Stage $stage" }
}

@Composable
private fun ResultRow(label: String, value: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .cartoonBorder(2.dp, ColorPanelBrownLight, shape)
            .clip(shape)
            .background(ColorChrome)
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
            style = GameTypography.statValue,
            color = ColorTextOnDark, 
            fontWeight = FontWeight.Black
        )
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun ResultScreenWinPreview() {
    ResultScreen(
        result = CombatResult(
            outcome           = CombatOutcome.WIN,
            stage             = 3,
            coinsEarned       = 60,
            handUsed          = YahtzeeAttackCategory.YAHTZEE,
            duplicateItemName = "Twig Wand"
        ),
        onClaimReward = {}
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun ResultScreenLossPreview() {
    ResultScreen(
        result = CombatResult(CombatOutcome.LOSS, 1, coinsEarned = 10),
        onClaimReward = {}
    )
}
