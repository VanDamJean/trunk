export const CONDITION_TYPES = ['producerCount', 'resourceEarned', 'stat', 'upgradeCount', 'questClaimed', 'chapterCompleted'];
export const REWARD_TYPES = ['resources'];

export const CHAPTER_DEFINITIONS = [
  {
    id: 'chapter1-office-reef',
    titleKey: 'chapter1Title',
    descriptionKey: 'chapter1Description',
    entryQuestIds: ['quest-first-interns'],
    completionQuestId: 'quest-chapter1-complete'
  }
];

export const QUEST_DEFINITIONS = [
  {
    id: 'quest-first-interns',
    chapterId: 'chapter1-office-reef',
    titleKey: 'questFirstInternsTitle',
    descriptionKey: 'questFirstInternsDescription',
    unlockConditions: [],
    completionConditions: [{ type: 'producerCount', producerId: 'driftPolyps', target: 5 }],
    reward: { plankton: 65 },
    nextUnlockKey: 'nextUnlockFirstShrimp'
  },
  {
    id: 'quest-first-shrimp',
    chapterId: 'chapter1-office-reef',
    titleKey: 'questFirstShrimpTitle',
    descriptionKey: 'questFirstShrimpDescription',
    unlockConditions: [{ type: 'questClaimed', questId: 'quest-first-interns' }],
    completionConditions: [{ type: 'producerCount', producerId: 'cleanerShrimp', target: 3 }],
    reward: { pearls: 8 },
    nextUnlockKey: 'nextUnlockFirstCapsule'
  },
  {
    id: 'quest-first-capsule',
    chapterId: 'chapter1-office-reef',
    titleKey: 'questFirstCapsuleTitle',
    descriptionKey: 'questFirstCapsuleDescription',
    unlockConditions: [{ type: 'questClaimed', questId: 'quest-first-interns' }],
    completionConditions: [{ type: 'stat', stat: 'capsulesOpened', target: 1 }],
    reward: { pearls: 22 },
    nextUnlockKey: 'nextUnlockFirstUpgrade'
  },
  {
    id: 'quest-first-upgrade',
    chapterId: 'chapter1-office-reef',
    titleKey: 'questFirstUpgradeTitle',
    descriptionKey: 'questFirstUpgradeDescription',
    unlockConditions: [{ type: 'questClaimed', questId: 'quest-first-shrimp' }],
    completionConditions: [{ type: 'upgradeCount', target: 1 }],
    reward: { tideEnergy: 5 },
    nextUnlockKey: 'nextUnlockFirstCrab'
  },
  {
    id: 'quest-first-crab',
    chapterId: 'chapter1-office-reef',
    titleKey: 'questFirstCrabTitle',
    descriptionKey: 'questFirstCrabDescription',
    unlockConditions: [{ type: 'questClaimed', questId: 'quest-first-shrimp' }],
    completionConditions: [{ type: 'producerCount', producerId: 'crabBranchBoss', target: 1 }],
    reward: { pearls: 18 },
    nextUnlockKey: 'nextUnlockChapterComplete'
  },
  {
    id: 'quest-chapter1-complete',
    chapterId: 'chapter1-office-reef',
    titleKey: 'questChapter1CompleteTitle',
    descriptionKey: 'questChapter1CompleteDescription',
    unlockConditions: [
      { type: 'questClaimed', questId: 'quest-first-capsule' },
      { type: 'questClaimed', questId: 'quest-first-upgrade' },
      { type: 'questClaimed', questId: 'quest-first-crab' }
    ],
    completionConditions: [
      { type: 'producerCount', producerId: 'crabBranchBoss', target: 1 },
      { type: 'upgradeCount', target: 2 },
      { type: 'stat', stat: 'capsulesOpened', target: 1 }
    ],
    reward: { plankton: 1500, tideEnergy: 18 },
    nextUnlockKey: 'nextUnlockComingSoon'
  }
];

export const QUEST_MAP = Object.fromEntries(QUEST_DEFINITIONS.map((quest) => [quest.id, quest]));
export const CHAPTER_MAP = Object.fromEntries(CHAPTER_DEFINITIONS.map((chapter) => [chapter.id, chapter]));
