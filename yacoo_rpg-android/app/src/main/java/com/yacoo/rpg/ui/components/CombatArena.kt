package com.yacoo.rpg.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.game.EquipmentSet
import com.yacoo.rpg.game.NodeType
import com.yacoo.rpg.ui.theme.ColorCard
import com.yacoo.rpg.ui.theme.ColorDangerBottom
import com.yacoo.rpg.ui.theme.ColorDangerTop
import com.yacoo.rpg.ui.theme.ColorHpFillStart
import com.yacoo.rpg.ui.theme.ColorInk
import com.yacoo.rpg.ui.theme.ColorInkStrong
import com.yacoo.rpg.ui.theme.GameTypography
import com.yacoo.rpg.ui.theme.bounceIn
import com.yacoo.rpg.ui.theme.cartoonBorder

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
            .padding(horizontal = 16.dp)
    ) {
        CombatHeroPanel(
            equipment = equipment,
            heroHp = heroHp,
            heroMaxHp = heroMaxHp,
            heroX = heroX,
            floatingDamage = floatingDamage,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        CombatEnemyPanel(
            nodeType = nodeType,
            enemyName = enemyName,
            enemyHp = enemyHp,
            enemyMaxHp = enemyMaxHp,
            monsterScale = monsterScale,
            floatingDamage = floatingDamage,
            modifier = Modifier.align(Alignment.CenterEnd)
        )

        CombatFeedbackBanner(
            feedback = feedback,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-20).dp)
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
        Box(modifier = Modifier.size(70.dp), contentAlignment = Alignment.Center) {
            HeroPaperdollCanvas(equipment = equipment, size = 54.dp)
            FloatingDamageText(
                visible = floatingDamage?.second == true,
                amount = floatingDamage?.first ?: 0,
                color = ColorDangerBottom,
                fontSize = 30,
                modifier = Modifier.offset(y = (-50).dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        HpBar(
            current = heroHp,
            max = heroMaxHp,
            color = ColorHpFillStart,
            modifier = Modifier.width(80.dp)
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
        Box(modifier = Modifier.size(70.dp), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.scale(monsterScale)) {
                MonsterCanvas(nodeType = nodeType, modifier = Modifier.size(54.dp))
            }
            FloatingDamageText(
                visible = floatingDamage?.second == false,
                amount = floatingDamage?.first ?: 0,
                color = Color(0xFFFFD43F),
                fontSize = 34,
                modifier = Modifier.offset(y = (-50).dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        HpBar(
            current = enemyHp,
            max = enemyMaxHp,
            color = ColorDangerTop,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = enemyName,
            style = GameTypography.caption,
            color = ColorCard,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun CombatFeedbackBanner(feedback: String, modifier: Modifier = Modifier) {
    if (feedback.isEmpty()) return

    Box(
        modifier = modifier
            .cartoonBorder(2.dp, ColorInk, RoundedCornerShape(12.dp))
            .background(ColorInkStrong.copy(alpha = 0.8f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = feedback,
            style = GameTypography.buttonSmall,
            color = ColorCard,
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
