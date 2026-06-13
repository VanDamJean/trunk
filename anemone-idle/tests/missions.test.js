import { describe, expect, it } from 'vitest';
import { buyProducer, createInitialEconomy } from '../src/economy.js';
import { claimMission, createMissionState, getVisibleMissions } from '../src/missions.js';

function buildPolyps(count) {
  let economy = createInitialEconomy();
  economy.resources.plankton = 1000;
  for (let index = 0; index < count; index += 1) {
    economy = buyProducer(economy, 'driftPolyps').state;
  }
  return economy;
}

describe('missions', () => {
  it('tracks producer-count progress and blocks early claims', () => {
    const economy = buildPolyps(3);
    const missions = createMissionState();
    const visible = getVisibleMissions(economy, missions);
    const result = claimMission(economy, missions, 'first-bloom');

    expect(visible[0].progress).toBe(4);
    expect(visible[0].complete).toBe(false);
    expect(result.claimed).toBe(false);
  });

  it('claims complete missions once and pays rewards', () => {
    const economy = buildPolyps(5);
    const missions = createMissionState();
    const result = claimMission(economy, missions, 'first-bloom');
    const duplicate = claimMission(result.economy, result.missionState, 'first-bloom');

    expect(result.claimed).toBe(true);
    expect(result.economy.resources.plankton).toBeGreaterThan(economy.resources.plankton);
    expect(result.missionState.claimed).toContain('first-bloom');
    expect(duplicate.claimed).toBe(false);
  });
});
