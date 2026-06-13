package com.yacoo.rpg.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yacoo.rpg.game.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

private val Context.dataStore by preferencesDataStore(name = "yacoo_save")
private val SAVE_KEY = stringPreferencesKey("yacoo-rpg-save-v2")

/**
 * Serializes / deserializes MetaSave to/from DataStore using kotlinx.serialization.
 * Handles v1→v2 migration by inspecting the JSON before decoding.
 */
class MetaRepository(private val context: Context) {

    val metaFlow: Flow<MetaSave> = context.dataStore.data.map { prefs ->
        val raw = prefs[SAVE_KEY]
        if (raw == null) createDefaultMeta() else parseMeta(raw)
    }

    suspend fun save(meta: MetaSave) {
        context.dataStore.edit { prefs ->
            prefs[SAVE_KEY] = encodeMeta(meta)
        }
    }

    suspend fun reset() {
        context.dataStore.edit { prefs -> prefs.remove(SAVE_KEY) }
    }

    // ── Serialization helpers ─────────────────────────────────────────

    private fun encodeMeta(meta: MetaSave): String {
        val json = buildJsonObject {
            put("version", meta.version)
            put("coins", meta.coins)
            put("bestChapter", meta.bestChapter)
            put("totalRuns", meta.totalRuns)
            put("equipment", encodeEquipmentSet(meta.equipment))
            meta.runInProgress?.let    { put("runInProgress", encodeRunState(it)) }
            meta.lastCombatResult?.let { put("lastCombatResult", encodeCombatResult(it)) }
        }
        return json.toString()
    }

    private fun parseMeta(raw: String): MetaSave {
        return try {
            val obj = Json.parseToJsonElement(raw).jsonObject
            val version = obj["version"]?.jsonPrimitive?.intOrNull ?: 1

            if (version < 2) {
                // v1 migration: use stage field to derive bestChapter
                val stage  = obj["stage"]?.jsonPrimitive?.intOrNull ?: 1
                val coins  = obj["coins"]?.jsonPrimitive?.intOrNull ?: 0
                val eq     = obj["equipment"]?.jsonObject?.let { decodeEquipmentSet(it) }
                             ?: cloneEquipment()
                createDefaultMeta().copy(
                    coins       = coins,
                    equipment   = eq,
                    bestChapter = migrateV1Chapter(stage)
                )
            } else {
                val coins       = obj["coins"]?.jsonPrimitive?.intOrNull ?: 0
                val bestChapter = obj["bestChapter"]?.jsonPrimitive?.intOrNull ?: 1
                val totalRuns   = obj["totalRuns"]?.jsonPrimitive?.intOrNull ?: 0
                val equipment   = obj["equipment"]?.jsonObject?.let { decodeEquipmentSet(it) }
                                  ?: cloneEquipment()
                val run         = obj["runInProgress"]?.jsonObject?.let { decodeRunState(it) }
                val result      = obj["lastCombatResult"]?.jsonObject?.let { decodeCombatResult(it) }
                MetaSave(version = 2, coins = coins, equipment = equipment,
                         bestChapter = bestChapter, totalRuns = totalRuns,
                         runInProgress = run, lastCombatResult = result)
            }
        } catch (_: Exception) {
            createDefaultMeta()
        }
    }

    // ── EquipmentSet ──────────────────────────────────────────────────

    private fun encodeEquipmentSet(eq: EquipmentSet): JsonObject = buildJsonObject {
        put("weapon", encodeItem(eq.weapon))
        put("armor",  encodeItem(eq.armor))
        put("charm",  encodeItem(eq.charm))
        put("boots",  encodeItem(eq.boots))
    }

    private fun encodeItem(item: EquipmentItem): JsonObject = buildJsonObject {
        put("id",    item.id)
        put("slot",  item.slot.name.lowercase())
        put("name",  item.name)
        put("level", item.level)
        item.specialtyHand?.let { put("specialtyHand", it.name) }
    }

    private fun decodeEquipmentSet(obj: JsonObject): EquipmentSet = EquipmentSet(
        weapon = decodeItem(obj["weapon"]!!.jsonObject, EquipmentSlot.WEAPON),
        armor  = decodeItem(obj["armor"]!!.jsonObject,  EquipmentSlot.ARMOR),
        charm  = decodeItem(obj["charm"]!!.jsonObject,  EquipmentSlot.CHARM),
        boots  = decodeItem(obj["boots"]!!.jsonObject,  EquipmentSlot.BOOTS)
    )

    private fun decodeItem(obj: JsonObject, slot: EquipmentSlot): EquipmentItem = EquipmentItem(
        id    = obj["id"]?.jsonPrimitive?.content ?: "",
        slot  = slot,
        name  = obj["name"]?.jsonPrimitive?.content ?: slot.name,
        level = obj["level"]?.jsonPrimitive?.intOrNull ?: 1
    )

    // ── RunState ──────────────────────────────────────────────────────

    private fun encodeRunState(run: RunState): JsonObject = buildJsonObject {
        put("seed",       run.seed)
        put("chapter",    run.chapter)
        put("nodeIndex",  run.nodeIndex)
        put("hp",         run.hp)
        put("maxHp",      run.maxHp)
        put("diceCount",  run.diceCount)
        put("maxRolls",   run.maxRolls)
        put("scrap",      run.scrap)
        put("map", buildJsonObject {
            put("chapter", run.map.chapter)
            put("nodes", buildJsonArray {
                run.map.nodes.forEach { n ->
                    add(buildJsonObject {
                        put("id",      n.id)
                        put("type",    n.type.name)
                        put("cleared", n.cleared)
                    })
                }
            })
        })
    }

    private fun decodeRunState(obj: JsonObject): RunState {
        val mapObj  = obj["map"]!!.jsonObject
        val nodes   = mapObj["nodes"]!!.jsonArray.map { nodeEl ->
            val n = nodeEl.jsonObject
            MapNode(
                id      = n["id"]!!.jsonPrimitive.content,
                type    = NodeType.valueOf(n["type"]!!.jsonPrimitive.content),
                cleared = n["cleared"]!!.jsonPrimitive.boolean
            )
        }
        return RunState(
            seed       = obj["seed"]!!.jsonPrimitive.long,
            chapter    = obj["chapter"]!!.jsonPrimitive.int,
            nodeIndex  = obj["nodeIndex"]!!.jsonPrimitive.int,
            hp         = obj["hp"]!!.jsonPrimitive.int,
            maxHp      = obj["maxHp"]!!.jsonPrimitive.int,
            diceCount  = obj["diceCount"]!!.jsonPrimitive.int,
            maxRolls   = obj["maxRolls"]!!.jsonPrimitive.int,
            scrap      = obj["scrap"]?.jsonPrimitive?.intOrNull ?: 0,
            map        = ChapterMap(chapter = mapObj["chapter"]!!.jsonPrimitive.int, nodes = nodes)
        )
    }

    // ── CombatResult ─────────────────────────────────────────────────

    private fun encodeCombatResult(r: CombatResult): JsonObject = buildJsonObject {
        put("outcome",     r.outcome.name)
        put("stage",       r.stage)
        put("coinsEarned", r.coinsEarned)
        r.handUsed?.let           { put("handUsed", it.name) }
        r.duplicateItemName?.let  { put("duplicateItemName", it) }
    }

    private fun decodeCombatResult(obj: JsonObject): CombatResult = CombatResult(
        outcome           = CombatOutcome.valueOf(obj["outcome"]!!.jsonPrimitive.content),
        stage             = obj["stage"]!!.jsonPrimitive.int,
        coinsEarned       = obj["coinsEarned"]!!.jsonPrimitive.int,
        handUsed          = obj["handUsed"]?.jsonPrimitive?.content?.let { YahtzeeAttackCategory.valueOf(it) },
        duplicateItemName = obj["duplicateItemName"]?.jsonPrimitive?.contentOrNull
    )
}
