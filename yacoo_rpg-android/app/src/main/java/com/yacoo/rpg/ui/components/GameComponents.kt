package com.yacoo.rpg.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import com.yacoo.rpg.ui.theme.*
import com.yacoo.rpg.game.*
import com.yacoo.rpg.R

// ── Text Outline & Shadow ───────────────────────────────────────────

/** No-op placeholder — text shadow is applied via TextStyle.shadow, not a Modifier. */
fun Modifier.cartoonShadowText(): Modifier = this


// ── Rarity Enum ───────────────────────────────────────────────────────

enum class GameRarity(val color: Color, val bgColor: Color, val label: String) {
    COMMON(RarityCommon, RarityCommonBg, "Common"),
    UNCOMMON(RarityUncommon, RarityUncommonBg, "Uncommon"),
    RARE(RarityRare, RarityRareBg, "Rare"),
    EPIC(RarityEpic, RarityEpicBg, "Epic"),
    LEGENDARY(RarityLegendary, RarityLegendaryBg, "Legendary")
}

fun equipRarity(level: Int): GameRarity = when {
    level >= 10 -> GameRarity.LEGENDARY
    level >= 7  -> GameRarity.EPIC
    level >= 4  -> GameRarity.RARE
    level >= 2  -> GameRarity.UNCOMMON
    else        -> GameRarity.COMMON
}

// ── Button Variant Enum ──────────────────────────────────────────────

enum class GameButtonVariant {
    PRIMARY, SECONDARY, DANGER
}

// ── GameScreenSurface ────────────────────────────────────────────────

@Composable
fun GameScreenSurface(
    modifier: Modifier = Modifier,
    verticalScroll: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColorScreenBg)
            .then(
                if (verticalScroll)
                    Modifier.verticalScroll(rememberScrollState())
                else Modifier
            )
            .padding(GameSpacing.screenPadding),
        content = content
    )
}

// ── GameCard ─────────────────────────────────────────────────────────

/**
 * Cartoon styled card with solid background, thick borders, and 3D offset shadow.
 */
@Composable
fun GameCard(
    modifier: Modifier = Modifier,
    rarity: GameRarity = GameRarity.COMMON,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    
    // Base layout enclosing the cartoon border & shadow
    Box(
        modifier = modifier
            .padding(bottom = 6.dp, end = 6.dp) // extra padding to prevent shadow clipping
            .cartoonShadow(shadowOffset = 5.dp, color = ColorInk, shape = shape)
            .cartoonBorder(strokeWidth = 2.5.dp, color = ColorInk, shape = shape)
            .clip(shape)
            .background(rarity.bgColor)
            .then(
                if (onClick != null) Modifier.clickable(role = Role.Button, onClick = { onClick() })
                else Modifier
            )
            .padding(GameSpacing.cardPadding)
    ) {
        Column {
            // Adding a small rarity badge accent line on the left side inside the card
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vertical accent bar matching rarity
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(rarity.color)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    content()
                }
            }
        }
    }
}

// ── GameButton (Capybara Go 3D Tactile Button) ──────────────────────

@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: GameButtonVariant = GameButtonVariant.PRIMARY,
    height: androidx.compose.ui.unit.Dp = 48.dp
) {
    val shape = RoundedCornerShape(percent = 50)

    // Main face gradient + bottom face solid color per variant
    val gradientStart: Color
    val gradientEnd: Color
    val bottomFaceColor: Color
    val textColor: Color

    when (variant) {
        GameButtonVariant.PRIMARY -> {
            gradientStart = ColorBtnPrimaryStart
            gradientEnd = ColorBtnPrimaryEnd
            bottomFaceColor = Color(0xFF2B9337)
            textColor = ColorTextOnPrimary
        }
        GameButtonVariant.SECONDARY -> {
            gradientStart = ColorBtnSecondaryStart
            gradientEnd = ColorBtnSecondaryEnd
            bottomFaceColor = Color(0xFFCC8A00)
            textColor = ColorInkStrong
        }
        GameButtonVariant.DANGER -> {
            gradientStart = ColorBtnDangerStart
            gradientEnd = ColorBtnDangerEnd
            bottomFaceColor = Color(0xFFA62140)
            textColor = ColorTextOnDark
        }
    }

    val disabledColor = Color(0xFF6B6973)
    val contentAlpha = if (enabled) 1f else 0.4f

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animate bottom face height: 5dp normal → 1dp pressed
    val bottomHeight by animateDpAsState(
        targetValue = if (isPressed && enabled) 1.dp else 5.dp,
        label = "BottomFaceHeight"
    )
    // Animate main face offset: 0dp normal → 4dp pressed (pushed down)
    val mainOffset by animateDpAsState(
        targetValue = if (isPressed && enabled) 4.dp else 0.dp,
        label = "MainFaceOffset"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
    ) {
        // Main face — slides down on press
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = mainOffset)
                .clip(shape)
                .then(
                    if (enabled)
                        Modifier.background(Brush.verticalGradient(listOf(gradientStart, gradientEnd)))
                    else
                        Modifier.background(disabledColor.copy(alpha = contentAlpha))
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = GameTypography.buttonLarge,
                color = textColor.copy(alpha = contentAlpha),
                fontWeight = FontWeight.ExtraBold
            )
        }

        // Bottom face — the 3D depth strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomHeight)
                .clip(shape)
                .then(
                    if (enabled)
                        Modifier.background(bottomFaceColor)
                    else
                        Modifier.background(disabledColor.copy(alpha = contentAlpha * 0.7f))
                )
        )
    }
}

// ── GameChip ─────────────────────────────────────────────────────────

@Composable
fun GameChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    primary: Boolean = false
) {
    val bg = if (primary)
        Brush.verticalGradient(listOf(ColorBtnPrimaryStart, ColorBtnPrimaryEnd))
    else
        Brush.verticalGradient(listOf(ColorCreamWarm, ColorCreamSoft))

    val labelColor = if (primary) ColorTextOnPrimary else ColorTextSecondary
    val valueColor = if (primary) ColorTextOnPrimary else ColorTextPrimary
    val shape = RoundedCornerShape(10.dp)

    Row(
        modifier = modifier
            .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = shape)
            .clip(shape)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(GameSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = GameTypography.chipLabel,
            color = labelColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = GameTypography.chipValue,
            color = valueColor,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

// ── SectionHeader ────────────────────────────────────────────────────

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = GameSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = GameTypography.sectionTitle,
            color = ColorTextOnDark, // Section headers stand out on dark bg
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.weight(1f)
        )
        if (action != null) {
            action()
        }
    }
}

// ── ItemSlotCard ─────────────────────────────────────────────────────

@Composable
fun ItemSlotCard(
    icon: GameIconRole,
    name: String,
    subtitle: String? = null,
    rarity: GameRarity = GameRarity.COMMON,
    selected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val bgColor = if (selected) ColorCardHighlight else rarity.bgColor
    val borderStrokeWidth = if (selected) 3.dp else 2.dp
    val borderColor = if (selected) ColorSecondaryBottom else ColorInk

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp, end = 4.dp)
            .cartoonShadow(shadowOffset = 4.dp, color = ColorInk, shape = shape)
            .cartoonBorder(strokeWidth = borderStrokeWidth, color = borderColor, shape = shape)
            .clip(shape)
            .background(bgColor)
            .clickable(role = Role.Button, onClick = { onClick() })
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(GameSpacing.md)
        ) {
            // Rounded cartoon background icon container
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(rarity.color.copy(alpha = 0.2f))
                    .border(1.5.dp, ColorInk, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                GameIcon(icon = icon, fontSize = 42f)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = GameTypography.statValue,
                    color = ColorTextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = GameTypography.caption,
                        color = ColorTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            RarityBadge(rarity = rarity)
        }
    }
}

// ── RarityBadge ──────────────────────────────────────────────────────

@Composable
fun RarityBadge(
    rarity: GameRarity,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(6.dp)
    Box(
        modifier = modifier
            .cartoonBorder(strokeWidth = 1.5.dp, color = ColorInk, shape = shape)
            .clip(shape)
            .background(rarity.color)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = rarity.label,
            style = GameTypography.badge,
            color = ColorTextOnDark,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}

// ── GameTab ──────────────────────────────────────────────────────────

@Composable
fun GameTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    val bg = if (selected) ColorNavIndicator else Color.Transparent
    val borderModifier = if (selected) Modifier.cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = shape) else Modifier

    Box(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .then(borderModifier)
            .clip(shape)
            .background(bg)
            .clickable(role = Role.Button, onClick = { onClick() })
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(
            text = text,
            style = if (selected) GameTypography.navLabelSelected else GameTypography.navLabel,
            color = if (selected) ColorInk else ColorNavInactive,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

// ── Overhauled components matching reference mockups ────────────────

private fun getRarityGradient(rarity: GameRarity): Brush {
    val (start, end) = when (rarity) {
        GameRarity.COMMON -> Pair(Color(0xFFB8B5C0), Color(0xFF7A768A))
        GameRarity.UNCOMMON -> Pair(Color(0xFF76D74B), Color(0xFF3B8E1D))
        GameRarity.RARE -> Pair(Color(0xFF4BA5FF), Color(0xFF1475CD))
        GameRarity.EPIC -> Pair(Color(0xFFD08DFF), Color(0xFF7A2FD9))
        GameRarity.LEGENDARY -> Pair(Color(0xFFFFB74D), Color(0xFFE67E17))
    }
    return Brush.verticalGradient(listOf(start, end))
}

private fun getRarityBannerColor(rarity: GameRarity): Color = when (rarity) {
    GameRarity.COMMON -> Color(0xFF5A5766)
    GameRarity.UNCOMMON -> Color(0xFF235A0F)
    GameRarity.RARE -> Color(0xFF0F5085)
    GameRarity.EPIC -> Color(0xFF531F9B)
    GameRarity.LEGENDARY -> Color(0xFF9E530C)
}

@Composable
fun EquippedSlotCard(
    slot: EquipmentSlot,
    item: EquipmentItem,
    isSelected: Boolean,
    showUpgradeIndicator: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Float = 32f,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp)
) {
    val itemRarity = equipRarity(item.level)
    val gradient = getRarityGradient(itemRarity)
    val bannerColor = getRarityBannerColor(itemRarity)
    
    val borderThickness = if (isSelected) 3.dp else 2.dp
    val borderColor = if (isSelected) ColorPrimaryTop else ColorInk
    val heldOffset = if (isSelected) (-2).dp else 0.dp
    val shadowOffset = if (isSelected) 2.dp else 3.dp
    
    Box(
        modifier = modifier
            .padding(bottom = 3.dp, end = 3.dp)
            .offset(y = heldOffset)
            .cartoonShadow(shadowOffset = shadowOffset, color = ColorInk, shape = shape)
            .cartoonBorder(strokeWidth = borderThickness, color = borderColor, shape = shape)
            .clip(shape)
            .background(gradient)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Full width level banner at top
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(15.dp)
                .background(bannerColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "LV.${item.level}",
                fontSize = 7.5.sp,
                fontWeight = FontWeight.Black,
                color = ColorTextPrimary
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .background(ColorInk)
            )
        }
        
        // Inner inset container for the item icon
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 18.dp, bottom = 4.dp, start = 4.dp, end = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.22f))
                .border(1.dp, ColorInk.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            GameIcon(
                icon = when(slot) {
                    EquipmentSlot.WEAPON -> GameIconRole.WEAPON
                    EquipmentSlot.ARMOR -> GameIconRole.ARMOR
                    EquipmentSlot.CHARM -> GameIconRole.CHARM
                    EquipmentSlot.BOOTS -> GameIconRole.BOOTS
                },
                fontSize = iconSize
            )
        }
        
        // S Badge (for level >= 4)
        if (item.level >= 4) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 2.dp, y = (-2).dp)
            ) {
                Text(
                    text = "S",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = ColorPrimaryTop,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = ColorInk,
                            offset = Offset(1f, 1f),
                            blurRadius = 0f
                        )
                    )
                )
            }
        }
        
        // Upgrade arrow indicator at bottom-right
        if (showUpgradeIndicator) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 1.dp, y = 1.dp)
                    .size(13.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CA52E))
                    .border(1.dp, ColorInk, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▲",
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun CombatPowerBadge(
    power: Int,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .padding(bottom = 3.dp, end = 3.dp)
            .cartoonShadow(shadowOffset = 3.dp, color = ColorInk, shape = shape)
            .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = shape)
            .clip(shape)
            .background(Color(0xFFFFD43F))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GameIcon(icon = GameIconRole.POWER, fontSize = 28f)
        Text(
            text = "POWER: $power",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = ColorInk
        )
    }
}

@Composable
fun HorizontalStatsBar(
    hp: Int,
    attack: Int,
    defense: Int,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 3.dp, end = 3.dp)
            .cartoonShadow(shadowOffset = 3.dp, color = ColorInk, shape = shape)
            .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = shape)
            .clip(shape)
            .background(Color(0xFF2C2A36))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatBarItem(icon = GameIconRole.HEAL, value = "$hp", tint = ColorHpFillStart)
        StatBarItem(icon = GameIconRole.ATTACK, value = "$attack", tint = ColorBtnDangerStart)
        StatBarItem(icon = GameIconRole.DEFEND, value = "$defense", tint = ColorSecondaryTop)
    }
}

@Composable
private fun StatBarItem(
    icon: GameIconRole,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GameIcon(icon = icon, fontSize = 26f, tint = tint)
        Text(
            text = value,
            color = Color(0xFFFFFDF9),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

// ── Overhaul UI Phase 3 New Components ────────────────────────────────

@Composable
fun PurpleItemCard(
    modifier: Modifier = Modifier,
    rarity: GameRarity = GameRarity.COMMON,
    level: Int? = null,
    slot: EquipmentSlot? = null,
    isEquipped: Boolean = false,
    showUpgradeIndicator: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val gradient = getRarityGradient(rarity)
    val bannerColor = getRarityBannerColor(rarity)
    
    Box(
        modifier = modifier
            .padding(bottom = 4.dp, end = 4.dp)
            .cartoonShadow(shadowOffset = 3.dp, color = ColorInk, shape = shape)
            .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = shape)
            .clip(shape)
            .background(gradient)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = { onClick() }) else Modifier)
    ) {
        // Inner Inset Box for Content/Icon
        val topPadding = if (level != null || slot != null) 16.dp else 4.dp
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding, bottom = 4.dp, start = 4.dp, end = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.22f))
                .border(1.dp, ColorInk.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
        
        // Top-Left Slot Type Badge
        if (slot != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-1).dp, y = (-1).dp)
                    .size(15.dp)
                    .clip(RoundedCornerShape(bottomEnd = 6.dp))
                    .background(ColorInkSoft)
                    .border(1.dp, ColorInk, RoundedCornerShape(bottomEnd = 6.dp)),
                contentAlignment = Alignment.Center
            ) {
                GameIcon(
                    icon = when(slot) {
                        EquipmentSlot.WEAPON -> GameIconRole.WEAPON
                        EquipmentSlot.ARMOR -> GameIconRole.ARMOR
                        EquipmentSlot.CHARM -> GameIconRole.CHARM
                        EquipmentSlot.BOOTS -> GameIconRole.BOOTS
                    },
                    fontSize = 10f,
                    tint = ColorTextPrimary
                )
            }
        }
        
        // Top-Right Level Badge
        if (level != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 1.dp, y = (-1).dp)
                    .clip(RoundedCornerShape(bottomStart = 6.dp))
                    .background(bannerColor)
                    .border(1.dp, ColorInk, RoundedCornerShape(bottomStart = 6.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "LV.$level",
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black,
                    color = ColorTextPrimary
                )
            }
        }
        
        // S Badge (if Epic/Legendary)
        if (rarity == GameRarity.EPIC || rarity == GameRarity.LEGENDARY) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 2.dp, y = (-2).dp)
            ) {
                Text(
                    text = "S",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = ColorPrimaryTop,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = ColorInk,
                            offset = Offset(1f, 1f),
                            blurRadius = 0f
                        )
                    )
                )
            }
        }
        
        // Equipped / Upgrade Indicator at bottom-right
        if (isEquipped) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 1.dp, y = 1.dp)
                    .clip(RoundedCornerShape(topStart = 6.dp))
                    .background(Color(0xFF4CA52E))
                    .border(1.dp, ColorInk, RoundedCornerShape(topStart = 6.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "E",
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black,
                    color = ColorTextPrimary
                )
            }
        } else if (showUpgradeIndicator) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 1.dp, y = 1.dp)
                    .size(13.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CA52E))
                    .border(1.dp, ColorInk, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▲",
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun DarkOverlayPanel(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ColorOverlayDim),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun RewardBanner(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // Simple banner representation since we don't have the generated PNG yet
        val shape = RoundedCornerShape(24.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .cartoonShadow(6.dp, ColorInk, shape)
                .cartoonBorder(3.dp, ColorInk, shape)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(Color(0xFFFFD96F), Color(0xFFFFAE00)))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = GameTypography.screenTitle,
                color = ColorInk,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun UpgradeSuccessPanel(
    itemName: String,
    oldLevel: Int,
    newLevel: Int,
    slot: EquipmentSlot = EquipmentSlot.WEAPON,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(300.dp)
            .cartoonShadow(6.dp, ColorInk, RoundedCornerShape(24.dp))
            .cartoonBorder(4.dp, ColorInk, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(ColorSurfacePanel)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RewardBanner(title = "UPGRADE SUCCESS!")
        Spacer(modifier = Modifier.height(24.dp))
        
        // Item placeholder
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(listOf(ColorItemPurple, ColorItemPurpleDark)))
                .border(3.dp, ColorInk, RoundedCornerShape(20.dp))
                .pulseGlow(),
            contentAlignment = Alignment.Center
        ) {
             GameIcon(icon = when (slot) {
                 EquipmentSlot.WEAPON -> GameIconRole.WEAPON
                 EquipmentSlot.ARMOR -> GameIconRole.ARMOR
                 EquipmentSlot.CHARM -> GameIconRole.CHARM
                 EquipmentSlot.BOOTS -> GameIconRole.BOOTS
             }, fontSize = 104f)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = itemName, style = GameTypography.sectionTitle, color = ColorInk)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "LV.$oldLevel", style = GameTypography.buttonLarge, color = ColorMuted)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "➡", style = GameTypography.buttonLarge, color = ColorPrimaryBottom)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "LV.$newLevel", style = GameTypography.buttonLarge, color = ColorPrimaryBottom)
        }
    }
}

@Composable
fun ChunkyProgressBar(
    progress: Float,
    colorStart: Color = ColorPrimaryTop,
    colorEnd: Color = ColorPrimaryBottom,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 24.dp
) {
    val shape = RoundedCornerShape(percent = 50)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .cartoonShadow(3.dp, ColorInk, shape)
            .cartoonBorder(3.dp, ColorInk, shape)
            .clip(shape)
            .background(ColorInkSoft)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                .clip(shape)
                .background(Brush.horizontalGradient(listOf(colorStart, colorEnd)))
                .border(2.dp, ColorInk, shape)
        )
    }
}

@Composable
fun BrownSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = GameSpacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .cartoonShadow(3.dp, ColorInk, RoundedCornerShape(12.dp))
                .cartoonBorder(2.dp, ColorInk, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.verticalGradient(listOf(ColorPanelBrownLight, ColorPanelBrownDark)))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = title,
                style = GameTypography.sectionTitle,
                color = ColorCard,
                fontWeight = FontWeight.Black
            )
        }
        if (action != null) {
            action()
        }
    }
}

@Composable
fun ResourceChipRow(
    hp: Int,
    attack: Int,
    coins: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(GameSpacing.screenPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ResourceChip(icon = GameIconRole.HEAL, value = "$hp", bgColor = ColorDangerTop)
        ResourceChip(icon = GameIconRole.ATTACK, value = "$attack", bgColor = ColorSecondaryBottom)
        ResourceChip(icon = GameIconRole.GOLD, value = "$coins", bgColor = Color(0xFFFFD43F))
    }
}

@Composable
private fun ResourceChip(
    icon: GameIconRole,
    value: String,
    bgColor: Color
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .cartoonShadow(3.dp, ColorInk, shape)
            .cartoonBorder(2.dp, ColorInk, shape)
            .clip(shape)
            .background(ColorHudBg)
            .padding(start = 4.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(bgColor)
                .border(2.dp, ColorInk, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            GameIcon(icon = icon, fontSize = 26f)
        }
        Text(
            text = value,
            style = GameTypography.chipValue,
            color = ColorCard,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun SunburstBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "sunburst")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sunburst_rotation"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .rotate(rotation)
    ) {
        val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
        val radius = size.width.coerceAtLeast(size.height)
        val path = androidx.compose.ui.graphics.Path()

        val numRays = 12
        val angleStep = 360f / (numRays * 2)

        for (i in 0 until numRays * 2) {
            if (i % 2 == 0) {
                path.moveTo(center.x, center.y)
                val angle1 = Math.toRadians((i * angleStep).toDouble())
                val angle2 = Math.toRadians(((i + 1) * angleStep).toDouble())

                path.lineTo(
                    center.x + radius * kotlin.math.cos(angle1).toFloat(),
                    center.y + radius * kotlin.math.sin(angle1).toFloat()
                )
                path.lineTo(
                    center.x + radius * kotlin.math.cos(angle2).toFloat(),
                    center.y + radius * kotlin.math.sin(angle2).toFloat()
                )
                path.close()
            }
        }

        drawPath(
            path = path,
            color = Color(0x33FFFFFF) // Semi-transparent white rays
        )
    }
}

// ── Global Adventure Background ───────────────────────────────────────

@Composable
fun AdventureBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ColorScreenBg)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_home_dark_forest),
            contentDescription = "Adventure Background",
            contentScale = ContentScale.Crop,
            alpha = 0.4f, // Dim the bright background elements/moon
            modifier = Modifier.fillMaxSize()
        )
    }
}
