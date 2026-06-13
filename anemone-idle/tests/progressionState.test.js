import { describe, expect, it } from 'vitest';
import { buyProducerInState, claimQuestInState, createInitialState } from '../src/gameState.js';

function buyProducerTimes(state, producerId, count) {
  let next = state;
  next.economy.resources.plankton = 10000;
  for (let index = 0; index < count; index += 1) {
    next = buyProducerInState(next, producerId);
  }
  return next;
}

describe('progression state', () => {
  it('quest lifecycle advances from active to completed to claimed', () => {
    const initial = createInitialState(0);
    const completed = buyProducerTimes(initial, 'driftPolyps', 5);
    const claimed = claimQuestInState(completed, 'quest-first-interns');

    expect(initial.progression.quests['quest-first-interns']).toBe('active');
    expect(completed.progression.quests['quest-first-interns']).toBe('completed');
    expect(claimed.claimed).toBe(true);
    expect(claimed.progression.quests['quest-first-interns']).toBe('claimed');
    expect(claimed.progression.quests['quest-first-shrimp']).toBe('active');
  });

  it('blocks duplicate rewards', () => {
    const completed = buyProducerTimes(createInitialState(0), 'driftPolyps', 5);
    const claimed = claimQuestInState(completed, 'quest-first-interns');
    const duplicate = claimQuestInState(claimed, 'quest-first-interns');

    expect(duplicate.claimed).toBe(false);
    expect(duplicate.economy.resources.plankton).toBe(claimed.economy.resources.plankton);
  });
});
