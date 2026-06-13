package com.yacoo.rpg.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yacoo.rpg.game.*
import com.yacoo.rpg.ui.components.YacooShell
import com.yacoo.rpg.ui.screens.*
import com.yacoo.rpg.viewmodel.GameViewModel

private fun Screen.route(): String = name.lowercase()

@Composable
fun YacooNavGraph(viewModel: GameViewModel) {
    val navController = rememberNavController()
    val meta      by viewModel.meta.collectAsState()
    val run       by viewModel.run.collectAsState()
    val gameScreen by viewModel.screen.collectAsState()
    val language  by viewModel.language.collectAsState()

    val hero  = getHeroStats(meta.equipment)
    val stage = run?.chapter ?: meta.bestChapter

    LaunchedEffect(gameScreen) {
        val route = gameScreen.route()
        if (navController.currentDestination?.route != route) {
            navController.navigate(route) {
                popUpTo(Screen.HOME.route()) { saveState = true }
                launchSingleTop = true
                restoreState    = true
            }
        }
    }

    YacooShell(
        current    = gameScreen,
        stage      = stage,
        coins      = meta.coins,
        power      = hero.power,
        language   = language,
        onNavigate = { screen -> viewModel.navigate(screen) }
    ) {
        NavHost(
            navController = navController, 
            startDestination = Screen.HOME.route(),
            enterTransition = { androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) },
            exitTransition = { androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300)) }
        ) {
            composable(Screen.HOME.route()) {
                HomeScreen(
                    meta          = meta,
                    heroStats     = hero,
                    language      = language,
                    onLanguageChange = { next -> viewModel.setLanguage(next) },
                    onStartCombat = { viewModel.startCombat() },
                    onNavigate    = { s -> viewModel.navigate(s) },
                    onReset       = { viewModel.reset() }
                )
            }
            composable(Screen.COMBAT.route()) {
                CombatScreen(
                    stage     = stage,
                    equipment = meta.equipment,
                    run       = run,
                    language  = language,
                    onFinish  = { outcome, hand, hp -> viewModel.finishCombat(outcome, hand, hp) }
                )
            }
            composable(Screen.EQUIPMENT.route()) {
                EquipmentScreen(
                    equipment = meta.equipment,
                    coins = meta.coins,
                    language = language,
                    onClose = { viewModel.navigate(Screen.HOME) }
                )
            }
            composable(Screen.UPGRADE.route()) {
                UpgradeScreen(
                    meta      = meta,
                    language  = language,
                    onUpgrade = { slot -> viewModel.upgrade(slot) }
                )
            }
            composable(Screen.GACHA.route()) {
                GachaScreen(
                    coins = meta.coins,
                    gems = 0,
                    language = language,
                    onBack = { viewModel.navigate(Screen.HOME) }
                )
            }
            composable(Screen.RESULT.route()) {
                ResultScreen(
                    result        = meta.lastCombatResult,
                    language      = language,
                    onClaimReward = { viewModel.claimReward() }
                )
            }
            composable(Screen.RUN_MAP.route()) {
                if (run != null) {
                    RunMapScreen(
                        run          = run!!,
                        language     = language,
                        onStartNode  = { viewModel.startNode() }
                    )
                }
            }
            composable(Screen.REWARD_PICK.route()) {
                if (run != null && run!!.pendingReward != null) {
                    RewardPickScreen(
                        run          = run!!,
                        language     = language,
                        onPickReward = { reward -> viewModel.pickReward(reward) }
                    )
                }
            }
            composable(Screen.RUN_RESULT.route()) {
                RunResultScreen(
                    meta          = meta,
                    language      = language,
                    onReturnHome  = { viewModel.navigate(Screen.HOME) }
                )
            }
        }
    }
}
