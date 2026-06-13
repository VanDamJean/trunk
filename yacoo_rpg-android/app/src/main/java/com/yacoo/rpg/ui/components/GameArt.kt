package com.yacoo.rpg.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yacoo.rpg.R
import com.yacoo.rpg.game.EquipmentSet
import com.yacoo.rpg.game.EquipmentSlot
import com.yacoo.rpg.game.NodeType
import com.yacoo.rpg.ui.theme.*

/**
 * Premium Game Art rendering.
 * Replaced complex canvas geometry with high quality AI-generated comic PNGs.
 */

// ── HeroCanvas (Loads hero_body PNG) ──────────────────────────────────

@Composable
fun HeroCanvas(
    modifier: Modifier = Modifier,
    alphaMul: Float = 1f
) {
    Image(
        painter = painterResource(id = R.drawable.hero_body),
        contentDescription = "Yacoo Chibi Bear Hero",
        alpha = alphaMul,
        modifier = modifier
    )
}

// ── ForestMonster Beholder Canvas (Loads monster_beholder PNG) ────────

@Composable
fun MonsterCanvas(
    nodeType: NodeType? = null,
    modifier: Modifier = Modifier,
    alphaMul: Float = 1f
) {
    val drawableId = when (nodeType) {
        NodeType.BOSS -> R.drawable.monster_beholder
        NodeType.ELITE -> R.drawable.monster_elite
        NodeType.BATTLE -> R.drawable.monster_slime
        else -> R.drawable.monster_slime
    }
    
    Image(
        painter = painterResource(id = drawableId),
        contentDescription = "Monster",
        alpha = alphaMul,
        modifier = modifier
    )
}

// ── LayeredArena Canvas background (Preserved cute vector landscape) ─

@Composable
fun ArenaBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sx = w / 360f
        val sy = h / 210f

        fun px(x: Float) = x * sx
        fun py(y: Float) = y * sy

        // Sky
        drawRect(Brush.verticalGradient(listOf(ArtSky, Color(0xFFCDF0FF))))
        // Sun
        drawCircle(ArtSun, radius = px(28f), center = Offset(px(294f), py(45f)))
        // Back hill
        val hillBack = Path().apply {
            moveTo(px(0f), py(115f))
            cubicTo(px(43f),py(75f), px(88f),py(86f), px(123f),py(112f))
            cubicTo(px(165f),py(61f), px(228f),py(68f), px(262f),py(112f))
            cubicTo(px(296f),py(89f), px(329f),py(92f), px(360f),py(121f))
            lineTo(px(360f),py(210f)); lineTo(px(0f),py(210f)); close()
        }
        drawPath(hillBack, ArtHillBack)
        // Front hill
        val hillFront = Path().apply {
            moveTo(px(0f), py(150f))
            cubicTo(px(35f),py(119f), px(78f),py(127f), px(112f),py(150f))
            cubicTo(px(154f),py(111f), px(210f),py(120f), px(242f),py(150f))
            cubicTo(px(284f),py(125f), px(324f),py(130f), px(360f),py(154f))
            lineTo(px(360f),py(210f)); lineTo(px(0f),py(210f)); close()
        }
        drawPath(hillFront, ArtHillFront)
        // Ground
        val ground = Path().apply {
            moveTo(px(0f), py(160f))
            cubicTo(px(72f),py(149f), px(125f),py(168f), px(180f),py(160f))
            cubicTo(px(238f),py(150f), px(288f),py(156f), px(360f),py(164f))
            lineTo(px(360f),py(210f)); lineTo(px(0f),py(210f)); close()
        }
        drawPath(ground, ArtGround)
        // Path
        val road = Path().apply {
            moveTo(px(139f),py(210f))
            cubicTo(px(150f),py(182f), px(169f),py(164f), px(187f),py(161f))
            cubicTo(px(210f),py(174f), px(226f),py(191f), px(238f),py(210f))
            close()
        }
        drawPath(road, ArtPath)
    }
}

// ── HeroPaperdollCanvas (Overlay layered PNG equipment layout) ───────

@Composable
fun HeroPaperdollCanvas(
    equipment: EquipmentSet,
    highlightSlot: EquipmentSlot? = null,
    size: Dp = 120.dp,
    modifier: Modifier = Modifier
) {
    val weaponAlpha = if (highlightSlot != null && highlightSlot != EquipmentSlot.WEAPON) 0.4f else 1f
    val armorAlpha  = if (highlightSlot != null && highlightSlot != EquipmentSlot.ARMOR)  0.4f else 1f
    val charmAlpha  = if (highlightSlot != null && highlightSlot != EquipmentSlot.CHARM)  0.4f else 1f
    val bootsAlpha  = if (highlightSlot != null && highlightSlot != EquipmentSlot.BOOTS)  0.4f else 1f

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Base Hero body
        Image(
            painter = painterResource(id = R.drawable.hero_body),
            contentDescription = "Hero Base Body",
            modifier = Modifier.fillMaxSize(0.85f)
        )

        // Layer 2: Boots overlay (bottom area)
        if (equipment.boots.level > 0) {
            Image(
                painter = painterResource(id = R.drawable.icon_boots),
                contentDescription = "Boots Overlay",
                alpha = bootsAlpha,
                modifier = Modifier
                    .fillMaxSize(0.35f)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-4).dp)
            )
        }

        // Layer 3: Armor overlay (center body area)
        if (equipment.armor.level > 0) {
            Image(
                painter = painterResource(id = R.drawable.icon_armor),
                contentDescription = "Armor Overlay",
                alpha = armorAlpha,
                modifier = Modifier
                    .fillMaxSize(0.48f)
                    .align(Alignment.Center)
                    .offset(y = 12.dp)
            )
        }

        // Layer 4: Weapon overlay (held on the right side)
        if (equipment.weapon.level > 0) {
            Image(
                painter = painterResource(id = R.drawable.icon_weapon),
                contentDescription = "Weapon Overlay",
                alpha = weaponAlpha,
                modifier = Modifier
                    .fillMaxSize(0.45f)
                    .align(Alignment.CenterEnd)
                    .offset(x = 4.dp, y = (-4).dp)
            )
        }

        // Layer 5: Charm pendant overlay (neck/chest area)
        if (equipment.charm.level > 0) {
            Image(
                painter = painterResource(id = R.drawable.icon_charm),
                contentDescription = "Charm Overlay",
                alpha = charmAlpha,
                modifier = Modifier
                    .fillMaxSize(0.26f)
                    .align(Alignment.TopCenter)
                    .offset(y = 22.dp)
            )
        }
    }
}
