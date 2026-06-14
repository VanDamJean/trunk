package com.yacoo.rpg.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var drawResultText by remember { mutableStateOf<String?>(null) }
    val labels = gachaLabels(language)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ColorOverlayDim),
        contentAlignment = Alignment.Center
    ) {
        // Main Supply-Pack Popup Box
        Column(
            modifier = Modifier
                .width(320.dp)
                .cartoonShadow(5.dp, ColorInk, RoundedCornerShape(20.dp))
                .cartoonBorder(3.dp, ColorOutlineSubtle, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF211A3A))
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ribbon Title Banner at top of the popup
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(ColorDangerTop, ColorDangerBottom)
                        )
                    )
                    .border(2.dp, Color(0xFF8A211B)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (language == AppLanguage.KOREAN) "모험가 보급 상자" else "Adventure Supply Pack",
                    color = ColorTextOnDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            }

            // Chest Illustration
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .bounceIn()
                    .floatBobbing(amount = 6.dp)
                    .pulseGlow(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .cartoonBorder(2.5.dp, ColorInk, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.radialGradient(listOf(Color(0xFFFFD96F), Color(0xFFFFAE00))))
                ) {
                    GameIcon(
                        icon = GameIconRole.CHEST,
                        fontSize = 80f,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // Subtitle info
            Text(
                text = labels.subtitle,
                color = ColorTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Product Cards Columns
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Two small cards side-by-side
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Weapon Chest Draw 1x
                    ProductCard(
                        title = labels.weapon,
                        price = 100,
                        coins = coins,
                        icon = GameIconRole.WEAPON,
                        badge = "x1",
                        modifier = Modifier.weight(1f)
                    ) {
                        val res = onDraw(true)
                        if (res.isNotEmpty()) drawResultText = res
                    }

                    // Armor Chest Draw 1x
                    ProductCard(
                        title = labels.armor,
                        price = 100,
                        coins = coins,
                        icon = GameIconRole.ARMOR,
                        badge = "x1",
                        modifier = Modifier.weight(1f)
                    ) {
                        val res = onDraw(false)
                        if (res.isNotEmpty()) drawResultText = res
                    }
                }

                // One large card for Draw 10x
                LargeProductCard(
                    title = if (language == AppLanguage.KOREAN) "고급 10회 소환" else "Premium Draw 10x",
                    price = 1000,
                    coins = coins,
                    icon = GameIconRole.CHEST,
                    badge = "x10"
                ) {
                    val results = mutableListOf<String>()
                    for (i in 0 until 10) {
                        val res = onDraw(true)
                        if (res.isNotEmpty()) results.add(res)
                    }
                    if (results.isNotEmpty()) {
                        drawResultText = results.joinToString("\n")
                    }
                }
            }

            // Close "X" Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .cartoonBorder(2.dp, ColorInk, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(ColorDangerBottom)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "X",
                    color = ColorTextOnDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // Congrats Result Overlay Modal
        drawResultText?.let { result ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorOverlayDim.copy(alpha = 0.95f))
                    .clickable { drawResultText = null },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .width(280.dp)
                        .cartoonShadow(5.dp, ColorInk, RoundedCornerShape(20.dp))
                        .cartoonBorder(3.dp, ColorOutlineSubtle, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1C1635))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RewardBanner(title = "CONGRATS!")
                    
                    Box(
                        modifier = Modifier
                            .size(116.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.verticalGradient(listOf(ColorSecondaryTop, ColorSecondaryBottom)))
                            .border(2.5.dp, ColorInk, RoundedCornerShape(16.dp))
                            .pulseGlow(),
                        contentAlignment = Alignment.Center
                    ) {
                        GameIcon(
                            icon = GameIconRole.CHEST,
                            fontSize = 94f
                        )
                    }
                    
                    Text(
                        text = result,
                        color = ColorTextPrimary,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp
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
private fun ProductCard(
    title: String,
    price: Int,
    coins: Int,
    icon: GameIconRole,
    badge: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val enabled = coins >= price
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = modifier
            .cartoonBorder(1.5.dp, ColorInk, shape)
            .clip(shape)
            .background(Color(0xFF2D214A))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(66.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color(0xFF1C1635))
            )
            GameIcon(icon = icon, fontSize = 50f)
            Text(
                badge,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(ColorPrimaryBottom, RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                color = ColorTextOnPrimary,
                fontSize = 8.sp,
                fontWeight = FontWeight.Black
            )
        }
        
        Text(
            title,
            color = ColorTextPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            textAlign = TextAlign.Center
        )

        PriceButton(price = price, enabled = enabled, onClick = onClick)
    }
}

@Composable
private fun LargeProductCard(
    title: String,
    price: Int,
    coins: Int,
    icon: GameIconRole,
    badge: String,
    onClick: () -> Unit
) {
    val enabled = coins >= price
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .cartoonBorder(1.5.dp, ColorInk, shape)
            .clip(shape)
            .background(Color(0xFF2D214A))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFF1C1635))
                )
                GameIcon(icon = icon, fontSize = 44f)
                Text(
                    badge,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(ColorPrimaryBottom, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    color = ColorTextOnPrimary,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Text(
                title,
                color = ColorTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
        }

        PriceButton(price = price, enabled = enabled, onClick = onClick, modifier = Modifier.width(90.dp))
    }
}

@Composable
private fun PriceButton(
    price: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    val bg = if (enabled) ColorPrimaryBottom else ColorDisabled
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .cartoonBorder(1.5.dp, ColorInk, shape)
            .clip(shape)
            .background(bg)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 4.dp)
    ) {
        GameIcon(GameIconRole.GOLD, fontSize = 18f)
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "$price",
            color = if (enabled) ColorTextOnPrimary else ColorTextSecondary,
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
        subtitle = "신비한 보급품 상자들을 획득하세요!",
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
        subtitle = "Acquire mystic supply packs!",
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
