import { describe, expect, it } from 'vitest';
import { chooseCategoryForBot, chooseHoldsForBot } from './basicBot';
import type { CategoryId, Dice } from '../game/categories';

describe('chooseHoldsForBot', () => {
  it('holds all dice for large straight and yacht', () => {
    expect(holds([1, 2, 3, 4, 5])).toEqual([true, true, true, true, true]);
    expect(holds([6, 6, 6, 6, 6])).toEqual([true, true, true, true, true]);
  });

  it('holds four-kind and yacht groups before lower priority patterns', () => {
    expect(holds([6, 6, 6, 6, 1])).toEqual([true, true, true, true, false]);
    expect(holds([5, 5, 5, 5, 2])).toEqual([true, true, true, true, false]);
  });

  it('uses deterministic straight candidate order', () => {
    expect(holds([1, 2, 3, 4, 6])).toEqual([true, true, true, true, false]);
    expect(holds([1, 2, 3, 4, 5])).toEqual([true, true, true, true, true]);
  });

  it('keeps exact full house, triples, two pairs, then one pair', () => {
    expect(holds([3, 3, 3, 4, 4])).toEqual([true, true, true, true, true]);
    expect(holds([6, 6, 6, 2, 1])).toEqual([true, true, true, false, false]);
    expect(holds([6, 6, 4, 4, 1])).toEqual([true, true, true, true, false]);
    expect(holds([2, 2, 4, 5, 6])).toEqual([true, true, false, false, false]);
  });

  it('falls back to highest repeated face then highest die', () => {
    expect(holds([6, 6, 2, 3, 5])).toEqual([true, true, false, false, false]);
    expect(holds([1, 3, 5, 2, 6])).toEqual([false, false, false, false, true]);
  });

  it('returns no holds after the final roll', () => {
    expect(chooseHoldsForBot({ dice: [6, 6, 6, 2, 1], rollCount: 3, usedCategories: [] })).toEqual([
      false,
      false,
      false,
      false,
      false
    ]);
  });
});

describe('chooseCategoryForBot', () => {
  it('chooses the highest immediate score among unused categories', () => {
    expect(chooseCategoryForBot({ dice: [6, 6, 6, 6, 1], usedCategories: [] })).toBe('fourKind');
  });

  it('never chooses a used category', () => {
    expect(chooseCategoryForBot({ dice: [6, 6, 6, 6, 6], usedCategories: ['yacht'] })).toBe('fourKind');
  });

  it('uses deterministic category tie-breaks for equal scores', () => {
    const used: CategoryId[] = ['yacht', 'largeStraight', 'fourKind', 'fullHouse'];

    expect(chooseCategoryForBot({ dice: [1, 2, 3, 4, 6], usedCategories: used })).toBe('choice');
  });

  it('returns null when no legal category remains', () => {
    expect(
      chooseCategoryForBot({
        dice: [1, 2, 3, 4, 5],
        usedCategories: [
          'ones',
          'twos',
          'threes',
          'fours',
          'fives',
          'sixes',
          'choice',
          'fourKind',
          'fullHouse',
          'smallStraight',
          'largeStraight',
          'yacht'
        ]
      })
    ).toBeNull();
  });

  it('does not expose random or network-dependent decisions', () => {
    expect(chooseHoldsForBot.toString()).not.toContain('Math.random');
    expect(chooseCategoryForBot.toString()).not.toContain('fetch');
  });
});

function holds(dice: Dice): readonly [boolean, boolean, boolean, boolean, boolean] {
  return chooseHoldsForBot({ dice, rollCount: 1, usedCategories: [] });
}
