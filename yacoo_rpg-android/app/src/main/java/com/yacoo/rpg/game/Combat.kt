package com.yacoo.rpg.game

import kotlin.math.floor
import kotlin.math.max

fun createEnemy(stage: Int): EnemyStats = EnemyStats(
    stage  = stage,
    maxHp  = 100 + 40 * (stage - 1),
    attack = 18  + 4  * (stage - 1),
    name   = if (stage % 5 == 0) "Moss Boss" else "Forest Grump $stage"
)

fun clampHp(value: Int): Int = max(0, value)

fun calculateDiceDamage(equipment: EquipmentSet, hand: YahtzeeHand): Int {
    val hero = getHeroStats(equipment)
    return floor(hero.attack * hand.multiplier * (1 + hero.diceBonus)).toInt()
}

fun calculateSelectedCategoryDamage(equipment: EquipmentSet, category: YahtzeeAttackCategory): Int {
    val hero = getHeroStats(equipment)
    return floor(hero.attack * category.multiplier * (1 + hero.diceBonus)).toInt()
}

fun calculateHeroAutoDamage(equipment: EquipmentSet): Int {
    val hero = getHeroStats(equipment)
    return hero.attack + hero.autoBonus
}

fun calculateEnemyDamage(enemy: EnemyStats, equipment: EquipmentSet): Int {
    val hero = getHeroStats(equipment)
    return max(1, enemy.attack - hero.defense)
}

fun calculateUltimateDamage(
    dice: List<DieValue>,
    hand: YahtzeeHand,
    equipment: EquipmentSet
): Int {
    val hero    = getHeroStats(equipment)
    val pipSum  = dice.sum()
    return floor((pipSum + hero.attack) * hand.multiplier * (1 + hero.diceBonus)).toInt()
}

fun calculateUltimateCategoryDamage(
    dice: List<DieValue>,
    category: YahtzeeAttackCategory,
    equipment: EquipmentSet
): Int {
    val hero   = getHeroStats(equipment)
    val pipSum = dice.sum()
    return floor((pipSum + hero.attack) * category.multiplier * (1 + hero.diceBonus)).toInt()
}
