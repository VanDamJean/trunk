package com.yacoo.rpg.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.game.YahtzeeAttackCategory
import com.yacoo.rpg.ui.theme.*

@Composable
fun CombatActionCard(
    category: YahtzeeAttackCategory,
    isValid: Boolean,
    canAttack: Boolean,
    previewDmg: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val enabled = canAttack && isValid
    val shape = RoundedCornerShape(12.dp)

    val bg = if (isValid) Color(0xFF3A2A60) else Color(0xFF211A3A)
    val borderColor = if (isValid) ColorSecondaryTop else ColorOutlineSubtle
    val borderThickness = if (isValid) 2.5.dp else 1.5.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp, end = 4.dp)
            .cartoonShadow(if (isValid) 2.dp else 0.dp, ColorInk, shape)
            .cartoonBorder(strokeWidth = borderThickness, color = borderColor, shape = shape)
            .clip(shape)
            .background(bg)
            .then(if (enabled) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            category.label,
            fontWeight = FontWeight.Black,
            color = if (isValid) ColorTextPrimary else ColorTextSecondary,
            fontSize = 12.sp,
            maxLines = 1
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "x${category.multiplier}",
                fontWeight = FontWeight.Black,
                color = if (isValid) ColorPrimaryTop else ColorTextTertiary,
                fontSize = 11.sp
            )
            if (previewDmg != null) {
                Text(
                    text = "$previewDmg DMG",
                    fontWeight = FontWeight.Black,
                    color = ColorPrimaryTop,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun BattleLogCard(title: String, body: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .cartoonShadow(2.dp, ColorInk, shape)
            .cartoonBorder(2.dp, ColorInk, shape)
            .clip(shape)
            .background(Color(0xFF1C1635))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(title, color = ColorTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Black)
        Text(body, color = ColorTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Black)
    }
}
