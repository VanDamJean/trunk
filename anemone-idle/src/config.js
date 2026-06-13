export const RESOURCE_KEYS = ['plankton', 'pearls', 'tideEnergy'];

export const RESOURCE_LABELS = {
  plankton: 'Plankton',
  pearls: 'Pearls',
  tideEnergy: 'Tide Energy'
};

export const RESOURCE_ICONS = {
  plankton: '🫧',
  pearls: '⚪',
  tideEnergy: '🌊'
};

export const MAX_OFFLINE_SECONDS = 60 * 60 * 4;
export const SAVE_KEY = 'anemone-idle-save-v1';
export const BALANCE_COST_GROWTH = 1.15;
export const BALANCE_UPGRADE_COST_GROWTH = 1.65;

export const REWARDED_AD_BUFF = {
  id: 'executiveSoupOverdrive',
  multiplier: 3,
  durationMs: 30 * 1000,
  cooldownMs: 5 * 60 * 1000
};

export const PRODUCERS = [
  {
    id: 'driftPolyps',
    name: 'Drift Polyps',
    icon: '🪸',
    flavor: 'Tiny tentacles comb food from the passing blue.',
    costResource: 'plankton',
    baseCost: 12,
    costGrowth: BALANCE_COST_GROWTH,
    producesResource: 'plankton',
    baseRate: 0.22
  },
  {
    id: 'cleanerShrimp',
    name: 'Cleaner Shrimp',
    icon: '🦐',
    flavor: 'Helpful reef keepers herd morsels toward the colony.',
    costResource: 'plankton',
    baseCost: 95,
    costGrowth: BALANCE_COST_GROWTH,
    producesResource: 'plankton',
    producesProducer: 'driftPolyps',
    baseRate: 0.035
  },
  {
    id: 'crabBranchBoss',
    name: 'Crab Branch Boss',
    icon: '🦀',
    flavor: 'A clawed regional director stamps shrimp into existence.',
    costResource: 'plankton',
    baseCost: 1350,
    costGrowth: BALANCE_COST_GROWTH,
    producesProducer: 'cleanerShrimp',
    baseRate: 0.015
  },
  {
    id: 'whaleShareholder',
    name: 'Whale Shareholder',
    icon: '🐋',
    flavor: 'One boardroom splash hires a crab empire by accident.',
    costResource: 'pearls',
    baseCost: 24000,
    costGrowth: BALANCE_COST_GROWTH,
    producesProducer: 'crabBranchBoss',
    baseRate: 0.002
  },
  {
    id: 'shellNursery',
    name: 'Shell Nursery',
    icon: '🐚',
    flavor: 'Patient shells polish grit into small moon-bright pearls.',
    costResource: 'plankton',
    baseCost: 720,
    costGrowth: BALANCE_COST_GROWTH,
    producesResource: 'pearls',
    baseRate: 0.08
  },
  {
    id: 'moonCurrent',
    name: 'Moon Current',
    icon: '🌙',
    flavor: 'A looping tide ribbon stores power for deeper blooms.',
    costResource: 'pearls',
    baseCost: 28,
    costGrowth: BALANCE_COST_GROWTH,
    producesResource: 'tideEnergy',
    baseRate: 0.035
  }
];

export const PRODUCER_MAP = Object.fromEntries(PRODUCERS.map((producer) => [producer.id, producer]));

export const UPGRADE_DEFINITIONS = [
  {
    id: 'silkTentacles',
    name: 'Silk Tentacles',
    description: 'All producers breathe 25% faster.',
    baseCost: { pearls: 18 },
    multiplier: 1.25
  },
  {
    id: 'prismReef',
    name: 'Prism Reef',
    description: 'All producers breathe 35% faster.',
    baseCost: { pearls: 90, tideEnergy: 8 },
    multiplier: 1.35
  },
  {
    id: 'deepBloom',
    name: 'Deep Bloom',
    description: 'Capsules ripen 35% sooner.',
    baseCost: { tideEnergy: 35 },
    capsuleSpeed: 0.65
  }
];

export const UPGRADE_MAP = Object.fromEntries(UPGRADE_DEFINITIONS.map((upgrade) => [upgrade.id, upgrade]));

export const MISSION_DEFINITIONS = [
  {
    id: 'first-bloom',
    title: 'Wake the colony',
    description: 'Own 5 Drift Polyps.',
    goal: { type: 'producerCount', producerId: 'driftPolyps', target: 5 },
    reward: { plankton: 65 }
  },
  {
    id: 'shrimp-shift',
    title: 'Recruit reef helpers',
    description: 'Own 3 Cleaner Shrimp.',
    goal: { type: 'producerCount', producerId: 'cleanerShrimp', target: 3 },
    reward: { pearls: 8 }
  },
  {
    id: 'pearl-cache',
    title: 'Polish a cache',
    description: 'Collect 25 lifetime pearls.',
    goal: { type: 'resourceEarned', resource: 'pearls', target: 25 },
    reward: { tideEnergy: 5 }
  },
  {
    id: 'branch-boss',
    title: 'Open a crab branch',
    description: 'Own 1 Crab Branch Boss.',
    goal: { type: 'producerCount', producerId: 'crabBranchBoss', target: 1 },
    reward: { pearls: 18 }
  },
  {
    id: 'tidal-gift',
    title: 'Open a reef capsule',
    description: 'Claim 1 capsule reward.',
    goal: { type: 'stat', stat: 'capsulesOpened', target: 1 },
    reward: { pearls: 22 }
  },
  {
    id: 'whale-boardroom',
    title: 'Flood the boardroom',
    description: 'Own 1 Whale Shareholder.',
    goal: { type: 'producerCount', producerId: 'whaleShareholder', target: 1 },
    reward: { plankton: 9000, tideEnergy: 30 }
  },
  {
    id: 'prismatic-growth',
    title: 'Evolve the reef',
    description: 'Buy 2 pearl upgrades.',
    goal: { type: 'stat', stat: 'upgradesPurchased', target: 2 },
    reward: { plankton: 1500, tideEnergy: 18 }
  }
];

export const CAPSULE_BASE_DURATION_MS = 5 * 60 * 1000;
