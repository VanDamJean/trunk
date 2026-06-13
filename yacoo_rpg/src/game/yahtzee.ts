import { ATTACK_CATEGORIES, COMBAT_TIMING, HANDS } from './constants';
import type { DieValue, Rng, YahtzeeAttackCategory, YahtzeeHandResult } from './types';

const VALUES: DieValue[] = [1, 2, 3, 4, 5, 6];

function toDieValue(value: number): DieValue {
  if (!VALUES.includes(value as DieValue)) {
    throw new Error(`Invalid die value: ${value}`);
  }
  return value as DieValue;
}

export function rollDie(rng: Rng): DieValue {
  return toDieValue(Math.floor(rng() * 6) + 1);
}

export function rollDice(previous: DieValue[] = [], held: boolean[] = [], rng: Rng = Math.random): DieValue[] {
  return Array.from({ length: COMBAT_TIMING.diceCount }, (_, index) => {
    if (held[index] && previous[index] !== undefined) {
      return previous[index];
    }
    return rollDie(rng);
  });
}

function countsFor(dice: DieValue[]): Map<DieValue, number> {
  const counts = new Map<DieValue, number>();
  dice.forEach((die) => counts.set(die, (counts.get(die) ?? 0) + 1));
  return counts;
}

function hasStraight(dice: DieValue[], sequence: DieValue[]): boolean {
  const unique = new Set(dice);
  return sequence.every((value) => unique.has(value));
}

export function isAttackCategoryValid(dice: DieValue[], category: YahtzeeAttackCategory): boolean {
  if (dice.length !== COMBAT_TIMING.diceCount) {
    return false;
  }

  const counts = [...countsFor(dice).values()];

  switch (category) {
    case 'chance':
      return true;
    case 'pair':
      return counts.some((count) => count >= 2);
    case 'twoPair':
      return counts.filter((count) => count >= 2).length >= 2;
    case 'threeKind':
      return counts.some((count) => count >= 3);
    case 'smallStraight':
      return (
        hasStraight(dice, [1, 2, 3, 4]) ||
        hasStraight(dice, [2, 3, 4, 5]) ||
        hasStraight(dice, [3, 4, 5, 6])
      );
    case 'fullHouse':
      return counts.filter((count) => count === 3).length === 1 && counts.filter((count) => count === 2).length === 1;
    case 'fourKind':
      return counts.some((count) => count >= 4);
    case 'largeStraight':
      return hasStraight(dice, [1, 2, 3, 4, 5]) || hasStraight(dice, [2, 3, 4, 5, 6]);
    case 'yahtzee':
      return counts.some((count) => count === 5);
  }
}

export function getValidAttackCategories(dice: DieValue[]): YahtzeeAttackCategory[] {
  if (dice.length !== COMBAT_TIMING.diceCount) {
    return [];
  }

  return ATTACK_CATEGORIES.filter(({ category }) => isAttackCategoryValid(dice, category)).map(({ category }) => category);
}

export function evaluateHand(dice: DieValue[]): YahtzeeHandResult {
  if (dice.length !== COMBAT_TIMING.diceCount) {
    throw new Error(`Expected ${COMBAT_TIMING.diceCount} dice, got ${dice.length}`);
  }

  const counts = [...countsFor(dice).values()].sort((a, b) => b - a);
  const pairCount = counts.filter((count) => count >= 2).length;
  const largeStraight = hasStraight(dice, [1, 2, 3, 4, 5]) || hasStraight(dice, [2, 3, 4, 5, 6]);
  const smallStraight =
    hasStraight(dice, [1, 2, 3, 4]) ||
    hasStraight(dice, [2, 3, 4, 5]) ||
    hasStraight(dice, [3, 4, 5, 6]);

  if (counts[0] === 5) return HANDS.yahtzee;
  if (largeStraight) return HANDS.largeStraight;
  if (counts[0] >= 4) return HANDS.fourKind;
  if (counts[0] === 3 && counts[1] === 2) return HANDS.fullHouse;
  if (smallStraight) return HANDS.smallStraight;
  if (counts[0] >= 3) return HANDS.threeKind;
  if (pairCount >= 2) return HANDS.twoPair;
  if (pairCount >= 1) return HANDS.pair;
  return HANDS.chance;
}
