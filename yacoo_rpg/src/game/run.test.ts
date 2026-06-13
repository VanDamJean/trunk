import { describe, expect, it } from 'vitest';
import { createDefaultMeta } from './equipment';
import {
  advanceChapter,
  advanceNode,
  createChapterMap,
  createRun,
  currentNode,
  isChapterCleared,
  isRunOver,
  settleRun
} from './run';

describe('createChapterMap', () => {
  it('creates a map with the correct node count', () => {
    const map = createChapterMap(1, () => 0.5);
    expect(map.nodes).toHaveLength(6);
  });

  it('always starts with battle and ends with boss', () => {
    const map = createChapterMap(1, Math.random);
    expect(map.nodes[0].type).toBe('battle');
    expect(map.nodes[5].type).toBe('boss');
  });

  it('is deterministic given the same rng sequence', () => {
    let seed = 0;
    const seededRng = () => ((seed = (seed * 16807 + 0) % 2147483647) / 2147483647);
    const a = createChapterMap(1, seededRng);
    seed = 0;
    const seededRng2 = () => ((seed = (seed * 16807 + 0) % 2147483647) / 2147483647);
    const b = createChapterMap(1, seededRng2);
    expect(a.nodes.map((n) => n.type)).toEqual(b.nodes.map((n) => n.type));
  });

  it('starts with all nodes uncleared', () => {
    const map = createChapterMap(2, () => 0);
    expect(map.nodes.every((n) => !n.cleared)).toBe(true);
  });
});

describe('createRun', () => {
  it('creates a run with starting stats from equipment', () => {
    const meta = createDefaultMeta();
    const run = createRun(meta, () => 0.5);
    // Default equipment: maxHp = 120 + 1*15 = 135
    expect(run.hp).toBe(135);
    expect(run.maxHp).toBe(135);
    expect(run.chapter).toBe(1);
    expect(run.nodeIndex).toBe(0);
    expect(run.diceCount).toBe(5);
    expect(run.maxRolls).toBe(3);
  });
});

describe('advanceNode', () => {
  it('marks the current node as cleared and increments nodeIndex', () => {
    const meta = createDefaultMeta();
    const run = createRun(meta, () => 0.5);
    const next = advanceNode(run);
    expect(next.map.nodes[0].cleared).toBe(true);
    expect(next.nodeIndex).toBe(1);
    expect(run.nodeIndex).toBe(0); // original unchanged (immutable)
  });
});

describe('isRunOver / isChapterCleared', () => {
  it('detects run over when hp reaches 0', () => {
    const meta = createDefaultMeta();
    const run = createRun(meta, () => 0.5);
    expect(isRunOver(run)).toBe(false);
    expect(isRunOver({ ...run, hp: 0 })).toBe(true);
  });

  it('detects chapter cleared when nodeIndex exceeds map length', () => {
    const meta = createDefaultMeta();
    const run = createRun(meta, () => 0.5);
    expect(isChapterCleared(run)).toBe(false);
    expect(isChapterCleared({ ...run, nodeIndex: 6 })).toBe(true);
  });
});

describe('currentNode', () => {
  it('returns the node at nodeIndex', () => {
    const meta = createDefaultMeta();
    const run = createRun(meta, () => 0.5);
    const node = currentNode(run);
    expect(node).not.toBeNull();
    expect(node?.type).toBe('battle');
  });

  it('returns null when past the end', () => {
    const meta = createDefaultMeta();
    const run = { ...createRun(meta, () => 0.5), nodeIndex: 999 };
    expect(currentNode(run)).toBeNull();
  });
});

describe('advanceChapter', () => {
  it('increments chapter and resets nodeIndex', () => {
    const meta = createDefaultMeta();
    const run = createRun(meta, () => 0.5);
    const next = advanceChapter(run, () => 0.3);
    expect(next.chapter).toBe(2);
    expect(next.nodeIndex).toBe(0);
    expect(next.map.chapter).toBe(2);
  });
});

describe('settleRun', () => {
  it('adds coins and updates bestChapter', () => {
    const meta = createDefaultMeta();
    const run = createRun(meta, () => 0.5);
    const run5 = { ...run, chapter: 5 };
    const settled = settleRun(meta, run5, 120);
    expect(settled.coins).toBe(120);
    expect(settled.bestChapter).toBe(5);
    expect(settled.totalRuns).toBe(1);
    expect(settled.runInProgress).toBeUndefined();
  });

  it('does not downgrade bestChapter', () => {
    const meta = { ...createDefaultMeta(), bestChapter: 10 };
    const run = createRun(meta, () => 0.5);
    const settled = settleRun(meta, { ...run, chapter: 3 }, 0);
    expect(settled.bestChapter).toBe(10);
  });
});
