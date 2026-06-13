import { describe, expect, it, beforeEach } from 'vitest';
import { STORAGE_KEY } from './constants';
import { createDefaultSave } from './equipment';
import { isValidMetaSave, isValidSave, loadMeta, migrateV1ToV2, resetMeta, saveMeta } from './storage';
import type { GameSave, MetaSave } from './types';

const validV1Save: GameSave = {
  stage: 7,
  coins: 150,
  equipment: {
    weapon: { id: 'twig-wand', slot: 'weapon', name: 'Twig Wand', level: 2 },
    armor: { id: 'leaf-hoodie', slot: 'armor', name: 'Leaf Hoodie', level: 1 },
    charm: { id: 'lucky-acorn', slot: 'charm', name: 'Lucky Acorn', level: 1 },
    boots: { id: 'tiny-boots', slot: 'boots', name: 'Tiny Boots', level: 1 }
  }
};

describe('MetaSave validator', () => {
  it('accepts a valid v2 MetaSave', () => {
    const meta: MetaSave = {
      version: 2,
      coins: 100,
      equipment: validV1Save.equipment,
      bestChapter: 3,
      totalRuns: 5
    };
    expect(isValidMetaSave(meta)).toBe(true);
  });

  it('rejects a v1 save as MetaSave', () => {
    expect(isValidMetaSave(validV1Save)).toBe(false);
  });

  it('rejects missing fields', () => {
    expect(isValidMetaSave({ version: 2, coins: 0 })).toBe(false);
    expect(isValidMetaSave(null)).toBe(false);
    expect(isValidMetaSave(undefined)).toBe(false);
  });
});

describe('v1 → v2 migration', () => {
  it('preserves coins and equipment from v1', () => {
    const meta = migrateV1ToV2(validV1Save);
    expect(meta.version).toBe(2);
    expect(meta.coins).toBe(150);
    expect(meta.equipment.weapon.level).toBe(2);
  });

  it('converts stage to bestChapter (ceil of stage/6)', () => {
    expect(migrateV1ToV2({ ...validV1Save, stage: 1 }).bestChapter).toBe(1);
    expect(migrateV1ToV2({ ...validV1Save, stage: 6 }).bestChapter).toBe(1);
    expect(migrateV1ToV2({ ...validV1Save, stage: 7 }).bestChapter).toBe(2);
    expect(migrateV1ToV2({ ...validV1Save, stage: 12 }).bestChapter).toBe(2);
    expect(migrateV1ToV2({ ...validV1Save, stage: 13 }).bestChapter).toBe(3);
  });

  it('starts totalRuns at 0 and has no runInProgress', () => {
    const meta = migrateV1ToV2(validV1Save);
    expect(meta.totalRuns).toBe(0);
    expect(meta.runInProgress).toBeUndefined();
  });
});

describe('loadMeta', () => {
  beforeEach(() => localStorage.clear());

  it('returns default meta when storage is empty', () => {
    const meta = loadMeta();
    expect(meta.version).toBe(2);
    expect(meta.coins).toBe(0);
    expect(meta.bestChapter).toBe(1);
  });

  it('loads a valid v2 MetaSave', () => {
    const meta: MetaSave = {
      version: 2,
      coins: 999,
      equipment: validV1Save.equipment,
      bestChapter: 5,
      totalRuns: 10
    };
    localStorage.setItem(STORAGE_KEY, JSON.stringify(meta));
    const loaded = loadMeta();
    expect(loaded.coins).toBe(999);
    expect(loaded.bestChapter).toBe(5);
  });

  it('migrates a v1 save found in storage', () => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(validV1Save));
    const loaded = loadMeta();
    expect(loaded.version).toBe(2);
    expect(loaded.coins).toBe(150);
  });

  it('falls back to default on corrupt data', () => {
    localStorage.setItem(STORAGE_KEY, '{bad json');
    expect(loadMeta().coins).toBe(0);
    localStorage.setItem(STORAGE_KEY, JSON.stringify({ version: 2 }));
    expect(loadMeta().coins).toBe(0);
  });
});

describe('saveMeta / resetMeta', () => {
  beforeEach(() => localStorage.clear());

  it('saves and reloads MetaSave correctly', () => {
    const meta: MetaSave = {
      version: 2,
      coins: 777,
      equipment: createDefaultSave().equipment,
      bestChapter: 4,
      totalRuns: 3
    };
    saveMeta(meta);
    const loaded = loadMeta();
    expect(loaded.coins).toBe(777);
    expect(loaded.bestChapter).toBe(4);
  });

  it('resetMeta clears storage and returns default', () => {
    saveMeta({ version: 2, coins: 500, equipment: createDefaultSave().equipment, bestChapter: 2, totalRuns: 1 });
    const fresh = resetMeta();
    expect(fresh.coins).toBe(0);
    expect(localStorage.getItem(STORAGE_KEY)).toBeNull();
  });
});
