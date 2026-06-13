import { describe, expect, it } from 'vitest';
import { REWARDED_AD_BUFF } from '../src/config.js';
import { getTotalRates } from '../src/economy.js';
import {
  claimRewardedAdBuffInState,
  advanceGame,
  buyProducerInState,
  claimCapsule,
  claimMissionInState,
  claimQuestInState,
  cloneState,
  createInitialState,
  formatResourceAmount,
  getPulsePlanktonGain,
  MIN_PULSE_PLANKTON_GAIN,
  PULSE_PRODUCTION_SECONDS,
  pulseReefInState,
  sanitizeState,
  setLocaleInState,
  startCapsule
} from '../src/gameState.js';

function stockedState(now) {
  const state = createInitialState(now);
  state.economy.resources.plankton = 1000;
  return state;
}

describe('gameState capsules', () => {
  it('starts only one capsule at a time', () => {
    const started = startCapsule(createInitialState(1000), 1000);
    const second = startCapsule(started, 2000);

    expect(started.started).toBe(true);
    expect(started.capsule.readyAt).toBeGreaterThan(1000);
    expect(second.started).toBe(false);
    expect(second.capsule.readyAt).toBe(started.capsule.readyAt);
  });

  it('claims a ready capsule and increments stats', () => {
    const withProducer = buyProducerInState(stockedState(1000), 'driftPolyps');
    const started = startCapsule(withProducer, 1000);
    const claimed = claimCapsule(started, started.capsule.readyAt);

    expect(claimed.claimed).toBe(true);
    expect(claimed.capsule).toBeNull();
    expect(claimed.economy.stats.capsulesOpened).toBe(1);
    expect(claimed.economy.resources.pearls).toBeGreaterThan(0);
  });
});

describe('gameState manual play and settings', () => {
  it('advances fresh games with idle plankton income before any pulse click', () => {
    const state = createInitialState(0);
    const advanced = advanceGame(state, 10_000);

    expect(advanced.economy.resources.plankton).toBeGreaterThan(state.economy.resources.plankton);
  });

  it('keeps pulse gain at the minimum when production is zero', () => {
    const state = createInitialState(1000);

    Object.keys(state.economy.producers).forEach((producerId) => {
      state.economy.producers[producerId] = 0;
    });

    expect(getPulsePlanktonGain(state.economy)).toBe(MIN_PULSE_PLANKTON_GAIN);
  });

  it('scales pulse gain from current plankton production', () => {
    const state = createInitialState(1000);
    state.economy.producers.driftPolyps = 1;
    const rate = getTotalRates(state.economy).plankton;

    expect(getPulsePlanktonGain(state.economy)).toBe(
      Math.max(MIN_PULSE_PLANKTON_GAIN, Math.ceil(MIN_PULSE_PLANKTON_GAIN + rate * PULSE_PRODUCTION_SECONDS))
    );
    expect(getPulsePlanktonGain(state.economy)).toBeGreaterThan(MIN_PULSE_PLANKTON_GAIN);
  });

  it('increases pulse gain as plankton production grows', () => {
    const lowProduction = createInitialState(1000);
    const highProduction = createInitialState(1000);
    lowProduction.economy.producers.driftPolyps = 1;
    highProduction.economy.producers.driftPolyps = 10;

    expect(getPulsePlanktonGain(highProduction.economy)).toBeGreaterThan(getPulsePlanktonGain(lowProduction.economy));
  });

  it('pulses the reef for computed immediate plankton income', () => {
    const state = createInitialState(1000);
    state.economy.producers.driftPolyps = 4;
    const gain = getPulsePlanktonGain(state.economy);
    const pulsed = pulseReefInState(state);

    expect(pulsed.economy.resources.plankton - state.economy.resources.plankton).toBe(gain);
    expect(pulsed.economy.lifetimeEarned.plankton - state.economy.lifetimeEarned.plankton).toBe(gain);
    expect(pulsed.notice).toBe('noticePulse');
  });

  it('stores supported locales and normalizes invalid locale values', () => {
    const state = createInitialState(1000);
    const korean = setLocaleInState(state, 'ko');
    const fallback = setLocaleInState(state, 'bad-locale');

    expect(korean.settings.locale).toBe('ko');
    expect(fallback.settings.locale).toBe('en');
  });

  it('applies rewarded ad overdrive to production for 30 seconds', () => {
    const state = stockedState(1000);
    state.economy.producers.driftPolyps = 1;
    const buffed = claimRewardedAdBuffInState(state, 1000, 'test-reward');
    const advanced = advanceGame(buffed, 11_000);
    const unbuffed = advanceGame(state, 11_000);

    expect(advanced.economy.resources.plankton - state.economy.resources.plankton).toBeGreaterThan(
      (unbuffed.economy.resources.plankton - state.economy.resources.plankton) * 2
    );
    expect(buffed.adBuffs.rewardId).toBe('test-reward');
  });

  it('blocks rewarded ad claims during cooldown and reopens after waiting', () => {
    const state = stockedState(1000);
    const first = claimRewardedAdBuffInState(state, 1000, 'first-reward');
    const blocked = claimRewardedAdBuffInState(first, 2000, 'blocked-reward');
    const reopened = claimRewardedAdBuffInState(first, 1000 + REWARDED_AD_BUFF.cooldownMs, 'second-reward');

    expect(first.claimed).toBe(true);
    expect(blocked.claimed).toBe(false);
    expect(blocked.notice).toBe('noticeAdBuffCooldown');
    expect(blocked.adBuffs.activeUntil).toBe(first.adBuffs.activeUntil);
    expect(blocked.adBuffs.lastClaimedAt).toBe(first.adBuffs.lastClaimedAt);
    expect(blocked.adBuffs.rewardId).toBe('first-reward');
    expect(reopened.claimed).toBe(true);
    expect(reopened.adBuffs.rewardId).toBe('second-reward');
  });

  it('formats invalid and huge values without Infinity or NaN text', () => {
    const values = [NaN, Infinity, -Infinity, -10, 1e12, 1e308];

    values.forEach((value) => {
      const formatted = formatResourceAmount(value);
      expect(formatted).not.toContain('Infinity');
      expect(formatted).not.toContain('NaN');
      expect(formatted.length).toBeLessThanOrEqual(10);
    });

    expect(formatResourceAmount(1e12)).toBe('1.0t');
    expect(formatResourceAmount(1e308)).toBe('1.0e308');
  });

  it('sanitizes non-finite timestamps during clone and load normalization', () => {
    const raw = createInitialState(0);
    raw.lastSavedAt = Infinity;
    raw.lastTickAt = NaN;

    expect(Number.isFinite(cloneState(raw).lastSavedAt)).toBe(true);
    expect(Number.isFinite(sanitizeState(raw, 5000).lastSavedAt)).toBe(true);
    expect(Number.isFinite(sanitizeState(raw, 5000).lastTickAt)).toBe(true);
  });

  it('legacy mission claims block duplicate mapped quest rewards', () => {
    let state = stockedState(1000);
    for (let index = 0; index < 5; index += 1) {
      state = buyProducerInState(state, 'driftPolyps');
    }

    const legacyClaimed = claimMissionInState(state, 'first-bloom');
    const duplicateQuest = claimQuestInState(legacyClaimed, 'quest-first-interns');

    expect(legacyClaimed.claimed).toBe(true);
    expect(legacyClaimed.progression.quests['quest-first-interns']).toBe('claimed');
    expect(duplicateQuest.claimed).toBe(false);
    expect(duplicateQuest.economy.resources.plankton).toBe(legacyClaimed.economy.resources.plankton);
  });
});
