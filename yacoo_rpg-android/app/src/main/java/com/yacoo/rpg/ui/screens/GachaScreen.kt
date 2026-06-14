package com.yacoo.rpg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextAlign
import com.yacoo.rpg.game.AppLanguage
import com.yacoo.rpg.ui.components.*
import com.yacoo.rpg.ui.theme.*

@Composable
fun GachaScreen(
    coins: Int,
    gems: Int,
    language: AppLanguage = AppLanguage.ENGLISH,
    onBack: () -> Unit,
    onDraw: (Boolean) -> String, // true for weapon chest, false for armor chest
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    var drawResultText by remember { mutableStateOf<String?>(null) }
    val labels = gachaLabels(language)
    val tabs = listOf(labels.weapon, labels.armor)

    // Mystic Background
    DarkOverlayPanel(modifier = modifier) {
        SunburstBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .padding(top = 10.dp, bottom = 120.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TopStatsBar(
                stage = 1,
                coins = coins,
                gems = gems,
                power = 100, // placeholder
                language = language
            )

            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = labels.title, 
                        style = GameTypography.screenTitle, 
                        color = Color(0xFFFFFDF9),
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp
                    )
                    Text(
                        text = labels.subtitle, 
                        style = GameTypography.caption, 
                        color = Color(0xFFC78CFF),
                        fontWeight = FontWeight.Black
                    )
                }
                
                // 3D Close/Back Button
                val backShape = RoundedCornerShape(12.dp)
                Box(
                    modifier = Modifier
                        .padding(bottom = 3.dp, end = 3.dp)
                        .cartoonShadow(shadowOffset = 3.dp, color = ColorInk, shape = backShape)
                        .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = backShape)
                        .clip(backShape)
                        .background(ColorSecondaryBottom)
                        .clickable { onBack() }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = labels.home,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = ColorInk
                    )
                }
            }

            // Resources Row (Top HUD)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResourceChip(labels.gold, coins, labels.soon, bgColor = Color(0xFFFFD43F), modifier = Modifier.weight(1f))
                ResourceChip(labels.gem, gems, labels.soon, bgColor = Color(0xFFE261FF), modifier = Modifier.weight(1f))
            }

            PromoStrip(labels = labels)

            // Custom 3D Tab Row
            val tabRowShape = RoundedCornerShape(16.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .cartoonShadow(3.dp, ColorInk, tabRowShape)
                    .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = tabRowShape)
                    .clip(tabRowShape)
                    .background(ColorInkSoft)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, title ->
                    GameTab(
                        text = title,
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Draw Action Panel
            DrawPanel(
                kind = tabs[selectedTab], 
                labels = labels, 
                isWeapon = selectedTab == 0,
                coins = coins,
                onDraw = { isWeapon, count ->
                    val results = mutableListOf<String>()
                    for (i in 0 until count) {
                        val result = onDraw(isWeapon)
                        if (result.isNotEmpty()) {
                            results.add(result)
                        }
                    }
                    if (results.isNotEmpty()) {
                        drawResultText = results.joinToString("\n")
                    }
                },
                modifier = Modifier.wrapContentHeight()
            )
        }

        // Draw Result Modal Dialog
        drawResultText?.let { result ->
            DarkOverlayPanel(
                modifier = Modifier.clickable { drawResultText = null }
            ) {
                Column(
                    modifier = Modifier
                        .width(280.dp)
                        .cartoonShadow(6.dp, ColorInk, RoundedCornerShape(20.dp))
                        .cartoonBorder(4.dp, ColorInk, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(ColorSurfacePanel)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RewardBanner(title = "CONGRATS!")
                    
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.verticalGradient(listOf(ColorSecondaryTop, ColorSecondaryBottom)))
                            .border(3.dp, ColorInk, RoundedCornerShape(16.dp))
                            .pulseGlow(),
                        contentAlignment = Alignment.Center
                    ) {
                        GameIcon(
                            icon = if (selectedTab == 0) GameIconRole.WEAPON else GameIconRole.ARMOR,
                            fontSize = 64f
                        )
                    }
                    
                    Text(
                        text = result,
                        style = GameTypography.statValue,
                        color = ColorInk,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    
                    SecondaryButton(
                        text = "OK",
                        onClick = { drawResultText = null },
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PromoStrip(labels: GachaLabels) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .cartoonShadow(3.dp, ColorInk, shape)
            .cartoonBorder(2.dp, ColorInk, shape)
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(ColorDangerTop, ColorSecondaryBottom)))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        GameIcon(GameIconRole.STAR, fontSize = 24f)
        Column(modifier = Modifier.weight(1f)) {
            Text(labels.limited, color = ColorTextOnDark, fontSize = 14.sp, fontWeight = FontWeight.Black)
            Text(labels.limitedDesc, color = Color(0xFFFFF3D3), fontSize = 10.sp, fontWeight = FontWeight.Black)
        }
        Box(
            modifier = Modifier
                .cartoonBorder(1.5.dp, ColorInk, RoundedCornerShape(9.dp))
                .clip(RoundedCornerShape(9.dp))
                .background(ColorCard)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(labels.soon, color = ColorInk, fontSize = 10.sp, fontWeight = FontWeight.Black)
        }
    }
}


@Composable
private fun ResourceChip(label: String, value: Int?, emptyText: String, bgColor: Color, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = shape)
            .clip(shape)
            .background(ColorChrome)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(16.dp).clip(androidx.compose.foundation.shape.CircleShape).background(bgColor).border(1.dp, ColorInk, androidx.compose.foundation.shape.CircleShape)
            )
            Text(
                text = value?.toString() ?: emptyText, 
                fontSize = 14.sp, 
                fontWeight = FontWeight.Black, 
                color = Color(0xFFFFFDF9)
            )
        }
    }
}

@Composable
private fun DrawPanel(
    kind: String,
    labels: GachaLabels,
    isWeapon: Boolean,
    coins: Int,
    onDraw: (Boolean, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val panelShape = RoundedCornerShape(24.dp)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp, end = 6.dp)
            .cartoonShadow(shadowOffset = 6.dp, color = ColorInk, shape = panelShape)
            .cartoonBorder(strokeWidth = 3.dp, color = ColorInk, shape = panelShape)
            .clip(panelShape)
            .background(ColorSurfacePanel)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // Pity Bar
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        labels.pityProgress,
                        style = GameTypography.caption,
                        color = ColorInkSoft,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "0/10",
                        style = GameTypography.caption,
                        color = ColorPrimaryBottom,
                        fontWeight = FontWeight.Black
                    )
                }
                ChunkyProgressBar(progress = 0f, colorStart = ColorPrimaryTop, colorEnd = ColorPrimaryBottom)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mystic Treasure Chest
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .bounceIn()
                        .floatBobbing(amount = 8.dp)
                        .pulseGlow(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .cartoonShadow(6.dp, ColorInk, RoundedCornerShape(20.dp))
                            .cartoonBorder(4.dp, ColorInk, RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .background(Brush.radialGradient(listOf(Color(0xFFFFF3D3), Color(0xFFFF9500))))
                    ) {
                        GameIcon(
                            icon = GameIconRole.CHEST,
                            fontSize = 90f,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        GameIcon(
                            icon = if (isWeapon) GameIconRole.WEAPON else GameIconRole.ARMOR,
                            fontSize = 32f,
                            modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                        )
                    }
                }
                
                RewardBanner(title = "$kind ${labels.draw}")
                
                Text(
                    text = labels.placeholder,
                    style = GameTypography.bodyText,
                    color = ColorTextSecondary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniRewardPreview(GameIconRole.WEAPON, "S", RarityEpic, Modifier.weight(1f))
                    MiniRewardPreview(GameIconRole.ARMOR, "A", RarityRare, Modifier.weight(1f))
                    MiniRewardPreview(GameIconRole.CHARM, "SS", RarityLegendary, Modifier.weight(1f))
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryButton(
                    labels.drawOne,
                    onClick = { onDraw(isWeapon, 1) }, 
                    enabled = coins >= 100,
                    modifier = Modifier.weight(1f).height(54.dp).bounceIn().pulseGlow(),
                    height = 54.dp
                )
                PrimaryButton(
                    labels.drawTen,
                    onClick = { onDraw(isWeapon, 10) }, 
                    enabled = coins >= 1000,
                    modifier = Modifier.weight(1f).height(54.dp).bounceIn().pulseGlow(),
                    height = 54.dp
                )
            }
        }
    }
}

@Composable
private fun MiniRewardPreview(icon: GameIconRole, grade: String, color: Color, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .cartoonShadow(3.dp, ColorInk, shape)
            .cartoonBorder(2.dp, ColorInk, shape)
            .clip(shape)
            .background(color.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        GameIcon(icon, fontSize = 34f)
        Text(
            grade,
            modifier = Modifier.align(Alignment.TopStart).background(ColorCard, RoundedCornerShape(bottomEnd = 8.dp)).padding(horizontal = 5.dp),
            color = ColorInk,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black
        )
    }
}

private data class GachaLabels(
    val title: String,
    val subtitle: String,
    val home: String,
    val gold: String,
    val gem: String,
    val energy: String,
    val soon: String,
    val weapon: String,
    val armor: String,
    val draw: String,
    val placeholder: String,
    val comingSoon: String,
    val limited: String,
    val limitedDesc: String,
    val pityProgress: String,
    val drawOne: String,
    val drawTen: String
)

private fun gachaLabels(language: AppLanguage): GachaLabels = when (language) {
    AppLanguage.KOREAN -> GachaLabels(
        title = "상점",
        subtitle = "신비한 보물상자",
        home = "홈",
        gold = "골드",
        gem = "보석",
        energy = "번개",
        soon = "예정",
        weapon = "무기 상자",
        armor = "방어구 상자",
        draw = "뽑기",
        placeholder = "임시 페이지입니다. 실제 재화, 확률, 보상은 규칙 정의 후 활성화됩니다.",
        comingSoon = "준비 중",
        limited = "기간한정 보물상자",
        limitedDesc = "고급 장비 카드 미리보기",
        pityProgress = "보장 진행도",
        drawOne = "열기 x1",
        drawTen = "열기 x10"
    )
    AppLanguage.ENGLISH -> GachaLabels(
        title = "Shop",
        subtitle = "Mystic Treasure",
        home = "Home",
        gold = "Gold",
        gem = "Gem",
        energy = "Energy",
        soon = "Soon",
        weapon = "Weapon Chest",
        armor = "Armor Chest",
        draw = "Draw",
        placeholder = "Placeholder page. Economy, rates, and rewards stay disabled until real gacha rules are defined.",
        comingSoon = "Coming Soon",
        limited = "Limited Treasure",
        limitedDesc = "Preview high-grade gear cards",
        pityProgress = "Pity Progress",
        drawOne = "Draw x1",
        drawTen = "Draw x10"
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun GachaScreenPreview() {
    GachaScreen(coins = 1200, gems = 0, onBack = {}, onDraw = { _ -> "" })
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812, name = "Gacha Korean")
@Composable
private fun GachaScreenKoreanPreview() {
    GachaScreen(coins = 13_500, gems = 1352, language = AppLanguage.KOREAN, onBack = {}, onDraw = { _ -> "" })
}
