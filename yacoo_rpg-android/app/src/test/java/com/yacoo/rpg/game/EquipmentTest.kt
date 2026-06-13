package com.yacoo.rpg.game

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EquipmentTest {

    @Test fun `default meta has correct structure`() {
        val meta = createDefaultMeta()
        assertEquals(2, meta.version)
        assertEquals(0, meta.coins)
        assertEquals(1, meta.bestChapter)
        assertEquals(1, meta.equipment.weapon.level)
    }

    @Test fun `upgradeCost equals costPerLevel times current level`() {
        val item = Constants.STARTING_EQUIPMENT.weapon  // level=1
        assertEquals(25, upgradeCost(item))
    }

    @Test fun `canUpgrade returns false when level at cap`() {
        val item = Constants.STARTING_EQUIPMENT.weapon.copy(level = 10)
        assertFalse(canUpgrade(item, 999))
    }

    @Test fun `canUpgrade returns false when not enough coins`() {
        val item = Constants.STARTING_EQUIPMENT.weapon  // cost=25
        assertFalse(canUpgrade(item, 10))
    }

    @Test fun `upgradeEquipment increases level and deducts cost`() {
        val meta = createDefaultMeta().copy(coins = 100)
        val updated = upgradeEquipment(meta, EquipmentSlot.WEAPON)
        assertEquals(2, updated.equipment.weapon.level)
        assertEquals(75, updated.coins)   // 100 - 25
    }

    @Test fun `upgradeEquipment is no-op when not enough coins`() {
        val meta = createDefaultMeta()   // coins=0
        val updated = upgradeEquipment(meta, EquipmentSlot.WEAPON)
        assertSame(meta, updated)
    }

    @Test fun `getHeroStats scales with equipment levels`() {
        val base  = getHeroStats(Constants.STARTING_EQUIPMENT)
        val eq2   = Constants.STARTING_EQUIPMENT.copy(
            weapon = Constants.STARTING_EQUIPMENT.weapon.copy(level = 2)
        )
        val stats2 = getHeroStats(eq2)
        assertTrue(stats2.attack > base.attack)
    }

    @Test fun `power is positive`() {
        assertTrue(getHeroStats(Constants.STARTING_EQUIPMENT).power > 0)
    }

    @Test fun `migrateV1Chapter converts stage to chapter`() {
        assertEquals(1, migrateV1Chapter(1))
        assertEquals(1, migrateV1Chapter(6))
        assertEquals(2, migrateV1Chapter(7))
    }
}
