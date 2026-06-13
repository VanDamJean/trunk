import { BALANCE_COST_GROWTH, BALANCE_UPGRADE_COST_GROWTH, PRODUCER_MAP, PRODUCERS, RESOURCE_KEYS, UPGRADE_MAP } from './config.js';
import {
  applyProducerSpawnProgress,
  createProducerProgressState,
  getProducerResourceTarget,
  getProducerSpawnTarget,
  isProducerSpawner
} from './logic/production.js';

const MAX_COST = Number.MAX_VALUE;
const MAX_UPGRADE_LEVEL = 500;

function finiteNonNegative(value, fallback = 0) {
  const number = Number(value);
  return Number.isFinite(number) && number >= 0 ? number : fallback;
}

function finiteNonNegativeInteger(value, max = Number.MAX_SAFE_INTEGER) {
  return Math.min(max, Math.floor(finiteNonNegative(value)));
}

function finiteCost(value) {
  return Number.isFinite(value) ? Math.ceil(Math.max(0, value)) : MAX_COST;
}

function finiteSum(current, delta) {
  const sum = finiteNonNegative(current) + finiteNonNegative(delta);
  return Number.isFinite(sum) ? sum : Number.MAX_VALUE;
}

export function createResourceBag(values = {}) {
  return RESOURCE_KEYS.reduce((resources, key) => {
    resources[key] = finiteNonNegative(values[key]);
    return resources;
  }, {});
}

export function createProducerState(values = {}) {
  return PRODUCERS.reduce((producers, producer) => {
    producers[producer.id] = finiteNonNegativeInteger(values[producer.id]);
    return producers;
  }, {});
}

export function createInitialEconomy() {
  return {
    resources: createResourceBag({ plankton: 20 }),
    lifetimeEarned: createResourceBag({ plankton: 20 }),
    producers: createProducerState({ driftPolyps: 1 }),
    producerProgress: createProducerProgressState(),
    purchasedUpgrades: [],
    upgradeLevels: createUpgradeLevelState(),
    stats: {
      purchases: 0,
      missionsClaimed: 0,
      capsulesOpened: 0,
      upgradesPurchased: 0,
      autoHires: 0
    }
  };
}

export function createUpgradeLevelState(values = {}) {
  return Object.fromEntries(Object.keys(UPGRADE_MAP).map((upgradeId) => [
    upgradeId,
    finiteNonNegativeInteger(values[upgradeId], MAX_UPGRADE_LEVEL)
  ]));
}

export function getUpgradeProductionMultiplier(state) {
  return Object.entries(state.upgradeLevels || {}).reduce((multiplier, [upgradeId, level]) => {
    return multiplier * (UPGRADE_MAP[upgradeId]?.multiplier || 1) ** level;
  }, 1);
}

export function getCapsuleDurationMultiplier(state) {
  return Object.entries(state.upgradeLevels || {}).reduce((multiplier, [upgradeId, level]) => {
    return multiplier * (UPGRADE_MAP[upgradeId]?.capsuleSpeed || 1) ** level;
  }, 1);
}

export function getUpgradeLevel(state, upgradeId) {
  return finiteNonNegativeInteger(state.upgradeLevels?.[upgradeId], MAX_UPGRADE_LEVEL);
}

export function getUpgradeCost(state, upgradeId) {
  const upgrade = UPGRADE_MAP[upgradeId];
  if (!upgrade) {
    throw new Error(`Unknown upgrade: ${upgradeId}`);
  }
  const level = getUpgradeLevel(state, upgradeId);
  return Object.fromEntries(Object.entries(upgrade.baseCost).map(([resource, base]) => [
    resource,
    finiteCost(base * BALANCE_UPGRADE_COST_GROWTH ** level)
  ]));
}

export function getProducerCost(state, producerId) {
  const producer = PRODUCER_MAP[producerId];
  if (!producer) {
    throw new Error(`Unknown producer: ${producerId}`);
  }
  const owned = state.producers[producerId] || 0;
  return finiteCost(producer.baseCost * producer.costGrowth ** owned);
}

export function getProducerRate(state, producerId) {
  const producer = PRODUCER_MAP[producerId];
  if (!producer) {
    throw new Error(`Unknown producer: ${producerId}`);
  }
  const owned = state.producers[producerId] || 0;
  return finiteNonNegative(owned * producer.baseRate * getUpgradeProductionMultiplier(state));
}

export function getTotalRates(state) {
  return PRODUCERS.reduce((rates, producer) => {
    const resource = getProducerResourceTarget(producer);
    if (resource) {
      rates[resource] += getProducerRate(state, producer.id);
    }
    return rates;
  }, createResourceBag());
}

export function getProducerSpawnRates(state) {
  return PRODUCERS.reduce((rates, producer) => {
    const targetProducerId = getProducerSpawnTarget(producer);
    if (targetProducerId) {
      rates[targetProducerId] = (rates[targetProducerId] || 0) + getProducerRate(state, producer.id);
    }
    return rates;
  }, {});
}

export function canBuyProducer(state, producerId) {
  const producer = PRODUCER_MAP[producerId];
  return Boolean(producer) && state.resources[producer.costResource] >= getProducerCost(state, producerId);
}

export function buyProducer(state, producerId) {
  const producer = PRODUCER_MAP[producerId];
  if (!producer || !canBuyProducer(state, producerId)) {
    return { state, purchased: false };
  }

  const cost = getProducerCost(state, producerId);
  const next = cloneEconomy(state);
  next.resources[producer.costResource] -= cost;
  next.producers[producerId] += 1;
  next.stats.purchases += 1;
  return { state: next, purchased: true, cost };
}

export function canAffordCost(state, cost) {
  return Object.entries(cost).every(([resource, amount]) => finiteNonNegative(state.resources[resource]) >= finiteNonNegative(amount, MAX_COST));
}

export function spendCost(state, cost) {
  const next = cloneEconomy(state);
  Object.entries(cost).forEach(([resource, amount]) => {
    next.resources[resource] = finiteNonNegative(next.resources[resource] - finiteNonNegative(amount, MAX_COST));
  });
  return next;
}

export function purchaseUpgrade(state, upgradeId) {
  const upgrade = UPGRADE_MAP[upgradeId];
  if (!upgrade) {
    return { state, purchased: false };
  }

  const cost = getUpgradeCost(state, upgradeId);
  if (!canAffordCost(state, cost)) {
    return { state, purchased: false };
  }

  const next = spendCost(state, cost);
  next.upgradeLevels[upgradeId] += 1;
  if (!next.purchasedUpgrades.includes(upgradeId)) {
    next.purchasedUpgrades.push(upgradeId);
  }
  next.stats.upgradesPurchased += 1;
  return { state: next, purchased: true, cost };
}

export function addResources(state, rewards) {
  const next = cloneEconomy(state);
  Object.entries(rewards).forEach(([resource, amount]) => {
    const value = finiteNonNegative(amount);
    next.resources[resource] = finiteSum(next.resources[resource], value);
    next.lifetimeEarned[resource] = finiteSum(next.lifetimeEarned[resource], value);
  });
  return next;
}

export function applyProduction(state, seconds, productionMultiplier = 1) {
  const safeSeconds = finiteNonNegative(seconds);
  if (safeSeconds === 0) {
    return cloneEconomy(state);
  }

  const safeMultiplier = finiteNonNegative(productionMultiplier);
  const next = cloneEconomy(state);
  const rates = getTotalRates(state);
  RESOURCE_KEYS.forEach((resource) => {
    const value = finiteNonNegative(rates[resource] * safeSeconds * safeMultiplier);
    next.resources[resource] = finiteSum(next.resources[resource], value);
    next.lifetimeEarned[resource] = finiteSum(next.lifetimeEarned[resource], value);
  });

  PRODUCERS.filter(isProducerSpawner).forEach((producer) => {
    applyProducerSpawnProgress(next, producer, getProducerRate(state, producer.id) * safeSeconds * safeMultiplier);
  });

  return next;
}

export function cloneEconomy(state) {
  return {
    resources: createResourceBag(state.resources),
    lifetimeEarned: createResourceBag(state.lifetimeEarned),
    producers: createProducerState(state.producers),
    producerProgress: createProducerProgressState(state.producerProgress),
    purchasedUpgrades: [...(state.purchasedUpgrades || [])],
    upgradeLevels: createUpgradeLevelState({
      ...Object.fromEntries((state.purchasedUpgrades || []).map((upgradeId) => [upgradeId, 1])),
      ...(state.upgradeLevels || {})
    }),
    stats: {
      purchases: finiteNonNegativeInteger(state.stats?.purchases),
      missionsClaimed: finiteNonNegativeInteger(state.stats?.missionsClaimed),
      capsulesOpened: finiteNonNegativeInteger(state.stats?.capsulesOpened),
      upgradesPurchased: finiteNonNegativeInteger(state.stats?.upgradesPurchased),
      autoHires: finiteNonNegativeInteger(state.stats?.autoHires)
    }
  };
}
