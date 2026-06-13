import { CAPSULE_BASE_DURATION_MS, RESOURCE_KEYS } from './config.js';
import { canClaimRewardedAdBuff, claimRewardedAdBuff, createAdBuffState, getRewardedAdProductionMultiplier } from './adBuffManager.js';
import {
  addResources,
  applyProduction,
  buyProducer,
  cloneEconomy,
  createInitialEconomy,
  getCapsuleDurationMultiplier,
  getTotalRates,
  purchaseUpgrade
} from './economy.js';
import { normalizeLocale } from './i18n.js';
import { claimMission, cloneMissionState, createMissionState } from './missions.js';
import { mapLegacyMissionId } from './progression/legacyMissionAdapter.js';
import { migrateProgressionState } from './progression/migrations.js';
import { claimQuestReward, cloneProgressionState, createProgressionState, refreshProgressionState } from './progression/state.js';

export const MIN_PULSE_PLANKTON_GAIN = 3;
export const PULSE_PRODUCTION_SECONDS = 10;

export function getPulsePlanktonGain(economy) {
  const planktonRate = getTotalRates(economy).plankton;
  return Math.max(MIN_PULSE_PLANKTON_GAIN, Math.ceil(MIN_PULSE_PLANKTON_GAIN + planktonRate * PULSE_PRODUCTION_SECONDS));
}

export function createInitialState(now = Date.now()) {
  return {
    version: 2,
    economy: createInitialEconomy(),
    missions: createMissionState(),
    progression: createProgressionState(),
    settings: { locale: 'en' },
    adBuffs: createAdBuffState(),
    capsule: null,
    lastSavedAt: now,
    lastTickAt: now,
    notice: 'noticeInitial',
    noticeArgs: {}
  };
}

export function advanceGame(state, now = Date.now()) {
  const elapsedSeconds = Math.max(0, (now - state.lastTickAt) / 1000);
  return refreshStateProgression({
    ...cloneState(state),
    economy: applyProduction(state.economy, elapsedSeconds, getRewardedAdProductionMultiplier(state.adBuffs, now)),
    lastTickAt: now
  });
}

export function claimRewardedAdBuffInState(state, now = Date.now(), rewardId) {
  const claimed = canClaimRewardedAdBuff(state.adBuffs, now);
  return refreshStateProgression({
    ...cloneState(state),
    adBuffs: claimed ? claimRewardedAdBuff(state.adBuffs, now, rewardId) : createAdBuffState(state.adBuffs),
    notice: claimed ? 'noticeAdBuffClaimed' : 'noticeAdBuffCooldown',
    noticeArgs: {},
    claimed
  });
}

export function buyProducerInState(state, producerId) {
  const result = buyProducer(state.economy, producerId);
  return refreshStateProgression({
    ...cloneState(state),
    economy: result.state,
    notice: result.purchased ? 'noticeBuyProducer' : 'noticeNeedResources',
    noticeArgs: {},
    purchased: result.purchased
  });
}

export function pulseReefInState(state) {
  const amount = getPulsePlanktonGain(state.economy);
  return refreshStateProgression({
    ...cloneState(state),
    economy: addResources(state.economy, { plankton: amount }),
    notice: 'noticePulse',
    noticeArgs: { amount: formatResourceAmount(amount) }
  });
}

export function setLocaleInState(state, locale) {
  return {
    ...cloneState(state),
    settings: { locale: normalizeLocale(locale) },
    notice: 'noticeLocale',
    noticeArgs: {}
  };
}

export function purchaseUpgradeInState(state, upgradeId) {
  const result = purchaseUpgrade(state.economy, upgradeId);
  return refreshStateProgression({
    ...cloneState(state),
    economy: result.state,
    notice: result.purchased ? 'noticeBuyUpgrade' : 'noticeUpgradeBlocked',
    noticeArgs: {},
    purchased: result.purchased
  });
}

export function claimMissionInState(state, missionId) {
  const result = claimMission(state.economy, state.missions, missionId);
  const next = {
    ...cloneState(state),
    economy: result.economy,
    missions: result.missionState,
    notice: result.claimed ? 'noticeMissionClaimed' : 'noticeMissionBlocked',
    noticeArgs: {},
    claimed: result.claimed
  };
  return refreshStateProgression(result.claimed ? markMappedQuestClaimed(next, missionId) : next);
}

export function claimQuestInState(state, questId) {
  const result = claimQuestReward(cloneState(state), questId);
  return {
    ...result.state,
    notice: result.claimed ? 'noticeMissionClaimed' : 'noticeMissionBlocked',
    noticeArgs: {},
    claimed: result.claimed,
    reward: result.reward
  };
}

export function getCapsuleDurationMs(state) {
  return Math.round(CAPSULE_BASE_DURATION_MS * getCapsuleDurationMultiplier(state.economy));
}

export function startCapsule(state, now = Date.now()) {
  if (state.capsule) {
    return refreshStateProgression({ ...cloneState(state), started: false, notice: 'noticeCapsuleBusy', noticeArgs: {} });
  }
  const durationMs = getCapsuleDurationMs(state);
  return refreshStateProgression({
    ...cloneState(state),
    capsule: {
      startedAt: now,
      readyAt: now + durationMs,
      durationMs
    },
    notice: 'noticeCapsuleStarted',
    noticeArgs: {},
    started: true
  });
}

export function isCapsuleReady(state, now = Date.now()) {
  return Boolean(state.capsule && now >= state.capsule.readyAt);
}

export function getCapsuleReward(economy) {
  const planktonRate = Math.max(4, economy.producers.driftPolyps * 16 + economy.producers.cleanerShrimp * 60);
  const pearlReward = Math.max(2, Math.floor(economy.producers.shellNursery * 3 + economy.stats.missionsClaimed + 1));
  const tideReward = economy.producers.moonCurrent > 0 ? Math.max(1, Math.floor(economy.producers.moonCurrent / 2)) : 0;
  return {
    plankton: planktonRate,
    pearls: pearlReward,
    tideEnergy: tideReward
  };
}

export function claimCapsule(state, now = Date.now()) {
  if (!isCapsuleReady(state, now)) {
    return refreshStateProgression({ ...cloneState(state), claimed: false, notice: 'noticeCapsuleWaiting', noticeArgs: {} });
  }

  const reward = getCapsuleReward(state.economy);
  const economy = addResources(state.economy, reward);
  economy.stats.capsulesOpened += 1;
  return refreshStateProgression({
    ...cloneState(state),
    economy,
    capsule: null,
    notice: 'noticeCapsuleClaimed',
    noticeArgs: {},
    claimed: true,
    reward
  });
}

export function sanitizeState(raw, now = Date.now()) {
  const base = createInitialState(now);
  if (!raw || typeof raw !== 'object') {
    return base;
  }

  const economy = cloneEconomy({
    ...base.economy,
    ...(raw.economy || {})
  });
  ensureStarterProducer(economy);

  const missions = cloneMissionState(raw.missions || base.missions);
  const progression = migrateProgressionState(raw.progression, missions, economy);

  return refreshStateProgression({
    version: 2,
    economy,
    missions,
    progression,
    settings: sanitizeSettings(raw.settings || base.settings),
    adBuffs: createAdBuffState(raw.adBuffs || base.adBuffs),
    capsule: sanitizeCapsule(raw.capsule),
    lastSavedAt: finiteNumber(raw.lastSavedAt, now),
    lastTickAt: finiteNumber(raw.lastTickAt, finiteNumber(raw.lastSavedAt, now)),
    notice: typeof raw.notice === 'string' ? raw.notice : base.notice,
    noticeArgs: sanitizeNoticeArgs(raw.noticeArgs)
  });
}

export function cloneState(state) {
  return {
    version: 2,
    economy: cloneEconomy(state.economy),
    missions: cloneMissionState(state.missions),
    progression: cloneProgressionState(state.progression),
    settings: sanitizeSettings(state.settings),
    adBuffs: createAdBuffState(state.adBuffs),
    capsule: sanitizeCapsule(state.capsule),
    lastSavedAt: finiteNumber(state.lastSavedAt, 0),
    lastTickAt: finiteNumber(state.lastTickAt, 0),
    notice: state.notice || 'noticeInitial',
    noticeArgs: sanitizeNoticeArgs(state.noticeArgs)
  };
}

function refreshStateProgression(state) {
  return {
    ...state,
    progression: refreshProgressionState(state)
  };
}

function ensureStarterProducer(economy) {
  if (Object.values(economy.producers).every((count) => count === 0)) {
    economy.producers.driftPolyps = 1;
  }
}

function markMappedQuestClaimed(state, missionId) {
  const questId = mapLegacyMissionId(missionId);
  if (!questId) return state;
  const progression = cloneProgressionState(state.progression);
  progression.quests[questId] = 'claimed';
  return { ...state, progression };
}

function finiteNumber(value, fallback) {
  const number = Number(value);
  return Number.isFinite(number) ? number : fallback;
}

function sanitizeCapsule(capsule) {
  if (!capsule || typeof capsule !== 'object') {
    return null;
  }
  const startedAt = Number(capsule.startedAt);
  const readyAt = Number(capsule.readyAt);
  const durationMs = Number(capsule.durationMs);
  if (![startedAt, readyAt, durationMs].every(Number.isFinite)) {
    return null;
  }
  return { startedAt, readyAt, durationMs };
}

function sanitizeSettings(settings) {
  return { locale: normalizeLocale(settings?.locale) };
}

function sanitizeNoticeArgs(args) {
  if (!args || typeof args !== 'object') {
    return {};
  }
  return Object.fromEntries(Object.entries(args).map(([key, value]) => [key, String(value)]));
}

export function formatResourceAmount(value) {
  const amount = Number(value);
  if (!Number.isFinite(amount) || amount < 0) return '0.0';
  if (amount >= 1000000000000000) return amount.toExponential(1).replace('e+', 'e');
  if (amount >= 1000000000000) return `${(amount / 1000000000000).toFixed(1)}t`;
  if (amount >= 1000000000) return `${(amount / 1000000000).toFixed(1)}b`;
  if (amount >= 1000000) return `${(amount / 1000000).toFixed(1)}m`;
  if (amount >= 10000) return `${(amount / 1000).toFixed(1)}k`;
  if (amount >= 1000) return Math.floor(amount).toLocaleString('en-US');
  if (amount >= 100) return Math.floor(amount).toString();
  return amount.toFixed(amount < 10 ? 1 : 0);
}

export function formatReward(reward) {
  return RESOURCE_KEYS
    .filter((resource) => reward[resource])
    .map((resource) => `${formatResourceAmount(reward[resource])} ${resource}`)
    .join(', ');
}
