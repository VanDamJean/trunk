package com.yacoo.rpg.game

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CombatTest {

    private val defaultEquipment = Constants.STARTING_EQUIPMENT

    @Test fun `createEnemy stage 1 has correct stats`() {
        val enemy = createEnemy(1)
        assertEquals(100, enemy.maxHp)
        assertEquals(18,  enemy.attack)
        assertEquals("Forest Grump 1", enemy.name)
    }

    @Test fun `createEnemy stage 5 is a boss`() {
        val enemy = createEnemy(5)
        assertEquals("Moss Boss", enemy.name)
    }

    @Test fun `createEnemy scales with stage`() {
        val e1 = createEnemy(1)
        val e3 = createEnemy(3)
        assertTrue(e3.maxHp > e1.maxHp)
        assertTrue(e3.attack > e1.attack)
    }

    @Test fun `calculateUltimateCategoryDamage is positive`() {
        val dice = listOf(3, 3, 3, 3, 3)
        val dmg = calculateUltimateCategoryDamage(dice, YahtzeeAttackCategory.YAHTZEE, defaultEquipment)
        assertTrue(dmg > 0)
    }

    @Test fun `yahtzee multiplier is higher than chance`() {
        val dice = listOf(3, 3, 3, 3, 3)
        val yDmg = calculateUltimateCategoryDamage(dice, YahtzeeAttackCategory.YAHTZEE, defaultEquipment)
        val cDmg = calculateUltimateCategoryDamage(dice, YahtzeeAttackCategory.CHANCE, defaultEquipment)
        assertTrue(yDmg > cDmg)
    }

    @Test fun `calculateEnemyDamage is at least 1`() {
        val enemy = createEnemy(1)
        val dmg = calculateEnemyDamage(enemy, defaultEquipment)
        assertTrue(dmg >= 1)
    }

    @Test fun `clampHp never goes below 0`() {
        assertEquals(0, clampHp(-100))
        assertEquals(50, clampHp(50))
    }

    @Test fun `damage formula matches expected value for default equipment`() {
        // weapon level=1 → attack=13, charm level=1 → diceBonus=0.05
        // dice=[3,3,3,3,3] pipSum=15, yahtzee multiplier=6.0
        // floor((15+13)*6.0*(1+0.05)) = floor(28*6.0*1.05) = floor(176.4) = 176
        val dice = listOf(3, 3, 3, 3, 3)
        val dmg = calculateUltimateCategoryDamage(dice, YahtzeeAttackCategory.YAHTZEE, defaultEquipment)
        assertEquals(176, dmg)
    }
}
