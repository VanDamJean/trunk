package com.yacoo.rpg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.game.RewardChoice
import com.yacoo.rpg.game.RunState
import com.yacoo.rpg.game.*
import com.yacoo.rpg.ui.components.*
import com.yacoo.rpg.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RewardPickScreen(
    run: RunState,
    language: AppLanguage = AppLanguage.KOREAN,
    onPickReward: (RewardChoice) -> Unit,
    modifier: Modifier = Modifier
) {
    val labels = rewardPickLabels(language)
    val choices = run.pendingReward ?: emptyList()

    DarkOverlayPanel(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                SunburstBackground(modifier = Modifier.size(240.dp))
                
                Column(
                    modifier = Modifier.floatBobbing(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GameIcon(GameIconRole.REWARD, fontSize = 80f)
                    Text(
                        labels.title,
                        style      = GameTypography.screenTitle,
                        color      = Color(0xFFFFD43F), // Gold Color
                        fontSize   = 40.sp,
                        modifier   = Modifier.pulseGlow()
                    )
                    Text(
                        labels.subtitle,
                        style = GameTypography.bodyText,
                        color = Color(0xFFFFFDF9),
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            if (choices.isEmpty()) {
                Text(labels.empty, color = Color(0xFFFFFDF9), style = GameTypography.bodyText)
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    choices.forEachIndexed { idx, reward ->
                        key(idx) {
                            RewardCard(
                                reward = reward, 
                                labels = labels, 
                                onPick = { onPickReward(reward) },
                                modifier = Modifier.staggerSlideIn(delayMs = idx * 100)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardCard(reward: RewardChoice, labels: RewardPickLabels, onPick: () -> Unit, modifier: Modifier = Modifier) {
    val cardShape = RoundedCornerShape(20.dp)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, end = 8.dp)
            .bounceIn()
            .floatBobbing(amount = 4.dp, durationMs = 3000)
            .cartoonShadow(shadowOffset = 6.dp, color = ColorInk, shape = cardShape)
            .cartoonBorder(strokeWidth = 3.dp, color = ColorSecondaryTop, shape = cardShape)
            .clip(cardShape)
            .background(ColorChrome)
            .clickable(role = Role.Button, onClick = onPick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconBgShape = RoundedCornerShape(12.dp)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .cartoonBorder(2.dp, ColorInk, iconBgShape)
                    .clip(iconBgShape)
                    .background(ColorItemPurpleLight),
                contentAlignment = Alignment.Center
            ) {
                GameIcon(rewardIcon(reward.kind), fontSize = 40f)
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    rewardLabel(reward, labels),
                    style      = GameTypography.statValue,
                    color      = Color(0xFFFFFDF9),
                    fontWeight = FontWeight.Black
                )
                Text(
                    rewardDescription(reward.kind, labels),
                    style = GameTypography.caption,
                    color = ColorTextSecondary,
                    fontWeight = FontWeight.Black
                )
            }
            
            Box(
                modifier = Modifier
                    .cartoonBorder(2.dp, ColorInk, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(ColorSecondaryBottom)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    labels.pick, 
                    fontWeight = FontWeight.Black, 
                    color = ColorInk, 
                    fontSize = 12.sp
                )
            }
        }
    }
}

private data class RewardPickLabels(
    val title: String,
    val subtitle: String,
    val empty: String,
    val pick: String,
    val heal: String,
    val scrap: String,
    val dice: String,
    val reroll: String,
    val reward: String,
    val healDesc: String,
    val scrapDesc: String,
    val diceDesc: String,
    val rerollDesc: String
)

private fun rewardPickLabels(language: AppLanguage): RewardPickLabels = when (language) {
    AppLanguage.KOREAN -> RewardPickLabels("보상 선택", "하나를 선택하세요", "보상 없음", "선택", "회복", "스크랩", "주사위", "리롤", "보상", "현재 HP를 회복합니다", "강화 재료를 획득합니다", "주사위 개수가 영구적으로 증가합니다", "리롤 횟수가 영구적으로 증가합니다")
    AppLanguage.ENGLISH -> RewardPickLabels("Choose Reward", "Pick one reward", "No rewards", "Pick", "Heal", "Scrap", "Dice", "Reroll", "Reward", "Recover current HP", "Gain upgrade materials", "Increase dice count for this run", "Increase rerolls for this run")
}

private fun rewardIcon(kind: RewardKind): GameIconRole = when (kind) {
    RewardKind.HEAL   -> GameIconRole.HEAL
    RewardKind.SCRAP  -> GameIconRole.SCRAP
    RewardKind.DICE   -> GameIconRole.DICE
    RewardKind.REROLL -> GameIconRole.RESET
}

private fun rewardLabel(r: RewardChoice, labels: RewardPickLabels): String = when (r.kind) {
    RewardKind.HEAL   -> "${labels.heal} +${r.amount} HP"
    RewardKind.SCRAP  -> "${labels.scrap} +${r.amount}"
    RewardKind.DICE   -> "${labels.dice} +${r.amount}"
    RewardKind.REROLL -> "${labels.reroll} +${r.amount}"
}

private fun rewardDescription(kind: RewardKind, labels: RewardPickLabels): String = when (kind) {
    RewardKind.HEAL   -> labels.healDesc
    RewardKind.SCRAP  -> labels.scrapDesc
    RewardKind.DICE   -> labels.diceDesc
    RewardKind.REROLL -> labels.rerollDesc
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun RewardPickScreenPreview() {
    val run = createRun(createDefaultMeta()) { 0.5 }
    RewardPickScreen(
        run = run.copy(pendingReward = generateNodeReward(NodeType.TREASURE) { 0.5 }),
        onPickReward = {}
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812, name = "Reward Pick Empty")
@Composable
private fun RewardPickScreenEmptyPreview() {
    RewardPickScreen(
        run = createRun(createDefaultMeta()) { 0.2 }.copy(pendingReward = emptyList()),
        language = AppLanguage.ENGLISH,
        onPickReward = {}
    )
}
