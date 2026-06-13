package com.yacoo.rpg.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yacoo.rpg.data.MetaRepository
import com.yacoo.rpg.game.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Central game state — mirrors the web useGameState hook.
 * All mutation goes through this ViewModel so the UI stays stateless.
 */
class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = MetaRepository(application)

    private val _meta = MutableStateFlow(createDefaultMeta())
    val meta: StateFlow<MetaSave> = _meta.asStateFlow()

    private val _run = MutableStateFlow<RunState?>(null)
    val run: StateFlow<RunState?> = _run.asStateFlow()

    private val _screen = MutableStateFlow(Screen.HOME)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    init {
        viewModelScope.launch {
            repo.metaFlow.collect { saved ->
                _meta.value = saved
                _run.value  = saved.runInProgress
                if (saved.lastCombatResult != null && _screen.value == Screen.HOME) {
                    _screen.value = Screen.RESULT
                }
            }
        }
    }

    // ── Navigation ────────────────────────────────────────────────────

    fun navigate(screen: Screen) {
        _screen.value = screen
    }

    fun setLanguage(language: AppLanguage) {
        _language.value = language
    }

    // ── Combat lifecycle ──────────────────────────────────────────────

    fun startCombat() {
        val run = _run.value ?: createRun(_meta.value)
        _run.value = run
        persist { it.copy(runInProgress = run) }
        _screen.value = Screen.COMBAT
    }

    /**
     * Begin the current map node. Routes by node type:
     * TREASURE → advance + open reward pick; REST → advance + auto-heal;
     * BATTLE/ELITE/BOSS → normal combat (startCombat).
     */
    fun startNode() {
        val run = _run.value ?: return
        val node = currentNode(run) ?: return
        when (node.type) {
            NodeType.TREASURE -> {
                val rewards = generateNodeReward(NodeType.TREASURE)
                val updated = advanceNode(run).copy(pendingReward = rewards)
                _run.value = updated
                persist { it.copy(runInProgress = updated) }
                _screen.value = Screen.REWARD_PICK
            }
            NodeType.REST -> {
                val healAmount = (run.maxHp * 0.3).toInt().coerceAtLeast(1)
                val updated = advanceNode(applyHealToRun(run, healAmount))
                _run.value = updated
                persist { it.copy(runInProgress = updated) }
                _screen.value = Screen.RUN_MAP
            }
            else -> startCombat()
        }
    }

    fun finishCombat(
        outcome: CombatOutcome,
        handUsed: YahtzeeHand? = null,
        heroHp: Int = 0,
        rng: Rng = Math::random
    ) {
        val state = MetaAppState(_meta.value, _run.value, _screen.value)
        val next  = finishCombatMeta(state, outcome, heroHp, handUsed, rng)
        _meta.value   = next.meta
        _run.value    = next.run
        _screen.value = next.screen
        persistMeta(next.meta)
    }

    fun claimReward() {
        val state = MetaAppState(_meta.value, _run.value, _screen.value)
        val next  = claimRewardMeta(state)
        _meta.value   = next.meta
        _run.value    = next.run
        _screen.value = next.screen
        persistMeta(next.meta)
    }

    // ── Reward pick (run map node reward) ─────────────────────────────

    fun pickReward(reward: com.yacoo.rpg.game.RewardChoice) {
        val run = _run.value ?: return
        val updatedRun = when (reward.kind) {
            RewardKind.HEAL   -> run.copy(
                hp            = minOf(run.maxHp, run.hp + reward.amount),
                pendingReward = null
            )
            RewardKind.DICE   -> run.copy(
                diceCount     = run.diceCount + reward.amount,
                pendingReward = null
            )
            RewardKind.REROLL -> run.copy(
                maxRolls      = run.maxRolls + reward.amount,
                pendingReward = null
            )
            RewardKind.SCRAP  -> run.copy(
                scrap         = run.scrap + reward.amount,
                pendingReward = null
            )
        }
        _run.value = updatedRun
        persist { it.copy(runInProgress = updatedRun) }
        _screen.value = Screen.RUN_MAP
    }

    // ── Upgrade ───────────────────────────────────────────────────────

    fun upgrade(slot: EquipmentSlot) {
        val updated = upgradeSlotMeta(_meta.value, slot)
        _meta.value = updated
        persistMeta(updated)
    }

    // ── Reset ─────────────────────────────────────────────────────────

    fun reset() {
        viewModelScope.launch {
            repo.reset()
            _meta.value   = createDefaultMeta()
            _run.value    = null
            _screen.value = Screen.HOME
        }
    }

    // ── Internal ──────────────────────────────────────────────────────

    private fun persist(transform: (MetaSave) -> MetaSave) {
        val updated = transform(_meta.value)
        _meta.value = updated
        persistMeta(updated)
    }

    private fun persistMeta(meta: MetaSave) {
        viewModelScope.launch { repo.save(meta) }
    }
}
