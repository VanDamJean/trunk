import { getProducerCost, getProducerSpawnRates, getTotalRates } from '../economy.js';
import { QUEST_DEFINITIONS, QUEST_MAP } from './content.js';
import { evaluateCondition, refreshProgressionState } from './state.js';

export const LEGACY_MISSION_TO_QUEST = {
  'first-bloom': 'quest-first-interns',
  'shrimp-shift': 'quest-first-shrimp',
  'pearl-cache': 'quest-first-upgrade',
  'branch-boss': 'quest-first-crab',
  'tidal-gift': 'quest-first-capsule',
  'whale-boardroom': null,
  'prismatic-growth': 'quest-chapter1-complete'
};

export function getVisibleProgressionQuests(state, limit = 3) {
  const progression = refreshProgressionState(state);
  return QUEST_DEFINITIONS
    .filter((quest) => progression.quests[quest.id] === 'active' || progression.quests[quest.id] === 'completed')
    .slice(0, limit)
    .map((quest) => decorateQuest(state, quest, progression));
}

export function getAllProgressionQuests(state) {
  const progression = refreshProgressionState(state);
  return QUEST_DEFINITIONS.map((quest) => decorateQuest(state, quest, progression));
}

export function getChapterDashboard(state) {
  const quests = getAllProgressionQuests(state);
  const completedCount = quests.filter((quest) => quest.status === 'claimed').length;
  const nextQuest = quests.find((quest) => quest.status === 'completed') || quests.find((quest) => quest.status === 'active') || null;
  const rates = getTotalRates(state.economy);
  const spawnRates = getProducerSpawnRates(state.economy);
  const spawnProgress = Object.entries(state.economy.producerProgress || {})
    .filter(([producerId]) => state.economy.producers[producerId] > 0)
    .map(([producerId, progress]) => ({ producerId, progress: Math.floor((progress || 0) * 100), rate: spawnRates[producerId] || 0 }));
  const crabCost = getProducerCost(state.economy, 'crabBranchBoss');

  return {
    chapterId: 'chapter1-office-reef',
    progressPercent: Math.round((completedCount / quests.length) * 100),
    nextQuest,
    rates,
    spawnProgress,
    bottleneckResource: state.economy.resources.plankton < crabCost ? 'plankton' : 'pearls'
  };
}

function decorateQuest(state, quest, progression) {
  const condition = quest.completionConditions[0];
  const progress = getConditionProgress(state, condition, progression);
  return {
    ...quest,
    status: progression.quests[quest.id],
    complete: progression.quests[quest.id] === 'completed',
    progress: progress.value,
    target: progress.target,
    progressLabelKey: progress.labelKey
  };
}

function getConditionProgress(state, condition, progression) {
  if (!condition) return { value: 1, target: 1, labelKey: 'progressGeneric' };
  if (condition.type === 'producerCount') {
    return { value: state.economy.producers[condition.producerId] || 0, target: condition.target, labelKey: 'progressProducer' };
  }
  if (condition.type === 'resourceEarned') {
    return { value: state.economy.lifetimeEarned[condition.resource] || 0, target: condition.target, labelKey: 'progressResource' };
  }
  if (condition.type === 'stat') {
    return { value: state.economy.stats[condition.stat] || 0, target: condition.target, labelKey: 'progressStat' };
  }
  if (condition.type === 'upgradeCount') {
    return { value: state.economy.stats.upgradesPurchased || 0, target: condition.target, labelKey: 'progressUpgrade' };
  }
  if (condition.type === 'questClaimed') {
    return { value: evaluateCondition(state, condition, progression) ? 1 : 0, target: 1, labelKey: 'progressQuest' };
  }
  return { value: 0, target: 1, labelKey: 'progressGeneric' };
}

export function mapLegacyMissionId(missionId) {
  return Object.hasOwn(LEGACY_MISSION_TO_QUEST, missionId) ? LEGACY_MISSION_TO_QUEST[missionId] : undefined;
}

export function getQuestByLegacyMissionId(missionId) {
  const questId = mapLegacyMissionId(missionId);
  return questId ? QUEST_MAP[questId] : null;
}
