package com.yacoo.rpg.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.R
import com.yacoo.rpg.game.*
import com.yacoo.rpg.ui.components.*
import com.yacoo.rpg.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RunMapScreen(
    run: RunState,
    language: AppLanguage = AppLanguage.KOREAN,
    onStartNode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val labels = runMapLabels(language)
    val nodes    = run.map.nodes
    val current  = run.nodeIndex
    val bottomContentClearance = bottomNavContentClearance()

    DarkOverlayPanel(modifier = modifier) {
        // Modal Container
        Box(
            modifier = Modifier
                .padding(bottom = bottomContentClearance)
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .cartoonShadow(8.dp, ColorInk, RoundedCornerShape(24.dp))
                .cartoonBorder(4.dp, ColorInk, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(ColorSurfacePanel) // Background underneath parchment
        ) {
            // Layer 1: Parchment paper background cropped and filled to the modal
            Image(
                painter = painterResource(id = R.drawable.bg_map_parchment),
                contentDescription = "Parchment Map Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().alpha(0.9f)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    val headerShape = RoundedCornerShape(20.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp, end = 6.dp)
                            .cartoonShadow(shadowOffset = 5.dp, color = ColorInk, shape = headerShape)
                            .cartoonBorder(strokeWidth = 3.dp, color = ColorInk, shape = headerShape)
                            .clip(headerShape)
                            .background(ColorSurfacePanel)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(
                                    modifier = Modifier
                                        .cartoonBorder(2.dp, ColorInk, RoundedCornerShape(8.dp))
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ColorDangerTop)
                                        .padding(horizontal = 10.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = labels.mapTitle,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = ColorTextOnDark
                                    )
                                }
                                Text(
                                    text = "${labels.chapter} ${run.chapter}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ColorInk
                                )
                                Text(
                                    text = "${labels.hp} ${run.hp} / ${run.maxHp}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ColorTextSecondary
                                )
                            }
                            Box(modifier = Modifier.width(140.dp)) {
                                ChunkyProgressBar(
                                    progress = run.hp.toFloat() / run.maxHp.coerceAtLeast(1),
                                    colorStart = ColorHpFillStart,
                                    colorEnd = ColorHpFillEnd
                                )
                            }
                        }
                    }
                }

                itemsIndexed(nodes, key = { idx, _ -> idx }) { index, node ->
                    val isCurrent = index == current
                    val isCleared = index < current
                    val isFuture  = index > current
                    val isBoss    = node.type == NodeType.BOSS

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(if (isBoss) 64.dp else 56.dp)
                                    .cartoonShadow(shadowOffset = if (isCurrent) 4.dp else 2.dp, color = ColorInk, shape = CircleShape)
                                    .cartoonBorder(
                                        strokeWidth = if (isCurrent || isBoss) 3.dp else 2.dp,
                                        color = if (isCurrent) ColorPrimaryBottom else if (isBoss) ColorDangerTop else ColorInk,
                                        shape = CircleShape
                                    )
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCleared -> ColorDisabled
                                            isBoss -> Color(0xFF3B1E2B)
                                            isCurrent -> ColorPrimaryTop
                                            else -> ColorChrome
                                        }
                                    )
                                    .then(if (isBoss && !isCleared) Modifier.pulseGlow() else Modifier),
                                contentAlignment = Alignment.Center
                            ) {
                                GameIcon(
                                    icon = nodeIcon(node.type),
                                    fontSize = if (isBoss) 32f else 24f,
                                    tint = if (isCurrent || isBoss) Color(0xFFFFFDF9) else ColorInk
                                )
                            }

                            val nodeCardShape = RoundedCornerShape(16.dp)
                            val cardBg = when {
                                isCurrent -> ColorCreamWarm
                                isCleared -> ColorDisabled
                                isBoss -> Color(0xFF4C2738)
                                else -> Color(0xFFFFFDF9)
                            }
                            val cardBorderColor = if (isCurrent) ColorPrimaryBottom else if (isBoss) ColorDangerTop else ColorInk

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(bottom = 4.dp, end = 4.dp)
                                    .cartoonShadow(shadowOffset = 4.dp, color = ColorInk, shape = nodeCardShape)
                                    .cartoonBorder(strokeWidth = 3.dp, color = cardBorderColor, shape = nodeCardShape)
                                    .clip(nodeCardShape)
                                    .background(cardBg)
                                    .padding(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = nodeLabel(node.type, labels),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = if (isFuture && !isBoss) ColorInkSoft else if (isBoss) Color(0xFFFFFDF9) else ColorInk
                                        )
                                        Text(
                                            text = when {
                                                isCleared -> labels.cleared
                                                isCurrent -> labels.current
                                                isFuture -> labels.locked
                                                else -> ""
                                            },
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isBoss && !isCleared) ColorDangerBottom else ColorTextSecondary
                                        )
                                        if (isCurrent) {
                                            Text(
                                                text = labels.tapHint,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = ColorPrimaryBottom
                                            )
                                        }
                                    }

                                    if (isCleared) {
                                        GameIcon(icon = GameIconRole.CONFIRM, fontSize = 20f, tint = ColorPrimaryBottom)
                                    } else if (isCurrent) {
                                        Box(
                                            modifier = Modifier
                                                .cartoonBorder(1.5.dp, ColorInk, RoundedCornerShape(8.dp))
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(ColorSecondaryBottom)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(labels.start, color = ColorInk, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }
                        }

                        if (index < nodes.size - 1) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 25.dp)
                                    .height(30.dp)
                                    .width(6.dp)
                                    .cartoonBorder(1.5.dp, ColorInk, RoundedCornerShape(3.dp))
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (isCleared) ColorPrimaryTop else ColorInkSoft)
                                    .then(if (isCleared) Modifier.pulseGlow() else Modifier)
                            )
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(16.dp))

                    if (current < nodes.size) {
                        GameButton(
                            text = "${nodeIcon(nodes[current].type).fallback} ${nodeLabel(nodes[current].type, labels)} ${labels.start}",
                            onClick = onStartNode,
                            variant = GameButtonVariant.PRIMARY,
                            modifier = Modifier.fillMaxWidth().height(60.dp)
                        )
                    } else {
                        val completeShape = RoundedCornerShape(16.dp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .cartoonBorder(strokeWidth = 3.dp, color = ColorInk, shape = completeShape)
                                .clip(completeShape)
                                .background(ColorPrimaryBottom)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = labels.complete,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                color = ColorInk,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun nodeIcon(type: NodeType): GameIconRole = when (type) {
    NodeType.BATTLE   -> GameIconRole.ATTACK
    NodeType.ELITE    -> GameIconRole.DEFEAT
    NodeType.TREASURE -> GameIconRole.TREASURE
    NodeType.REST     -> GameIconRole.HEAL
    NodeType.BOSS     -> GameIconRole.BOSS
}

private data class RunMapLabels(val battle: String, val elite: String, val treasure: String, val rest: String, val boss: String, val cleared: String, val current: String, val locked: String, val start: String, val complete: String, val mapTitle: String, val tapHint: String, val chapter: String, val hp: String)

private fun runMapLabels(language: AppLanguage): RunMapLabels = when (language) {
    AppLanguage.KOREAN -> RunMapLabels("전투", "엘리트 전투", "보물", "휴식", "보스", "완료", "현재 위치", "미개방", "진행", "챕터 완료! 다음 챕터로 진행합니다…", "대탐험 지도", "현재 노드 진행 가능", "챕터", "HP")
    AppLanguage.ENGLISH -> RunMapLabels("Battle", "Elite Battle", "Treasure", "Rest", "Boss", "Cleared", "Current", "Locked", "Start", "Chapter complete! Moving to the next chapter…", "Adventure Map", "Current node ready", "Chapter", "HP")
}

private fun nodeLabel(type: NodeType, labels: RunMapLabels): String = when (type) {
    NodeType.BATTLE   -> labels.battle
    NodeType.ELITE    -> labels.elite
    NodeType.TREASURE -> labels.treasure
    NodeType.REST     -> labels.rest
    NodeType.BOSS     -> labels.boss
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun RunMapScreenPreview() {
    RunMapScreen(
        run = createRun(createDefaultMeta()) { 0.5 },
        onStartNode = {}
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812, name = "Run Map English")
@Composable
private fun RunMapScreenEnglishPreview() {
    RunMapScreen(
        run = createRun(createDefaultMeta()) { 0.9 }.copy(nodeIndex = 2),
        language = AppLanguage.ENGLISH,
        onStartNode = {}
    )
}
