import { describe, expect, it } from 'vitest';
import { ATTACK_CATEGORIES } from './constants';
import { evaluateHand, getValidAttackCategories, isAttackCategoryValid, rollDice } from './yahtzee';
import type { DieValue, YahtzeeAttackCategory } from './types';

describe('evaluateHand', () => {
  it.each([
    [[1, 1, 1, 1, 1], 'yahtzee'],
    [[2, 2, 2, 3, 3], 'fullHouse'],
    [[1, 2, 3, 4, 5], 'largeStraight'],
    [[1, 1, 2, 3, 4], 'smallStraight'],
    [[1, 1, 2, 2, 4], 'twoPair'],
    [[1, 1, 2, 3, 5], 'pair'],
    [[1, 3, 4, 5, 6], 'smallStraight'],
    [[1, 3, 3, 4, 6], 'pair']
  ])('classifies %j as %s', (dice, hand) => {
    expect(evaluateHand(dice as DieValue[]).hand).toBe(hand);
  });

  it('chooses the highest ranked matching hand', () => {
    expect(evaluateHand([2, 2, 2, 2, 5]).hand).toBe('fourKind');
  });
});

describe('attack category selection', () => {
  it('exports the frozen attack categories with multipliers', () => {
    expect(ATTACK_CATEGORIES.map(({ category }) => category)).toEqual([
      'chance',
      'pair',
      'twoPair',
      'threeKind',
      'smallStraight',
      'fullHouse',
      'fourKind',
      'largeStraight',
      'yahtzee'
    ]);
    expect(ATTACK_CATEGORIES.map(({ label, multiplier }) => ({ label, multiplier }))).toEqual([
      { label: 'Chance', multiplier: 1.0 },
      { label: 'Pair', multiplier: 1.2 },
      { label: 'Two Pair', multiplier: 1.5 },
      { label: 'Three of a Kind', multiplier: 1.8 },
      { label: 'Small Straight', multiplier: 2.1 },
      { label: 'Full House', multiplier: 2.5 },
      { label: 'Four of a Kind', multiplier: 3.2 },
      { label: 'Large Straight', multiplier: 3.8 },
      { label: 'Yahtzee', multiplier: 6.0 }
    ]);
  });

  it.each([
    [[] as DieValue[], []],
    [[1, 2, 3, 4] as DieValue[], []]
  ])('requires complete five-dice rolls for valid categories: %j', (dice, categories) => {
    expect(getValidAttackCategories(dice)).toEqual(categories);
    expect(isAttackCategoryValid(dice, 'chance')).toBe(false);
  });

  const categoryValidityCases: Array<{
    dice: DieValue[];
    valid: YahtzeeAttackCategory[];
    invalid: YahtzeeAttackCategory[];
  }> = [
    {
      dice: [2, 2, 2, 2, 5],
      valid: ['chance', 'pair', 'threeKind', 'fourKind'],
      invalid: ['twoPair', 'fullHouse', 'smallStraight', 'largeStraight', 'yahtzee']
    },
    {
      dice: [2, 2, 2, 3, 3],
      valid: ['chance', 'pair', 'twoPair', 'threeKind', 'fullHouse'],
      invalid: ['fourKind', 'smallStraight', 'largeStraight', 'yahtzee']
    },
    {
      dice: [1, 2, 3, 4, 5],
      valid: ['chance', 'smallStraight', 'largeStraight'],
      invalid: ['pair', 'twoPair', 'threeKind', 'fullHouse', 'fourKind', 'yahtzee']
    }
  ];

  it.each(categoryValidityCases)('returns deterministic selected-category validity for $dice', ({ dice, valid, invalid }) => {
    expect(getValidAttackCategories(dice)).toEqual(valid);
    valid.forEach((category) => expect(isAttackCategoryValid(dice, category)).toBe(true));
    invalid.forEach((category) => expect(isAttackCategoryValid(dice, category)).toBe(false));
  });
});

describe('rollDice', () => {
  it('preserves held dice while rerolling unlocked dice', () => {
    const values = [0.1, 0.2, 0.3];
    const rng = () => values.shift() ?? 0.4;
    const result = rollDice([6, 5, 4, 3, 2], [true, false, true, false, false], rng);
    expect(result[0]).toBe(6);
    expect(result[2]).toBe(4);
    expect(result[1]).not.toBe(5);
    expect(result).toHaveLength(5);
  });
});
