package com.yacoo.rpg.game

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class YahtzeeTest {

    @Test fun `rollDie returns value between 1 and 6`() {
        listOf(0.0, 0.166, 0.333, 0.5, 0.833, 0.999).forEach { seed ->
            val v = rollDie { seed }
            assertTrue(v in 1..6, "seed=$seed produced $v")
        }
    }

    @Test fun `rollDice returns 5 values`() {
        val result = rollDice(rng = { 0.5 })
        assertEquals(5, result.size)
    }

    @Test fun `rollDice respects held dice`() {
        val previous = listOf(3, 3, 3, 3, 3)
        val held = listOf(true, true, false, false, false)
        val result = rollDice(previous = previous, held = held, rng = { 0.0 })
        assertEquals(3, result[0])
        assertEquals(3, result[1])
        // non-held get re-rolled (rng=0.0 → value 1)
        assertEquals(1, result[2])
    }

    @Test fun `evaluateHand detects yahtzee`() {
        val result = evaluateHand(listOf(6, 6, 6, 6, 6))
        assertEquals(YahtzeeAttackCategory.YAHTZEE, result.hand)
    }

    @Test fun `evaluateHand detects large straight 1-5`() {
        val result = evaluateHand(listOf(1, 2, 3, 4, 5))
        assertEquals(YahtzeeAttackCategory.LARGE_STRAIGHT, result.hand)
    }

    @Test fun `evaluateHand detects large straight 2-6`() {
        val result = evaluateHand(listOf(2, 3, 4, 5, 6))
        assertEquals(YahtzeeAttackCategory.LARGE_STRAIGHT, result.hand)
    }

    @Test fun `evaluateHand detects four of a kind`() {
        val result = evaluateHand(listOf(4, 4, 4, 4, 2))
        assertEquals(YahtzeeAttackCategory.FOUR_KIND, result.hand)
    }

    @Test fun `evaluateHand detects full house`() {
        val result = evaluateHand(listOf(3, 3, 3, 5, 5))
        assertEquals(YahtzeeAttackCategory.FULL_HOUSE, result.hand)
    }

    @Test fun `evaluateHand detects small straight`() {
        val result = evaluateHand(listOf(1, 2, 3, 4, 6))
        assertEquals(YahtzeeAttackCategory.SMALL_STRAIGHT, result.hand)
    }

    @Test fun `evaluateHand detects three of a kind`() {
        val result = evaluateHand(listOf(2, 2, 2, 4, 6))
        assertEquals(YahtzeeAttackCategory.THREE_KIND, result.hand)
    }

    @Test fun `evaluateHand detects two pair`() {
        val result = evaluateHand(listOf(1, 1, 2, 2, 5))
        assertEquals(YahtzeeAttackCategory.TWO_PAIR, result.hand)
    }

    @Test fun `evaluateHand detects pair`() {
        val result = evaluateHand(listOf(1, 1, 2, 3, 5))
        assertEquals(YahtzeeAttackCategory.PAIR, result.hand)
    }

    @Test fun `evaluateHand returns chance for no pattern`() {
        val result = evaluateHand(listOf(1, 2, 3, 5, 6))
        assertEquals(YahtzeeAttackCategory.CHANCE, result.hand)
    }

    @Test fun `getValidAttackCategories includes chance always`() {
        val cats = getValidAttackCategories(listOf(1, 2, 3, 5, 6))
        assertTrue(YahtzeeAttackCategory.CHANCE in cats)
    }

    @Test fun `getValidAttackCategories returns empty for wrong count`() {
        val cats = getValidAttackCategories(listOf(1, 2, 3))
        assertTrue(cats.isEmpty())
    }

    @Test fun `multiplier ordering is correct`() {
        val chance  = evaluateHand(listOf(1, 2, 3, 5, 6))
        val yahtzee = evaluateHand(listOf(6, 6, 6, 6, 6))
        assertTrue(chance.multiplier < yahtzee.multiplier)
    }
}
