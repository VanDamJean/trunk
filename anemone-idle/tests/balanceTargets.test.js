import { describe, expect, it } from 'vitest';
import { CHAPTER1_BALANCE_TARGETS, validateBalanceTargets } from '../src/progression/balanceTargets.js';

describe('balance targets', () => {
  it('keeps Chapter 1 targets finite and sourced', () => {
    expect(validateBalanceTargets()).toEqual([]);
    expect(CHAPTER1_BALANCE_TARGETS.map((target) => target.id)).toContain('target-900s-first-crab');
  });

  it('rejects invalid target fixtures', () => {
    expect(validateBalanceTargets([{ id: 'bad', timeSeconds: Infinity, metric: 'x', min: 1, source: 'test' }]).join('\n')).toContain('invalid balance target');
  });
});
