package com.yacoo.rpg.game

import kotlin.math.ceil
import kotlin.math.max

// ── Storage interface (injected, like web's `Storage` param) ──────────

interface MetaStorage {
    fun load(): String?
    fun save(json: String)
    fun remove()
}

// ── Validators ────────────────────────────────────────────────────────

fun isValidEquipmentItem(map: Map<*, *>?, slot: EquipmentSlot): Boolean {
    if (map == null) return false
    return map["slot"] == slot.name.lowercase() &&
           map["id"] is String &&
           map["name"] is String &&
           (map["level"] as? Number)?.toInt()?.let { it >= 1 } == true
}

// ── Migration ─────────────────────────────────────────────────────────

/** Converts old v1 stage number → bestChapter for v2 */
fun migrateV1Chapter(stage: Int): Int = max(1, ceil(stage / 6.0).toInt())
