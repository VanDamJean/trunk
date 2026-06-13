package com.yacoo.rpg.game

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AppStateTest {

    private val defaultState = MetaAppState(
        meta   = createDefaultMeta().copy(coins = 0),
        run    = null,
        screen = Screen.COMBAT
    )

    @Test fun `finishCombatMeta win goes to result screen`() {
        val result = finishCombatMeta(defaultState, CombatOutcome.WIN, heroHp = 100, rng = { 0.9 })
        assertEquals(Screen.RESULT, result.screen)
        assertNotNull(result.meta.lastCombatResult)
        assertEquals(CombatOutcome.WIN, result.meta.lastCombatResult?.outcome)
    }

    @Test fun `finishCombatMeta loss with no run goes to result screen`() {
        val result = finishCombatMeta(defaultState, CombatOutcome.LOSS, heroHp = 0, rng = { 0.9 })
        assertEquals(Screen.RESULT, result.screen)
    }

    @Test fun `claimRewardMeta adds coins and goes home`() {
        val combatResult = CombatResult(CombatOutcome.WIN, 1, coinsEarned = 50)
        val state = defaultState.copy(
            meta = defaultState.meta.copy(lastCombatResult = combatResult)
        )
        val next = claimRewardMeta(state)
        assertEquals(Screen.HOME, next.screen)
        assertEquals(50, next.meta.coins)
        assertNull(next.meta.lastCombatResult)
    }

    @Test fun `claimRewardMeta with no result goes home`() {
        val next = claimRewardMeta(defaultState)
        assertEquals(Screen.HOME, next.screen)
    }

    @Test fun `upgradeSlotMeta delegates to upgradeEquipment`() {
        val meta = createDefaultMeta().copy(coins = 100)
        val updated = upgradeSlotMeta(meta, EquipmentSlot.WEAPON)
        assertEquals(2, updated.equipment.weapon.level)
    }

    @Test fun `win reward with rng always returning non-duplicate has base coins`() {
        val meta = createDefaultMeta()
        val reward = createWinReward(1, meta.equipment, rng = { 0.9 })
        assertEquals(40, reward.coinsEarned)
        assertNull(reward.duplicateItemName)
    }

    @Test fun `loss reward is 10 coins`() {
        val reward = createLossReward()
        assertEquals(10, reward.coinsEarned)
    }

    // ── Run-combat behavior (P0 fixes) ───────────────────────────────────

    private fun runState(chapter: Int = 1, nodeIndex: Int = 0, hp: Int = 100): RunState {
        val meta = createDefaultMeta()
        return RunState(
            seed      = 1L,
            chapter   = chapter,
            map       = createChapterMap(chapter) { 0.5 },
            nodeIndex = nodeIndex,
            hp        = hp,
            maxHp     = 120,
            diceCount = 5,
            maxRolls  = 3,
            scrap     = 0
        )
    }

    @Test fun `win-run persists post-combat hero HP into the run`() {
        val state = defaultState.copy(run = runState(hp = 120))
        val result = finishCombatMeta(state, CombatOutcome.WIN, heroHp = 70, rng = { 0.9 })
        assertEquals(70, result.run?.hp)
        assertEquals(70, result.meta.runInProgress?.hp)
    }

    @Test fun `win-run never goes below 0 HP`() {
        val state = defaultState.copy(run = runState(hp = 120))
        val result = finishCombatMeta(state, CombatOutcome.WIN, heroHp = -5, rng = { 0.9 })
        assertEquals(0, result.run?.hp)
    }

    @Test fun `win-run advances node index`() {
        val state = defaultState.copy(run = runState(nodeIndex = 0))
        val result = finishCombatMeta(state, CombatOutcome.WIN, heroHp = 100, rng = { 0.9 })
        assertEquals(1, result.run?.nodeIndex)
    }

    @Test fun `win-run clearing last node rolls into next chapter`() {
        val lastNodeIndex = (runState().map.nodes.size - 1)
        val state = defaultState.copy(run = runState(nodeIndex = lastNodeIndex))
        val result = finishCombatMeta(state, CombatOutcome.WIN, heroHp = 100, rng = { 0.9 })
        assertEquals(2, result.run?.chapter)
        assertEquals(0, result.run?.nodeIndex)
    }

    @Test fun `loss-run goes to RUN_RESULT and clears run`() {
        val state = defaultState.copy(run = runState(hp = 120))
        val result = finishCombatMeta(state, CombatOutcome.LOSS, heroHp = 0, rng = { 0.9 })
        assertEquals(Screen.RUN_RESULT, result.screen)
        assertNull(result.run)
        assertNull(result.meta.runInProgress)
    }

    @Test fun `loss-run does not leave lastCombatResult set`() {
        val state = defaultState.copy(run = runState(hp = 120))
        val result = finishCombatMeta(state, CombatOutcome.LOSS, heroHp = 0, rng = { 0.9 })
        assertNull(result.meta.lastCombatResult)
    }

    @Test fun `loss-run coins added exactly once`() {
        val startCoins = 50
        val state = defaultState.copy(
            meta = defaultState.meta.copy(coins = startCoins),
            run  = runState(hp = 120)
        )
        val result = finishCombatMeta(state, CombatOutcome.LOSS, heroHp = 0, rng = { 0.9 })
        assertEquals(startCoins + 10, result.meta.coins)
    }

    @Test fun `claimRewardMeta called twice does not double-pay`() {
        val combatResult = CombatResult(CombatOutcome.WIN, 1, coinsEarned = 50)
        val state = defaultState.copy(
            meta = defaultState.meta.copy(coins = 0, lastCombatResult = combatResult)
        )
        val first  = claimRewardMeta(state)
        val second = claimRewardMeta(first)
        assertEquals(50, first.meta.coins)
        assertEquals(50, second.meta.coins)
        assertNull(second.meta.lastCombatResult)
    }
}
