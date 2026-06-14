package com.yacoo.rpg.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.R

/**
 * Transitional icon abstraction for Yacoo RPG.
 * Loads premium PNG assets from R.drawable if available; otherwise falls back to emojis.
 */

// ── Icon Role Enum ────────────────────────────────────────────────────

enum class GameIconRole(val fallback: String) {
    // Navigation
    HOME("🏠"),
    BATTLE("⚔️"),
    GEAR("🛡"),
    UPGRADE("⬆️"),
    DRAW("🎲"),

    // Equipment slots
    WEAPON("⚔️"),
    ARMOR("🛡"),
    CHARM("💎"),
    BOOTS("👟"),

    // Resources
    GOLD("💰"),
    GEM("💎"),
    ENERGY("⚡"),
    POWER("💪"),
    SCRAP("🔩"),

    // Combat
    ATTACK("⚔️"),
    DEFEND("🛡"),
    DICE("🎲"),
    HEAL("❤️"),

    // Rewards / Progression
    TREASURE("📦"),
    REWARD("🎁"),
    BOSS("👹"),
    CHEST("🗃️"),

    // Actions
    SETTINGS("⚙️"),
    CLOSE("✕"),
    BACK("←"),
    RESET("🔄"),
    CONFIRM("✓"),

    // Status
    VICTORY("🏆"),
    DEFEAT("💀"),
    LOCK("🔒"),
    STAR("⭐"),
    ARROW_UP("⬆"),
    PLAYER_AVATAR("🐻");
}

// ── Composable Icon Renderer (PNG + Emoji Fallback) ───────────────────

@Composable
fun GameIcon(
    icon: GameIconRole,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    fontSize: Float = 18f
) {
    val drawableId = when (icon) {
        GameIconRole.WEAPON -> R.drawable.icon_weapon
        GameIconRole.ARMOR -> R.drawable.icon_armor
        GameIconRole.CHARM -> R.drawable.icon_charm
        GameIconRole.BOOTS -> R.drawable.icon_boots
        GameIconRole.PLAYER_AVATAR -> R.drawable.hero_body
        GameIconRole.BOSS -> R.drawable.monster_beholder
        GameIconRole.GOLD -> R.drawable.icon_gold_3d
        GameIconRole.GEM -> R.drawable.icon_gem_3d
        GameIconRole.ENERGY -> R.drawable.icon_energy_3d
        GameIconRole.POWER -> R.drawable.icon_power_3d
        GameIconRole.VICTORY -> R.drawable.banner_ribbon_gold
        
        GameIconRole.HOME -> R.drawable.icon_nav_home
        GameIconRole.BATTLE -> R.drawable.icon_nav_battle
        GameIconRole.GEAR -> R.drawable.icon_nav_gear
        GameIconRole.UPGRADE -> R.drawable.icon_nav_upgrade
        GameIconRole.DRAW -> R.drawable.icon_nav_draw
        GameIconRole.TREASURE -> R.drawable.chest_treasure
        GameIconRole.CHEST -> R.drawable.chest_treasure

        // Newly added icons
        GameIconRole.SCRAP -> R.drawable.icon_scrap
        GameIconRole.ATTACK -> R.drawable.icon_attack
        GameIconRole.DEFEND -> R.drawable.icon_defend
        GameIconRole.DICE -> R.drawable.icon_dice
        GameIconRole.HEAL -> R.drawable.icon_heal
        GameIconRole.REWARD -> R.drawable.icon_reward
        GameIconRole.SETTINGS -> R.drawable.icon_settings
        GameIconRole.CLOSE -> R.drawable.icon_close
        GameIconRole.BACK -> R.drawable.icon_back
        GameIconRole.RESET -> R.drawable.icon_reset
        GameIconRole.CONFIRM -> R.drawable.icon_confirm
        GameIconRole.DEFEAT -> R.drawable.icon_defeat
        GameIconRole.LOCK -> R.drawable.icon_lock
        GameIconRole.STAR -> R.drawable.icon_star
        GameIconRole.ARROW_UP -> R.drawable.icon_arrow_up
    }

    Image(
        painter = painterResource(id = drawableId),
        contentDescription = icon.name,
        modifier = modifier.size(fontSize.dp)
    )
}

// ── Convenience Aliases ───────────────────────────────────────────────

object GameIcons {
    // Navigation
    val home    = GameIconRole.HOME
    val battle  = GameIconRole.BATTLE
    val gear    = GameIconRole.GEAR
    val upgrade = GameIconRole.UPGRADE
    val draw    = GameIconRole.DRAW

    // Equipment slots
    val weapon = GameIconRole.WEAPON
    val armor  = GameIconRole.ARMOR
    val charm  = GameIconRole.CHARM
    val boots  = GameIconRole.BOOTS

    // Resources
    val gold   = GameIconRole.GOLD
    val gem    = GameIconRole.GEM
    val energy = GameIconRole.ENERGY
    val power  = GameIconRole.POWER
    val scrap  = GameIconRole.SCRAP

    // Combat
    val attack = GameIconRole.ATTACK
    val defend = GameIconRole.DEFEND
    val dice   = GameIconRole.DICE
    val heal   = GameIconRole.HEAL

    // Rewards
    val treasure = GameIconRole.TREASURE
    val reward   = GameIconRole.REWARD
    val boss     = GameIconRole.BOSS
    val chest    = GameIconRole.CHEST

    // Actions
    val settings = GameIconRole.SETTINGS
    val close    = GameIconRole.CLOSE
    val back     = GameIconRole.BACK
    val reset    = GameIconRole.RESET
    val confirm  = GameIconRole.CONFIRM

    // Status
    val victory      = GameIconRole.VICTORY
    val defeat       = GameIconRole.DEFEAT
    val lock         = GameIconRole.LOCK
    val star         = GameIconRole.STAR
    val arrowUp      = GameIconRole.ARROW_UP
    val playerAvatar = GameIconRole.PLAYER_AVATAR
}
