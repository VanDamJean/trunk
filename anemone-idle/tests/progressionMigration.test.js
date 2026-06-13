import { describe, expect, it } from 'vitest';
import { createInitialState, sanitizeState } from '../src/gameState.js';

describe('progression migration', () => {
  it('migrates v1 saves and preserves claimed legacy missions', () => {
    const raw = createInitialState(0);
    raw.version = 1;
    raw.missions.claimed = ['first-bloom'];
    raw.economy.producerProgress.cleanerShrimp = 0.75;
    const migrated = sanitizeState(raw, 1000);

    expect(migrated.version).toBe(2);
    expect(migrated.progression.quests['quest-first-interns']).toBe('claimed');
    expect(migrated.economy.producerProgress.cleanerShrimp).toBe(0.75);
  });

  it('does not duplicate claimed rewards during migration', () => {
    const raw = createInitialState(0);
    raw.version = 1;
    raw.missions.claimed = ['first-bloom'];
    raw.economy.resources.plankton = 123;
    const migrated = sanitizeState(raw, 1000);

    expect(migrated.economy.resources.plankton).toBe(123);
  });

  it('maps legacy pearl-cache to the matching tide reward quest', () => {
    const raw = createInitialState(0);
    raw.version = 1;
    raw.missions.claimed = ['pearl-cache'];
    raw.economy.resources.tideEnergy = 5;
    const migrated = sanitizeState(raw, 1000);

    expect(migrated.progression.quests['quest-first-upgrade']).toBe('claimed');
    expect(migrated.economy.resources.tideEnergy).toBe(5);
  });
});
