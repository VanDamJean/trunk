package com.yacoo.rpg.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.game.EquipmentSet
import com.yacoo.rpg.game.NodeType
import com.yacoo.rpg.ui.theme.*

@Composable
fun CombatArena(
    equipment: EquipmentSet,
    heroHp: Int,
    heroMaxHp: Int,
    heroX: Float,
    enemyName: String,
    enemyHp: Int,
    enemyMaxHp: Int,
    monsterScale: Float,
    nodeType: NodeType?,
    feedback: String,
    floatingDamage: Pair<Int, Boolean>?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 0.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val boardLeft = size.width * 0.05f
            val boardRight = size.width * 0.95f
            val boardTop = size.height * 0.15f
            val boardBottom = size.height * 0.90f

            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF5E351D), Color(0xFF2F180C), Color(0xFF100804))
                ),
                topLeft = Offset(size.width * 0.02f, size.height * 0.04f),
                size = Size(size.width * 0.96f, size.height * 0.91f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(28.dp.toPx(), 28.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFF090508),
                topLeft = Offset(size.width * 0.02f, size.height * 0.04f),
                size = Size(size.width * 0.96f, size.height * 0.91f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(28.dp.toPx(), 28.dp.toPx()),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
            )
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF3A1D0E), Color(0xFFB87836), Color(0xFF3A1D0E))
                ),
                topLeft = Offset(size.width * 0.08f, size.height * 0.08f),
                size = Size(size.width * 0.84f, 15.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx(), 20.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFF120B09),
                topLeft = Offset(size.width * 0.43f, size.height * 0.045f),
                size = Size(size.width * 0.14f, 42.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx(), 12.dp.toPx())
            )
            drawCircle(
                color = Color(0xFFD49A3B),
                radius = 13.dp.toPx(),
                center = Offset(size.width * 0.5f, size.height * 0.09f)
            )
            drawCircle(
                color = Color(0xFF2A170E),
                radius = 8.dp.toPx(),
                center = Offset(size.width * 0.5f, size.height * 0.09f)
            )

            drawRoundRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF44966A), Color(0xFF1F724E), Color(0xFF123421)),
                    center = Offset(size.width * 0.50f, size.height * 0.46f),
                    radius = size.width * 0.70f
                ),
                topLeft = Offset(boardLeft, boardTop),
                size = Size(boardRight - boardLeft, boardBottom - boardTop),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx(), 22.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xAA11170C),
                topLeft = Offset(boardLeft + 4.dp.toPx(), boardTop + 4.dp.toPx()),
                size = Size(boardRight - boardLeft - 8.dp.toPx(), boardBottom - boardTop - 8.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
            )

            val stitch = Color(0xB9D8C083)
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
            val cols = 6
            val rows = 4
            for (c in 1 until cols) {
                val x = boardLeft + (boardRight - boardLeft) * c / cols
                drawLine(stitch, Offset(x, boardTop), Offset(x, boardBottom), 2.0.dp.toPx(), pathEffect = pathEffect)
            }
            for (r in 1 until rows) {
                val y = boardTop + (boardBottom - boardTop) * r / rows
                drawLine(stitch, Offset(boardLeft, y), Offset(boardRight, y), 2.0.dp.toPx(), pathEffect = pathEffect)
            }

            val slash = Path().apply {
                moveTo(size.width * 0.36f, size.height * 0.57f)
                cubicTo(size.width * 0.46f, size.height * 0.52f, size.width * 0.57f, size.height * 0.46f, size.width * 0.66f, size.height * 0.44f)
            }
            drawPath(slash, Color(0x66FFFFFF), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 7.dp.toPx()))
            drawPath(slash, Color(0xFFFFD23A), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx()))
            drawCircle(Color(0x22F8E2A2), radius = 20.dp.toPx(), center = Offset(size.width * 0.50f, size.height * 0.61f))
            drawCircle(Color(0x33F8E2A2), radius = 11.dp.toPx(), center = Offset(size.width * 0.50f, size.height * 0.28f))

            drawRoundRect(
                color = Color(0xFF7A291B),
                topLeft = Offset(size.width * 0.012f, size.height * 0.22f),
                size = Size(34.dp.toPx(), size.height * 0.36f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFF173F80),
                topLeft = Offset(size.width - 34.dp.toPx() - size.width * 0.012f, size.height * 0.22f),
                size = Size(34.dp.toPx(), size.height * 0.36f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
            drawCircle(Color(0xFFFFD34D), radius = 4.dp.toPx(), center = Offset(size.width * 0.047f, size.height * 0.27f))
            drawCircle(Color(0xFFFFD34D), radius = 4.dp.toPx(), center = Offset(size.width * 0.953f, size.height * 0.27f))
            drawCircle(Color(0xFF643B1C), radius = 8.dp.toPx(), center = Offset(size.width * 0.08f, size.height * 0.84f))
            drawCircle(Color(0xFF643B1C), radius = 8.dp.toPx(), center = Offset(size.width * 0.92f, size.height * 0.84f))
            drawCircle(Color(0xFFFFD34D).copy(alpha = 0.32f), radius = 4.dp.toPx(), center = Offset(size.width * 0.08f, size.height * 0.84f))
            drawCircle(Color(0xFFFFD34D).copy(alpha = 0.32f), radius = 4.dp.toPx(), center = Offset(size.width * 0.92f, size.height * 0.84f))
        }

        CombatArenaPawMark(
            modifier = Modifier
                .align(Alignment.Center)
                .size(56.dp)
                .offset(y = (-14).dp)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            CombatHeroPanel(
                equipment = equipment,
                heroHp = heroHp,
                heroMaxHp = heroMaxHp,
                heroX = heroX,
                floatingDamage = floatingDamage,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 34.dp, y = (-28).dp)
            )

            CombatEnemyPanel(
                nodeType = nodeType,
                enemyName = enemyName,
                enemyHp = enemyHp,
                enemyMaxHp = enemyMaxHp,
                monsterScale = monsterScale,
                floatingDamage = floatingDamage,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-30).dp, y = (-28).dp)
            )

            CombatFeedbackBanner(
                feedback = feedback,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 44.dp)
            )
        }
    }
}

@Composable
private fun CombatArenaPawMark(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawCircle(Color(0x44D8C083), radius = size.minDimension * 0.16f, center = Offset(size.width * 0.5f, size.height * 0.64f))
        listOf(0.22f, 0.38f, 0.62f, 0.78f).forEachIndexed { index, x ->
            val y = if (index == 1 || index == 2) 0.30f else 0.42f
            drawCircle(Color(0x44D8C083), radius = size.minDimension * 0.08f, center = Offset(size.width * x, size.height * y))
        }
    }
}

@Composable
fun ChessPieceBase(ringColor: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        // Draw an isometric-like base for characters
        val baseColorTop = Color(0xFF18181A)
        val baseColorSide = Color(0xFF06050B)
        val shadowColor = Color(0x88000000)
        
        // Shadow
        drawOval(
            color = shadowColor,
            topLeft = Offset(-size.width * 0.1f, size.height * 0.4f),
            size = Size(size.width * 1.2f, size.height)
        )
        
        // Base side
        drawOval(
            color = baseColorSide,
            topLeft = Offset(0f, size.height * 0.4f),
            size = Size(size.width, size.height * 0.5f)
        )
        drawRect(
            color = baseColorSide,
            topLeft = Offset(0f, size.height * 0.25f),
            size = Size(size.width, size.height * 0.4f)
        )
        
        // Base top (where character stands)
        drawOval(
            color = baseColorTop,
            topLeft = Offset(0f, 0f),
            size = Size(size.width, size.height * 0.5f)
        )
        
        // Inner glowing ring
        drawOval(
            color = ringColor,
            topLeft = Offset(size.width * 0.15f, size.height * 0.08f),
            size = Size(size.width * 0.7f, size.height * 0.34f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
        )
    }
}

@Composable
fun CombatHeroPanel(
    equipment: EquipmentSet,
    heroHp: Int,
    heroMaxHp: Int,
    heroX: Float,
    floatingDamage: Pair<Int, Boolean>?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.offset(x = heroX.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(108.dp), contentAlignment = Alignment.BottomCenter) {
            ChessPieceBase(ringColor = Color(0xFF45E8FF), modifier = Modifier.size(90.dp, 31.dp).offset(y = 8.dp))
            
            HeroPaperdollCanvas(equipment = equipment, size = 74.dp, modifier = Modifier.offset(y = (-13).dp))
            
            FloatingDamageText(
                visible = floatingDamage?.second == true,
                amount = floatingDamage?.first ?: 0,
                color = ColorDangerBottom,
                fontSize = 34,
                modifier = Modifier.offset(y = (-68).dp)
            )
        }
        Spacer(Modifier.height(5.dp))
        CombatHpBarWithText(
            current = heroHp,
            max = heroMaxHp,
            color = Color(0xFFE8342F),
            modifier = Modifier.width(112.dp)
        )
    }
}

@Composable
fun CombatEnemyPanel(
    nodeType: NodeType?,
    enemyName: String,
    enemyHp: Int,
    enemyMaxHp: Int,
    monsterScale: Float,
    floatingDamage: Pair<Int, Boolean>?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(108.dp), contentAlignment = Alignment.BottomCenter) {
            ChessPieceBase(ringColor = Color(0xFFE8342F), modifier = Modifier.size(90.dp, 31.dp).offset(y = 8.dp))
            
            Box(modifier = Modifier.scale(monsterScale).offset(y = (-10).dp)) {
                MonsterCanvas(nodeType = nodeType, modifier = Modifier.size(72.dp))
            }
            
            FloatingDamageText(
                visible = floatingDamage?.second == false,
                amount = floatingDamage?.first ?: 0,
                color = Color(0xFFFFD43F),
                fontSize = 38,
                modifier = Modifier.offset(y = (-68).dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = enemyName,
            style = GameTypography.caption,
            color = Color(0xFFFFFDF9),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .offset(y = (-2).dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xCC090508))
                .padding(horizontal = 7.dp, vertical = 1.dp)
        )
        CombatHpBarWithText(
            current = enemyHp,
            max = enemyMaxHp,
            color = Color(0xFFE8342F),
            modifier = Modifier.width(112.dp)
        )
    }
}

@Composable
fun CombatHpBarWithText(
    current: Int,
    max: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(18.dp)
            .cartoonBorder(2.5.dp, Color(0xFF06050B), RoundedCornerShape(percent = 50))
            .clip(RoundedCornerShape(percent = 50))
            .background(Color(0xFF06050B)),
        contentAlignment = Alignment.CenterStart
    ) {
        val fraction = (current.toFloat() / max.coerceAtLeast(1)).coerceIn(0f, 1f)
        Box(
            modifier = Modifier
                .height(18.dp)
                .fillMaxWidth(fraction)
                .background(Brush.verticalGradient(listOf(Color(0xFFFF6745), color)))
        )
        Text(
            text = "$current / $max",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun CombatFeedbackBanner(feedback: String, modifier: Modifier = Modifier) {
    if (feedback.isEmpty()) return

    Box(
        modifier = modifier
            .cartoonShadow(3.dp, Color(0xAA000000), RoundedCornerShape(12.dp))
            .cartoonBorder(2.dp, Color(0xFFFFD43F), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFF111217), Color(0xFF251807), Color(0xFF111217))))
            .padding(horizontal = 18.dp, vertical = 7.dp)
    ) {
        Text(
            text = feedback,
            style = GameTypography.buttonSmall,
            color = Color(0xFFFFE37B),
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun FloatingDamageText(
    visible: Boolean,
    amount: Int,
    color: Color,
    fontSize: Int,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it } + fadeIn(),
        exit = fadeOut()
    ) {
        Text(
            text = "-$amount",
            style = GameTypography.screenTitle,
            color = color,
            fontSize = fontSize.sp,
            modifier = modifier.bounceIn()
        )
    }
}
