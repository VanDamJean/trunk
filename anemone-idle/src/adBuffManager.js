import { REWARDED_AD_BUFF } from './config.js';

export function createAdBuffState(values = {}) {
  return {
    activeUntil: finiteNonNegative(values.activeUntil),
    lastClaimedAt: finiteNonNegative(values.lastClaimedAt),
    rewardId: typeof values.rewardId === 'string' ? values.rewardId : null
  };
}

function finiteNonNegative(value) {
  const number = Number(value);
  return Number.isFinite(number) && number >= 0 ? number : 0;
}

export function claimRewardedAdBuff(buffState, now = Date.now(), rewardId = `local-ad-${now}`) {
  const current = createAdBuffState(buffState);
  if (!canClaimRewardedAdBuff(current, now)) {
    return current;
  }

  return {
    ...current,
    activeUntil: now + REWARDED_AD_BUFF.durationMs,
    lastClaimedAt: now,
    rewardId
  };
}

export function canClaimRewardedAdBuff(buffState, now = Date.now()) {
  return getRewardedAdBuffCooldownRemainingMs(buffState, now) === 0;
}

export function getRewardedAdBuffCooldownRemainingMs(buffState, now = Date.now()) {
  const state = createAdBuffState(buffState);
  if (!state.rewardId) {
    return 0;
  }
  const cooldownUntil = state.lastClaimedAt + REWARDED_AD_BUFF.cooldownMs;
  return Math.max(0, cooldownUntil - now);
}

export function getRewardedAdProductionMultiplier(buffState, now = Date.now()) {
  const active = createAdBuffState(buffState).activeUntil > now;
  // 광고 본 뒤 30초간 전체 생산 300% 폭주. 꿈틀밥도, 자동 고용도, 임원용 국수 팔도 같이 미쳐 돈다.
  return active ? REWARDED_AD_BUFF.multiplier : 1;
}
