package com.yacoo.rpg.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.yacoo.rpg.game.AppLanguage
import com.yacoo.rpg.game.DieValue
import com.yacoo.rpg.game.EquipmentSet
import com.yacoo.rpg.game.YahtzeeAttackCategory
import com.yacoo.rpg.game.calculateUltimateCategoryDamage
import com.yacoo.rpg.ui.theme.ColorInk
import com.yacoo.rpg.ui.theme.ColorPanelBrownDark
import com.yacoo.rpg.ui.theme.ColorPanelBrownLight
import com.yacoo.rpg.ui.theme.ColorSurfacePanel
import com.yacoo.rpg.ui.theme.GameIconRole
import com.yacoo.rpg.ui.theme.cartoonBorder
import com.yacoo.rpg.ui.theme.cartoonShadow
import com.yacoo.rpg.ui.theme.staggerSlideIn

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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .cartoonShadow(5.dp, ColorInk, RoundedCornerShape(20.dp))
            .cartoonBorder(2.5.dp, ColorInk, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(ColorSurfacePanel)
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BattleLogCard(
                title = "$titleLabel $stage · $turnLabel $turnCount",
                body = feedback.ifEmpty {
                    if (language == AppLanguage.KOREAN) "영웅이 다음 공격을 준비합니다." else "The hero prepares the next strike."
                },
                modifier = Modifier.staggerSlideIn(delayMs = 80)
            )

            DiceTray(
                diceCount = diceCount,
                displayDice = displayDice,
                held = held,
                rolling = rollPhase == CombatRollUiPhase.ROLLING,
                canAttack = canAttack,
                onToggleHeld = onToggleHeld
            )

            when (rollPhase) {
                CombatRollUiPhase.IDLE -> PrimaryButton(
                    text = "${GameIconRole.DICE.fallback} $rollDiceLabel",
                    onClick = onInitialRoll,
                    enabled = canChoose,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                )
                CombatRollUiPhase.ROLLING -> PrimaryButton(
                    text = rollingLabel,
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                )
                CombatRollUiPhase.SETTLED -> SecondaryButton(
                    text = "$rerollLabel ($rollsLeft$leftLabel)",
                    onClick = onReroll,
                    enabled = canChoose && rollsLeft > 0,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                )
            }

            BrownSectionHeader(title = handAttackLabel)
            YahtzeeAttackCategory.entries.chunked(2).forEach { rowCategories ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    rowCategories.forEach { category ->
                        val isValid = category in validCategories
                        val previewDmg = if (canAttack && isValid) {
                            calculateUltimateCategoryDamage(dice, category, equipment)
                        } else {
                            null
                        }

                        CombatActionCard(
                            category = category,
                            isValid = isValid,
                            canAttack = canAttack,
                            previewDmg = previewDmg,
                            onClick = { onCategoryAttack(category) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowCategories.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            SecondaryButton(
                text = "$basicAttackLabel ($autoDamage)",
                onClick = onBasicAttack,
                enabled = canChoose && rollPhase != CombatRollUiPhase.ROLLING,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            )
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
    onToggleHeld: (Int) -> Unit
) {
    val trayShape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .cartoonShadow(2.dp, ColorInk, trayShape)
            .cartoonBorder(2.dp, ColorInk, trayShape)
            .clip(trayShape)
            .background(Brush.verticalGradient(listOf(ColorPanelBrownLight, ColorPanelBrownDark)))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(diceCount) { i ->
            AnimatedDie(
                value = if (displayDice.isEmpty()) null else displayDice.getOrNull(i),
                held = held.getOrElse(i) { false },
                rolling = rolling && !held.getOrElse(i) { false },
                enabled = canAttack,
                onClick = { if (canAttack) onToggleHeld(i) },
                modifier = Modifier.weight(1f).aspectRatio(1f)
            )
        }
    }
}
