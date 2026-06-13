import { describe, expect, it } from 'vitest';
import { MISSION_DEFINITIONS, PRODUCERS, UPGRADE_DEFINITIONS } from '../src/config.js';
import {
  applyProduction,
  buyProducer,
  canAffordCost,
  canBuyProducer,
  createInitialEconomy,
  createResourceBag,
  getProducerCost,
  getProducerSpawnRates,
  getTotalRates,
  getUpgradeProductionMultiplier,
  getUpgradeCost,
  getUpgradeLevel,
  purchaseUpgrade
} from '../src/economy.js';
import {
  advanceGame,
  buyProducerInState,
  claimMissionInState,
  createInitialState,
  pulseReefInState,
  purchaseUpgradeInState
} from '../src/gameState.js';
import { createProducerProgressState } from '../src/logic/production.js';
import { isMissionComplete } from '../src/missions.js';

function withResources(resources) {
  const state = createInitialEconomy();
  return {
    ...state,
    resources: { ...state.resources, ...resources },
    lifetimeEarned: { ...state.lifetimeEarned, ...resources }
  };
}

function claimReadyMissions(state) {
  return MISSION_DEFINITIONS.reduce((next, mission) => {
    if (next.missions.claimed.includes(mission.id) || !isMissionComplete(next.economy, mission)) {
      return next;
    }
    return claimMissionInState(next, mission.id);
  }, state);
}

function buyAffordableBalanceItems(state) {
  let next = state;
  for (let guard = 0; guard < 200; guard += 1) {
    const producerBuys = PRODUCERS
      .filter((producer) => canBuyProducer(next.economy, producer.id))
      .map((producer) => ({ type: 'producer', id: producer.id, cost: getProducerCost(next.economy, producer.id) }));
    const upgradeBuys = UPGRADE_DEFINITIONS
      .map((upgrade) => ({ type: 'upgrade', id: upgrade.id, cost: getUpgradeCost(next.economy, upgrade.id) }))
      .filter((buy) => canAffordCost(next.economy, buy.cost))
      .map((buy) => ({ ...buy, cost: Object.values(buy.cost).reduce((sum, value) => sum + value, 0) }));
    const buy = [...producerBuys, ...upgradeBuys].sort((a, b) => b.cost - a.cost)[0];
    if (!buy) {
      return next;
    }
    next = buy.type === 'producer' ? buyProducerInState(next, buy.id) : purchaseUpgradeInState(next, buy.id);
  }
  return next;
}

function simulateActiveStart(seconds) {
  let state = createInitialState(0);
  for (let second = 0; second <= seconds; second += 1) {
    state = advanceGame(state, second * 1000);
    state = pulseReefInState(state);
    state = claimReadyMissions(state);
    state = buyAffordableBalanceItems(state);
  }
  return state.economy;
}

describe('economy', () => {
  it('starts fresh economies with passive plankton production', () => {
    const economy = createInitialEconomy();

    expect(economy.producers.driftPolyps).toBe(1);
    expect(getTotalRates(economy).plankton).toBeGreaterThan(0);
  });

  it('buys producers, spends cost, and scales next cost', () => {
    const economy = withResources({ plankton: 100 });
    const firstCost = getProducerCost(economy, 'driftPolyps');
    const previousOwned = economy.producers.driftPolyps;
    const result = buyProducer(economy, 'driftPolyps');

    expect(result.purchased).toBe(true);
    expect(result.state.producers.driftPolyps).toBe(previousOwned + 1);
    expect(result.state.resources.plankton).toBe(100 - firstCost);
    expect(getProducerCost(result.state, 'driftPolyps')).toBeGreaterThan(firstCost);
  });

  it('applies per-second production into resources and lifetime totals', () => {
    const bought = buyProducer(withResources({ plankton: 100 }), 'driftPolyps').state;
    const advanced = applyProduction(bought, 10);

    expect(advanced.resources.plankton).toBeGreaterThan(bought.resources.plankton);
    expect(advanced.lifetimeEarned.plankton).toBeGreaterThan(bought.lifetimeEarned.plankton);
  });

  it('auto-hires lower-tier producers through the office hierarchy', () => {
    const economy = withResources({ plankton: 5000, pearls: 30000 });
    economy.producers.cleanerShrimp = 1;
    economy.producers.crabBranchBoss = 1;
    economy.producers.whaleShareholder = 1;

    const advanced = applyProduction(economy, 1000);

    expect(advanced.producers.driftPolyps).toBeGreaterThan(economy.producers.driftPolyps);
    expect(advanced.producers.cleanerShrimp).toBeGreaterThan(economy.producers.cleanerShrimp);
    expect(advanced.producers.crabBranchBoss).toBeGreaterThan(economy.producers.crabBranchBoss);
    expect(advanced.stats.autoHires).toBeGreaterThan(0);
  });

  it('reports producer spawn rates separately from resource rates', () => {
    const economy = withResources({ plankton: 5000 });
    economy.producers.cleanerShrimp = 2;

    expect(getTotalRates(economy).plankton).toBeGreaterThan(0);
    expect(getProducerSpawnRates(economy).driftPolyps).toBeGreaterThan(0);

    const advanced = applyProduction(economy, 1000);
    expect(advanced.resources.plankton).toBeGreaterThan(economy.resources.plankton);
    expect(advanced.producers.driftPolyps).toBeGreaterThan(economy.producers.driftPolyps);
  });

  it('pearl upgrades increase all production rates once purchased', () => {
    const economy = buyProducer(withResources({ plankton: 1000, pearls: 100 }), 'driftPolyps').state;
    const before = getTotalRates(economy).plankton;
    const upgraded = purchaseUpgrade(economy, 'silkTentacles');

    expect(upgraded.purchased).toBe(true);
    expect(getTotalRates(upgraded.state).plankton).toBeGreaterThan(before);
  });

  it('scales repeated upgrade costs with a stronger wall than producer costs', () => {
    const economy = withResources({ pearls: 1000 });
    const firstCost = getUpgradeCost(economy, 'silkTentacles').pearls;
    const first = purchaseUpgrade(economy, 'silkTentacles').state;
    const secondCost = getUpgradeCost(first, 'silkTentacles').pearls;

    expect(firstCost).toBe(18);
    expect(secondCost).toBeGreaterThan(Math.ceil(18 * 1.15));
    expect(getUpgradeLevel(first, 'silkTentacles')).toBe(1);
  });

  it('keeps active early-game growth finite by 15 minutes', () => {
    const fiveMinuteEconomy = simulateActiveStart(5 * 60);
    const fifteenMinuteEconomy = simulateActiveStart(15 * 60);
    const fifteenMinuteRates = getTotalRates(fifteenMinuteEconomy);

    expect(fiveMinuteEconomy.producers.cleanerShrimp).toBeGreaterThanOrEqual(8);
    expect(fifteenMinuteEconomy.producers.crabBranchBoss).toBeGreaterThanOrEqual(1);
    expect(Number.isFinite(getUpgradeProductionMultiplier(fifteenMinuteEconomy))).toBe(true);
    expect(Object.values(fifteenMinuteRates).every(Number.isFinite)).toBe(true);
    expect(fifteenMinuteRates.plankton).toBeLessThan(Number.MAX_SAFE_INTEGER);
  });

  it('uses a tuned 1.35 multiplier for prism reef', () => {
    const economy = withResources({ pearls: 1000, tideEnergy: 1000 });
    const upgraded = purchaseUpgrade(economy, 'prismReef').state;

    expect(getUpgradeProductionMultiplier(upgraded)).toBeCloseTo(1.35);
  });

  it('sanitizes invalid numeric economy fields to finite values', () => {
    expect(createInitialEconomy().resources.plankton).toBe(20);
    expect(createProducerProgressState({ cleanerShrimp: Infinity }).cleanerShrimp).toBe(0);

    const resources = createResourceBag({ plankton: Infinity, pearls: NaN, tideEnergy: -Infinity });
    expect(resources.plankton).toBe(0);
    expect(resources.pearls).toBe(0);
    expect(resources.tideEnergy).toBe(0);
  });

  it('caps extreme cost calculations to finite numbers', () => {
    const economy = createInitialEconomy();
    economy.producers.driftPolyps = 1_000_000_000;
    economy.upgradeLevels.silkTentacles = 1_000_000_000;

    expect(Number.isFinite(getProducerCost(economy, 'driftPolyps'))).toBe(true);
    expect(Number.isFinite(getUpgradeCost(economy, 'silkTentacles').pearls)).toBe(true);
  });
});
