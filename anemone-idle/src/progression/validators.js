import { PRODUCER_MAP, RESOURCE_KEYS } from '../config.js';
import { CHAPTER_DEFINITIONS, CONDITION_TYPES, QUEST_DEFINITIONS } from './content.js';

const RESOURCE_SET = new Set(RESOURCE_KEYS);

export function validateProgressionContent({ chapters = CHAPTER_DEFINITIONS, quests = QUEST_DEFINITIONS } = {}) {
  const errors = [];
  const chapterIds = collectDuplicateAwareIds(chapters, 'chapter id', errors);
  const questIds = collectDuplicateAwareIds(quests, 'quest id', errors);

  quests.forEach((quest) => {
    if (!chapterIds.has(quest.chapterId)) {
      errors.push(`missing chapter id: ${quest.chapterId}`);
    }
    validateConditions(quest.unlockConditions || [], questIds, chapterIds, errors);
    validateConditions(quest.completionConditions || [], questIds, chapterIds, errors);
    validateReward(quest.reward, errors);
  });

  chapters.forEach((chapter) => {
    (chapter.entryQuestIds || []).forEach((questId) => {
      if (!questIds.has(questId)) errors.push(`missing entry quest id: ${questId}`);
    });
    if (chapter.completionQuestId && !questIds.has(chapter.completionQuestId)) {
      errors.push(`missing completion quest id: ${chapter.completionQuestId}`);
    }
  });

  errors.push(...findQuestCycles(quests));
  return errors;
}

function collectDuplicateAwareIds(items, label, errors) {
  const ids = new Set();
  items.forEach((item) => {
    if (!item.id) {
      errors.push(`missing ${label}`);
      return;
    }
    if (ids.has(item.id)) errors.push(`duplicate ${label}: ${item.id}`);
    ids.add(item.id);
  });
  return ids;
}

function validateConditions(conditions, questIds, chapterIds, errors) {
  conditions.forEach((condition) => {
    if (!CONDITION_TYPES.includes(condition.type)) {
      errors.push(`invalid condition type: ${condition.type}`);
      return;
    }
    if ('target' in condition && !isPositiveFinite(condition.target)) {
      errors.push(`invalid condition target: ${condition.type}`);
    }
    if (condition.type === 'producerCount' && !PRODUCER_MAP[condition.producerId]) {
      errors.push(`missing producer id: ${condition.producerId}`);
    }
    if (condition.type === 'resourceEarned' && !RESOURCE_SET.has(condition.resource)) {
      errors.push(`missing resource id: ${condition.resource}`);
    }
    if (condition.type === 'questClaimed' && !questIds.has(condition.questId)) {
      errors.push(`missing quest id: ${condition.questId}`);
    }
    if (condition.type === 'chapterCompleted' && !chapterIds.has(condition.chapterId)) {
      errors.push(`missing chapter id: ${condition.chapterId}`);
    }
  });
}

function validateReward(reward, errors) {
  if (!reward || typeof reward !== 'object') {
    errors.push('invalid reward');
    return;
  }
  Object.entries(reward).forEach(([resource, amount]) => {
    if (!RESOURCE_SET.has(resource)) errors.push(`missing reward resource id: ${resource}`);
    if (!isPositiveFinite(amount)) errors.push(`invalid reward amount: ${resource}`);
  });
}

function findQuestCycles(quests) {
  const graph = Object.fromEntries(quests.map((quest) => [quest.id, (quest.unlockConditions || [])
    .filter((condition) => condition.type === 'questClaimed')
    .map((condition) => condition.questId)]));
  const visiting = new Set();
  const visited = new Set();
  const errors = [];

  function visit(id, path = []) {
    if (visiting.has(id)) {
      errors.push(`circular quest prerequisite: ${[...path, id].join(' -> ')}`);
      return;
    }
    if (visited.has(id) || !graph[id]) return;
    visiting.add(id);
    graph[id].forEach((next) => visit(next, [...path, id]));
    visiting.delete(id);
    visited.add(id);
  }

  Object.keys(graph).forEach((id) => visit(id));
  return errors;
}

function isPositiveFinite(value) {
  const number = Number(value);
  return Number.isFinite(number) && number > 0;
}
