package com.yacoo.rpg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.game.*
import com.yacoo.rpg.ui.components.*
import com.yacoo.rpg.ui.theme.*

@Composable
fun HomeScreen(
    meta: MetaSave,
    heroStats: HeroStats,
    language: AppLanguage = AppLanguage.ENGLISH,
    onLanguageChange: (AppLanguage) -> Unit = {},
    onStartCombat: () -> Unit,
    onNavigate: (com.yacoo.rpg.game.Screen) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLanguageDialogOpen by remember { mutableStateOf(false) }
    val labels = homeLabels(language)

    if (isLanguageDialogOpen) {
        HomeLanguageDialog(
            selectedLanguage = language,
            closeLabel = labels.close,
            onSelectLanguage = { lang ->
                onLanguageChange(lang)
                isLanguageDialogOpen = false
            },
            onDismiss = { isLanguageDialogOpen = false }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 78.dp, bottom = 110.dp)
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdventureTicket(labels = labels, chapter = meta.bestChapter, power = heroStats.power)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HomeSideButton(labels.quest, GameIconRole.TREASURE) { onNavigate(Screen.RUN_MAP) }
                    HomeSideButton(labels.shop, GameIconRole.CHEST) { onNavigate(Screen.GACHA) }
                    HomeSideButton(labels.gear, GameIconRole.GEAR) { onNavigate(Screen.EQUIPMENT) }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HomeSideButton(labels.upgrade, GameIconRole.UPGRADE) { onNavigate(Screen.UPGRADE) }
                    HomeSideButton(labels.language, GameIconRole.SETTINGS) { isLanguageDialogOpen = true }
                    HomeSideButton(labels.reset, GameIconRole.RESET, danger = true, onClick = onReset)
                }

                HeroLobbyStage(meta = meta, stageLabel = labels.stage)
            }

            HorizontalStatsBar(
                hp = heroStats.maxHp,
                attack = heroStats.power,
                defense = heroStats.defense,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            ModeShortcutRow(labels = labels, onNavigate = onNavigate)
            RankingPreview(labels = labels, heroStats = heroStats, meta = meta)

            GameButton(
                text = labels.startCombat,
                onClick = onStartCombat,
                variant = GameButtonVariant.SECONDARY,
                modifier = Modifier
                    .width(268.dp)
                    .height(70.dp)
                    .pulseGlow()
            )
        }
    }
}

@Composable
private fun FloatingSideButton(icon: GameIconRole, onClick: () -> Unit) {
    val shape = androidx.compose.foundation.shape.CircleShape
    Box(
        modifier = Modifier
            .size(48.dp)
            .cartoonShadow(3.dp, ColorInk, shape)
            .cartoonBorder(2.dp, ColorInk, shape)
            .clip(shape)
            .background(Color(0xFFFFFDF9))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        GameIcon(icon = icon, fontSize = 24f)
    }
}

@Composable
private fun AdventureTicket(labels: HomeLabels, chapter: Int, power: Int) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp, end = 4.dp)
            .cartoonShadow(4.dp, ColorInk, shape)
            .cartoonBorder(3.dp, ColorInk, shape)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(ColorCreamWarm, ColorParchment)))
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(labels.chapterTitle, color = ColorInk, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text(labels.chapterSubtitle(chapter), color = ColorPanelBrownDark, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
            Box(
                modifier = Modifier
                    .cartoonBorder(2.dp, ColorInk, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorChrome)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                    GameIcon(GameIconRole.POWER, fontSize = 18f)
                    Text(formatCompact(power), color = ColorTextOnDark, fontSize = 13.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun HeroLobbyStage(meta: MetaSave, stageLabel: String) {
    Box(
        modifier = Modifier
            .size(width = 230.dp, height = 250.dp)
            .floatBobbing(amount = 8.dp, durationMs = 2400),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 12.dp)
                .width(150.dp)
                .height(28.dp)
                .clip(CircleShape)
                .background(Color(0x33000000))
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 22.dp)
                .cartoonShadow(3.dp, ColorInk, RoundedCornerShape(14.dp))
                .cartoonBorder(3.dp, ColorInk, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.verticalGradient(listOf(ColorSecondaryTop, ColorSecondaryBottom)))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text("$stageLabel ${meta.bestChapter}", color = ColorInk, fontSize = 13.sp, fontWeight = FontWeight.Black)
        }
        HeroPaperdollCanvas(equipment = meta.equipment, size = 205.dp)
    }
}

@Composable
private fun HomeSideButton(label: String, icon: GameIconRole, danger: Boolean = false, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = Modifier
            .width(58.dp)
            .cartoonShadow(3.dp, ColorInk, shape)
            .cartoonBorder(2.dp, ColorInk, shape)
            .clip(shape)
            .background(if (danger) ColorDangerTop else ColorCard)
            .clickable { onClick() }
            .padding(vertical = 7.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            GameIcon(icon, fontSize = 26f)
            if (!danger) {
                Text(
                    "!",
                    modifier = Modifier
                        .offset(x = 7.dp, y = (-5).dp)
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(ColorDangerTop),
                    color = ColorTextOnDark,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
        Text(
            label,
            color = if (danger) ColorTextOnDark else ColorInk,
            fontSize = 9.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.Black,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ModeShortcutRow(labels: HomeLabels, onNavigate: (Screen) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ModeChip(labels.event, GameIconRole.STAR, Modifier.weight(1f)) { onNavigate(Screen.RUN_MAP) }
        ModeChip(labels.explore, GameIconRole.TREASURE, Modifier.weight(1f)) { onNavigate(Screen.COMBAT) }
        ModeChip(labels.challenge, GameIconRole.BATTLE, Modifier.weight(1f)) { onNavigate(Screen.UPGRADE) }
    }
}

@Composable
private fun ModeChip(text: String, icon: GameIconRole, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .height(54.dp)
            .cartoonShadow(3.dp, ColorInk, shape)
            .cartoonBorder(2.dp, ColorInk, shape)
            .clip(shape)
            .background(ColorCard.copy(alpha = 0.95f))
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GameIcon(icon, fontSize = 24f)
        Spacer(Modifier.width(4.dp))
        Text(text, color = ColorInk, fontSize = 11.sp, fontWeight = FontWeight.Black, maxLines = 2, textAlign = TextAlign.Center)
    }
}

@Composable
private fun RankingPreview(labels: HomeLabels, heroStats: HeroStats, meta: MetaSave) {
    val shape = RoundedCornerShape(18.dp)
    val rows = listOf(
        Triple("1", "seigom", "65.46M"),
        Triple("2", "Korean", "27.63M"),
        Triple("17", "Yacoo", formatCompact(heroStats.power + meta.bestChapter * 1000))
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .cartoonShadow(4.dp, ColorInk, shape)
            .cartoonBorder(3.dp, ColorInk, shape)
            .clip(shape)
            .background(ColorSurfacePanel)
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .cartoonBorder(2.dp, ColorInk, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(ColorDangerTop)
                    .padding(horizontal = 22.dp, vertical = 4.dp)
            ) {
                Text(labels.rankingTitle, color = ColorTextOnDark, fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
            rows.forEach { (rank, name, score) ->
                RankingRow(rank = rank, name = name, score = score, highlight = name == "Yacoo")
            }
        }
    }
}

@Composable
private fun RankingRow(rank: String, name: String, score: String, highlight: Boolean) {
    val rowShape = RoundedCornerShape(11.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .cartoonBorder(1.5.dp, ColorInkSoft, rowShape)
            .clip(rowShape)
            .background(if (highlight) Color(0xFFD8F7FF) else ColorParchmentLight)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(rank, color = ColorInk, fontSize = 15.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(26.dp), textAlign = TextAlign.Center)
        GameIcon(GameIconRole.PLAYER_AVATAR, fontSize = 22f)
        Text(name, color = ColorInk, fontSize = 13.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
        GameIcon(GameIconRole.STAR, fontSize = 15f)
        Text(score, color = ColorPanelBrownDark, fontSize = 12.sp, fontWeight = FontWeight.Black)
    }
}

private fun formatCompact(value: Int): String = when {
    value >= 1_000_000 -> "${value / 100_000 / 10f}M"
    value >= 1_000 -> "${value / 100 / 10f}K"
    else -> value.toString()
}

@Composable
private fun CanvasBackground() {
    // A simple placeholder background since we lack the PNG
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-30).dp, y = 80.dp)
                .size(80.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(ColorSun)
        )
    }
}

val ColorSun = Color(0xFFFFEA6C)

@Composable
private fun HomeLanguageDialog(
    selectedLanguage: AppLanguage,
    closeLabel: String,
    onSelectLanguage: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ColorSurfacePanel,
        modifier = Modifier
            .cartoonBorder(strokeWidth = 3.dp, color = ColorInk, shape = shape)
            .clip(shape),
        title = {
            Text(
                "언어 / Language",
                style = GameTypography.sectionTitle,
                color = ColorTextPrimary,
                fontWeight = FontWeight.Black
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LanguageChoiceButton(
                    text = "한국어",
                    selected = selectedLanguage == AppLanguage.KOREAN,
                    onClick = { onSelectLanguage(AppLanguage.KOREAN) }
                )
                LanguageChoiceButton(
                    text = "English",
                    selected = selectedLanguage == AppLanguage.ENGLISH,
                    onClick = { onSelectLanguage(AppLanguage.ENGLISH) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(closeLabel, color = ColorInk, fontWeight = FontWeight.Black)
            }
        }
    )
}

@Composable
private fun LanguageChoiceButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val bg = if (selected) ColorPrimaryTop else ColorCard
    val borderCol = if (selected) ColorInk else ColorMuted
    
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .cartoonBorder(strokeWidth = 2.dp, color = borderCol, shape = shape)
            .clip(shape)
            .background(bg)
    ) {
        Text(
            text = text,
            style = GameTypography.buttonSmall,
            color = if (selected) ColorInk else ColorInkSoft,
            fontWeight = FontWeight.Black
        )
    }
}

private data class HomeLabels(
    val startCombat: String,
    val runs: String,
    val reset: String,
    val chapterTitle: String,
    val rankingTitle: String,
    val quest: String,
    val shop: String,
    val gear: String,
    val upgrade: String,
    val language: String,
    val event: String,
    val explore: String,
    val challenge: String,
    val stage: String,
    val close: String,
    val chapterSubtitle: (Int) -> String
)

private fun homeLabels(language: AppLanguage): HomeLabels = when (language) {
    AppLanguage.KOREAN -> HomeLabels(
        startCombat = "시작",
        runs = "플레이 횟수",
        reset = "초기화",
        chapterTitle = "킹덤 방어전",
        rankingTitle = "도전 기록 랭킹",
        quest = "대탐험",
        shop = "상점",
        gear = "장비",
        upgrade = "특성",
        language = "언어",
        event = "이벤트",
        explore = "탐방",
        challenge = "별 등급 도전",
        stage = "스테이지",
        close = "닫기",
        chapterSubtitle = { chapter -> "${chapter}. 앰버베이 II  ·  최고 생존 ${chapter + 7}일" }
    )
    AppLanguage.ENGLISH -> HomeLabels(
        startCombat = "START",
        runs = "Runs",
        reset = "Reset",
        chapterTitle = "Kingdom Defense",
        rankingTitle = "Challenge Ranking",
        quest = "Quest",
        shop = "Shop",
        gear = "Gear",
        upgrade = "Talent",
        language = "Lang",
        event = "Event",
        explore = "Explore",
        challenge = "Star Challenge",
        stage = "STAGE",
        close = "Close",
        chapterSubtitle = { chapter -> "$chapter. Amber Bay II  ·  Best ${chapter + 7} days" }
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun HomeScreenPreview() {
    val meta = createDefaultMeta().copy(coins = 250)
    val hero = getHeroStats(meta.equipment)
    HomeScreen(
        meta          = meta,
        heroStats     = hero,
        onStartCombat = {},
        onNavigate    = {},
        onReset       = {}
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812, name = "Home Korean")
@Composable
private fun HomeScreenKoreanPreview() {
    val meta = createDefaultMeta().copy(coins = 1200, bestChapter = 17)
    HomeScreen(
        meta = meta,
        heroStats = getHeroStats(meta.equipment),
        language = AppLanguage.KOREAN,
        onStartCombat = {},
        onNavigate = {},
        onReset = {}
    )
}
