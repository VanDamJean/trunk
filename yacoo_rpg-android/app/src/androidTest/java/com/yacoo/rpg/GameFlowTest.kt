package com.yacoo.rpg

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yacoo.rpg.game.CombatOutcome
import com.yacoo.rpg.game.CombatResult
import com.yacoo.rpg.game.Constants
import com.yacoo.rpg.game.YahtzeeAttackCategory
import com.yacoo.rpg.game.createDefaultMeta
import com.yacoo.rpg.game.createRun
import com.yacoo.rpg.game.getHeroStats
import com.yacoo.rpg.game.generateNodeReward
import com.yacoo.rpg.game.NodeType
import com.yacoo.rpg.ui.screens.CombatScreen
import com.yacoo.rpg.ui.screens.EquipmentScreen
import com.yacoo.rpg.ui.screens.HomeScreen
import com.yacoo.rpg.ui.screens.ResultScreen
import com.yacoo.rpg.ui.screens.RewardPickScreen
import com.yacoo.rpg.ui.screens.RunMapScreen
import com.yacoo.rpg.ui.screens.RunResultScreen
import com.yacoo.rpg.ui.screens.UpgradeScreen
import com.yacoo.rpg.ui.theme.YacooTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose UI smoke tests for each Screen. Verifies the primary content renders
 * and key buttons are clickable. Run with `./gradlew connectedAndroidTest`
 * (requires a running emulator or device).
 */
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun displaysStartCombatButton() {
        val meta = createDefaultMeta()
        composeRule.setContent {
            YacooTheme {
                HomeScreen(
                    meta          = meta,
                    heroStats     = getHeroStats(meta.equipment),
                    onStartCombat = {},
                    onNavigate    = {},
                    onReset       = {}
                )
            }
        }
        composeRule.onNodeWithText("⚔️  Start Combat").assertIsDisplayed()
    }

    @Test
    fun startCombatClickTriggersCallback() {
        val meta = createDefaultMeta()
        var clicked = false
        composeRule.setContent {
            YacooTheme {
                HomeScreen(
                    meta          = meta,
                    heroStats     = getHeroStats(meta.equipment),
                    onStartCombat = { clicked = true },
                    onNavigate    = {},
                    onReset       = {}
                )
            }
        }
        composeRule.onNodeWithText("⚔️  Start Combat").performClick()
        assert(clicked)
    }

    @Test
    fun displaysGearAndUpgradeButtons() {
        val meta = createDefaultMeta()
        composeRule.setContent {
            YacooTheme {
                HomeScreen(
                    meta          = meta,
                    heroStats     = getHeroStats(meta.equipment),
                    onStartCombat = {},
                    onNavigate    = {},
                    onReset       = {}
                )
            }
        }
        composeRule.onNodeWithText("🛡 Gear").assertIsDisplayed()
        composeRule.onNodeWithText("⬆️ Upgrade").assertIsDisplayed()
    }
}

@RunWith(AndroidJUnit4::class)
class CombatScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun displaysStageHeaderAndRollButton() {
        composeRule.setContent {
            YacooTheme {
                CombatScreen(
                    stage     = 3,
                    equipment = Constants.STARTING_EQUIPMENT,
                    run       = null,
                    onFinish  = { _, _, _ -> }
                )
            }
        }
        composeRule.onNodeWithText("전투 — Stage 3").assertIsDisplayed()
        composeRule.onNodeWithText("🎲 주사위 굴리기").assertIsDisplayed()
    }
}

@RunWith(AndroidJUnit4::class)
class EquipmentScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun displaysTitleAndWeaponName() {
        composeRule.setContent {
            YacooTheme {
                EquipmentScreen(equipment = Constants.STARTING_EQUIPMENT)
            }
        }
        composeRule.onNodeWithText("Equipment").assertIsDisplayed()
        composeRule.onNodeWithText("Twig Wand").assertIsDisplayed()
    }
}

@RunWith(AndroidJUnit4::class)
class UpgradeScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun displaysTitleAndCoins() {
        val meta = createDefaultMeta().copy(coins = 250)
        composeRule.setContent {
            YacooTheme {
                UpgradeScreen(meta = meta, onUpgrade = {})
            }
        }
        composeRule.onNodeWithText("Upgrade").assertIsDisplayed()
        composeRule.onNodeWithText("250 coins").assertIsDisplayed()
    }
}

@RunWith(AndroidJUnit4::class)
class ResultScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun winDisplaysVictoryAndClaimButton() {
        composeRule.setContent {
            YacooTheme {
                ResultScreen(
                    result = CombatResult(
                        outcome     = CombatOutcome.WIN,
                        stage       = 3,
                        coinsEarned = 60,
                        handUsed    = YahtzeeAttackCategory.YAHTZEE
                    ),
                    onClaimReward = {}
                )
            }
        }
        composeRule.onNodeWithText("Victory! 🎉").assertIsDisplayed()
        composeRule.onNodeWithText("Claim Reward").assertIsDisplayed()
    }

    @Test
    fun claimClickTriggersCallback() {
        var claimed = false
        composeRule.setContent {
            YacooTheme {
                ResultScreen(
                    result = CombatResult(
                        outcome     = CombatOutcome.WIN,
                        stage       = 1,
                        coinsEarned = 40
                    ),
                    onClaimReward = { claimed = true }
                )
            }
        }
        composeRule.onNodeWithText("Claim Reward").performClick()
        assert(claimed)
    }
}

@RunWith(AndroidJUnit4::class)
class RunMapScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun displaysChapterHeaderAndProceedButton() {
        val run = createRun(createDefaultMeta()) { 0.5 }
        composeRule.setContent {
            YacooTheme {
                RunMapScreen(run = run, onStartNode = {})
            }
        }
        composeRule.onNodeWithText("Chapter 1").assertIsDisplayed()
        composeRule.onNodeWithText("⚔️ 전투 진행").assertIsDisplayed()
    }

    @Test
    fun proceedClickTriggersCallback() {
        val run = createRun(createDefaultMeta()) { 0.5 }
        var started = false
        composeRule.setContent {
            YacooTheme {
                RunMapScreen(run = run, onStartNode = { started = true })
            }
        }
        composeRule.onNodeWithText("⚔️ 전투 진행").performClick()
        assert(started)
    }
}

@RunWith(AndroidJUnit4::class)
class RewardPickScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun displaysThreeRewardChoices() {
        val run = createRun(createDefaultMeta()) { 0.5 }
        composeRule.setContent {
            YacooTheme {
                RewardPickScreen(
                    run = run.copy(pendingReward = generateNodeReward(NodeType.TREASURE) { 0.5 }),
                    onPickReward = {}
                )
            }
        }
        composeRule.onNodeWithText("보상 선택").assertIsDisplayed()
        repeat(3) {
            composeRule.onAllNodes(hasText("선택"))[it].assertIsDisplayed()
        }
    }
}

@RunWith(AndroidJUnit4::class)
class RunResultScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun displaysRunOverAndHomeButton() {
        val meta = createDefaultMeta().copy(bestChapter = 4, totalRuns = 7, coins = 320)
        composeRule.setContent {
            YacooTheme {
                RunResultScreen(meta = meta, onReturnHome = {})
            }
        }
        composeRule.onNodeWithText("런 종료").assertIsDisplayed()
        composeRule.onNodeWithText("🏠 홈으로 돌아가기").assertIsDisplayed()
    }
}

private fun hasText(text: String) = androidx.compose.ui.test.hasText(text)
