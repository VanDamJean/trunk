package com.yacoo.rpg.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.R
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
    onPickReward: (RewardChoice) -> Unit = {},
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
    
    var floatingDamage by remember { mutableStateOf<Pair<Int, Boolean>?>(null) }

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
        val rollPlan = createDiceRollPlan(previous = prev, held = heldMask, diceCount = diceCount)
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
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF05040A))
    ) {
        Image(
            painter = painterResource(R.drawable.bg_home_dark_forest),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.46f
        )
        CombatScreenBackdrop(modifier = Modifier.matchParentSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = combatBottomPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.11f)
                    .statusBarsPadding()
            ) {
                CompactCombatHUD(
                    stage = stage,
                    coins = run?.scrap ?: 0,
                    turnCount = turnCount,
                    isEnemyTurn = isEnemyTurn,
                    onExit = onExitCombat
                )
            }

            // Board arena: capped so it cannot starve the dice/action console.
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
                    .weight(0.38f)
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
                    .fillMaxWidth()
                    .weight(0.51f)
            )
        }

        // Overlay RewardPickScreen if there's a pending reward
        if (run?.pendingReward != null) {
            RewardPickScreen(
                run = run,
                language = language,
                onPickReward = onPickReward,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun CombatScreenBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawRect(
            brush = Brush.verticalGradient(
                listOf(
                    Color(0x66000000),
                    Color(0x3312051F),
                    Color(0xEE05040A)
                )
            )
        )
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color(0xDD0A0612), Color(0xFF05040A)),
                startY = size.height * 0.42f,
                endY = size.height
            ),
            topLeft = androidx.compose.ui.geometry.Offset(0f, size.height * 0.42f)
        )
        drawCircle(
            color = Color(0x2420C9FF),
            radius = size.width * 0.68f,
            center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.18f)
        )
        drawLine(
            color = Color(0x666B4A25),
            start = androidx.compose.ui.geometry.Offset(size.width * 0.06f, size.height * 0.505f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.94f, size.height * 0.505f),
            strokeWidth = 2.dp.toPx()
        )
        repeat(18) { i ->
            val x = size.width * ((i * 37 % 100) / 100f)
            val y = size.height * (0.07f + ((i * 23 % 36) / 100f))
            drawCircle(
                color = Color(0x77FFD86A),
                radius = (1.2f + (i % 3) * 0.55f).dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
        }
    }
}

@Composable
private fun CompactCombatHUD(
    stage: Int,
    coins: Int,
    turnCount: Int,
    isEnemyTurn: Boolean,
    onExit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .height(46.dp)
                .width(46.dp)
                .cartoonShadow(4.dp, Color(0xAA000000), RoundedCornerShape(15.dp))
                .cartoonBorder(3.dp, Color(0xFF9B6A34), RoundedCornerShape(15.dp))
                .clip(RoundedCornerShape(15.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF6B3E1F), Color(0xFF241109))))
                .clickable(onClick = onExit),
            contentAlignment = Alignment.Center
        ) {
            Text("II", color = Color(0xFFFFF0D0), fontSize = 18.sp, fontWeight = FontWeight.Black)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .padding(top = 18.dp)
                        .height(12.dp)
                        .width(166.dp)
                        .cartoonBorder(1.5.dp, Color(0xFF38200E), RoundedCornerShape(percent = 50))
                        .clip(RoundedCornerShape(percent = 50))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF5A3218), Color(0xFFD19B4A), Color(0xFF5A3218))))
                )
                Row(
                    modifier = Modifier
                        .height(39.dp)
                        .cartoonShadow(3.dp, Color(0xAA000000), RoundedCornerShape(16.dp))
                        .cartoonBorder(2.dp, Color(0xFFB37C3B), RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.verticalGradient(listOf(Color(0xFF201626), Color(0xFF090612))))
                        .padding(horizontal = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GameIcon(GameIconRole.STAR, fontSize = 14f, tint = Color(0xFFFFD86A))
                    Text(
                        text = "STAGE $stage",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black,
                                offset = androidx.compose.ui.geometry.Offset(0f, 3f),
                                blurRadius = 3f
                            )
                        )
                    )
                    GameIcon(GameIconRole.STAR, fontSize = 14f, tint = Color(0xFFFFD86A))
                }
            }

            Box(
                modifier = Modifier
                    .offset(y = (-2).dp)
                    .height(22.dp)
                    .width(118.dp)
                    .cartoonBorder(1.5.dp, Color(0xFF7A5228), RoundedCornerShape(percent = 50))
                    .clip(RoundedCornerShape(percent = 50))
                    .background(Brush.verticalGradient(listOf(Color(0xFFDCA657), Color(0xFF6C421E)))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isEnemyTurn) "Enemy Turn" else "Turn $turnCount",
                    color = if (isEnemyTurn) Color(0xFFFFEBE7) else Color(0xFFFFF6DC),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color(0x90000000),
                            offset = androidx.compose.ui.geometry.Offset(0f, 2f),
                            blurRadius = 2f
                        )
                    )
                )
            }
        }

        Row(
            modifier = Modifier
                .height(46.dp)
                .cartoonShadow(4.dp, Color(0xAA000000), RoundedCornerShape(percent = 50))
                .cartoonBorder(3.dp, Color(0xFF9B6A34), RoundedCornerShape(percent = 50))
                .clip(RoundedCornerShape(percent = 50))
                .background(Brush.verticalGradient(listOf(Color(0xFF1F1A16), Color(0xFF090706))))
                .padding(start = 4.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .cartoonBorder(2.dp, Color(0xFFFFD43F), CircleShape)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(Color(0xFFFFF0A8), Color(0xFFD38E16)))),
                contentAlignment = Alignment.Center
            ) {
                GameIcon(icon = GameIconRole.GOLD, fontSize = 22f, tint = Color(0xFF4A2B09))
            }
            Text(
                text = "$coins",
                color = Color(0xFFFFE070),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }
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
