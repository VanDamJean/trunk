import { CATEGORY_ORDER, type CategoryId, type Dice, type DieValue } from '../game/categories';
import { diceCounts, scoreCategory } from '../game/scoring';

export type BotDecisionInput = {
  dice: Dice;
  rollCount: number;
  usedCategories: readonly CategoryId[];
};

export type BotCategoryInput = {
  dice: Dice;
  usedCategories: readonly CategoryId[];
};

export const BOT_CATEGORY_TIE_BREAK: readonly CategoryId[] = [
  'yacht',
  'largeStraight',
  'fourKind',
  'fullHouse',
  'choice',
  'smallStraight',
  'sixes',
  'fives',
  'fours',
  'threes',
  'twos',
  'ones'
];

const STRAIGHT_HOLD_CANDIDATES: readonly DieValue[][] = [
  [2, 3, 4, 5],
  [1, 2, 3, 4],
  [3, 4, 5, 6]
];

export function chooseHoldsForBot(input: BotDecisionInput): readonly [boolean, boolean, boolean, boolean, boolean] {
  if (input.rollCount >= 3) {
    return [false, false, false, false, false];
  }

  const largeStraightHold = holdAllIfLargeStraight(input.dice);
  if (largeStraightHold) {
    return largeStraightHold;
  }

  const counts = diceCounts(input.dice);
  const groupFace = highestFaceWithAtLeast(counts, 4);
  if (groupFace) {
    return holdFace(input.dice, groupFace);
  }

  const straightHold = holdStraightCandidate(input.dice);
  if (straightHold) {
    return straightHold;
  }

  const fullHouseHold = holdFullHouseCandidate(input.dice, counts);
  if (fullHouseHold) {
    return fullHouseHold;
  }

  const repeatedFace = highestRepeatedFace(counts);
  if (repeatedFace) {
    return holdFace(input.dice, repeatedFace);
  }

  return holdHighestDie(input.dice);
}

export function chooseCategoryForBot(input: BotCategoryInput): CategoryId | null {
  const used = new Set(input.usedCategories);
  const unusedCategories = CATEGORY_ORDER.filter((category) => !used.has(category));

  if (unusedCategories.length === 0) {
    return null;
  }

  return unusedCategories.reduce((best, candidate) => {
    const bestScore = scoreCategory(best, input.dice);
    const candidateScore = scoreCategory(candidate, input.dice);

    if (candidateScore > bestScore) {
      return candidate;
    }

    if (candidateScore < bestScore) {
      return best;
    }

    return categoryPriority(candidate) < categoryPriority(best) ? candidate : best;
  });
}

function holdAllIfLargeStraight(dice: Dice): readonly [boolean, boolean, boolean, boolean, boolean] | null {
  const faces = new Set<DieValue>(dice);
  const isLargeStraight =
    [1, 2, 3, 4, 5].every((face) => faces.has(face as DieValue)) ||
    [2, 3, 4, 5, 6].every((face) => faces.has(face as DieValue));
  const isYacht = Object.values(diceCounts(dice)).some((count) => count === 5);

  return isLargeStraight || isYacht ? [true, true, true, true, true] : null;
}

function holdStraightCandidate(dice: Dice): readonly [boolean, boolean, boolean, boolean, boolean] | null {
  for (const candidate of STRAIGHT_HOLD_CANDIDATES) {
    const hold = [false, false, false, false, false];
    const usedIndexes = new Set<number>();

    for (const face of candidate) {
      const index = dice.findIndex((die, dieIndex) => die === face && !usedIndexes.has(dieIndex));
      if (index === -1) {
        break;
      }
      hold[index] = true;
      usedIndexes.add(index);
    }

    if (usedIndexes.size === candidate.length) {
      return toHoldTuple(hold);
    }
  }

  return null;
}

function holdFullHouseCandidate(
  dice: Dice,
  counts: Record<DieValue, number>
): readonly [boolean, boolean, boolean, boolean, boolean] | null {
  const triples = facesWithCount(counts, 3);
  const pairs = facesWithCount(counts, 2);

  if (triples.length === 1 && pairs.length === 1) {
    return [true, true, true, true, true];
  }

  const triple = triples[0];
  if (triple !== undefined) {
    return holdFace(dice, triple);
  }

  if (pairs.length >= 2) {
    return holdFaces(dice, pairs.slice(0, 2));
  }

  const pair = pairs[0];
  if (pair !== undefined) {
    return holdFace(dice, pair);
  }

  return null;
}

function highestFaceWithAtLeast(counts: Record<DieValue, number>, minimum: number): DieValue | null {
  return descendingFaces().find((face) => counts[face] >= minimum) ?? null;
}

function highestRepeatedFace(counts: Record<DieValue, number>): DieValue | null {
  return highestFaceWithAtLeast(counts, 2);
}

function facesWithCount(counts: Record<DieValue, number>, exactCount: number): DieValue[] {
  return descendingFaces().filter((face) => counts[face] === exactCount);
}

function holdFace(dice: Dice, face: DieValue): readonly [boolean, boolean, boolean, boolean, boolean] {
  return toHoldTuple(dice.map((die) => die === face));
}

function holdFaces(dice: Dice, faces: readonly DieValue[]): readonly [boolean, boolean, boolean, boolean, boolean] {
  const faceSet = new Set(faces);
  return toHoldTuple(dice.map((die) => faceSet.has(die)));
}

function holdHighestDie(dice: Dice): readonly [boolean, boolean, boolean, boolean, boolean] {
  const highest = descendingFaces().find((face) => dice.includes(face));
  const index = highest ? dice.findIndex((die) => die === highest) : 0;
  return toHoldTuple(dice.map((_, dieIndex) => dieIndex === index));
}

function categoryPriority(category: CategoryId): number {
  return BOT_CATEGORY_TIE_BREAK.indexOf(category);
}

function descendingFaces(): DieValue[] {
  return [6, 5, 4, 3, 2, 1];
}

function toHoldTuple(values: readonly boolean[]): readonly [boolean, boolean, boolean, boolean, boolean] {
  return [values[0] ?? false, values[1] ?? false, values[2] ?? false, values[3] ?? false, values[4] ?? false];
}
