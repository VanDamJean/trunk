import { ATTACK_CATEGORIES, HANDS } from './constants';
import { getHeroStats } from './equipment';
import type { DieValue, EnemyStats, EquipmentSet, RunState, YahtzeeAttackCategory, YahtzeeHand } from './types';

export function createEnemy(stage: number): EnemyStats {
  // Rebalanced for turn-based combat (M1.5).
  // Target: ~5–8 turns per fight at default equipment.
  // attack scaled so hero takes ~25–40% max HP per turn without upgrades.
  return {
    stage,
    maxHp: 100 + 40 * (stage - 1),
    attack: 18 + 4 * (stage - 1),
    name: stage % 5 === 0 ? 'Moss Boss' : `Forest Grump ${stage}`
  };
}

export function clampHp(value: number): number {
  return Math.max(0, Math.floor(value));
}

export function calculateDiceDamage(equipment: EquipmentSet, hand: YahtzeeHand): number {
  const hero = getHeroStats(equipment);
  return Math.floor(hero.attack * HANDS[hand].multiplier * (1 + hero.diceBonus));
}

export function calculateSelectedCategoryDamage(equipment: EquipmentSet, category: YahtzeeAttackCategory): number {
  const hero = getHeroStats(equipment);
  const metadata = ATTACK_CATEGORIES.find((item) => item.category === category);

  if (!metadata) {
    throw new Error(`Unknown attack category: ${category}`);
  }

  return Math.floor(hero.attack * metadata.multiplier * (1 + hero.diceBonus));
}

export function calculateHeroAutoDamage(equipment: EquipmentSet): number {
  const hero = getHeroStats(equipment);
  return hero.attack + hero.autoBonus;
}

export function calculateEnemyDamage(enemy: EnemyStats, equipment: EquipmentSet): number {
  const hero = getHeroStats(equipment);
  return Math.max(1, enemy.attack - hero.defense);
}

/**
 * Ultimate (dice special attack) damage — M1 version.
 * Formula: floor((pipSum + weaponAttack) * handMultiplier * (1 + diceBonus))
 * M3 will add traitDamageBonus, specialtyMultiplier, treasure effects.
 */
export function calculateUltimateDamage(args: {
  dice: DieValue[];
  hand: YahtzeeHand;
  equipment: EquipmentSet;
  run?: RunState;
}): number {
  const { dice, hand, equipment } = args;
  const hero = getHeroStats(equipment);
  const pipSum = dice.reduce((sum, d) => sum + d, 0);
  const handMeta = HANDS[hand];
  return Math.floor((pipSum + hero.attack) * handMeta.multiplier * (1 + hero.diceBonus));
}

export function calculateUltimateCategoryDamage(args: {
  dice: DieValue[];
  category: YahtzeeAttackCategory;
  equipment: EquipmentSet;
  run?: RunState;
}): number {
  const { dice, category, equipment } = args;
  const metadata = ATTACK_CATEGORIES.find((item) => item.category === category);
  if (!metadata) throw new Error(`Unknown attack category: ${category}`);
  const hero = getHeroStats(equipment);
  const pipSum = dice.reduce((sum, d) => sum + d, 0);
  return Math.floor((pipSum + hero.attack) * metadata.multiplier * (1 + hero.diceBonus));
}
