import { addResources } from '../economy.js';
import { CHAPTER_DEFINITIONS, CHAPTER_MAP, QUEST_DEFINITIONS, QUEST_MAP } from './content.js';

export const QUEST_STATUSES = ['locked', 'active', 'completed', 'claimed'];
export const CHAPTER_STATUSES = ['locked', 'active', 'completed'];

export function createProgressionState(values = {}) {
  return {
    chapters: Object.fromEntries(CHAPTER_DEFINITIONS.map((chapter) => [
      chapter.id,
      sanitizeStatus(values.chapters?.[chapter.id], CHAPTER_STATUSES, chapter.id === 'chapter1-office-reef' ? 'active' : 'locked')
    ])),
    quests: Object.fromEntries(QUEST_DEFINITIONS.map((quest) => [
      quest.id,
      sanitizeStatus(values.quests?.[quest.id], QUEST_STATUSES, quest.unlockConditions.length === 0 ? 'active' : 'locked')
    ]))
  };
}

export function cloneProgressionState(progression) {
  return createProgressionState(progression);
}

export function refreshProgressionState(gameState) {
  const progression = cloneProgressionState(gameState.progression);

  for (let guard = 0; guard < QUEST_DEFINITIONS.length; guard += 1) {
    let changed = false;
    QUEST_DEFINITIONS.forEach((quest) => {
      const current = progression.quests[quest.id];
      if (current === 'claimed') return;
      const unlocked = evaluateConditions(gameState, quest.unlockConditions, progression);
      const completed = unlocked && evaluateConditions(gameState, quest.completionConditions, progression);
      const next = !unlocked ? 'locked' : completed ? 'completed' : 'active';
      if (current !== next) {
        progression.quests[quest.id] = next;
        changed = true;
      }
    });
    if (!changed) break;
  }

  CHAPTER_DEFINITIONS.forEach((chapter) => {
    const completionStatus = progression.quests[chapter.completionQuestId];
    if (completionStatus === 'claimed') {
      progression.chapters[chapter.id] = 'completed';
    } else if ((chapter.entryQuestIds || []).some((questId) => progression.quests[questId] !== 'locked')) {
      progression.chapters[chapter.id] = 'active';
    }
  });

  return progression;
}

export function evaluateConditions(gameState, conditions = [], progression = gameState.progression) {
  return conditions.every((condition) => evaluateCondition(gameState, condition, progression));
}

export function evaluateCondition(gameState, condition, progression = gameState.progression) {
  const economy = gameState.economy;
  if (condition.type === 'producerCount') return (economy.producers[condition.producerId] || 0) >= condition.target;
  if (condition.type === 'resourceEarned') return (economy.lifetimeEarned[condition.resource] || 0) >= condition.target;
  if (condition.type === 'stat') return (economy.stats[condition.stat] || 0) >= condition.target;
  if (condition.type === 'upgradeCount') return (economy.stats.upgradesPurchased || 0) >= condition.target;
  if (condition.type === 'questClaimed') return progression?.quests?.[condition.questId] === 'claimed';
  if (condition.type === 'chapterCompleted') return progression?.chapters?.[condition.chapterId] === 'completed';
  return false;
}

export function claimQuestReward(gameState, questId) {
  const quest = QUEST_MAP[questId];
  const progression = refreshProgressionState(gameState);
  if (!quest || progression.quests[questId] !== 'completed') {
    return { state: { ...gameState, progression }, claimed: false };
  }

  const economy = addResources(gameState.economy, quest.reward);
  economy.stats.missionsClaimed += 1;
  const claimedProgression = cloneProgressionState(progression);
  claimedProgression.quests[questId] = 'claimed';
  const claimedState = { ...gameState, economy, progression: claimedProgression };

  return {
    state: { ...claimedState, progression: refreshProgressionState(claimedState) },
    claimed: true,
    reward: quest.reward
  };
}

export function getQuestDefinition(questId) {
  return QUEST_MAP[questId] || null;
}

export function getChapterDefinition(chapterId) {
  return CHAPTER_MAP[chapterId] || null;
}

function sanitizeStatus(status, allowed, fallback) {
  return allowed.includes(status) ? status : fallback;
}
