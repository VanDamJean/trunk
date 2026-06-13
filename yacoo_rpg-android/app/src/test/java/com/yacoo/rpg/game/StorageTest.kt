package com.yacoo.rpg.game

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class StorageTest {

    @Test fun `isValidEquipmentItem rejects null map`() {
        assertFalse(isValidEquipmentItem(null, EquipmentSlot.WEAPON))
    }

    @Test fun `isValidEquipmentItem accepts valid weapon`() {
        val map: Map<String, Any> = mapOf(
            "slot" to "weapon", "id" to "sword", "name" to "Sword", "level" to 3
        )
        assertTrue(isValidEquipmentItem(map, EquipmentSlot.WEAPON))
    }

    @Test fun `isValidEquipmentItem rejects wrong slot`() {
        val map: Map<String, Any> = mapOf(
            "slot" to "armor", "id" to "shield", "name" to "Shield", "level" to 2
        )
        assertFalse(isValidEquipmentItem(map, EquipmentSlot.WEAPON))
    }

    @Test fun `isValidEquipmentItem rejects missing id`() {
        val map: Map<String, Any> = mapOf(
            "slot" to "weapon", "name" to "Sword", "level" to 1
        )
        assertFalse(isValidEquipmentItem(map, EquipmentSlot.WEAPON))
    }

    @Test fun `isValidEquipmentItem rejects level zero`() {
        val map: Map<String, Any> = mapOf(
            "slot" to "weapon", "id" to "sword", "name" to "Sword", "level" to 0
        )
        assertFalse(isValidEquipmentItem(map, EquipmentSlot.WEAPON))
    }

    @Test fun `isValidEquipmentItem rejects negative level`() {
        val map: Map<String, Any> = mapOf(
            "slot" to "weapon", "id" to "sword", "name" to "Sword", "level" to -1
        )
        assertFalse(isValidEquipmentItem(map, EquipmentSlot.WEAPON))
    }

    @Test fun `migrateV1Chapter converts stage 1 to chapter 1`() {
        assertEquals(1, migrateV1Chapter(1))
    }

    @Test fun `migrateV1Chapter converts stage 6 to chapter 1`() {
        assertEquals(1, migrateV1Chapter(6))
    }

    @Test fun `migrateV1Chapter converts stage 7 to chapter 2`() {
        assertEquals(2, migrateV1Chapter(7))
    }

    @Test fun `migrateV1Chapter converts stage 12 to chapter 2`() {
        assertEquals(2, migrateV1Chapter(12))
    }

    @Test fun `migrateV1Chapter converts stage 0 to chapter 1`() {
        assertEquals(1, migrateV1Chapter(0))
    }

    @Test fun `migrateV1Chapter converts negative stage to chapter 1`() {
        assertEquals(1, migrateV1Chapter(-5))
    }
}
