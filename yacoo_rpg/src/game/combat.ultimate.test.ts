import { describe, expect, it } from 'vitest';
import { createDefaultSave } from './equipment';
import { calculateUltimateCategoryDamage, calculateUltimateDamage } from './combat';
import type { DieValue } from './types';

describe('calculateUltimateDamage (pip-sum based)', () => {
  const equipment = createDefaultSave().equipment;
  // Default: attack=13, diceBonus=0.05

  it('returns higher damage for higher pip sums on same hand', () => {
    const lowDice: DieValue[] = [1, 1, 1, 1, 1]; // sum=5
    const highDice: DieValue[] = [6, 6, 6, 6, 6]; // sum=30
    const low = calculateUltimateDamage({ dice: lowDice, hand: 'yahtzee', equipment });
    const high = calculateUltimateDamage({ dice: highDice, hand: 'yahtzee', equipment });
    expect(high).toBeGreaterThan(low);
  });

  it('returns higher damage for higher hand multiplier on same dice', () => {
    const dice: DieValue[] = [3, 3, 3, 3, 3];
    const pair = calculateUltimateDamage({ dice, hand: 'pair', equipment });
    const yahtzee = calculateUltimateDamage({ dice, hand: 'yahtzee', equipment });
    expect(yahtzee).toBeGreaterThan(pair);
  });

  it('is deterministic: same inputs yield same output', () => {
    const dice: DieValue[] = [2, 4, 6, 2, 4];
    const a = calculateUltimateDamage({ dice, hand: 'twoPair', equipment });
    const b = calculateUltimateDamage({ dice, hand: 'twoPair', equipment });
    expect(a).toBe(b);
  });

  it('calculates formula correctly: floor((pipSum + attack) * multiplier * (1 + diceBonus))', () => {
    // attack=13, diceBonus=0.05, yahtzee multiplier=6.0
    // dice all 5: pipSum=25, formula = floor((25+13)*6.0*1.05) = floor(239.4) = 239
    const dice: DieValue[] = [5, 5, 5, 5, 5];
    expect(calculateUltimateDamage({ dice, hand: 'yahtzee', equipment })).toBe(239);
  });
});

describe('calculateUltimateCategoryDamage', () => {
  const equipment = createDefaultSave().equipment;

  it('matches calculateUltimateDamage for the same hand', () => {
    const dice: DieValue[] = [2, 2, 2, 3, 3];
    const fromHand = calculateUltimateDamage({ dice, hand: 'fullHouse', equipment });
    const fromCategory = calculateUltimateCategoryDamage({ dice, category: 'fullHouse', equipment });
    expect(fromCategory).toBe(fromHand);
  });

  it('differentiates pair vs fourKind damage on the same dice', () => {
    const dice: DieValue[] = [4, 4, 4, 4, 2];
    const pair = calculateUltimateCategoryDamage({ dice, category: 'pair', equipment });
    const fourKind = calculateUltimateCategoryDamage({ dice, category: 'fourKind', equipment });
    expect(fourKind).toBeGreaterThan(pair);
  });

  it('throws on unknown category', () => {
    const dice: DieValue[] = [1, 2, 3, 4, 5];
    expect(() =>
      calculateUltimateCategoryDamage({ dice, category: 'unknown' as never, equipment })
    ).toThrow();
  });
});
