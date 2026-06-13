package com.yacoo.rpg.game

enum class NodeType { BATTLE, ELITE, TREASURE, REST, BOSS }

data class MapNode(
    val id: String,
    val type: NodeType,
    val cleared: Boolean
)

data class ChapterMap(
    val chapter: Int,
    val nodes: List<MapNode>
)

enum class RewardKind { HEAL, SCRAP, DICE, REROLL }

data class RewardChoice(
    val kind: RewardKind,
    val amount: Int
)

data class RunState(
    val seed: Long,
    val chapter: Int,
    val map: ChapterMap,
    val nodeIndex: Int,
    val hp: Int,
    val maxHp: Int,
    val diceCount: Int,
    val maxRolls: Int,
    val scrap: Int,
    val pendingReward: List<RewardChoice>? = null
)
