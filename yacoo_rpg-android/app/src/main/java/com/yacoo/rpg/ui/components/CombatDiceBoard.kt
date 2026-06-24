package com.yacoo.rpg.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.game.AppLanguage
import com.yacoo.rpg.game.DieValue
import com.yacoo.rpg.game.EquipmentSet
import com.yacoo.rpg.game.YahtzeeAttackCategory
import com.yacoo.rpg.game.calculateUltimateCategoryDamage
import com.yacoo.rpg.ui.theme.*

enum class CombatRollUiPhase { IDLE, ROLLING, SETTLED }

@Composable
fun CombatDiceBoard(
    stage: Int,
    turnCount: Int,
    diceCount: Int,
    dice: List<DieValue>,
    displayDice: List<DieValue>,
    held: List<Boolean>,
    rollsLeft: Int,
    rollPhase: CombatRollUiPhase,
    canChoose: Boolean,
    canAttack: Boolean,
    validCategories: List<YahtzeeAttackCategory>,
    equipment: EquipmentSet,
    autoDamage: Int,
    feedback: String,
    language: AppLanguage,
    titleLabel: String,
    turnLabel: String,
    rollDiceLabel: String,
    rollingLabel: String,
    rerollLabel: String,
    leftLabel: String,
    handAttackLabel: String,
    basicAttackLabel: String,
    onToggleHeld: (Int) -> Unit,
    onInitialRoll: () -> Unit,
    onReroll: () -> Unit,
    onCategoryAttack: (YahtzeeAttackCategory) -> Unit,
    onBasicAttack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .cartoonShadow(8.dp, Color(0xCC000000), RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
            .cartoonBorder(4.dp, Color(0xFF050308), RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF2C2445), Color(0xFF110B1D), Color(0xFF05040A)),
                    radius = 780f
                )
            )
            .padding(horizontal = 9.dp, vertical = 8.dp)
    ) {
        val compact = maxHeight < 380.dp
        val gap = if (compact) 5.dp else 7.dp
        val diceHeight = if (compact) 62.dp else 78.dp
        val summaryHeight = if (compact) 42.dp else 50.dp
        val ctaHeight = if (compact) 52.dp else 64.dp
        val bestCategory = if (canAttack && validCategories.isNotEmpty()) {
            validCategories.maxByOrNull { calculateUltimateCategoryDamage(dice, it, equipment) }
        } else null
        val bestDamage = bestCategory?.let { calculateUltimateCategoryDamage(dice, it, equipment) }

        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                color = Color(0x55100A18),
                topLeft = Offset(7.dp.toPx(), 7.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(size.width - 14.dp.toPx(), size.height - 14.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx(), 24.dp.toPx()),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
            )
            val stitch = Color(0x55C8A37A)
            val step = 26.dp.toPx()
            var x = 18.dp.toPx()
            while (x < size.width - 18.dp.toPx()) {
                drawLine(stitch, Offset(x, 12.dp.toPx()), Offset(x + 9.dp.toPx(), 12.dp.toPx()), strokeWidth = 1.5.dp.toPx())
                drawLine(stitch, Offset(x, size.height - 12.dp.toPx()), Offset(x + 9.dp.toPx(), size.height - 12.dp.toPx()), strokeWidth = 1.5.dp.toPx())
                x += step
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(gap)
        ) {
            DiceTray(
                diceCount = diceCount,
                displayDice = displayDice,
                held = held,
                rolling = rollPhase == CombatRollUiPhase.ROLLING,
                canAttack = canAttack,
                onToggleHeld = onToggleHeld,
                compact = compact,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(diceHeight)
            )

            SelectedHandPanel(
                rollPhase = rollPhase,
                bestCategory = bestCategory,
                bestDamage = bestDamage,
                rollDiceLabel = rollDiceLabel,
                rollingLabel = rollingLabel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(summaryHeight)
            )

            HandAttackGrid(
                dice = dice,
                canAttack = canAttack,
                validCategories = validCategories,
                equipment = equipment,
                bestCategory = bestCategory,
                compact = compact,
                onCategoryAttack = onCategoryAttack,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            CombatCtaRow(
                rollPhase = rollPhase,
                canChoose = canChoose,
                rollsLeft = rollsLeft,
                autoDamage = autoDamage,
                rollDiceLabel = rollDiceLabel,
                rollingLabel = rollingLabel,
                rerollLabel = rerollLabel,
                basicAttackLabel = basicAttackLabel,
                onInitialRoll = onInitialRoll,
                onReroll = onReroll,
                onBasicAttack = onBasicAttack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ctaHeight)
            )
        }
    }
}

@Composable
private fun SelectedHandPanel(
    rollPhase: CombatRollUiPhase,
    bestCategory: YahtzeeAttackCategory?,
    bestDamage: Int?,
    rollDiceLabel: String,
    rollingLabel: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(15.dp)
    val label = when (rollPhase) {
        CombatRollUiPhase.IDLE -> rollDiceLabel
        CombatRollUiPhase.ROLLING -> rollingLabel
        CombatRollUiPhase.SETTLED -> bestCategory?.label ?: rollDiceLabel
    }

    Row(
        modifier = modifier
            .cartoonShadow(5.dp, Color(0xBB000000), shape)
            .cartoonBorder(3.dp, Color(0xFFFFD43F), shape)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF251705), Color(0xFF080A10), Color(0xFF251705))
                )
            )
            .padding(horizontal = 11.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .cartoonBorder(2.dp, Color(0xFF6B4A25), RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFFFFE9AA), Color(0xFF986A30)))),
                contentAlignment = Alignment.Center
            ) {
                if (bestCategory != null) {
                    HandCategoryIcon(category = bestCategory, active = true, modifier = Modifier.fillMaxSize().padding(5.dp))
                } else {
                    GameIcon(icon = GameIconRole.DICE, fontSize = 20f)
                }
            }
            Text(
                text = label,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (bestDamage != null) {
            Text(
                text = "$bestDamage DMG",
                color = Color(0xFFFFD43F),
                fontSize = 19.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun HandAttackGrid(
    dice: List<DieValue>,
    canAttack: Boolean,
    validCategories: List<YahtzeeAttackCategory>,
    equipment: EquipmentSet,
    bestCategory: YahtzeeAttackCategory?,
    compact: Boolean,
    onCategoryAttack: (YahtzeeAttackCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = YahtzeeAttackCategory.entries.chunked(3)
    Box(
        modifier = modifier
            .cartoonBorder(2.5.dp, Color(0xFF3B2817), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF1B1430), Color(0xFF0A0710))))
            .padding(if (compact) 4.dp else 6.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val glow = Color(0x22FFD43F)
            drawRoundRect(
                color = glow,
                topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(size.width - 4.dp.toPx(), size.height - 4.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx(), 14.dp.toPx()),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.4.dp.toPx())
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(if (compact) 5.dp else 7.dp)
        ) {
            rows.forEach { rowCategories ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(if (compact) 5.dp else 7.dp)
                ) {
                    rowCategories.forEach { category ->
                        val isValid = category in validCategories
                        val previewDmg = if (canAttack && isValid) calculateUltimateCategoryDamage(dice, category, equipment) else null
                        HandAttackChip(
                            category = category,
                            isValid = isValid,
                            isBest = category == bestCategory,
                            canAttack = canAttack,
                            previewDmg = previewDmg,
                            compact = compact,
                            onClick = { onCategoryAttack(category) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiceTray(
    diceCount: Int,
    displayDice: List<DieValue>,
    held: List<Boolean>,
    rolling: Boolean,
    canAttack: Boolean,
    onToggleHeld: (Int) -> Unit,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    val trayShape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .cartoonShadow(6.dp, Color(0xBB000000), trayShape)
            .cartoonBorder(4.dp, Color(0xFF6B3A1B), trayShape)
            .clip(trayShape)
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFFC08440), Color(0xFF6F3A1D), Color(0xFF210E07)),
                    radius = 520f
                )
            )
            .padding(if (compact) 8.dp else 10.dp)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF84231D), Color(0xFF3B0D12))))
        )
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                color = Color(0x66100804),
                topLeft = Offset(9.dp.toPx(), 9.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(size.width - 18.dp.toPx(), size.height - 18.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
            )
            repeat(diceCount) { i ->
                val cellW = (size.width - 30.dp.toPx()) / diceCount
                val cx = 15.dp.toPx() + cellW * i + cellW / 2f
                drawRoundRect(
                    color = Color(0x55000000),
                    topLeft = Offset(cx - cellW * 0.38f, size.height * 0.18f),
                    size = androidx.compose.ui.geometry.Size(cellW * 0.76f, size.height * 0.64f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx(), 12.dp.toPx())
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (compact) 8.dp else 10.dp, vertical = if (compact) 5.dp else 7.dp),
            horizontalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(diceCount) { i ->
                AnimatedDie(
                    value = if (displayDice.isEmpty()) null else displayDice.getOrNull(i),
                    held = held.getOrElse(i) { false },
                    rolling = rolling && !held.getOrElse(i) { false },
                    enabled = canAttack,
                    onClick = { if (canAttack) onToggleHeld(i) },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                )
            }
        }
    }
}

@Composable
private fun HandAttackChip(
    category: YahtzeeAttackCategory,
    isValid: Boolean,
    isBest: Boolean,
    canAttack: Boolean,
    previewDmg: Int?,
    compact: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val enabled = canAttack && isValid
    val bgBrush = when {
        isBest -> Brush.radialGradient(listOf(Color(0xFF674405), Color(0xFF15100B), Color(0xFF050307)), radius = 240f)
        enabled -> Brush.radialGradient(listOf(Color(0xFF182237), Color(0xFF070A10)), radius = 220f)
        else -> Brush.verticalGradient(listOf(Color(0xFFF6E3B7), Color(0xFFD1AD6A)))
    }
    val textColor = when {
        isBest -> Color(0xFFFFF0A5)
        enabled -> Color(0xFFFFF0A5)
        else -> Color(0xFF3B2819)
    }
    val borderColor = when {
        isBest -> Color(0xFFFFD43F)
        enabled -> Color(0xFFFFD43F)
        else -> Color(0xFF07050B)
    }
    val shape = RoundedCornerShape(12.dp)
    val textSize = if (compact) 9.sp else 10.5f.sp
    val label = category.label.replace(" of a ", "\nof a ").replace(" Straight", "\nStraight")

    Box(
        modifier = modifier
            .offset(y = if (isBest) (-2).dp else 0.dp)
            .cartoonShadow(if (isBest) 4.dp else 2.dp, Color(0x80000000), shape)
            .cartoonBorder(if (isBest) 3.dp else 2.dp, borderColor, shape)
            .clip(shape)
            .background(bgBrush)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 4.dp, vertical = if (compact) 4.dp else 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                color = if (enabled || isBest) Color(0x33FFFFFF) else Color(0x44FFFFFF),
                topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(size.width - 8.dp.toPx(), size.height - 8.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(9.dp.toPx(), 9.dp.toPx()),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
            )
            if (isBest) {
                drawRoundRect(
                    color = Color(0x33FFD43F),
                    topLeft = Offset(8.dp.toPx(), 7.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(size.width - 16.dp.toPx(), size.height * 0.22f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HandCategoryIcon(
                category = category,
                active = isBest || enabled,
                modifier = Modifier
                    .height(if (compact) 15.dp else 18.dp)
                    .fillMaxWidth()
            )
            Text(
                text = label,
                color = textColor,
                fontSize = textSize,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = (textSize.value + 1.2f).sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = if (compact) 1.dp else 2.dp)
            )
            if (previewDmg != null) {
                Text(
                    text = "$previewDmg DMG",
                    color = Color.White,
                    fontSize = if (compact) 8.sp else 9.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(top = if (compact) 1.dp else 2.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Brush.verticalGradient(listOf(Color(0xFFFF5A48), Color(0xFFC91E20))))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun HandCategoryIcon(
    category: YahtzeeAttackCategory,
    active: Boolean,
    modifier: Modifier = Modifier
) {
    val iconColor = if (active) Color(0xFFFFD43F) else Color(0xFF6B4A25)
    val shadow = Color(0x77000000)
    Canvas(modifier = modifier) {
        val centerY = size.height * 0.52f
        val step = size.width / 7f
        val radius = size.height * 0.18f
        when (category) {
            YahtzeeAttackCategory.CHANCE -> {
                drawCircle(shadow, radius * 1.2f, Offset(size.width * 0.5f + 1.5f, centerY + 1.5f))
                drawCircle(iconColor, radius * 1.2f, Offset(size.width * 0.5f, centerY))
            }
            YahtzeeAttackCategory.PAIR,
            YahtzeeAttackCategory.TWO_PAIR -> {
                val cards = if (category == YahtzeeAttackCategory.PAIR) 2 else 4
                repeat(cards) { i ->
                    val x = size.width * 0.5f + (i - (cards - 1) / 2f) * step * 0.65f
                    drawRoundRect(
                        color = iconColor,
                        topLeft = Offset(x - radius * 1.1f, centerY - radius * 1.35f),
                        size = androidx.compose.ui.geometry.Size(radius * 2.2f, radius * 2.7f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius * 0.45f, radius * 0.45f)
                    )
                }
            }
            YahtzeeAttackCategory.THREE_KIND,
            YahtzeeAttackCategory.FOUR_KIND,
            YahtzeeAttackCategory.FULL_HOUSE,
            YahtzeeAttackCategory.YAHTZEE -> {
                val dots = when (category) {
                    YahtzeeAttackCategory.THREE_KIND -> 3
                    YahtzeeAttackCategory.FOUR_KIND -> 4
                    YahtzeeAttackCategory.FULL_HOUSE -> 5
                    else -> 5
                }
                repeat(dots) { i ->
                    val x = size.width * 0.5f + (i - (dots - 1) / 2f) * step * 0.55f
                    drawCircle(shadow, radius, Offset(x + 1f, centerY + 1f))
                    drawCircle(iconColor, radius, Offset(x, centerY))
                }
            }
            YahtzeeAttackCategory.SMALL_STRAIGHT,
            YahtzeeAttackCategory.LARGE_STRAIGHT -> {
                val dots = if (category == YahtzeeAttackCategory.SMALL_STRAIGHT) 4 else 5
                repeat(dots) { i ->
                    val x = size.width * 0.5f + (i - (dots - 1) / 2f) * step * 0.62f
                    drawCircle(iconColor, radius * 0.82f, Offset(x, centerY))
                    if (i > 0) {
                        val prevX = size.width * 0.5f + (i - 1 - (dots - 1) / 2f) * step * 0.62f
                        drawLine(iconColor, Offset(prevX, centerY), Offset(x, centerY), strokeWidth = radius * 0.55f)
                    }
                }
            }
        }
    }
}

@Composable
private fun CombatCtaRow(
    rollPhase: CombatRollUiPhase,
    canChoose: Boolean,
    rollsLeft: Int,
    autoDamage: Int,
    rollDiceLabel: String,
    rollingLabel: String,
    rerollLabel: String,
    basicAttackLabel: String,
    onInitialRoll: () -> Unit,
    onReroll: () -> Unit,
    onBasicAttack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        when (rollPhase) {
            CombatRollUiPhase.IDLE -> {
                PrimaryActionButton(
                    text = rollDiceLabel,
                    icon = GameIconRole.DICE,
                    onClick = onInitialRoll,
                    enabled = canChoose,
                    colorStart = Color(0xFF64E7F4),
                    colorEnd = Color(0xFF1E94BD),
                    shadowColor = Color(0xFF0C4D6B),
                    borderColor = Color(0xFF38B2C4),
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }

            CombatRollUiPhase.ROLLING -> {
                PrimaryActionButton(
                    text = rollingLabel,
                    icon = GameIconRole.DICE,
                    onClick = {},
                    enabled = false,
                    colorStart = Color(0xFF42374E),
                    colorEnd = Color(0xFF201B29),
                    shadowColor = Color(0xFF110D1A),
                    borderColor = Color(0xFF2C2438),
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }

            CombatRollUiPhase.SETTLED -> {
                PrimaryActionButton(
                    text = "$rerollLabel ($rollsLeft)",
                    icon = GameIconRole.RESET,
                    onClick = onReroll,
                    enabled = canChoose && rollsLeft > 0,
                    colorStart = Color(0xFF64E7F4),
                    colorEnd = Color(0xFF1E94BD),
                    shadowColor = Color(0xFF0C4D6B),
                    borderColor = Color(0xFF38B2C4),
                    modifier = Modifier.weight(0.92f).fillMaxHeight()
                )
                PrimaryActionButton(
                    text = attackButtonLabel(basicAttackLabel, autoDamage),
                    icon = GameIconRole.WEAPON,
                    onClick = onBasicAttack,
                    enabled = canChoose,
                    colorStart = Color(0xFFFFE470),
                    colorEnd = Color(0xFFE99D13),
                    shadowColor = Color(0xFF6F4304),
                    borderColor = Color(0xFFE8AD26),
                    modifier = Modifier.weight(1.08f).fillMaxHeight()
                )
            }
        }
    }
}

private fun attackButtonLabel(label: String, damage: Int): String {
    return if (label.equals("Basic Attack", ignoreCase = true)) "Attack $damage" else "$label $damage"
}

@Composable
private fun PrimaryActionButton(
    text: String,
    icon: GameIconRole,
    onClick: () -> Unit,
    enabled: Boolean,
    colorStart: Color,
    colorEnd: Color,
    shadowColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 5.dp)
                .clip(shape)
                .background(shadowColor)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .cartoonBorder(if (enabled) 3.5.dp else 2.dp, borderColor, shape)
                .clip(shape)
                .background(
                    if (enabled) Brush.verticalGradient(listOf(colorStart, colorEnd))
                    else Brush.verticalGradient(listOf(Color(0xFF3A2A60), Color(0xFF211A3A)))
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawRoundRect(
                    color = Color(0x44FFFFFF),
                    topLeft = Offset(8.dp.toPx(), 6.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(size.width - 16.dp.toPx(), size.height * 0.34f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx(), 12.dp.toPx())
                )
            }
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                GameIcon(
                    icon = icon,
                    fontSize = 24f,
                    tint = if (enabled) Color(0xFF090612) else Color(0xFF8E8A9F)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = text,
                    color = if (enabled) Color(0xFF090612) else Color(0xFF8E8A9F),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
