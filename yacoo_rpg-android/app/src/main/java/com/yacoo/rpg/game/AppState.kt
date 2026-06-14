package com.yacoo.rpg.game

fun finishCombatMeta(
    state: MetaAppState,
    outcome: CombatOutcome,
    heroHp: Int,
    handUsed: YahtzeeHand? = null,
    rng: Rng = Math::random
): MetaAppState {
    // Free combat from Home — no run to mutate
    if (state.run == null) {
        val stage = state.meta.bestChapter
        val reward = if (outcome == CombatOutcome.WIN) createWinReward(stage, state.meta.equipment, rng)
                     else createLossReward()
        val lastResult = CombatResult(outcome, stage, reward.coinsEarned, handUsed, reward.duplicateItemName)
        return state.copy(
            meta   = state.meta.copy(lastCombatResult = lastResult),
            screen = Screen.RESULT
        )
    }

    // Run combat
    val stage  = state.run.chapter
    val reward = if (outcome == CombatOutcome.WIN) createWinReward(stage, state.meta.equipment, rng)
                 else createLossReward()

    return if (outcome == CombatOutcome.LOSS) {
        // settleRun: coins += reward.coinsEarned, lastCombatResult = null, runInProgress = null.
        // Do NOT overwrite lastCombatResult afterwards (would create double-claim path).
        val settled = settleRun(state.meta, state.run, reward.coinsEarned)
        state.copy(
            meta   = settled,            // runInProgress already nulled by settleRun
            run    = null,
            screen = Screen.RUN_RESULT
        )
    } else {
        val lastResult = CombatResult(outcome, stage, reward.coinsEarned, handUsed, reward.duplicateItemName)
        // Advance node, persist damage
        var nextRun = advanceNode(state.run).copy(hp = heroHp.coerceIn(0, state.run.maxHp))
        // Roll into next chapter if this cleared the last node
        if (isChapterCleared(nextRun)) {
            nextRun = advanceChapter(nextRun, rng)
        }
        state.copy(
            meta   = state.meta.copy(
                lastCombatResult = lastResult,
                runInProgress    = nextRun         // persistence safety (P0-4)
            ),
            run    = nextRun,
            screen = Screen.RESULT
        )
    }
}

fun claimRewardMeta(state: MetaAppState): MetaAppState {
    val result = state.meta.lastCombatResult ?: return state.copy(screen = Screen.HOME)
    val nextMeta = state.meta.copy(
        coins            = state.meta.coins + result.coinsEarned,
        lastCombatResult = null
    )
    val nextScreen = if (state.run != null) Screen.RUN_MAP else Screen.HOME
    return state.copy(meta = nextMeta, screen = nextScreen)
}

fun upgradeSlotMeta(meta: MetaSave, slot: EquipmentSlot): MetaSave =
    upgradeEquipment(meta, slot)

fun drawChestMeta(meta: MetaSave, isWeapon: Boolean): Pair<MetaSave, String> =
    drawChest(meta, isWeapon)
