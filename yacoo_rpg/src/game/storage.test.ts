import { describe, expect, it } from 'vitest';
import { STORAGE_KEY } from './constants';
import { createDefaultSave } from './equipment';
import { loadSave, resetSave, saveGame } from './storage';

describe('storage', () => {
  it('loads defaults for missing or invalid saves', () => {
    localStorage.clear();
    expect(loadSave().stage).toBe(1);
    localStorage.setItem(STORAGE_KEY, '{bad json');
    expect(loadSave().coins).toBe(0);
    localStorage.setItem(STORAGE_KEY, JSON.stringify({ stage: 9, coins: 9 }));
    expect(loadSave().stage).toBe(1);
  });

  it('saves and resets valid game state', () => {
    const save = { ...createDefaultSave(), stage: 4, coins: 70 };
    saveGame(save);
    expect(loadSave().stage).toBe(4);
    resetSave();
    expect(localStorage.getItem(STORAGE_KEY)).toBeNull();
    expect(loadSave().stage).toBe(1);
  });
});
