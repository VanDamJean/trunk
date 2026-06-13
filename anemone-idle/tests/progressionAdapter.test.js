import { describe, expect, it } from 'vitest';
import { buyProducerInState, claimQuestInState, createInitialState } from '../src/gameState.js';
import { getVisibleProgressionQuests, LEGACY_MISSION_TO_QUEST } from '../src/progression/legacyMissionAdapter.js';

describe('progression adapter', () => {
  it('returns visible progression quests and hides claimed quests', () => {
    let state = createInitialState(0);
    state.economy.resources.plankton = 10000;
    for (let index = 0; index < 5; index += 1) state = buyProducerInState(state, 'driftPolyps');
    const claimed = claimQuestInState(state, 'quest-first-interns');
    const visible = getVisibleProgressionQuests(claimed, 3);

    expect(visible.length).toBeLessThanOrEqual(3);
    expect(visible.map((quest) => quest.id)).not.toContain('quest-first-interns');
    expect(visible.map((quest) => quest.id)).toContain('quest-first-shrimp');
  });

  it('maps legacy mission ids explicitly', () => {
    expect(Object.keys(LEGACY_MISSION_TO_QUEST)).toEqual(expect.arrayContaining([
      'first-bloom', 'shrimp-shift', 'pearl-cache', 'branch-boss', 'tidal-gift', 'whale-boardroom', 'prismatic-growth'
    ]));
  });
});
