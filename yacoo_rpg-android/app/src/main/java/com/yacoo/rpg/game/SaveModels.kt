package com.yacoo.rpg.game

data class MetaSave(
    val version: Int = 2,
    val coins: Int,
    val equipment: EquipmentSet,
    val bestChapter: Int,
    val totalRuns: Int,
    val runInProgress: RunState? = null,
    val lastCombatResult: CombatResult? = null
)

data class GameSave(
    val stage: Int,
    val coins: Int,
    val equipment: EquipmentSet,
    val lastResult: CombatResult? = null
)

data class MetaAppState(
    val meta: MetaSave,
    val run: RunState?,
    val screen: Screen
)
