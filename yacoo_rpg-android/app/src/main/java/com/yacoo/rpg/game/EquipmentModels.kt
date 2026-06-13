package com.yacoo.rpg.game

enum class EquipmentSlot { WEAPON, ARMOR, CHARM, BOOTS }

data class EquipmentItem(
    val id: String,
    val slot: EquipmentSlot,
    val name: String,
    val level: Int,
    val specialtyHand: YahtzeeHand? = null
)

data class EquipmentSet(
    val weapon: EquipmentItem,
    val armor: EquipmentItem,
    val charm: EquipmentItem,
    val boots: EquipmentItem
) {
    operator fun get(slot: EquipmentSlot): EquipmentItem = when (slot) {
        EquipmentSlot.WEAPON -> weapon
        EquipmentSlot.ARMOR  -> armor
        EquipmentSlot.CHARM  -> charm
        EquipmentSlot.BOOTS  -> boots
    }

    fun with(slot: EquipmentSlot, item: EquipmentItem): EquipmentSet = when (slot) {
        EquipmentSlot.WEAPON -> copy(weapon = item)
        EquipmentSlot.ARMOR  -> copy(armor  = item)
        EquipmentSlot.CHARM  -> copy(charm  = item)
        EquipmentSlot.BOOTS  -> copy(boots  = item)
    }
}

data class HeroStats(
    val maxHp: Int,
    val attack: Int,
    val defense: Int,
    val autoBonus: Int,
    val diceBonus: Double,
    val power: Int
)
