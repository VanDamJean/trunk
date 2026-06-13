import { createProgressionState, refreshProgressionState } from './state.js';
import { LEGACY_MISSION_TO_QUEST } from './legacyMissionAdapter.js';

export function migrateProgressionState(rawProgression, legacyMissions, economy) {
  const progression = createProgressionState(rawProgression);
  (legacyMissions?.claimed || []).forEach((missionId) => {
    const questId = LEGACY_MISSION_TO_QUEST[missionId];
    if (questId) progression.quests[questId] = 'claimed';
  });
  return refreshProgressionState({ economy, progression });
}
