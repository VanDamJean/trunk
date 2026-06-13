import { describe, expect, it } from 'vitest';
import { applyProduction, createInitialEconomy, getTotalRates } from '../src/economy.js';

describe('balance simulation safety', () => {
  it('offline cap remains finite', () => {
    const economy = createInitialEconomy();
    economy.producers.crabBranchBoss = 10;
    economy.producers.whaleShareholder = 2;
    const advanced = applyProduction(economy, 60 * 60 * 4);

    expect(Object.values(advanced.resources).every(Number.isFinite)).toBe(true);
    expect(Object.values(advanced.producers).every(Number.isFinite)).toBe(true);
    expect(getTotalRates(advanced).plankton).toBeLessThan(20000);
  });
});
