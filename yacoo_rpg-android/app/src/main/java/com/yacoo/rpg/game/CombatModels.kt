package com.yacoo.rpg.game

data class EnemyStats(
    val stage: Int,
    val maxHp: Int,
    val attack: Int,
    val name: String
)

enum class CombatOutcome { WIN, LOSS }

data class CombatResult(
    val outcome: CombatOutcome,
    val stage: Int,
    val coinsEarned: Int,
    val handUsed: YahtzeeHand? = null,
    val duplicateItemName: String? = null
)

data class RewardResult(
    val coinsEarned: Int,
    val duplicateItemName: String? = null
)
