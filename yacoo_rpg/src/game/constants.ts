import type { EquipmentSet, YahtzeeAttackCategoryMeta, YahtzeeHand, YahtzeeHandResult } from './types';

export const STORAGE_KEY = 'yacoo-rpg-save-v1';

export const HERO_BASE = {
  hp: 120,
  attack: 10,
  stage: 1,
  coins: 0
} as const;

export const COMBAT_TIMING = {
  // Removed: heroAttackMs, enemyAttackMs, diceChargeMs, maxDiceCharges (auto-battle폐기, M1.5)
  maxRolls: 3,
  diceCount: 5,
  enemyTurnDelayMs: 500  // 적 공격 연출 딜레이. 테스트에서는 0으로 주입.
} as const;

export const EQUIPMENT_RULES = {
  levelCap: 10,
  costPerCurrentLevel: 25,
  duplicateCoinValue: 20,
  duplicateChance: 0.3,
  weaponAttackPerLevel: 3,
  armorHpPerLevel: 15,
  armorDefensePerLevel: 1,
  charmDiceBonusPerLevel: 0.05,
  bootsAutoDamagePerLevel: 1
} as const;

export const ATTACK_CATEGORIES: readonly YahtzeeAttackCategoryMeta[] = [
  { category: 'chance', label: 'Chance', multiplier: 1.0 },
  { category: 'pair', label: 'Pair', multiplier: 1.2 },
  { category: 'twoPair', label: 'Two Pair', multiplier: 1.5 },
  { category: 'threeKind', label: 'Three of a Kind', multiplier: 1.8 },
  { category: 'smallStraight', label: 'Small Straight', multiplier: 2.1 },
  { category: 'fullHouse', label: 'Full House', multiplier: 2.5 },
  { category: 'fourKind', label: 'Four of a Kind', multiplier: 3.2 },
  { category: 'largeStraight', label: 'Large Straight', multiplier: 3.8 },
  { category: 'yahtzee', label: 'Yahtzee', multiplier: 6.0 }
] as const;

export const HANDS: Record<YahtzeeHand, YahtzeeHandResult> = {
  yahtzee: { hand: 'yahtzee', rank: 9, multiplier: 6.0, label: 'Yahtzee' },
  largeStraight: { hand: 'largeStraight', rank: 8, multiplier: 3.8, label: 'Large Straight' },
  fourKind: { hand: 'fourKind', rank: 7, multiplier: 3.2, label: 'Four of a Kind' },
  fullHouse: { hand: 'fullHouse', rank: 6, multiplier: 2.5, label: 'Full House' },
  smallStraight: { hand: 'smallStraight', rank: 5, multiplier: 2.1, label: 'Small Straight' },
  threeKind: { hand: 'threeKind', rank: 4, multiplier: 1.8, label: 'Three of a Kind' },
  twoPair: { hand: 'twoPair', rank: 3, multiplier: 1.5, label: 'Two Pair' },
  pair: { hand: 'pair', rank: 2, multiplier: 1.2, label: 'Pair' },
  chance: { hand: 'chance', rank: 1, multiplier: 1.0, label: 'Chance' }
};

export const STARTING_EQUIPMENT: EquipmentSet = {
  weapon: { id: 'twig-wand', slot: 'weapon', name: 'Twig Wand', level: 1 },
  armor: { id: 'leaf-hoodie', slot: 'armor', name: 'Leaf Hoodie', level: 1 },
  charm: { id: 'lucky-acorn', slot: 'charm', name: 'Lucky Acorn', level: 1 },
  boots: { id: 'tiny-boots', slot: 'boots', name: 'Tiny Boots', level: 1 }
};

export const EQUIPMENT_ORDER = ['weapon', 'armor', 'charm', 'boots'] as const;
