package com.yacoo.rpg.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.game.AppLanguage
import com.yacoo.rpg.game.EquipmentSlot
import com.yacoo.rpg.game.Screen
import com.yacoo.rpg.ui.theme.*

// ── Slanted Shape for Profile Block ───────────────────────────────────

class SlantedProfileShape(private val slantWidthPx: Float) : androidx.compose.ui.graphics.Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width - slantWidthPx, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

// ── TopStatsBar (Slanted Profile & Connected Bar) ─────────────────────

@Composable
fun TopStatsBar(
    stage: Int,
    coins: Int,
    gems: Int,
    power: Int,
    energy: Int? = null,
    language: AppLanguage = AppLanguage.ENGLISH,
    modifier: Modifier = Modifier
) {
    val labels = shellLabels(language)
    val density = androidx.compose.ui.platform.LocalDensity.current
    val slantWidth = 24.dp
    val slantWidthPx = with(density) { slantWidth.toPx() }
    val profileShape = remember(slantWidthPx) { SlantedProfileShape(slantWidthPx) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(54.dp), // Profile block hangs slightly below resource bar
        contentAlignment = Alignment.TopStart
    ) {
        // 1. Right Resource Bar Backing
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .align(Alignment.TopCenter)
                .background(Color(0xFF0F0A1A))
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(listOf(Color(0xFF4D3A78), Color(0xFF4D3A78))),
                    shape = RoundedCornerShape(0.dp)
                )
        ) {
            // 2. Resource Items (Aligned Right)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gold
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GameIcon(icon = GameIconRole.GOLD, fontSize = 24f)
                    Text(
                        text = "$coins",
                        color = Color(0xFFFFFDF9),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                // Gems
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GameIcon(icon = GameIconRole.GEM, fontSize = 24f)
                    Text(
                        text = "$gems",
                        color = Color(0xFFFFFDF9),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                if (energy != null) {
                    Spacer(modifier = Modifier.width(16.dp))
                    // Energy
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GameIcon(icon = GameIconRole.ENERGY, fontSize = 24f)
                        Text(
                            text = "$energy/35",
                            color = Color(0xFFFFFDF9),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // 3. Left Slanted Profile Block
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .width(190.dp)
                .height(52.dp)
                .cartoonShadow(shadowOffset = 3.dp, color = ColorInk, shape = profileShape)
                .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = profileShape)
                .clip(profileShape)
                .background(Brush.verticalGradient(listOf(Color(0xFF261D45), Color(0xFF130F25))))
                .padding(start = 12.dp, end = 26.dp) // Leave space on the right for slant
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Square Avatar
                val avatarShape = RoundedCornerShape(8.dp)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .cartoonBorder(1.5.dp, Color(0xFF7A58C8), shape = avatarShape)
                        .clip(avatarShape)
                        .background(Color(0xFF1C1635)),
                    contentAlignment = Alignment.Center
                ) {
                    GameIcon(icon = GameIconRole.PLAYER_AVATAR, fontSize = 28f)
                }

                // Name & Power info
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (language == AppLanguage.KOREAN) "용사 Stage $stage" else "Hero Stage $stage",
                        color = Color(0xFFE4C7FF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    // Combat Power Pill
                    Row(
                        modifier = Modifier
                            .cartoonBorder(1.dp, Color(0xFF4D3A78), RoundedCornerShape(4.dp))
                            .background(Color(0xFF1C1635))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        GameIcon(icon = GameIconRole.WEAPON, fontSize = 12f)
                        Text(
                            text = "$power",
                            color = Color(0xFFFFD43F), // Gold
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerPill(stage: Int, stageLabel: String = "Stage", modifier: Modifier = Modifier) {
    val cardShape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp, topEnd = 14.dp, bottomEnd = 14.dp)
    val avatarSize = 52.dp
    Box(
        modifier = modifier
            .padding(start = 12.dp)
            .wrapContentSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        // Backing card
        Row(
            modifier = Modifier
                .padding(start = 22.dp)
                .cartoonShadow(shadowOffset = 4.dp, color = ColorInk, shape = cardShape)
                .cartoonBorder(strokeWidth = 2.5.dp, color = Color(0xFF7A58C8), shape = cardShape)
                .clip(cardShape)
                .background(Brush.verticalGradient(listOf(Color(0xFF261D45), Color(0xFF130F25))))
                .padding(start = 38.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Yacoo",
                    color = Color(0xFFE4C7FF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "$stageLabel $stage",
                    color = Color(0xFFFFD43F),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
        
        // Floating avatar frame
        Box(
            modifier = Modifier
                .size(avatarSize)
                .offset(x = (-8).dp)
                .cartoonShadow(shadowOffset = 3.dp, color = ColorInk, shape = CircleShape)
                .cartoonBorder(strokeWidth = 2.5.dp, color = Color(0xFFFFD43F), shape = CircleShape)
                .clip(CircleShape)
                .background(Brush.verticalGradient(listOf(Color(0xFFFFEEAA), Color(0xFFFFAA3F)))),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1C1635)),
                contentAlignment = Alignment.Center
            ) {
                GameIcon(icon = GameIconRole.PLAYER_AVATAR, fontSize = 44f)
            }
        }
    }
}

@Composable
fun StatPill(
    label: String,
    value: String,
    icon: GameIconRole,
    iconBg: Color = ColorPrimaryBottom,
    modifier: Modifier = Modifier
) {
    val pillShape = RoundedCornerShape(percent = 50)
    val iconSize = 36.dp
    Box(
        modifier = modifier
            .padding(start = 8.dp)
            .wrapContentSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        // Pill backing
        Row(
            modifier = Modifier
                .padding(start = 16.dp)
                .cartoonShadow(shadowOffset = 3.dp, color = ColorInk, shape = pillShape)
                .cartoonBorder(strokeWidth = 2.dp, color = Color(0xFF7A58C8), shape = pillShape)
                .clip(pillShape)
                .background(Brush.verticalGradient(listOf(Color(0xFF1C1635), Color(0xFF0F0A1A))))
                .padding(start = 26.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                color = Color(0xFFFFFDF9),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
        }
        
        // Floating Icon
        Box(
            modifier = Modifier
                .size(iconSize)
                .cartoonShadow(shadowOffset = 2.dp, color = ColorInk, shape = CircleShape)
                .cartoonBorder(strokeWidth = 2.0.dp, color = ColorInk, shape = CircleShape)
                .clip(CircleShape)
                .background(Brush.verticalGradient(listOf(iconBg, iconBg.copy(alpha = 0.7f)))),
            contentAlignment = Alignment.Center
        ) {
            GameIcon(icon = icon, fontSize = 32f)
        }
    }
}

// ── Custom Bottom Navigation (3D Row) ─────────────────────────────────

private data class NavItem(val screen: Screen?, val iconRole: GameIconRole)

private val NAV_ITEMS = listOf(
    NavItem(null,             GameIconRole.SETTINGS),
    NavItem(Screen.EQUIPMENT, GameIconRole.GEAR),
    NavItem(Screen.HOME,      GameIconRole.HOME),
    NavItem(Screen.UPGRADE,   GameIconRole.UPGRADE),
    NavItem(Screen.GACHA,     GameIconRole.DRAW)
)

private val BottomNavContentClearance = 152.dp

@Composable
fun bottomNavContentClearance(): androidx.compose.ui.unit.Dp =
    WindowInsets.safeDrawing
        .only(WindowInsetsSides.Bottom)
        .asPaddingValues()
        .calculateBottomPadding() + BottomNavContentClearance

@Composable
fun YacooBottomNav(
    current: Screen,
    onNavigate: (Screen) -> Unit,
    onOptionsClick: () -> Unit,
    language: AppLanguage = AppLanguage.ENGLISH,
    modifier: Modifier = Modifier
) {
    val labels = shellLabels(language)
    val shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
    val centerIndex = NAV_ITEMS.size / 2
    val bottomPadding = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom).asPaddingValues().calculateBottomPadding()
    val navHeight = 84.dp
    val contentLift = 6.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Background bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(navHeight + bottomPadding + contentLift)
                .cartoonBorder(strokeWidth = 2.5.dp, color = Color(0xFF4A3A78), shape = shape)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(Color(0xFF1C1635), Color(0xFF0F0A1A))))
        )
        
        // Nav items row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = bottomPadding + contentLift)
                .height(navHeight)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            NAV_ITEMS.forEachIndexed { index, item ->
                val selected = item.screen != null && current == item.screen
                val isCenter = index == centerIndex
                val clickModifier = if (item.screen != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onNavigate(item.screen) }
                } else {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onOptionsClick() }
                }
                
                val targetScale = when {
                    selected && isCenter -> 1.12f
                    selected -> 1.08f
                    else -> 1f
                }
                val animatedScale by androidx.compose.animation.core.animateFloatAsState(
                    targetValue = targetScale,
                    animationSpec = androidx.compose.animation.core.spring(
                        dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                        stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                    ),
                    label = "navScale"
                )
                
                val slotShape = RoundedCornerShape(10.dp)
                
                if (isCenter) {
                    // CENTER CTA — protruding slot button
                    val centerShape = CircleShape
                    Box(
                        modifier = Modifier
                            .offset(y = (-14).dp) // Protrude above bar
                            .size(66.dp)
                            .graphicsLayer {
                                scaleX = animatedScale
                                scaleY = animatedScale
                            }
                            .cartoonShadow(shadowOffset = 3.dp, color = ColorInk, shape = centerShape)
                            .cartoonBorder(
                                strokeWidth = 3.dp,
                                color = if (selected) Color(0xFFFFFDF9) else ColorInk,
                                shape = centerShape
                            )
                            .clip(centerShape)
                            .background(
                                if (selected)
                                    Brush.verticalGradient(listOf(Color(0xFFFFEEAA), Color(0xFFFFAA3F))) // Bright Golden key
                                else
                                    Brush.verticalGradient(listOf(Color(0xFFD48A00), Color(0xFF8A5500)))  // Muted Golden key
                            )
                            .then(clickModifier),
                        contentAlignment = Alignment.Center
                    ) {
                        GameIcon(
                            icon = item.iconRole,
                            fontSize = 56f,
                            tint = if (selected) ColorInk else ColorInkSoft
                        )
                    }
                } else {
                    // Regular nav item in slot
                    val regularSlotShape = RoundedCornerShape(12.dp)
                    val isTabSelected = selected
                    val slotBgBrush = if (isTabSelected) {
                        Brush.verticalGradient(listOf(Color(0xFF2E244E), Color(0xFF19132C))) // Inner slot shadow look
                    } else {
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                    }
                    val borderModifier = if (isTabSelected) {
                        Modifier.cartoonBorder(1.5.dp, Color(0xFFB75CFF), regularSlotShape) // Glowing border
                    } else {
                        Modifier
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 6.dp, top = 8.dp)
                            .graphicsLayer {
                                scaleX = animatedScale
                                scaleY = animatedScale
                            }
                            .clip(regularSlotShape)
                            .background(slotBgBrush)
                            .then(borderModifier)
                            .then(clickModifier)
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        GameIcon(
                            icon = item.iconRole,
                            fontSize = 36f,
                            tint = if (isTabSelected) Color(0xFFFFFDF9) else ColorNavInactive
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = labels.navLabel(item),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isTabSelected) Color(0xFFB75CFF) else ColorNavInactive
                        )
                        if (isTabSelected) {
                            Spacer(modifier = Modifier.height(2.dp))
                            // Small indicator dot
                            Box(
                                modifier = Modifier
                                    .size(width = 8.dp, height = 3.dp)
                                    .clip(RoundedCornerShape(1.5.dp))
                                    .background(Color(0xFFB75CFF))
                            )
                        } else {
                            Spacer(modifier = Modifier.height(5.dp)) // Maintain size consistency
                        }
                    }
                }
            }
        }
    }
}

// ── YacooShell ────────────────────────────────────────────────────────

@Composable
fun YacooShell(
    current: Screen,
    stage: Int,
    coins: Int,
    gems: Int = 0,
    power: Int,
    energy: Int? = null,
    language: AppLanguage = AppLanguage.ENGLISH,
    onNavigate: (Screen) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit = {},
    onReset: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    var showOptions by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Global Continuous Background
        AdventureBackground(modifier = Modifier.fillMaxSize())

        // Main Stage Content
        Box(
            modifier = Modifier.fillMaxSize(),
            content = content
        )
        
        if (current != Screen.COMBAT) {
            YacooBottomNav(
                current = current,
                onNavigate = onNavigate,
                onOptionsClick = { showOptions = true },
                language = language,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
                    )
            )
        }

        if (showOptions) {
            ShellOptionsPanel(
                language = language,
                onLanguageChange = onLanguageChange,
                onReset = onReset,
                onDismiss = { showOptions = false }
            )
        }
    }
}

@Composable
private fun ShellOptionsPanel(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    val labels = shellLabels(language)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorOverlayDim)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .fillMaxWidth(0.65f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
                .background(Brush.verticalGradient(listOf(Color(0xFF211A3A), Color(0xFF1C1635))))
                .border(2.dp, Color(0xFF4D3A78), RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                .padding(vertical = 24.dp, horizontal = 16.dp)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = labels.options,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = ColorTextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .cartoonBorder(1.5.dp, ColorInk, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                                .background(ColorDangerBottom)
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "X",
                                color = ColorTextOnDark,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))

                    val menuItems = listOf(
                        Triple(if (language == AppLanguage.KOREAN) "공지사항" else "Notice", GameIconRole.STAR, false),
                        Triple(if (language == AppLanguage.KOREAN) "우편함" else "Mailbox", GameIconRole.REWARD, false),
                        Triple(if (language == AppLanguage.KOREAN) "도감" else "Collection", GameIconRole.TREASURE, false)
                    )

                    menuItems.forEach { (title, icon, enabled) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .cartoonBorder(1.5.dp, ColorInk, RoundedCornerShape(10.dp))
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF2D214A))
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                                .graphicsLayer { alpha = if (enabled) 1f else 0.5f }
                        ) {
                            GameIcon(icon = icon, fontSize = 24f)
                            Text(
                                text = title,
                                color = ColorTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (language == AppLanguage.KOREAN) "언어 설정" else "Language",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextSecondary
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LanguageChoiceButton(
                                text = "한국어",
                                selected = language == AppLanguage.KOREAN,
                                modifier = Modifier.weight(1f)
                            ) {
                                onLanguageChange(AppLanguage.KOREAN)
                            }
                            LanguageChoiceButton(
                                text = "English",
                                selected = language == AppLanguage.ENGLISH,
                                modifier = Modifier.weight(1f)
                            ) {
                                onLanguageChange(AppLanguage.ENGLISH)
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .cartoonBorder(2.dp, ColorInk, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(ColorDangerBottom)
                        .clickable {
                            onReset()
                            onDismiss()
                        }
                        .padding(horizontal = 12.dp),
                ) {
                    GameIcon(icon = GameIconRole.RESET, fontSize = 22f)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.KOREAN) "데이터 초기화" else "Reset Data",
                        fontSize = 12.sp,
                        color = ColorTextOnDark,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageChoiceButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .cartoonBorder(1.5.dp, ColorInk, shape)
            .clip(shape)
            .background(if (selected) ColorPrimaryBottom else Color(0xFF2D214A))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
    ) {
        if (selected) {
            GameIcon(icon = GameIconRole.CONFIRM, fontSize = 16f)
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text,
            color = if (selected) ColorTextOnPrimary else ColorTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
        )
    }
}

private data class ShellLabels(
    val stage: String,
    val gold: String,
    val gem: String,
    val energy: String,
    val power: String,
    val home: String,
    val battle: String,
    val gear: String,
    val upgrade: String,
    val draw: String,
    val options: String
) {
    fun navLabel(item: NavItem): String = when (item.screen) {
        null -> options
        Screen.HOME -> home
        Screen.COMBAT -> battle
        Screen.EQUIPMENT -> gear
        Screen.UPGRADE -> upgrade
        Screen.GACHA -> draw
        else -> item.screen.name.lowercase().replaceFirstChar { it.uppercase() }
    }
}

private fun shellLabels(language: AppLanguage): ShellLabels = when (language) {
    AppLanguage.KOREAN -> ShellLabels(
        stage = "스테이지",
        gold = "골드",
        gem = "보석",
        energy = "에너지",
        power = "전투력",
        home = "홈",
        battle = "전투",
        gear = "장비",
        upgrade = "강화",
        draw = "뽑기",
        options = "옵션"
    )
    AppLanguage.ENGLISH -> ShellLabels(
        stage = "Stage",
        gold = "Gold",
        gem = "Gem",
        energy = "Energy",
        power = "Power",
        home = "Home",
        battle = "Battle",
        gear = "Gear",
        upgrade = "Upgrade",
        draw = "Draw",
        options = "Options"
    )
}

// ── PrimaryButton / GhostButton (Refactored to match GameButton) ──────

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: androidx.compose.ui.unit.Dp = 48.dp
) {
    GameButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = GameButtonVariant.PRIMARY,
        height = height
    )
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: androidx.compose.ui.unit.Dp = 48.dp
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = shape)
            .clip(shape)
            .background(Color(0xFFFFFDF9))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = GameTypography.buttonSmall,
            color = ColorInk,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

// ── HpBar (굵은 카툰 체력바) ──────────────────────────────────────────

@Composable
fun HpBar(
    current: Int,
    max: Int,
    modifier: Modifier = Modifier,
    color: Color = ColorHpFillStart
) {
    val ratio = if (max > 0) (current.toFloat() / max).coerceIn(0f, 1f) else 0f
    val shape = RoundedCornerShape(8.dp)
    
    // Wrapped in a bold outline container
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp) // slightly taller
            .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = shape)
            .clip(shape)
            .background(ColorHpTrack)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(ratio)
                .clip(shape)
                .background(
                    Brush.horizontalGradient(listOf(ColorHpFillStart, ColorHpFillEnd))
                )
        )
    }
}

// ── Shared UI utilities ───────────────────────────────────────────────

fun slotEmoji(slot: EquipmentSlot): String = when (slot) {
    EquipmentSlot.WEAPON -> GameIconRole.WEAPON.fallback
    EquipmentSlot.ARMOR  -> GameIconRole.ARMOR.fallback
    EquipmentSlot.CHARM  -> GameIconRole.CHARM.fallback
    EquipmentSlot.BOOTS  -> GameIconRole.BOOTS.fallback
}
