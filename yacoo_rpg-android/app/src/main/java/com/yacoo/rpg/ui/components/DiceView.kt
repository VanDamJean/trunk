package com.yacoo.rpg.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.yacoo.rpg.game.DieValue
import com.yacoo.rpg.ui.theme.*

// ── Pip positions (normalized 0-1 in a 3x3 grid) ────────────────────

private val PIP_LAYOUT: Map<Int, List<Pair<Int, Int>>> = mapOf(
    1 to listOf(Pair(1, 1)),
    2 to listOf(Pair(0, 0), Pair(2, 2)),
    3 to listOf(Pair(0, 0), Pair(1, 1), Pair(2, 2)),
    4 to listOf(Pair(0, 0), Pair(2, 0), Pair(0, 2), Pair(2, 2)),
    5 to listOf(Pair(0, 0), Pair(2, 0), Pair(1, 1), Pair(0, 2), Pair(2, 2)),
    6 to listOf(Pair(0, 0), Pair(2, 0), Pair(0, 1), Pair(2, 1), Pair(0, 2), Pair(2, 2))
)

@Composable
fun DiePips(value: DieValue, pipColor: Color = ColorInk, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val pips   = PIP_LAYOUT[value] ?: return@Canvas
        val margin = size.width * 0.16f
        val cellW  = (size.width  - margin * 2f) / 3f
        val cellH  = (size.height - margin * 2f) / 3f
        
        // Slightly larger pips for comic punchy feel
        val baseRadius = size.width * 0.1f

        pips.forEach { (col, row) ->
            val cx = margin + cellW * col + cellW / 2f
            val cy = margin + cellH * row + cellH / 2f
            
            // Special rule: 1 pip is larger and highlighted red (classic dice vibe)
            val finalColor = if (value == 1) Color(0xFFD54132) else pipColor
            val finalRadius = if (value == 1) baseRadius * 1.5f else baseRadius

            drawCircle(
                color = finalColor,
                radius = finalRadius,
                center = androidx.compose.ui.geometry.Offset(cx, cy)
            )
            
            // Inner mini highlights for 3D comic pip reflections
            if (value != 1) {
                drawCircle(
                    color = Color(0x66FFFFFF),
                    radius = finalRadius * 0.3f,
                    center = androidx.compose.ui.geometry.Offset(cx - finalRadius * 0.3f, cy - finalRadius * 0.3f)
                )
            }
        }
    }
}

// ── Animated 3D Cartoon Die ──────────────────────────────────────────

@Composable
fun AnimatedDie(
    value: DieValue?,
    held: Boolean,
    rolling: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnim  = remember { Animatable(1f) }
    val bounceAnim = remember { Animatable(1f) }

    val rotationState = remember { Animatable(0f) }

    LaunchedEffect(rolling) {
        if (rolling) {
            rotationState.animateTo(
                targetValue = rotationState.value + 360f * 3,
                animationSpec = tween(600, easing = LinearEasing)
            )
        } else {
            rotationState.snapTo(0f)
        }
    }

    LaunchedEffect(rolling) {
        if (rolling) {
            scaleAnim.snapTo(0.5f)
            scaleAnim.animateTo(1f, tween(180, easing = FastOutSlowInEasing))
        } else {
            bounceAnim.snapTo(1f)
            bounceAnim.animateTo(1.15f, tween(60, easing = FastOutLinearInEasing))
            bounceAnim.animateTo(1f,    tween(120, easing = BounceEasing))
        }
    }

    // High contrast styling based on active/inactive state
    val bg = when {
        held    -> Color(0xFFD69B53)
        rolling -> Color(0xFFFFF4CC).copy(alpha = 0.8f)
        else    -> Color(0xFFFFF4CC)
    }
    
    // Custom cartoon outline colors: Held is secondary border, normal is black border
    val borderColor = if (held) ColorSecondaryBottom else ColorInk
    val strokeWidth = if (held) 3.dp else 2.5.dp
    
    // Float upward offset when die is held
    val heldOffset = if (held) (-4).dp else 0.dp
    
    val currentScale  = if (rolling) scaleAnim.value else bounceAnim.value
    val currentRotate = if (rolling) rotationState.value else 0f
    
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .padding(bottom = 6.dp, end = 6.dp) // padding space for 3D shadow
            .aspectRatio(1f)
            .scale(currentScale)
            .rotate(currentRotate)
            .offset(y = heldOffset)
            .cartoonShadow(shadowOffset = 5.dp, color = Color(0x66000000), shape = shape)
            .cartoonBorder(strokeWidth = strokeWidth, color = borderColor, shape = shape)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(bg, bg.copy(alpha = 0.8f))))
            .clickable(role = Role.Button, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (value != null) {
            DiePips(
                value    = value,
                pipColor = if (held) ColorInk else ColorInk,
                modifier = Modifier.fillMaxSize(0.7f)
            )
        } else {
            DiePips(
                value    = 1,
                pipColor = ColorDisabled,
                modifier = Modifier.fillMaxSize(0.7f)
            )
        }
    }
}

private val BounceEasing = Easing { t ->
    val n1 = 7.5625f
    val d1 = 2.75f
    when {
        t < 1f / d1 -> n1 * t * t
        t < 2f / d1 -> { val x = t - 1.5f / d1; n1 * x * x + 0.75f }
        t < 2.5f / d1 -> { val x = t - 2.25f / d1; n1 * x * x + 0.9375f }
        else -> { val x = t - 2.625f / d1; n1 * x * x + 0.984375f }
    }
}
