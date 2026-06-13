package com.yacoo.rpg.game

import com.yacoo.rpg.game.Constants.EquipmentRules
import com.yacoo.rpg.game.Constants.HeroBase
import com.yacoo.rpg.game.Constants.EQUIPMENT_ORDER
import com.yacoo.rpg.game.Constants.STARTING_EQUIPMENT
import kotlin.math.max
import kotlin.math.round

fun cloneEquipment(equipment: EquipmentSet = STARTING_EQUIPMENT): EquipmentSet =
    equipment.copy(
        weapon = equipment.weapon.copy(),
        armor  = equipment.armor.copy(),
        charm  = equipment.charm.copy(),
        boots  = equipment.boots.copy()
    )

fun createDefaultMeta(): MetaSave = MetaSave(
    version      = 2,
    coins        = HeroBase.COINS,
    equipment    = cloneEquipment(),
    bestChapter  = 1,
    totalRuns    = 0
)

fun upgradeCost(item: EquipmentItem): Int =
    EquipmentRules.COST_PER_CURRENT_LEVEL * item.level

fun canUpgrade(item: EquipmentItem, coins: Int): Boolean =
    item.level < EquipmentRules.LEVEL_CAP && coins >= upgradeCost(item)

fun upgradeEquipment(meta: MetaSave, slot: EquipmentSlot): MetaSave {
    val item = meta.equipment[slot]
    if (!canUpgrade(item, meta.coins)) return meta
    return meta.copy(
        coins     = meta.coins - upgradeCost(item),
        equipment = meta.equipment.with(slot, item.copy(level = item.level + 1))
    )
}

fun getHeroStats(equipment: EquipmentSet): HeroStats {
    val maxHp   = HeroBase.HP     + equipment.armor.level  * EquipmentRules.ARMOR_HP_PER_LEVEL
    val attack  = HeroBase.ATTACK + equipment.weapon.level * EquipmentRules.WEAPON_ATTACK_PER_LEVEL
    val defense = equipment.armor.level  * EquipmentRules.ARMOR_DEFENSE_PER_LEVEL
    val autoBonus  = equipment.boots.level  * EquipmentRules.BOOTS_AUTO_DAMAGE_PER_LEVEL
    val diceBonus  = equipment.charm.level  * EquipmentRules.CHARM_DICE_BONUS_PER_LEVEL
    return HeroStats(
        maxHp     = maxHp,
        attack    = attack,
        defense   = defense,
        autoBonus = autoBonus,
        diceBonus = diceBonus,
        power     = round(maxHp / 5.0 + attack * 6 + defense * 5 + autoBonus * 4 + diceBonus * 100).toInt()
    )
}

fun equipmentBonusLabel(item: EquipmentItem): String = when (item.slot) {
    EquipmentSlot.WEAPON -> "Attack +${item.level * EquipmentRules.WEAPON_ATTACK_PER_LEVEL}"
    EquipmentSlot.ARMOR  -> "HP +${item.level * EquipmentRules.ARMOR_HP_PER_LEVEL}, Defense +${item.level}"
    EquipmentSlot.CHARM  -> "Dice bonus +${round(item.level * EquipmentRules.CHARM_DICE_BONUS_PER_LEVEL * 100).toInt()}%"
    EquipmentSlot.BOOTS  -> "Auto damage +${item.level * EquipmentRules.BOOTS_AUTO_DAMAGE_PER_LEVEL}"
}

fun getEquipmentItems(equipment: EquipmentSet): List<EquipmentItem> =
    EQUIPMENT_ORDER.map { equipment[it] }
