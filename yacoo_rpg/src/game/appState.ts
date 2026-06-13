import { createLossReward, createWinReward } from './rewards';
import { upgradeEquipment } from './equipment';
import { advanceNode, settleRun } from './run';
import type {
  CombatOutcome,
  CombatResult,
  GameSave,
  MetaSave,
  Rng,
  RunState,
  Screen,
  YahtzeeHand
} from './types';

// ── Legacy AppState (v1 screens still use this) ───────────────────────

export interface AppState {
  save: GameSave;
  screen: Screen;
}

export function finishCombat(save: GameSave, outcome: CombatOutcome, handUsed?: YahtzeeHand, rng: Rng = Math.random): AppState {
  const reward = outcome === 'win' ? createWinReward(save.stage, save.equipment, rng) : createLossReward();
  const lastResult: CombatResult = {
    outcome,
    stage: save.stage,
    coinsEarned: reward.coinsEarned,
    handUsed,
    duplicateItemName: reward.duplicateItemName
  };
  return { save: { ...save, lastResult }, screen: 'result' };
}

export function claimReward(save: GameSave): AppState {
  if (!save.lastResult) {
    return { save, screen: 'home' };
  }
  const nextStage = save.lastResult.outcome === 'win' ? save.stage + 1 : save.stage;
  return {
    save: {
      ...save,
      stage: nextStage,
      coins: save.coins + save.lastResult.coinsEarned,
      lastResult: undefined
    },
    screen: 'home'
  };
}

export function upgradeSlot(save: GameSave, slot: keyof GameSave['equipment']): GameSave {
  return upgradeEquipment(save, slot);
}

// ── M1: MetaSave-based state transitions ──────────────────────────────

export interface MetaAppState {
  meta: MetaSave;
  run: RunState | null;
  screen: Screen;
}

export function finishCombatMeta(
  state: MetaAppState,
  outcome: CombatOutcome,
  handUsed?: YahtzeeHand,
  rng: Rng = Math.random
): MetaAppState {
  if (!state.run) {
    // Fallback: combat started without an active run (legacy / direct startCombat path)
    const stage = state.meta.bestChapter;
    const reward = outcome === 'win' ? createWinReward(stage, state.meta.equipment, rng) : createLossReward();
    const lastCombatResult: CombatResult = {
      outcome,
      stage,
      coinsEarned: reward.coinsEarned,
      handUsed,
      duplicateItemName: reward.duplicateItemName
    };
    return { meta: { ...state.meta, lastCombatResult }, run: null, screen: 'result' };
  }

  const stage = state.run.chapter;
  const reward = outcome === 'win'
    ? createWinReward(stage, state.meta.equipment, rng)
    : createLossReward();

  const lastCombatResult: CombatResult = {
    outcome,
    stage,
    coinsEarned: reward.coinsEarned,
    handUsed,
    duplicateItemName: reward.duplicateItemName
  };

  if (outcome === 'loss') {
    const settled = settleRun(state.meta, state.run, reward.coinsEarned);
    return {
      meta: { ...settled, lastCombatResult },
      run: null,
      screen: 'runResult'
    };
  }

  const nextRun = advanceNode(state.run);
  return {
    meta: { ...state.meta, lastCombatResult },
    run: { ...nextRun, hp: Math.max(0, nextRun.hp) },
    screen: 'result'
  };
}

export function claimRewardMeta(state: MetaAppState): MetaAppState {
  const result = state.meta.lastCombatResult;
  if (!result) return { ...state, screen: 'home' };

  const nextMeta: MetaSave = {
    ...state.meta,
    coins: state.meta.coins + result.coinsEarned,
    lastCombatResult: undefined
  };

  // If there's an active run, go back to the run map; otherwise home
  const nextScreen: Screen = state.run ? 'runMap' : 'home';
  return { meta: nextMeta, run: state.run, screen: nextScreen };
}

export function upgradeSlotMeta(meta: MetaSave, slot: keyof MetaSave['equipment']): MetaSave {
  return upgradeEquipment(meta, slot);
}
