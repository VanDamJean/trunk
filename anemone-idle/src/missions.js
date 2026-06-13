import { MISSION_DEFINITIONS } from './config.js';
import { addResources, cloneEconomy } from './economy.js';

export function createMissionState(claimed = []) {
  return {
    claimed: Array.isArray(claimed) ? [...new Set(claimed)] : []
  };
}

export function getMissionProgress(economy, mission) {
  const goal = mission.goal;
  if (goal.type === 'producerCount') {
    return economy.producers[goal.producerId] || 0;
  }
  if (goal.type === 'resourceEarned') {
    return economy.lifetimeEarned[goal.resource] || 0;
  }
  if (goal.type === 'stat') {
    return economy.stats[goal.stat] || 0;
  }
  return 0;
}

export function isMissionComplete(economy, mission) {
  return getMissionProgress(economy, mission) >= mission.goal.target;
}

export function getVisibleMissions(economy, missionState, limit = 3) {
  return MISSION_DEFINITIONS
    .filter((mission) => !missionState.claimed.includes(mission.id))
    .slice(0, limit)
    .map((mission) => ({
      ...mission,
      progress: getMissionProgress(economy, mission),
      complete: isMissionComplete(economy, mission)
    }));
}

export function claimMission(economy, missionState, missionId) {
  const mission = MISSION_DEFINITIONS.find((entry) => entry.id === missionId);
  if (!mission || missionState.claimed.includes(missionId) || !isMissionComplete(economy, mission)) {
    return { economy, missionState, claimed: false };
  }

  const rewarded = addResources(economy, mission.reward);
  rewarded.stats.missionsClaimed += 1;
  return {
    economy: rewarded,
    missionState: {
      claimed: [...missionState.claimed, missionId]
    },
    claimed: true,
    reward: mission.reward
  };
}

export function cloneMissionState(missionState) {
  return createMissionState(missionState?.claimed);
}

export function resetClaimedMissions(economy, missionState) {
  return {
    economy: cloneEconomy(economy),
    missionState: createMissionState(missionState?.claimed)
  };
}
