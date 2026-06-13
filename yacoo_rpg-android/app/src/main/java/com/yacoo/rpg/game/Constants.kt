package com.yacoo.rpg.game

object Constants {
    const val STORAGE_KEY = "yacoo-rpg-save-v2"

    object HeroBase {
        const val HP      = 120
        const val ATTACK  = 10
        const val STAGE   = 1
        const val COINS   = 0
    }

    object CombatTiming {
        const val MAX_ROLLS          = 3
        const val DICE_COUNT         = 5
        const val ENEMY_TURN_DELAY_MS = 500L
    }

    object EquipmentRules {
        const val LEVEL_CAP                  = 10
        const val COST_PER_CURRENT_LEVEL     = 25
        const val DUPLICATE_COIN_VALUE       = 20
        const val DUPLICATE_CHANCE           = 0.3
        const val WEAPON_ATTACK_PER_LEVEL    = 3
        const val ARMOR_HP_PER_LEVEL         = 15
        const val ARMOR_DEFENSE_PER_LEVEL    = 1
        const val CHARM_DICE_BONUS_PER_LEVEL = 0.05
        const val BOOTS_AUTO_DAMAGE_PER_LEVEL = 1
    }

    val EQUIPMENT_ORDER = listOf(
        EquipmentSlot.WEAPON,
        EquipmentSlot.ARMOR,
        EquipmentSlot.CHARM,
        EquipmentSlot.BOOTS
    )

    val STARTING_EQUIPMENT = EquipmentSet(
        weapon = EquipmentItem("twig-wand",   EquipmentSlot.WEAPON, "Twig Wand",    1),
        armor  = EquipmentItem("leaf-hoodie", EquipmentSlot.ARMOR,  "Leaf Hoodie",  1),
        charm  = EquipmentItem("lucky-acorn", EquipmentSlot.CHARM,  "Lucky Acorn",  1),
        boots  = EquipmentItem("tiny-boots",  EquipmentSlot.BOOTS,  "Tiny Boots",   1)
    )
}
