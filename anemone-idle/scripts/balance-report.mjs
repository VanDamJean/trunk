import { mkdirSync, writeFileSync } from 'node:fs';
import { PRODUCERS, UPGRADE_DEFINITIONS } from '../src/config.js';
import { canAffordCost, canBuyProducer, getProducerCost, getTotalRates, getUpgradeCost } from '../src/economy.js';
import {
  advanceGame,
  buyProducerInState,
  claimCapsule,
  claimQuestInState,
  createInitialState,
  pulseReefInState,
  purchaseUpgradeInState,
  startCapsule
} from '../src/gameState.js';
import { CHAPTER1_BALANCE_TARGETS } from '../src/progression/balanceTargets.js';
import { getVisibleProgressionQuests } from '../src/progression/legacyMissionAdapter.js';

const scenario = process.argv.includes('--scenario') ? process.argv[process.argv.indexOf('--scenario') + 1] : 'chapter1-active';
if (scenario !== 'chapter1-active') {
  console.error(`Unknown scenario: ${scenario}`);
  process.exit(1);
}

const checkpoints = new Set([60, 180, 300, 420, 720, 900]);
let state = createInitialState(0);
const timeline = [];

for (let second = 0; second <= 900; second += 1) {
  const now = second * 1000;
  state = advanceGame(state, now);
  state = pulseReefInState(state);
  state = runCapsuleStep(state, now);
  state = claimReadyQuests(state);
  state = buyUsefulItems(state, second);
  state = claimReadyQuests(state);

  if (checkpoints.has(second)) {
    timeline.push(snapshot(second, state));
  }
}

const failedTargets = evaluateTargets(timeline);
const report = {
  scenario,
  passed: failedTargets.length === 0,
  failedTargets,
  checkpoints: timeline
};

mkdirSync('.omo/evidence', { recursive: true });
writeFileSync('.omo/evidence/balance-chapter1-active.json', `${JSON.stringify(report, null, 2)}\n`);
console.log(JSON.stringify({ scenario, passed: report.passed, failedTargets }, null, 2));

if (failedTargets.length > 0) process.exit(1);

function runCapsuleStep(current, now) {
  if (!current.capsule) return startCapsule(current, now);
  if (now >= current.capsule.readyAt) return claimCapsule(current, now);
  return current;
}

function claimReadyQuests(current) {
  return getVisibleProgressionQuests(current, 6).reduce((next, quest) => {
    if (!quest.complete) return next;
    return claimQuestInState(next, quest.id);
  }, current);
}

function buyUsefulItems(current, second) {
  let next = current;
  for (let guard = 0; guard < 80; guard += 1) {
    const buy = choosePurchase(next, second);
    if (!buy) return next;
    next = buy.type === 'producer' ? buyProducerInState(next, buy.id) : purchaseUpgradeInState(next, buy.id);
  }
  return next;
}

function choosePurchase(current, second) {
  const priority = second < 240
    ? ['driftPolyps', 'cleanerShrimp', 'shellNursery', 'crabBranchBoss', 'moonCurrent', 'whaleShareholder']
    : ['crabBranchBoss', 'cleanerShrimp', 'shellNursery', 'driftPolyps', 'moonCurrent', 'whaleShareholder'];
  const producer = priority
    .map((id) => PRODUCERS.find((entry) => entry.id === id))
    .find((entry) => entry && canBuyProducer(current.economy, entry.id));
  if (producer) return { type: 'producer', id: producer.id };

  const upgrade = UPGRADE_DEFINITIONS.find((entry) => canAffordCost(current.economy, getUpgradeCost(current.economy, entry.id)));
  return upgrade ? { type: 'upgrade', id: upgrade.id } : null;
}

function snapshot(seconds, current) {
  const rates = getTotalRates(current.economy);
  const visibleQuests = getVisibleProgressionQuests(current, 6);
  return {
    seconds,
    resources: current.economy.resources,
    producers: current.economy.producers,
    rates,
    crabCost: getProducerCost(current.economy, 'crabBranchBoss'),
    activeQuests: visibleQuests.filter((quest) => !quest.complete).map((quest) => quest.id),
    completedQuests: visibleQuests.filter((quest) => quest.complete).map((quest) => quest.id),
    failedTargets: []
  };
}

function evaluateTargets(items) {
  return CHAPTER1_BALANCE_TARGETS.flatMap((target) => {
    const item = items.find((entry) => entry.seconds === target.timeSeconds);
    const value = item ? metricValue(item, target.metric) : undefined;
    const failed = !Number.isFinite(value) || ('min' in target && value < target.min) || ('max' in target && value > target.max);
    if (!failed) return [];
    if (item) item.failedTargets.push(target.id);
    return [{ id: target.id, metric: target.metric, expected: { min: target.min, max: target.max }, actual: value }];
  });
}

function metricValue(item, metric) {
  if (metric.startsWith('producer.')) return item.producers[metric.slice('producer.'.length)] || 0;
  if (metric.startsWith('rate.')) return item.rates[metric.slice('rate.'.length)] || 0;
  if (metric === 'canSeeCrabWall') return item.crabCost > 0 ? 1 : 0;
  return NaN;
}
