package com.yacoo.rpg.game

import com.yacoo.rpg.game.Constants.EQUIPMENT_ORDER
import com.yacoo.rpg.game.Constants.EquipmentRules

fun createWinReward(
    stage: Int,
    equipment: EquipmentSet,
    rng: Rng = Math::random
): RewardResult {
    val baseCoins = 30 + 10 * stage
    if (rng() >= EquipmentRules.DUPLICATE_CHANCE) {
        return RewardResult(coinsEarned = baseCoins)
    }
    val slotIndex = minOf(EQUIPMENT_ORDER.size - 1, (rng() * EQUIPMENT_ORDER.size).toInt())
    val item = equipment[EQUIPMENT_ORDER[slotIndex]]
    return RewardResult(
        coinsEarned       = baseCoins + EquipmentRules.DUPLICATE_COIN_VALUE,
        duplicateItemName = item.name
    )
}

fun createLossReward(): RewardResult = RewardResult(coinsEarned = 10)

fun generateNodeReward(
    nodeType: NodeType,
    rng: Rng = Math::random
): List<RewardChoice> = when (nodeType) {
    NodeType.TREASURE -> {
        val healAmt  = 20 + (rng() * 20).toInt()
        val scrapAmt = 15 + (rng() * 15).toInt()
        val third    = if (rng() < 0.5) RewardChoice(RewardKind.DICE, 1)
                       else RewardChoice(RewardKind.REROLL, 1)
        listOf(
            RewardChoice(RewardKind.HEAL, healAmt),
            RewardChoice(RewardKind.SCRAP, scrapAmt),
            third
        )
    }
    else -> emptyList()
}
