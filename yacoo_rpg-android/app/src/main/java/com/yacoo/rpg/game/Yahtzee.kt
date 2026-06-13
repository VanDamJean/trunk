package com.yacoo.rpg.game

import com.yacoo.rpg.game.Constants.CombatTiming

fun rollDie(rng: Rng = Math::random): DieValue {
    val raw = (rng() * 6).toInt() + 1
    return raw.coerceIn(1, 6)
}

fun rollDice(
    previous: List<DieValue> = emptyList(),
    held: List<Boolean> = emptyList(),
    rng: Rng = Math::random
): List<DieValue> = List(CombatTiming.DICE_COUNT) { i ->
    if (held.getOrNull(i) == true && previous.getOrNull(i) != null) {
        previous[i]
    } else {
        rollDie(rng)
    }
}

private fun countsFor(dice: List<DieValue>): Map<DieValue, Int> =
    dice.groupingBy { it }.eachCount()

private fun hasStraight(dice: List<DieValue>, sequence: List<DieValue>): Boolean {
    val unique = dice.toSet()
    return sequence.all { it in unique }
}

fun isAttackCategoryValid(dice: List<DieValue>, category: YahtzeeAttackCategory): Boolean {
    if (dice.size != CombatTiming.DICE_COUNT) return false
    val counts = countsFor(dice).values.toList()

    return when (category) {
        YahtzeeAttackCategory.CHANCE         -> true
        YahtzeeAttackCategory.PAIR           -> counts.any { it >= 2 }
        YahtzeeAttackCategory.TWO_PAIR       -> counts.count { it >= 2 } >= 2
        YahtzeeAttackCategory.THREE_KIND     -> counts.any { it >= 3 }
        YahtzeeAttackCategory.SMALL_STRAIGHT ->
            hasStraight(dice, listOf(1, 2, 3, 4)) ||
            hasStraight(dice, listOf(2, 3, 4, 5)) ||
            hasStraight(dice, listOf(3, 4, 5, 6))
        YahtzeeAttackCategory.FULL_HOUSE     ->
            counts.count { it == 3 } == 1 && counts.count { it == 2 } == 1
        YahtzeeAttackCategory.FOUR_KIND      -> counts.any { it >= 4 }
        YahtzeeAttackCategory.LARGE_STRAIGHT ->
            hasStraight(dice, listOf(1, 2, 3, 4, 5)) ||
            hasStraight(dice, listOf(2, 3, 4, 5, 6))
        YahtzeeAttackCategory.YAHTZEE        -> counts.any { it == 5 }
    }
}

fun getValidAttackCategories(dice: List<DieValue>): List<YahtzeeAttackCategory> {
    if (dice.size != CombatTiming.DICE_COUNT) return emptyList()
    return YahtzeeAttackCategory.entries.filter { isAttackCategoryValid(dice, it) }
}

fun evaluateHand(dice: List<DieValue>): YahtzeeHandResult {
    require(dice.size == CombatTiming.DICE_COUNT) {
        "Expected ${CombatTiming.DICE_COUNT} dice, got ${dice.size}"
    }
    val counts = countsFor(dice).values.sortedDescending()
    val pairCount = counts.count { it >= 2 }
    val largeStraight = hasStraight(dice, listOf(1, 2, 3, 4, 5)) ||
                        hasStraight(dice, listOf(2, 3, 4, 5, 6))
    val smallStraight = hasStraight(dice, listOf(1, 2, 3, 4)) ||
                        hasStraight(dice, listOf(2, 3, 4, 5)) ||
                        hasStraight(dice, listOf(3, 4, 5, 6))

    return when {
        counts[0] == 5        -> YahtzeeAttackCategory.YAHTZEE.toHandResult()
        largeStraight         -> YahtzeeAttackCategory.LARGE_STRAIGHT.toHandResult()
        counts[0] >= 4        -> YahtzeeAttackCategory.FOUR_KIND.toHandResult()
        counts[0] == 3 && counts.getOrElse(1) { 0 } == 2
                              -> YahtzeeAttackCategory.FULL_HOUSE.toHandResult()
        smallStraight         -> YahtzeeAttackCategory.SMALL_STRAIGHT.toHandResult()
        counts[0] >= 3        -> YahtzeeAttackCategory.THREE_KIND.toHandResult()
        pairCount >= 2        -> YahtzeeAttackCategory.TWO_PAIR.toHandResult()
        pairCount >= 1        -> YahtzeeAttackCategory.PAIR.toHandResult()
        else                  -> YahtzeeAttackCategory.CHANCE.toHandResult()
    }
}
