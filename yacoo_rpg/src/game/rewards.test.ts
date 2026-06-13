import { describe, expect, it } from 'vitest';
import { createDefaultSave } from './equipment';
import { createLossReward, createWinReward } from './rewards';

describe('rewards', () => {
  it('returns stage-scaled win coins before duplicate conversion', () => {
    const save = createDefaultSave();
    expect(createWinReward(3, save.equipment, () => 0.9)).toEqual({ coinsEarned: 60 });
  });

  it('converts duplicate equipment into bonus coins deterministically', () => {
    const save = createDefaultSave();
    const calls = [0.1, 0.0];
    const reward = createWinReward(1, save.equipment, () => calls.shift() ?? 0);
    expect(reward).toEqual({ coinsEarned: 60, duplicateItemName: 'Twig Wand' });
  });

  it('returns fixed loss reward', () => {
    expect(createLossReward()).toEqual({ coinsEarned: 10 });
  });
});
