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

@Composable
fun CombatScreen(
    stage: Int,
    equipment: EquipmentSet,
    run: RunState? = null,
    language: AppLanguage = AppLanguage.KOREAN,
    onFinish: (CombatOutcome, YahtzeeHand?, Int) -> Unit,
    soundManager: com.yacoo.rpg.ui.components.SoundManager? = null,
    enemyTurnDelayMs: Long = Constants.CombatTiming.ENEMY_TURN_DELAY_MS,
    modifier: Modifier = Modifier
) {
    val labels = combatLabels(language)
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
        rollPhase = RollPhase.ROLLING
        haptic.thump()
        soundManager?.playDiceRoll()
        scope.launch {
            var elapsed = 0L
            while (elapsed < ROLL_ANIM_MS) {
                displayDice = List(diceCount) { i ->
                    if (heldMask.getOrElse(i) { false }) prev.getOrElse(i) { 1 }
                    else rollDie()
                }
                delay(ROLL_TICK_MS); elapsed += ROLL_TICK_MS
            }
            val newDice = rollDice(prev, heldMask)
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
            modifier = Modifier.fillMaxSize(),
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

            // VS Stage Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
                    .padding(horizontal = 16.dp)
            ) {
                // Hero Side (Left)
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = heroX.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(70.dp), contentAlignment = Alignment.Center) {
                        HeroPaperdollCanvas(equipment = equipment, size = 54.dp)
                        
                        // Floating damage for hero
                        Column {
                            AnimatedVisibility(
                                visible = floatingDamage?.second == true,
                                enter = slideInVertically { it } + fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(
                                    text = "-${floatingDamage?.first ?: 0}",
                                    style = GameTypography.screenTitle,
                                    color = ColorDangerBottom,
                                    fontSize = 30.sp,
                                    modifier = Modifier.offset(y = (-50).dp).bounceIn()
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    HpBar(
                        current = heroHp,
                        max = hero.maxHp,
                        color = ColorHpFillStart,
                        modifier = Modifier.width(80.dp)
                    )
                }

                // Monster Side (Right)
                Column(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(70.dp), contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.scale(monsterScale)) {
                            val nodeType = run?.map?.nodes?.getOrNull(run.nodeIndex)?.type
                            MonsterCanvas(nodeType = nodeType, modifier = Modifier.size(54.dp))
                        }
                        
                        // Floating damage for monster
                        Column {
                            AnimatedVisibility(
                                visible = floatingDamage?.second == false,
                                enter = slideInVertically { it } + fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(
                                    text = "-${floatingDamage?.first ?: 0}",
                                    style = GameTypography.screenTitle,
                                    color = Color(0xFFFFD43F), // Yellow critical hit color
                                    fontSize = 34.sp, // Slightly larger for impact
                                    modifier = Modifier.offset(y = (-50).dp).bounceIn()
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    HpBar(
                        current = enemyHp,
                        max = enemy.maxHp,
                        color = ColorDangerTop,
                        modifier = Modifier.width(80.dp)
                    )
                    Text(
                        text = enemy.name,
                        style = GameTypography.caption,
                        color = ColorCard,
                        fontWeight = FontWeight.Black
                    )
                }
                
                // Center Feedback text
                if (feedback.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-20).dp)
                            .cartoonBorder(2.dp, ColorInk, RoundedCornerShape(12.dp))
                            .background(ColorInkStrong.copy(alpha = 0.8f))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = feedback,
                            style = GameTypography.buttonSmall,
                            color = ColorCard,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Lower Action Board
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .weight(2.2f)
                    .cartoonShadow(5.dp, ColorInk, RoundedCornerShape(20.dp))
                    .cartoonBorder(2.5.dp, ColorInk, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(ColorSurfacePanel)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Dice tray area
                    BattleLogCard(
                        title = "${labels.title} $stage · ${labels.turn} $turnCount",
                        body = feedback.ifEmpty { if (language == AppLanguage.KOREAN) "영웅이 다음 공격을 준비합니다." else "The hero prepares the next strike." },
                        modifier = Modifier.staggerSlideIn(delayMs = 80)
                    )

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
                                value   = if (rollPhase == RollPhase.IDLE) null else displayDice.getOrNull(i),
                                held    = held.getOrElse(i) { false },
                                rolling = rollPhase == RollPhase.ROLLING && !held.getOrElse(i) { false },
                                enabled = canAttack,
                                onClick = {
                                    if (canAttack) {
                                        held = held.mapIndexed { idx, v -> if (idx == i) !v else v }
                                        haptic.tick()
                                    }
                                },
                                modifier = Modifier.weight(1f).aspectRatio(1f)
                            )
                        }
                    }

                    // Roll commands
                    when (rollPhase) {
                        RollPhase.IDLE -> PrimaryButton(
                            text    = "${GameIconRole.DICE.fallback} ${labels.rollDice}",
                            onClick = {
                                if (isChoosing) { rollsLeft -= 1; startRolling(emptyList(), List(diceCount) { false }) }
                            },
                            enabled = isChoosing,
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        )
                        RollPhase.ROLLING -> PrimaryButton(
                            text = labels.rolling,
                            onClick = {},
                            enabled = false,
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        )
                        RollPhase.SETTLED -> SecondaryButton(
                            text    = "${labels.reroll} ($rollsLeft${labels.left})",
                            onClick = {
                                if (isChoosing && rollsLeft > 0) { rollsLeft -= 1; startRolling(dice, held) }
                            },
                            enabled = isChoosing && rollsLeft > 0,
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        )
                    }

                    BrownSectionHeader(title = labels.handAttack)
                    
                    // Hand items in 2-column grid
                    val categories = YahtzeeAttackCategory.entries
                    val chunked = categories.chunked(2)
                    chunked.forEach { rowCategories ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowCategories.forEach { category ->
                                val isValid   = category in validCategories
                                val previewDmg = if (canAttack && isValid)
                                    calculateUltimateCategoryDamage(dice, category, equipment) else null

                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    CombatActionCard(
                                        category   = category,
                                        isValid    = isValid,
                                        canAttack  = canAttack,
                                        previewDmg = previewDmg,
                                        onClick    = {
                                            if (canAttack && isValid) resolveAttack(
                                                calculateUltimateCategoryDamage(dice, category, equipment), category
                                            )
                                        }
                                    )
                                }
                            }
                            if (rowCategories.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // Auto basic attack
                    SecondaryButton(
                        text    = "${labels.basicAttack} ($autoDamage)",
                        onClick = { if (isChoosing && rollPhase != RollPhase.ROLLING) resolveAttack(autoDamage) },
                        enabled = isChoosing && rollPhase != RollPhase.ROLLING,
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    )
                }
            }
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
    val basicAttack: String
)

private fun combatLabels(language: AppLanguage): CombatLabels = when (language) {
    AppLanguage.KOREAN -> CombatLabels("스테이지", "적 턴", "턴", "주사위 굴리기", "굴리는 중…", "리롤", "회 남음", "족보 공격", "기본 공격")
    AppLanguage.ENGLISH -> CombatLabels("Stage", "Enemy Turn", "Turn", "Roll Dice", "Rolling…", "Reroll", " left", "Hand Attacks", "Basic Attack")
}

@Composable
private fun CombatActionCard(
    category: YahtzeeAttackCategory,
    isValid: Boolean,
    canAttack: Boolean,
    previewDmg: Int?,
    onClick: () -> Unit
) {
    val enabled = canAttack && isValid
    val shape = RoundedCornerShape(16.dp)
    
    val bg = if (isValid) ColorHeld else ColorCard
    val borderColor = if (isValid) ColorPrimaryBottom else ColorMuted
    val borderThickness = if (isValid) 3.dp else 2.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp, end = 4.dp)
            .cartoonShadow(if (isValid) 3.dp else 0.dp, ColorInk, shape)
            .cartoonBorder(strokeWidth = borderThickness, color = borderColor, shape = shape)
            .clip(shape)
            .background(bg)
            .then(if (enabled) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                category.label,
                fontWeight = FontWeight.Black,
                color      = if (isValid) ColorInkStrong else ColorInkSoft,
                fontSize   = 12.sp,
                maxLines   = 1
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "x${category.multiplier}",
                    fontWeight = FontWeight.Black,
                    color      = if (isValid) ColorPrimaryBottom else ColorInkSoft,
                    fontSize   = 11.sp
                )
                if (previewDmg != null) {
                    Text(
                        text = "$previewDmg DMG",
                        fontWeight = FontWeight.Black,
                        color      = ColorPrimaryBottom,
                        fontSize   = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BattleLogCard(title: String, body: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .cartoonShadow(3.dp, ColorInk, shape)
            .cartoonBorder(2.dp, ColorInk, shape)
            .clip(shape)
            .background(ColorParchmentLight)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(title, color = ColorInk, fontSize = 14.sp, fontWeight = FontWeight.Black)
        Text(body, color = ColorPanelBrownDark, fontSize = 12.sp, fontWeight = FontWeight.Black)
    }
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
