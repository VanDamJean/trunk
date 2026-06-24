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

    // Deep dim overlay
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xEE090612)) // Very dark overlay
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Neon Glow Title Area
            Text(
                text = labels.title,
                style = GameTypography.screenTitle,
                color = Color(0xFFB75CFF), // Neon Purple
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.pulseGlow()
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = labels.subtitle,
                style = GameTypography.bodyText,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(32.dp))

            if (choices.isEmpty()) {
                Text(labels.empty, color = Color.White, style = GameTypography.bodyText)
            } else {
                // 3 Card Density Layout
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    choices.take(3).forEachIndexed { idx, reward ->
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
    val cardShape = RoundedCornerShape(16.dp)
    
    // Obsidian / Dark Metal Base
    val cardBg = Color(0xFF16151A)
    
    val neonColor = when(reward.kind) {
        RewardKind.HEAL -> Color(0xFF88D84A) // Neon Green
        RewardKind.DICE, RewardKind.REROLL -> Color(0xFF45E8FF) // Neon Cyan
        RewardKind.SCRAP -> Color(0xFFFFD43F) // Neon Gold
        else -> Color(0xFFB75CFF)
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .cartoonBorder(strokeWidth = 3.dp, color = neonColor, shape = cardShape) // Neon glowing border
            .clip(cardShape)
            .background(cardBg)
            .clickable(role = Role.Button, onClick = onPick)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Large Neon Glowing Icon (No solid background)
            Box(
                modifier = Modifier.size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                GameIcon(rewardIcon(reward.kind), fontSize = 60f, tint = neonColor)
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    rewardLabel(reward, labels),
                    style = GameTypography.statValue,
                    color = neonColor, // Title matches neon color
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    rewardDescription(reward.kind, labels),
                    style = GameTypography.caption,
                    color = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(Modifier.height(8.dp))
                // Neon Star/Value indicators
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(if (reward.kind == RewardKind.HEAL) 1 else reward.amount) {
                        Text("✦", color = neonColor, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

private data class RewardPickLabels(
    val title: String,
    val subtitle: String,
    val empty: String,
    val heal: String,
    val scrap: String,
    val dice: String,
    val reroll: String,
    val healDesc: String,
    val scrapDesc: String,
    val diceDesc: String,
    val rerollDesc: String
)

private fun rewardPickLabels(language: AppLanguage): RewardPickLabels = when (language) {
    AppLanguage.KOREAN -> RewardPickLabels("보상 선택", "하나를 선택하세요", "보상 없음", "회복", "스크랩", "주사위", "리롤", "현재 HP를 회복합니다", "강화 재료를 획득합니다", "주사위 개수가 영구적으로 증가합니다", "리롤 횟수가 영구적으로 증가합니다")
    AppLanguage.ENGLISH -> RewardPickLabels("Choose Reward", "Pick one reward", "No rewards", "Heal", "Scrap", "Dice", "Reroll", "Recover current HP", "Gain upgrade materials", "Increase dice count for this run", "Increase rerolls for this run")
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
