package com.yacoo.rpg.game

enum class YahtzeeAttackCategory(val label: String, val multiplier: Double, val rank: Int) {
    CHANCE          ("Chance",          1.0, 1),
    PAIR            ("Pair",            1.2, 2),
    TWO_PAIR        ("Two Pair",        1.5, 3),
    THREE_KIND      ("Three of a Kind", 1.8, 4),
    SMALL_STRAIGHT  ("Small Straight",  2.1, 5),
    FULL_HOUSE      ("Full House",      2.5, 6),
    FOUR_KIND       ("Four of a Kind",  3.2, 7),
    LARGE_STRAIGHT  ("Large Straight",  3.8, 8),
    YAHTZEE         ("Yahtzee",         6.0, 9);

    fun toHandResult(): YahtzeeHandResult = YahtzeeHandResult(this, rank, multiplier, label)
}

typealias YahtzeeHand = YahtzeeAttackCategory

data class YahtzeeHandResult(
    val hand: YahtzeeHand,
    val rank: Int,
    val multiplier: Double,
    val label: String
)
