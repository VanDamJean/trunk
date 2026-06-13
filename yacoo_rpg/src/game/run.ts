import { COMBAT_TIMING, HERO_BASE } from './constants';
import { getHeroStats } from './equipment';
import type { ChapterMap, MapNode, MetaSave, NodeType, Rng, RunState } from './types';

const CHAPTER_NODE_COUNT = 6;
const NODE_SEQUENCE: NodeType[] = ['battle', 'battle', 'rest', 'elite', 'battle', 'boss'];

export function createChapterMap(chapter: number, rng: Rng = Math.random): ChapterMap {
  const nodes: MapNode[] = NODE_SEQUENCE.map((type, i) => ({
    id: `${chapter}-${i}`,
    type,
    cleared: false
  }));
  // Shuffle only the middle nodes (keep first=battle, last=boss fixed)
  for (let i = CHAPTER_NODE_COUNT - 2; i > 1; i--) {
    const j = 1 + Math.floor(rng() * i);
    if (j < CHAPTER_NODE_COUNT - 1) {
      [nodes[i], nodes[j]] = [nodes[j], nodes[i]];
    }
  }
  // Always enforce last node as boss
  nodes[CHAPTER_NODE_COUNT - 1] = { id: `${chapter}-boss`, type: 'boss', cleared: false };
  return { chapter, nodes };
}

export function createRun(meta: MetaSave, rng: Rng = Math.random): RunState {
  const seed = Math.floor(rng() * 2 ** 32);
  const heroStats = getHeroStats(meta.equipment);
  return {
    seed,
    chapter: 1,
    map: createChapterMap(1, rng),
    nodeIndex: 0,
    hp: heroStats.maxHp,
    maxHp: heroStats.maxHp,
    diceCount: COMBAT_TIMING.diceCount,
    maxRolls: COMBAT_TIMING.maxRolls,
    scrap: 0
  };
}

export function advanceNode(run: RunState): RunState {
  const clearedMap: ChapterMap = {
    ...run.map,
    nodes: run.map.nodes.map((n, i) =>
      i === run.nodeIndex ? { ...n, cleared: true } : n
    )
  };
  return { ...run, map: clearedMap, nodeIndex: run.nodeIndex + 1 };
}

export function applyHealToRun(run: RunState, amount: number): RunState {
  return { ...run, hp: Math.min(run.maxHp, run.hp + amount) };
}

export function isRunOver(run: RunState): boolean {
  return run.hp <= 0;
}

export function isChapterCleared(run: RunState): boolean {
  return run.nodeIndex >= run.map.nodes.length;
}

export function advanceChapter(run: RunState, rng: Rng = Math.random): RunState {
  const nextChapter = run.chapter + 1;
  return {
    ...run,
    chapter: nextChapter,
    map: createChapterMap(nextChapter, rng),
    nodeIndex: 0
  };
}

export function currentNode(run: RunState) {
  return run.map.nodes[run.nodeIndex] ?? null;
}

export function settleRun(meta: MetaSave, run: RunState, coinsEarned: number): MetaSave {
  return {
    ...meta,
    coins: meta.coins + coinsEarned,
    bestChapter: Math.max(meta.bestChapter, run.chapter),
    totalRuns: meta.totalRuns + 1,
    runInProgress: undefined,
    lastCombatResult: undefined
  };
}

export { HERO_BASE };
