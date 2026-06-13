package com.yacoo.rpg.ui.theme

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.*
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Cartoon UI styling helpers for outline and 3D shadow.
 */

fun Modifier.cartoonBorder(
    strokeWidth: Dp = 2.5.dp,
    color: Color = Color(0xFF1C1A1F),
    shape: Shape = RoundedCornerShape(12.dp)
): Modifier = this.border(width = strokeWidth, color = color, shape = shape)

fun Modifier.cartoonShadow(
    shadowOffset: Dp = 4.dp,
    color: Color = Color(0xFF1C1A1F),
    shape: Shape = RoundedCornerShape(12.dp)
): Modifier = this.drawBehind {
    val outline = shape.createOutline(this.size, this.layoutDirection, this)
    val offsetPx = shadowOffset.toPx()
    
    drawIntoCanvas { canvas ->
        canvas.save()
        canvas.translate(offsetPx, offsetPx)
        drawOutline(
            outline = outline,
            color = color
        )
        canvas.restore()
    }
}

/**
 * Combined 3D clickable modifier that shifts the content down when pressed.
 */
fun Modifier.cartoonClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Smooth translation down when pressed
    val translationY by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        label = "PressedTranslation"
    )
    
    this
        .offset(y = translationY)
        .clickable(
            role = Role.Button,
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
}

/**
 * Adds a continuous floating bobbing animation.
 */
fun Modifier.floatBobbing(amount: Dp = 4.dp, durationMs: Int = 1500): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "bobbing")
    val dy by infiniteTransition.animateFloat(
        initialValue = -amount.value,
        targetValue = amount.value,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bobbing_anim"
    )
    this.offset(y = dy.dp)
}

/**
 * Adds a glowing pulse scaling animation.
 */
fun Modifier.pulseGlow(minScale: Float = 1f, maxScale: Float = 1.05f, durationMs: Int = 1000): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsing")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulsing_anim"
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Stagger slide-in from bottom with fade (Capybara Go card reveal style).
 * Use with LaunchedEffect to trigger.
 */
fun Modifier.staggerSlideIn(
    delayMs: Int = 0,
    durationMs: Int = 300,
    offsetY: Float = 60f
): Modifier = composed {
    val animatable = remember { Animatable(0f) }
    
    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delayMs.toLong())
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMs, easing = FastOutSlowInEasing)
        )
    }
    
    this.graphicsLayer {
        translationY = (1f - animatable.value) * offsetY
        alpha = animatable.value
    }
}

/**
 * Bounce-in scale animation (for reward/damage emphasis).
 */
fun Modifier.bounceIn(durationMs: Int = 400): Modifier = composed {
    val animatable = remember { Animatable(0f) }
    
    androidx.compose.runtime.LaunchedEffect(Unit) {
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }
    
    this.graphicsLayer {
        scaleX = animatable.value
        scaleY = animatable.value
        alpha = animatable.value.coerceIn(0f, 1f)
    }
}
