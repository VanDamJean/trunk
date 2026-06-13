export type DieValue = 1 | 2 | 3 | 4 | 5 | 6;

export type YahtzeeAttackCategory =
  | 'chance'
  | 'pair'
  | 'twoPair'
  | 'threeKind'
  | 'smallStraight'
  | 'fullHouse'
  | 'fourKind'
  | 'largeStraight'
  | 'yahtzee';

export type YahtzeeHand = YahtzeeAttackCategory;

export interface YahtzeeAttackCategoryMeta {
  category: YahtzeeAttackCategory;
  label: string;
  multiplier: number;
}

export interface YahtzeeHandResult {
  hand: YahtzeeHand;
  rank: number;
  multiplier: number;
  label: string;
}

export type EquipmentSlot = 'weapon' | 'armor' | 'charm' | 'boots';

export interface EquipmentItem {
  id: string;
  slot: EquipmentSlot;
  name: string;
  level: number;
  specialtyHand?: YahtzeeHand;
}

export type EquipmentSet = Record<EquipmentSlot, EquipmentItem>;

// Legacy save shape (v1) — kept for migration only
export interface GameSave {
  stage: number;
  coins: number;
  equipment: EquipmentSet;
  lastResult?: CombatResult;
}

// ── M1: Meta / Run split ──────────────────────────────────────────────

export type NodeType = 'battle' | 'elite' | 'treasure' | 'rest' | 'boss';

export interface MapNode {
  id: string;
  type: NodeType;
  cleared: boolean;
}

export interface ChapterMap {
  chapter: number;
  nodes: MapNode[];
}

export type RewardChoice =
  | { kind: 'heal'; amount: number }
  | { kind: 'scrap'; amount: number }
  | { kind: 'dice'; amount: number }
  | { kind: 'reroll'; amount: number };

export interface RunState {
  seed: number;
  chapter: number;
  map: ChapterMap;
  nodeIndex: number;
  hp: number;
  maxHp: number;
  diceCount: number;
  maxRolls: number;
  scrap: number;
  pendingReward?: RewardChoice[];
}

/** Persistent cross-run save (v2) */
export interface MetaSave {
  version: 2;
  coins: number;
  equipment: EquipmentSet;
  bestChapter: number;
  totalRuns: number;
  runInProgress?: RunState;
  lastCombatResult?: CombatResult;
}

// ── Shared ────────────────────────────────────────────────────────────

export interface HeroStats {
  maxHp: number;
  attack: number;
  defense: number;
  autoBonus: number;
  diceBonus: number;
  power: number;
}

export interface EnemyStats {
  stage: number;
  maxHp: number;
  attack: number;
  name: string;
}

export type CombatOutcome = 'win' | 'loss';

export interface CombatResult {
  outcome: CombatOutcome;
  stage: number;
  coinsEarned: number;
  handUsed?: YahtzeeHand;
  duplicateItemName?: string;
}

export interface RewardResult {
  coinsEarned: number;
  duplicateItemName?: string;
}

export type Rng = () => number;

export type Screen =
  | 'home'
  | 'combat'
  | 'equipment'
  | 'upgrade'
  | 'result'
  | 'runMap'
  | 'rewardPick'
  | 'runResult';
