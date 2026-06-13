package com.yacoo.rpg.game

import com.yacoo.rpg.game.Constants.CombatTiming
import kotlin.math.max
import kotlin.math.min

private const val CHAPTER_NODE_COUNT = 6
private val NODE_SEQUENCE = listOf(
    NodeType.BATTLE, NodeType.BATTLE, NodeType.REST,
    NodeType.ELITE,  NodeType.BATTLE, NodeType.BOSS
)

fun createChapterMap(chapter: Int, rng: Rng = Math::random): ChapterMap {
    val nodes = NODE_SEQUENCE.mapIndexed { i, type ->
        MapNode(id = "$chapter-$i", type = type, cleared = false)
    }.toMutableList()

    // Shuffle middle nodes (keep first=battle, last=boss fixed)
    for (i in CHAPTER_NODE_COUNT - 2 downTo 2) {
        val j = 1 + (rng() * i).toInt()
        if (j < CHAPTER_NODE_COUNT - 1) {
            val tmp = nodes[i]; nodes[i] = nodes[j]; nodes[j] = tmp
        }
    }
    // Always enforce last as boss
    nodes[CHAPTER_NODE_COUNT - 1] = MapNode("$chapter-boss", NodeType.BOSS, false)
    return ChapterMap(chapter = chapter, nodes = nodes)
}

fun createRun(meta: MetaSave, rng: Rng = Math::random): RunState {
    val seed = (rng() * (1L shl 32)).toLong()
    val heroStats = getHeroStats(meta.equipment)
    return RunState(
        seed       = seed,
        chapter    = 1,
        map        = createChapterMap(1, rng),
        nodeIndex  = 0,
        hp         = heroStats.maxHp,
        maxHp      = heroStats.maxHp,
        diceCount  = CombatTiming.DICE_COUNT,
        maxRolls   = CombatTiming.MAX_ROLLS,
        scrap      = 0
    )
}

fun advanceNode(run: RunState): RunState {
    if (run.nodeIndex >= run.map.nodes.size) return run
    val clearedMap = run.map.copy(
        nodes = run.map.nodes.mapIndexed { i, n ->
            if (i == run.nodeIndex) n.copy(cleared = true) else n
        }
    )
    return run.copy(map = clearedMap, nodeIndex = run.nodeIndex + 1)
}

fun applyHealToRun(run: RunState, amount: Int): RunState =
    run.copy(hp = min(run.maxHp, run.hp + amount))

fun isRunOver(run: RunState): Boolean = run.hp <= 0

fun isChapterCleared(run: RunState): Boolean = run.nodeIndex >= run.map.nodes.size

fun advanceChapter(run: RunState, rng: Rng = Math::random): RunState {
    val nextChapter = run.chapter + 1
    return run.copy(
        chapter   = nextChapter,
        map       = createChapterMap(nextChapter, rng),
        nodeIndex = 0
    )
}

fun currentNode(run: RunState): MapNode? = run.map.nodes.getOrNull(run.nodeIndex)

fun settleRun(meta: MetaSave, run: RunState, coinsEarned: Int): MetaSave = meta.copy(
    coins            = meta.coins + coinsEarned,
    bestChapter      = max(meta.bestChapter, run.chapter),
    totalRuns        = meta.totalRuns + 1,
    runInProgress    = null,
    lastCombatResult = null
)
