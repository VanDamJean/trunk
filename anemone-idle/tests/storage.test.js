import { describe, expect, it } from 'vitest';
import { MAX_OFFLINE_SECONDS } from '../src/config.js';
import { buyProducerInState, createInitialState, setLocaleInState } from '../src/gameState.js';
import { createMemoryStorage, loadGame, saveGame } from '../src/storage.js';

function productiveState(now) {
  const state = createInitialState(now);
  state.economy.resources.plankton = 1000;
  return buyProducerInState(state, 'driftPolyps');
}

describe('storage', () => {
  it('saves and loads offline progress', () => {
    const storage = createMemoryStorage();
    const saved = saveGame(storage, productiveState(0), 0);
    const loaded = loadGame(storage, 60_000);

    expect(saved.lastSavedAt).toBe(0);
    expect(loaded.loaded).toBe(true);
    expect(loaded.offlineSeconds).toBe(60);
    expect(loaded.state.economy.resources.plankton).toBeGreaterThan(saved.economy.resources.plankton);
  });

  it('applies pending production before saving', () => {
    const storage = createMemoryStorage();
    const state = productiveState(0);
    const saved = saveGame(storage, state, 60_000);

    expect(saved.lastTickAt).toBe(60_000);
    expect(saved.economy.resources.plankton).toBeGreaterThan(state.economy.resources.plankton);
  });

  it('caps offline progress at the configured maximum', () => {
    const storage = createMemoryStorage();
    saveGame(storage, productiveState(0), 0);
    const loaded = loadGame(storage, (MAX_OFFLINE_SECONDS + 600) * 1000);

    expect(loaded.capped).toBe(true);
    expect(loaded.offlineSeconds).toBe(MAX_OFFLINE_SECONDS);
  });

  it('does not double-grant the same offline interval after saving a loaded state', () => {
    const storage = createMemoryStorage();
    saveGame(storage, productiveState(0), 0);

    const first = loadGame(storage, 60_000);
    saveGame(storage, first.state, 60_000);
    const second = loadGame(storage, 60_000);

    expect(second.offlineSeconds).toBe(0);
    expect(second.state.economy.resources.plankton).toBe(first.state.economy.resources.plankton);
  });

  it('does not grant offline production for future saved timestamps', () => {
    const storage = createMemoryStorage();
    const saved = saveGame(storage, productiveState(60_000), 60_000);

    const loaded = loadGame(storage, 0);

    expect(loaded.offlineSeconds).toBe(0);
    expect(loaded.state.economy.resources.plankton).toBe(saved.economy.resources.plankton);
  });

  it('normalizes legacy zero-producer saves into starter idle production', () => {
    const legacy = createInitialState(0);
    Object.keys(legacy.economy.producers).forEach((producerId) => {
      legacy.economy.producers[producerId] = 0;
    });
    const storage = createMemoryStorage({
      'anemone-idle-save-v1': JSON.stringify(legacy)
    });

    const loaded = loadGame(storage, 60_000);

    expect(loaded.state.economy.producers.driftPolyps).toBeGreaterThanOrEqual(1);
    expect(loaded.state.economy.resources.plankton).toBeGreaterThan(legacy.economy.resources.plankton);
  });

  it('returns a fresh state for corrupt saves', () => {
    const storage = createMemoryStorage({ 'anemone-idle-save-v1': '{bad json' });
    const loaded = loadGame(storage, 5000);

    expect(loaded.loaded).toBe(false);
    expect(loaded.state.economy.resources.plankton).toBe(20);
  });

  it('keeps gameplay running when storage reads or writes fail', () => {
    const throwingStorage = {
      getItem() {
        throw new Error('blocked storage');
      },
      setItem() {
        throw new Error('quota exceeded');
      },
      removeItem() {}
    };

    const loaded = loadGame(throwingStorage, 5000);
    const saved = saveGame(throwingStorage, productiveState(0), 60_000);

    expect(loaded.loaded).toBe(false);
    expect(loaded.state.economy.resources.plankton).toBe(20);
    expect(saved.economy.resources.plankton).toBeGreaterThan(20);
  });

  it('persists locale settings and defaults invalid saved locales to English', () => {
    const storage = createMemoryStorage();
    const korean = setLocaleInState(createInitialState(0), 'ko');
    saveGame(storage, korean, 0);

    const loaded = loadGame(storage, 0);
    const invalidStorage = createMemoryStorage({
      'anemone-idle-save-v1': JSON.stringify({ ...korean, settings: { locale: 'fr' } })
    });
    const invalid = loadGame(invalidStorage, 1000);

    expect(loaded.state.settings.locale).toBe('ko');
    expect(invalid.state.settings.locale).toBe('en');
  });

  it('saves sanitized finite numeric fields instead of JSON nulls', () => {
    const storage = createMemoryStorage();
    const state = createInitialState(0);
    state.economy.resources.plankton = Infinity;
    state.economy.resources.pearls = 1e250;
    state.economy.producerProgress.cleanerShrimp = Infinity;
    state.economy.upgradeLevels.silkTentacles = Infinity;

    saveGame(storage, state, 0);
    const raw = storage.getItem('anemone-idle-save-v1');
    const parsed = JSON.parse(raw);
    const loaded = loadGame(storage, 0);

    expect(parsed.economy.resources.plankton).toBe(0);
    expect(parsed.economy.resources.pearls).toBe(1e250);
    expect(parsed.economy.producerProgress.cleanerShrimp).toBe(0);
    expect(parsed.economy.upgradeLevels.silkTentacles).toBe(0);
    expect(loaded.state.economy.producerProgress.cleanerShrimp).toBe(0);
    expect(loaded.state.economy.upgradeLevels.silkTentacles).toBe(0);
  });

  it('loads older saves where JSON converted invalid numbers to null', () => {
    const state = createInitialState(0);
    state.economy.resources.plankton = null;
    state.economy.producerProgress.cleanerShrimp = null;
    state.economy.upgradeLevels.silkTentacles = null;
    const storage = createMemoryStorage({
      'anemone-idle-save-v1': JSON.stringify(state)
    });

    const loaded = loadGame(storage, 0);

    expect(loaded.loaded).toBe(true);
    expect(loaded.state.economy.resources.plankton).toBe(0);
    expect(loaded.state.economy.producerProgress.cleanerShrimp).toBe(0);
    expect(loaded.state.economy.upgradeLevels.silkTentacles).toBe(0);
  });
});
