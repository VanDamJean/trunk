package com.yacoo.rpg.game

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RewardsTest {

    @Test fun `generateNodeReward for TREASURE returns exactly 3 choices`() {
        val choices = generateNodeReward(NodeType.TREASURE) { 0.5 }
        assertEquals(3, choices.size)
    }

    @Test fun `generateNodeReward for TREASURE includes heal and scrap`() {
        val choices = generateNodeReward(NodeType.TREASURE) { 0.5 }
        val kinds = choices.map { it.kind }
        assertTrue(RewardKind.HEAL in kinds)
        assertTrue(RewardKind.SCRAP in kinds)
    }

    @Test fun `generateNodeReward for TREASURE third choice is dice or reroll`() {
        listOf(0.0, 0.49, 0.5, 0.99).forEach { seed ->
            val choices = generateNodeReward(NodeType.TREASURE) { seed }
            val third = choices[2].kind
            assertTrue(third == RewardKind.DICE || third == RewardKind.REROLL)
        }
    }

    @Test fun `generateNodeReward amounts are positive`() {
        listOf(0.0, 0.5, 0.99).forEach { seed ->
            val choices = generateNodeReward(NodeType.TREASURE) { seed }
            choices.forEach { c -> assertTrue(c.amount > 0) }
        }
    }

    @Test fun `generateNodeReward for non-treasure returns empty`() {
        assertEquals(0, generateNodeReward(NodeType.BATTLE).size)
        assertEquals(0, generateNodeReward(NodeType.ELITE).size)
        assertEquals(0, generateNodeReward(NodeType.BOSS).size)
        assertEquals(0, generateNodeReward(NodeType.REST).size)
    }
}
