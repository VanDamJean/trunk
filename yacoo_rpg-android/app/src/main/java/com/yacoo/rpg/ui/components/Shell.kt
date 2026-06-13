package com.yacoo.rpg.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.game.AppLanguage
import com.yacoo.rpg.game.EquipmentSlot
import com.yacoo.rpg.game.Screen
import com.yacoo.rpg.ui.theme.*

// ── TopStatsBar (浮遊 HUD Board) ──────────────────────────────────────

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
    
    // Floating pill-shaped top bar
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerPill(stage = stage, stageLabel = labels.stage)
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatPill(label = labels.gold, value = "$coins", icon = GameIconRole.GOLD)
                StatPill(label = labels.gem, value = "$gems", icon = GameIconRole.GEM)
                if (energy != null) {
                    StatPill(label = labels.energy, value = "$energy", icon = GameIconRole.ENERGY)
                }
            }
        }
    }
}

@Composable
fun PlayerPill(stage: Int, stageLabel: String = "Stage", modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = modifier
            .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = shape)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(ColorPrimaryTop, ColorPrimaryBottom)))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFFDF9))
                .cartoonBorder(strokeWidth = 1.5.dp, color = ColorInk, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            GameIcon(icon = GameIconRole.PLAYER_AVATAR, fontSize = 14f)
        }
        Column {
            Text("Yacoo", color = ColorTextOnPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text("$stageLabel $stage", color = ColorTextOnPrimary, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun StatPill(
    label: String,
    value: String,
    icon: GameIconRole,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = modifier
            .cartoonShadow(shadowOffset = 3.dp, color = ColorInk, shape = shape)
            .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = shape)
            .clip(shape)
            .background(ColorHudBg) // Use theme HUD background instead of hardcoded
            .padding(start = 6.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GameIcon(icon = icon, fontSize = 16f)
        Text(
            text = value,
            color = Color(0xFFFFFDF9),
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.cartoonShadowText() // Ensure text is readable over background
        )
    }
}

// ── Custom Bottom Navigation (3D Row) ─────────────────────────────────

private data class NavItem(val screen: Screen, val iconRole: GameIconRole)

private val NAV_ITEMS = listOf(
    NavItem(Screen.HOME,      GameIconRole.HOME),
    NavItem(Screen.COMBAT,    GameIconRole.BATTLE),
    NavItem(Screen.EQUIPMENT, GameIconRole.GEAR),
    NavItem(Screen.UPGRADE,   GameIconRole.UPGRADE),
    NavItem(Screen.GACHA,     GameIconRole.DRAW)
)

@Composable
fun YacooBottomNav(
    current: Screen,
    onNavigate: (Screen) -> Unit,
    language: AppLanguage = AppLanguage.ENGLISH,
    modifier: Modifier = Modifier
) {
    val labels = shellLabels(language)
    val shape = RoundedCornerShape(32.dp)
    val centerIndex = NAV_ITEMS.size / 2 // Index 2 = EQUIPMENT (center)
    
    // Floating distinct dark panel for bottom nav
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Background bar (sits behind everything)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .cartoonShadow(shadowOffset = 5.dp, color = ColorInk, shape = shape)
                .cartoonBorder(strokeWidth = 3.dp, color = ColorInk, shape = shape)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(
                    Color(0xCC1A1025), // Semi-transparent top
                    Color(0xE60F0A1A)  // More opaque bottom
                )))
        )
        
        // Nav items row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            NAV_ITEMS.forEachIndexed { index, item ->
                val selected = current == item.screen
                val isCenter = index == centerIndex
                
                // Animated scale for active tab
                val targetScale = when {
                    selected && isCenter -> 1.15f
                    selected -> 1.1f
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
                
                if (isCenter) {
                    // CENTER CTA — protruding circular button
                    Box(
                        modifier = Modifier
                            .offset(y = (-24).dp) // Protrude above bar
                            .size(68.dp)
                            .graphicsLayer {
                                scaleX = animatedScale
                                scaleY = animatedScale
                            }
                            .cartoonShadow(shadowOffset = 4.dp, color = ColorInk, shape = CircleShape)
                            .cartoonBorder(
                                strokeWidth = 3.dp,
                                color = if (selected) ColorNavActive else ColorInk,
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .background(
                                if (selected)
                                    Brush.verticalGradient(listOf(ColorSecondaryTop, ColorSecondaryBottom))
                                else
                                    Brush.verticalGradient(listOf(Color(0xFF2A2540), Color(0xFF1A1425)))
                            )
                            .then(if (selected) Modifier.pulseGlow(minScale = 1f, maxScale = 1.04f) else Modifier)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onNavigate(item.screen) },
                        contentAlignment = Alignment.Center
                    ) {
                        GameIcon(
                            icon = item.iconRole,
                            fontSize = 36f,
                            tint = if (selected) ColorInk else Color(0xFF888899)
                        )
                    }
                } else {
                    // Regular nav item
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 10.dp, top = 14.dp)
                            .graphicsLayer {
                                scaleX = animatedScale
                                scaleY = animatedScale
                                alpha = if (selected) 1f else 0.6f
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onNavigate(item.screen) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            GameIcon(
                                icon = item.iconRole,
                                fontSize = 26f,
                                tint = if (selected) ColorNavActive else Color(0xFF888899)
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = labels.navLabel(item.screen),
                            fontSize = 10.sp,
                            fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
                            color = if (selected) ColorNavActive else Color(0xFF888899)
                        )
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
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
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

        // Floating Top HUD
        TopStatsBar(
            stage = stage,
            coins = coins,
            gems = gems,
            power = power,
            energy = energy,
            language = language,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                )
        )
        
        // Floating Bottom Nav
        YacooBottomNav(
            current = current,
            onNavigate = onNavigate,
            language = language,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
                )
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
    val draw: String
) {
    fun navLabel(screen: Screen): String = when (screen) {
        Screen.HOME -> home
        Screen.COMBAT -> battle
        Screen.EQUIPMENT -> gear
        Screen.UPGRADE -> upgrade
        Screen.GACHA -> draw
        else -> screen.name.lowercase().replaceFirstChar { it.uppercase() }
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
        draw = "뽑기"
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
        draw = "Draw"
    )
}

// ── PrimaryButton / GhostButton (Refactored to match GameButton) ──────

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    GameButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = GameButtonVariant.PRIMARY
    )
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
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
