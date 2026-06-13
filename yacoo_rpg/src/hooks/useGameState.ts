import { useEffect, useState } from 'react';
import { claimRewardMeta, finishCombatMeta, upgradeSlotMeta } from '../game/appState';
import { createDefaultMeta } from '../game/equipment';
import { createRun } from '../game/run';
import { loadMeta, resetMeta, saveMeta } from '../game/storage';
import type { CombatOutcome, EquipmentSlot, MetaSave, RunState, Screen, YahtzeeHand } from '../game/types';

export function useGameState() {
  const [meta, setMeta] = useState<MetaSave>(() => loadMeta());
  const [run, setRun] = useState<RunState | null>(() => loadMeta().runInProgress ?? null);
  const [screen, setScreen] = useState<Screen>('home');

  useEffect(() => {
    saveMeta({ ...meta, runInProgress: run ?? undefined });
  }, [meta, run]);

  function getState() {
    return { meta, run, screen };
  }

  return {
    // Expose meta as "save" for backwards-compatible access by existing screens
    save: {
      stage: run?.chapter ?? meta.bestChapter,
      coins: meta.coins,
      equipment: meta.equipment,
      lastResult: meta.lastCombatResult
    },
    meta,
    run,
    screen,
    navigate: setScreen,

    startCombat: () => setScreen('combat'),

    startRun: () => {
      const newRun = createRun(meta);
      setRun(newRun);
      setMeta((m) => ({ ...m, runInProgress: newRun }));
      setScreen('runMap');
    },

    finishCombat: (outcome: CombatOutcome, handUsed?: YahtzeeHand) => {
      const next = finishCombatMeta(getState(), outcome, handUsed);
      setMeta(next.meta);
      setRun(next.run);
      setScreen(next.screen);
    },

    claimReward: () => {
      const next = claimRewardMeta(getState());
      setMeta(next.meta);
      setRun(next.run);
      setScreen(next.screen);
    },

    upgrade: (slot: EquipmentSlot) => {
      setMeta((current) => upgradeSlotMeta(current, slot));
    },

    reset: () => {
      const fresh = resetMeta();
      setMeta(fresh);
      setRun(null);
      setScreen('home');
    },

    grantCoins: (amount: number) => setMeta((current) => ({ ...current, coins: current.coins + amount })),

    forceResult: (outcome: CombatOutcome) => {
      const next = finishCombatMeta(getState(), outcome, outcome === 'win' ? 'yahtzee' : undefined, () => 0.9);
      setMeta(next.meta);
      setRun(next.run);
      setScreen(next.screen);
    },

    replaceMeta: (next: MetaSave) => setMeta(next),
    createDefaultMeta
  };
}
