import { CATEGORY_ORDER, type CategoryId, type Dice, type DieValue, type Scorecard, categoryFace, isUpperCategory } from './categories';

const STRAIGHT_SEQUENCES: readonly DieValue[][] = [
  [1, 2, 3, 4],
  [2, 3, 4, 5],
  [3, 4, 5, 6]
];

const LARGE_STRAIGHTS: readonly DieValue[][] = [
  [1, 2, 3, 4, 5],
  [2, 3, 4, 5, 6]
];

export function scoreCategory(category: CategoryId, dice: Dice): number {
  if (isUpperCategory(category)) {
    const face = categoryFace(category);
    return dice.filter((die) => die === face).reduce((sum, die) => sum + die, 0);
  }

  const counts = diceCounts(dice);
  const sum = diceSum(dice);

  switch (category) {
    case 'choice':
      return sum;
    case 'fourKind':
      return Object.values(counts).some((count) => count >= 4) ? sum : 0;
    case 'fullHouse':
      return isExactFullHouse(counts) ? sum : 0;
    case 'smallStraight':
      return hasStraight(dice, STRAIGHT_SEQUENCES) ? 15 : 0;
    case 'largeStraight':
      return hasStraight(dice, LARGE_STRAIGHTS) ? 30 : 0;
    case 'yacht':
      return Object.values(counts).some((count) => count === 5) ? 50 : 0;
  }
}

export function isCategorySatisfied(category: CategoryId, dice: Dice): boolean {
  if (isUpperCategory(category) || category === 'choice') {
    return true;
  }

  return scoreCategory(category, dice) > 0;
}

export function totalScore(scorecard: Scorecard): number {
  return CATEGORY_ORDER.reduce((sum, category) => sum + (scorecard[category] ?? 0), 0);
}

export function diceSum(dice: Dice): number {
  return dice.reduce((sum, die) => sum + die, 0);
}

export function diceCounts(dice: Dice): Record<DieValue, number> {
  return {
    1: dice.filter((die) => die === 1).length,
    2: dice.filter((die) => die === 2).length,
    3: dice.filter((die) => die === 3).length,
    4: dice.filter((die) => die === 4).length,
    5: dice.filter((die) => die === 5).length,
    6: dice.filter((die) => die === 6).length
  };
}

function isExactFullHouse(counts: Record<DieValue, number>): boolean {
  const sortedCounts = Object.values(counts).filter((count) => count > 0);
  sortedCounts.sort((a, b) => a - b);

  return sortedCounts.length === 2 && sortedCounts[0] === 2 && sortedCounts[1] === 3;
}

function hasStraight(dice: Dice, sequences: readonly DieValue[][]): boolean {
  const faces = new Set<DieValue>(dice);
  return sequences.some((sequence) => sequence.every((face) => faces.has(face)));
}
