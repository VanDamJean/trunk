import { describe, expect, it } from 'vitest';
import { createDefaultSave } from './equipment';
import { calculateDiceDamage, calculateEnemyDamage, calculateSelectedCategoryDamage, clampHp, createEnemy } from './combat';
import { isAttackCategoryValid } from './yahtzee';
import type { DieValue } from './types';

describe('combat helpers', () => {
  it('creates enemies from the turn-based stage formula', () => {
    // M1.5: new balance — maxHp = 100 + 40*(stage-1), attack = 18 + 4*(stage-1)
    expect(createEnemy(3)).toMatchObject({ stage: 3, maxHp: 180, attack: 26 });
  });

  it('calculates dice damage using attack, hand multiplier, and charm bonus', () => {
    const save = createDefaultSave();
    expect(calculateDiceDamage(save.equipment, 'fullHouse')).toBe(34);
  });

  it('calculates selected-category damage from the selected multiplier', () => {
    const save = createDefaultSave();
    const qualifyingDice: DieValue[] = [2, 2, 2, 2, 5];

    expect(isAttackCategoryValid(qualifyingDice, 'pair')).toBe(true);
    expect(isAttackCategoryValid(qualifyingDice, 'fourKind')).toBe(true);
    expect(calculateSelectedCategoryDamage(save.equipment, 'pair')).toBe(16);
    expect(calculateSelectedCategoryDamage(save.equipment, 'fourKind')).toBe(43);
  });

  it('keeps selected pair damage below automatic four-kind damage on four-kind dice', () => {
    const save = createDefaultSave();
    const fourKindDice: DieValue[] = [2, 2, 2, 2, 5];

    expect(isAttackCategoryValid(fourKindDice, 'pair')).toBe(true);
    expect(isAttackCategoryValid(fourKindDice, 'fourKind')).toBe(true);
    expect(calculateSelectedCategoryDamage(save.equipment, 'pair')).toBe(16);
    expect(calculateSelectedCategoryDamage(save.equipment, 'pair')).not.toBe(calculateSelectedCategoryDamage(save.equipment, 'fourKind'));
  });

  it('calculates selected full house and straight category damage deterministically', () => {
    const save = createDefaultSave();

    expect(calculateSelectedCategoryDamage(save.equipment, 'fullHouse')).toBe(34);
    expect(calculateSelectedCategoryDamage(save.equipment, 'largeStraight')).toBe(51);
    expect(calculateSelectedCategoryDamage(save.equipment, 'yahtzee')).toBe(81);
  });

  it('clamps HP to zero and keeps enemy damage at least one', () => {
    const save = createDefaultSave();
    expect(clampHp(-12)).toBe(0);
    expect(calculateEnemyDamage({ stage: 1, maxHp: 10, attack: 1, name: 'Tiny' }, save.equipment)).toBe(1);
  });
});
