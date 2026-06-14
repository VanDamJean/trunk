package com.yacoo.rpg.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import com.yacoo.rpg.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.game.*
import com.yacoo.rpg.ui.components.*
import com.yacoo.rpg.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class TurnPhase  { CHOOSING, ENEMY_TURN, OVER }
private enum class RollPhase  { IDLE, ROLLING, SETTLED }
private enum class StagePhase { APPROACH, FIGHT, VICTORY, ADVANCE }

private const val ROLL_ANIM_MS = 600L
private const val ROLL_TICK_MS = 55L
private const val ROLL_SETTLE_PREVIEW_MS = 165L

@Composable
fun CombatScreen(
    stage: Int,
    equipment: EquipmentSet,
    run: RunState? = null,
    language: AppLanguage = AppLanguage.KOREAN,
    onFinish: (CombatOutcome, YahtzeeHand?, Int) -> Unit,
    onExitCombat: () -> Unit = {},
    soundManager: com.yacoo.rpg.ui.components.SoundManager? = null,
    enemyTurnDelayMs: Long = Constants.CombatTiming.ENEMY_TURN_DELAY_MS,
    modifier: Modifier = Modifier
) {
    val labels = combatLabels(language)
    val combatBottomPadding = WindowInsets.safeDrawing
        .only(WindowInsetsSides.Bottom)
        .asPaddingValues()
        .calculateBottomPadding() + 12.dp
    val diceCount = run?.diceCount ?: Constants.CombatTiming.DICE_COUNT
    val maxRolls  = run?.maxRolls  ?: Constants.CombatTiming.MAX_ROLLS

    val enemy       = remember(stage) { createEnemy(stage) }
    val hero        = remember(equipment) { getHeroStats(equipment) }
    val autoDamage  = remember(equipment) { calculateHeroAutoDamage(equipment) }
    val enemyStrike = remember(enemy, equipment) { calculateEnemyDamage(enemy, equipment) }

    val haptic = rememberHapticManager()

    var heroHp   by remember(run, hero)  { mutableIntStateOf(run?.hp ?: hero.maxHp) }
    var enemyHp  by remember(enemy)      { mutableIntStateOf(enemy.maxHp) }

    var dice        by remember(stage, run) { mutableStateOf(List(diceCount) { 1 }) }
    var displayDice by remember(stage, run) { mutableStateOf(List(diceCount) { 1 }) }
    var held        by remember(stage, run) { mutableStateOf(List(diceCount) { false }) }
    var rollsLeft   by remember(stage, run) { mutableIntStateOf(maxRolls) }
    var turnPhase   by remember(stage, run) { mutableStateOf(TurnPhase.CHOOSING) }
    var rollPhase   by remember(stage, run) { mutableStateOf(RollPhase.IDLE) }
    var stagePhase  by remember(stage, run) { mutableStateOf(StagePhase.APPROACH) }
    var feedback    by remember(stage, run) { mutableStateOf("") }
    var turnCount   by remember(stage, run) { mutableIntStateOf(1) }
    var lastHand    by remember(stage, run) { mutableStateOf<YahtzeeHand?>(null) }
    
    // For floating damage numbers
    var floatingDamage by remember { mutableStateOf<Pair<Int, Boolean>?>(null) } // Damage amount, isHeroTarget

    val scope = rememberCoroutineScope()

    val validCategories = remember(dice, rollPhase) {
        if (rollPhase == RollPhase.SETTLED) getValidAttackCategories(dice) else emptyList()
    }

    val isChoosing  = turnPhase == TurnPhase.CHOOSING
    val isEnemyTurn = turnPhase == TurnPhase.ENEMY_TURN
    val canAttack   = isChoosing && rollPhase == RollPhase.SETTLED

    LaunchedEffect(stage) {
        stagePhase = StagePhase.APPROACH
        delay(400)
        stagePhase = StagePhase.FIGHT
    }

    fun startTurn() {
        dice = List(diceCount) { 1 }; displayDice = List(diceCount) { 1 }
        held = List(diceCount) { false }; rollsLeft = maxRolls
        rollPhase = RollPhase.IDLE; feedback = ""; turnPhase = TurnPhase.CHOOSING
    }

    fun startRolling(prev: List<DieValue>, heldMask: List<Boolean>) {
        val rollPlan = createDiceRollPlan(
            previous = prev,
            held = heldMask,
            diceCount = diceCount
        )
        rollPhase = RollPhase.ROLLING
        haptic.thump()
        soundManager?.playDiceRoll()
        scope.launch {
            var elapsed = 0L
            while (elapsed < ROLL_ANIM_MS) {
                val settling = elapsed >= ROLL_ANIM_MS - ROLL_SETTLE_PREVIEW_MS
                displayDice = List(diceCount) { i ->
                    if (heldMask.getOrElse(i) { false }) prev.getOrElse(i) { 1 }
                    else if (settling && i % 2 == (elapsed / ROLL_TICK_MS).toInt() % 2) rollPlan.targetFaces[i]
                    else rollDie()
                }
                delay(ROLL_TICK_MS); elapsed += ROLL_TICK_MS
            }
            val newDice = rollPlan.targetFaces
            dice = newDice; displayDice = newDice
            rollPhase = RollPhase.SETTLED
        }
    }

    fun showDamage(dmg: Int, isHero: Boolean) {
        scope.launch {
            floatingDamage = Pair(dmg, isHero)
            delay(800)
            floatingDamage = null
        }
    }

    fun resolveAttack(damage: Int, hand: YahtzeeHand? = null) {
        val next = clampHp(enemyHp - damage)
        enemyHp = next
        showDamage(damage, isHero = false)
        if (hand != null) lastHand = hand
        haptic.hit()
        soundManager?.playAttackHit()
        if (next <= 0) {
            turnPhase  = TurnPhase.OVER
            stagePhase = StagePhase.VICTORY
            feedback   = "${hand?.label ?: "Basic"}! Victory!"
            haptic.victory()
            scope.launch {
                delay(1200)
                stagePhase = StagePhase.ADVANCE
                delay(400)
                onFinish(CombatOutcome.WIN, hand ?: lastHand, heroHp)
            }
            return
        }
        feedback  = "$damage DMG!"
        turnPhase = TurnPhase.ENEMY_TURN
    }

    LaunchedEffect(turnPhase) {
        if (turnPhase != TurnPhase.ENEMY_TURN) return@LaunchedEffect
        delay(enemyTurnDelayMs)
        val nextHeroHp  = clampHp(heroHp - enemyStrike)
        heroHp = nextHeroHp
        showDamage(enemyStrike, isHero = true)
        haptic.hit()
        soundManager?.playAttackHit()
        if (nextHeroHp <= 0) {
            turnPhase = TurnPhase.OVER
            feedback  = "${enemy.name} ${enemyStrike} DMG. Defeat."
            onFinish(CombatOutcome.LOSS, null, nextHeroHp)
            return@LaunchedEffect
        }
        feedback   = "${enemy.name} ${enemyStrike} DMG"
        turnCount += 1
        startTurn()
    }

    val monsterScale by animateFloatAsState(
        targetValue = when (stagePhase) {
            StagePhase.APPROACH -> 0f
            StagePhase.VICTORY  -> 0f
            else                -> 1f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "monScale"
    )
    
    val heroX by animateFloatAsState(
        targetValue = if (stagePhase == StagePhase.ADVANCE) 200f else 0f,
        animationSpec = tween(600), label = "heroX"
    )

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_combat_arena),
            contentDescription = "Combat Arena Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = combatBottomPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopStatsBar(
                stage = stage,
                coins = run?.scrap ?: 0,
                gems = 0,
                power = hero.power,
                language = language
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                CombatExitButton(label = labels.exit, onClick = onExitCombat)
            }
            Spacer(modifier = Modifier.height(6.dp))
            // Turn Indicator (floating above combat)
            Box(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .cartoonShadow(3.dp, ColorInk, RoundedCornerShape(12.dp))
                    .cartoonBorder(2.dp, ColorInk, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isEnemyTurn) ColorDangerBottom else ColorSecondaryBottom)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isEnemyTurn) labels.enemyTurn else "${labels.turn} $turnCount",
                    style = GameTypography.chipValue,
                    color = ColorInk,
                    fontWeight = FontWeight.Black
                )
            }

            val nodeType = run?.map?.nodes?.getOrNull(run.nodeIndex)?.type
            CombatArena(
                equipment = equipment,
                heroHp = heroHp,
                heroMaxHp = hero.maxHp,
                heroX = heroX,
                enemyName = enemy.name,
                enemyHp = enemyHp,
                enemyMaxHp = enemy.maxHp,
                monsterScale = monsterScale,
                nodeType = nodeType,
                feedback = feedback,
                floatingDamage = floatingDamage,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
            )

            CombatDiceBoard(
                stage = stage,
                turnCount = turnCount,
                diceCount = diceCount,
                dice = dice,
                displayDice = if (rollPhase == RollPhase.IDLE) emptyList() else displayDice,
                held = held,
                rollsLeft = rollsLeft,
                rollPhase = when (rollPhase) {
                    RollPhase.IDLE -> CombatRollUiPhase.IDLE
                    RollPhase.ROLLING -> CombatRollUiPhase.ROLLING
                    RollPhase.SETTLED -> CombatRollUiPhase.SETTLED
                },
                canChoose = isChoosing,
                canAttack = canAttack,
                validCategories = validCategories,
                equipment = equipment,
                autoDamage = autoDamage,
                feedback = feedback,
                language = language,
                titleLabel = labels.title,
                turnLabel = labels.turn,
                rollDiceLabel = labels.rollDice,
                rollingLabel = labels.rolling,
                rerollLabel = labels.reroll,
                leftLabel = labels.left,
                handAttackLabel = labels.handAttack,
                basicAttackLabel = labels.basicAttack,
                onToggleHeld = { i ->
                    held = held.mapIndexed { idx, v -> if (idx == i) !v else v }
                    haptic.tick()
                },
                onInitialRoll = {
                    if (isChoosing) {
                        rollsLeft -= 1
                        startRolling(emptyList(), List(diceCount) { false })
                    }
                },
                onReroll = {
                    if (isChoosing && rollsLeft > 0) {
                        rollsLeft -= 1
                        startRolling(dice, held)
                    }
                },
                onCategoryAttack = { category ->
                    if (canAttack && category in validCategories) {
                        resolveAttack(calculateUltimateCategoryDamage(dice, category, equipment), category)
                    }
                },
                onBasicAttack = {
                    if (isChoosing && rollPhase != RollPhase.ROLLING) resolveAttack(autoDamage)
                },
                modifier = Modifier
                    .weight(2.2f)
            )
        }
    }
}

@Composable
private fun CombatExitButton(label: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .cartoonShadow(3.dp, ColorInk, shape)
            .cartoonBorder(2.dp, ColorInk, shape)
            .clip(shape)
            .background(Color(0xFFFFFDF9))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GameIcon(icon = GameIconRole.BACK, fontSize = 18f)
        Text(
            text = label,
            color = ColorInk,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
        )
    }
}

private data class CombatLabels(
    val title: String,
    val enemyTurn: String,
    val turn: String,
    val rollDice: String,
    val rolling: String,
    val reroll: String,
    val left: String,
    val handAttack: String,
    val basicAttack: String,
    val exit: String
)

private fun combatLabels(language: AppLanguage): CombatLabels = when (language) {
    AppLanguage.KOREAN -> CombatLabels("스테이지", "적 턴", "턴", "주사위 굴리기", "굴리는 중…", "리롤", "회 남음", "족보 공격", "기본 공격", "나가기")
    AppLanguage.ENGLISH -> CombatLabels("Stage", "Enemy Turn", "Turn", "Roll Dice", "Rolling…", "Reroll", " left", "Hand Attacks", "Basic Attack", "Exit")
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun CombatScreenPreview() {
    CombatScreen(
        stage     = 3,
        equipment = Constants.STARTING_EQUIPMENT,
        run       = null,
        onFinish  = { _, _, _ -> }
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812, name = "Combat English")
@Composable
private fun CombatScreenEnglishPreview() {
    CombatScreen(
        stage = 17,
        equipment = Constants.STARTING_EQUIPMENT,
        language = AppLanguage.ENGLISH,
        onFinish = { _, _, _ -> }
    )
}
