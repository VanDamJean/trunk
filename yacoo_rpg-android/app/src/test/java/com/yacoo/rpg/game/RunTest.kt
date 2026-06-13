package com.yacoo.rpg.game

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RunTest {

    @Test fun `createChapterMap produces six nodes with boss last`() {
        val map = createChapterMap(1) { 0.5 }
        assertEquals(6, map.nodes.size)
        assertEquals(NodeType.BATTLE, map.nodes.first().type)
        assertEquals(NodeType.BOSS, map.nodes.last().type)
    }

    @Test fun `createChapterMap keeps first and last fixed under shuffle`() {
        listOf(0.0, 0.25, 0.5, 0.75, 0.99).forEach { seed ->
            val map = createChapterMap(2) { seed }
            assertEquals(NodeType.BATTLE, map.nodes.first().type)
            assertEquals(NodeType.BOSS, map.nodes.last().type)
        }
    }

    @Test fun `advanceNode marks current cleared and increments index`() {
        val run = createRun(createDefaultMeta()) { 0.5 }
        val advanced = advanceNode(run)
        assertEquals(run.nodeIndex + 1, advanced.nodeIndex)
        assertTrue(advanced.map.nodes[run.nodeIndex].cleared)
    }

    @Test fun `isChapterCleared true when index past end`() {
        val run = createRun(createDefaultMeta()) { 0.5 }
        val past = run.copy(nodeIndex = run.map.nodes.size)
        assertTrue(isChapterCleared(past))
        assertFalse(isChapterCleared(run.copy(nodeIndex = run.map.nodes.size - 1)))
    }

    @Test fun `advanceChapter produces chapter+1 with fresh map and reset index`() {
        val run = createRun(createDefaultMeta()) { 0.5 }
        val advanced = advanceChapter(run) { 0.5 }
        assertEquals(run.chapter + 1, advanced.chapter)
        assertEquals(0, advanced.nodeIndex)
        assertEquals(6, advanced.map.nodes.size)
        assertNotSame(run.map, advanced.map)
    }

    @Test fun `applyHealToRun caps at maxHp`() {
        val run = createRun(createDefaultMeta()) { 0.5 }.copy(hp = 100, maxHp = 120)
        assertEquals(120, applyHealToRun(run, 50).hp)
    }

    @Test fun `applyHealToRun stacks below maxHp`() {
        val run = createRun(createDefaultMeta()) { 0.5 }.copy(hp = 30, maxHp = 120)
        assertEquals(60, applyHealToRun(run, 30).hp)
    }

    @Test fun `currentNode returns null past end`() {
        val run = createRun(createDefaultMeta()) { 0.5 }
        val past = run.copy(nodeIndex = run.map.nodes.size)
        assertNull(currentNode(past))
    }

    @Test fun `settleRun increments bestChapter when run is further`() {
        val meta = createDefaultMeta().copy(bestChapter = 1)
        val run  = createRun(meta) { 0.5 }.copy(chapter = 3)
        val settled = settleRun(meta, run, coinsEarned = 0)
        assertEquals(3, settled.bestChapter)
        assertEquals(1, settled.totalRuns)
        assertNull(settled.runInProgress)
        assertNull(settled.lastCombatResult)
    }
}
