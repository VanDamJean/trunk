package com.yacoo.rpg.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.game.*
import com.yacoo.rpg.ui.components.*
import com.yacoo.rpg.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UpgradeScreen(
    meta: MetaSave,
    onUpgrade: (EquipmentSlot) -> Unit,
    language: AppLanguage = AppLanguage.ENGLISH,
    modifier: Modifier = Modifier
) {
    val items = getEquipmentItems(meta.equipment)
    val labels = upgradeLabels(language)
    val scope = rememberCoroutineScope()
    
    var showSuccessSlot by remember { mutableStateOf<EquipmentSlot?>(null) }
    var previousLevel by remember { mutableIntStateOf(1) }

    DarkOverlayPanel(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp)
        ) {
            item {
                TopStatsBar(
                    stage = meta.bestChapter,
                    coins = meta.coins,
                    gems = 0,
                    power = getHeroStats(meta.equipment).power,
                    language = language
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = labels.title,
                        style = GameTypography.screenTitle,
                        color = ColorTextOnDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp
                    )
                    
                    // Gold Display Pill
                    val goldPillShape = RoundedCornerShape(10.dp)
                    Row(
                        modifier = Modifier
                            .padding(bottom = 3.dp, end = 3.dp)
                            .cartoonShadow(shadowOffset = 3.dp, color = ColorInk, shape = goldPillShape)
                            .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = goldPillShape)
                            .clip(goldPillShape)
                            .background(ColorCreamWarm)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GameIcon(icon = GameIconRole.GOLD, fontSize = 16f)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "${meta.coins} ${labels.coins}",
                            style = GameTypography.buttonSmall,
                            color = ColorInk,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            items(items, key = { it.slot.name }) { item ->
                UpgradeCard(
                    item = item,
                    coins = meta.coins,
                    labels = labels,
                    onUpgrade = {
                        previousLevel = item.level
                        onUpgrade(item.slot)
                        showSuccessSlot = item.slot
                        scope.launch {
                            delay(1500)
                            showSuccessSlot = null
                        }
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
        
        // Success Modal Overlay
        AnimatedVisibility(
            visible = showSuccessSlot != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val item = showSuccessSlot?.let { slot -> items.find { it.slot == slot } }
            if (item != null) {
                DarkOverlayPanel(
                    modifier = Modifier.clickable { showSuccessSlot = null }
                ) {
                    RankUpSuccessPanel(
                        itemName = item.name,
                        oldLevel = previousLevel,
                        newLevel = item.level,
                        slot = item.slot,
                        labels = labels
                    )
                }
            }
        }
    }
}

@Composable
private fun UpgradeCard(
    item: EquipmentItem,
    coins: Int,
    labels: UpgradeLabels,
    onUpgrade: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cost      = upgradeCost(item)
    val canUp     = canUpgrade(item, coins)
    val atCap     = item.level >= Constants.EquipmentRules.LEVEL_CAP
    val itemRarity = equipRarity(item.level)

    val cardShape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 3.dp, end = 3.dp)
            .cartoonShadow(shadowOffset = 2.dp, color = ColorInk, shape = cardShape)
            .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = cardShape)
            .clip(cardShape)
            .background(ColorSurfacePanel)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Outlined item icon container
            PurpleItemCard(
                modifier = Modifier.size(40.dp),
                rarity = itemRarity
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    GameIcon(
                        icon = when(item.slot) { 
                            EquipmentSlot.WEAPON -> GameIconRole.WEAPON
                            EquipmentSlot.ARMOR -> GameIconRole.ARMOR
                            EquipmentSlot.CHARM -> GameIconRole.CHARM
                            EquipmentSlot.BOOTS -> GameIconRole.BOOTS 
                        }, 
                        fontSize = 20f
                    )
                }
            }

            // Center: Info (Name, Level, Stat, Progress)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = item.name, 
                        style = GameTypography.statValue, 
                        color = itemRarity.color,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Lv.${item.level}/${Constants.EquipmentRules.LEVEL_CAP}", 
                        style = GameTypography.caption, 
                        color = ColorTextSecondary,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = upgradeBonusLabel(item, labels), 
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = ColorInkSoft
                )

                // Level Progress Bar (Compact)
                ChunkyProgressBar(
                    progress = item.level.toFloat() / Constants.EquipmentRules.LEVEL_CAP,
                    colorStart = ColorPrimaryTop,
                    colorEnd = ColorPrimaryBottom,
                    modifier = Modifier.height(10.dp),
                    height = 10.dp
                )
            }

            // Right: Upgrade Button or Max Badge
            if (atCap) {
                val maxShape = RoundedCornerShape(6.dp)
                Text(
                    text = labels.max, 
                    modifier = Modifier
                        .cartoonBorder(strokeWidth = 1.5.dp, color = ColorInk, shape = maxShape)
                        .clip(maxShape)
                        .background(ColorPrimaryBottom)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Black, 
                    color = ColorInk, 
                    fontSize = 11.sp
                )
            } else {
                GameButton(
                    text = if (canUp) "$cost ${GameIconRole.GOLD.fallback}" else "${labels.need}\n$cost",
                    onClick = onUpgrade,
                    variant = if (canUp) GameButtonVariant.PRIMARY else GameButtonVariant.SECONDARY,
                    enabled = canUp,
                    modifier = Modifier.width(90.dp).height(38.dp),
                    height = 38.dp
                )
            }
        }
    }
}

private data class UpgradeLabels(
    val title: String,
    val coins: String,
    val level: String,
    val max: String,
    val upgrade: String,
    val need: String,
    val attack: String,
    val hp: String,
    val defense: String,
    val diceBonus: String,
    val autoDamage: String
)

private fun upgradeLabels(language: AppLanguage): UpgradeLabels = when (language) {
    AppLanguage.KOREAN -> UpgradeLabels(
        title = "강화",
        coins = "골드",
        level = "레벨",
        max = "최대",
        upgrade = "강화",
        need = "필요",
        attack = "공격력",
        hp = "체력",
        defense = "방어력",
        diceBonus = "주사위 보너스",
        autoDamage = "자동 피해"
    )
    AppLanguage.ENGLISH -> UpgradeLabels(
        title = "Upgrade",
        coins = "coins",
        level = "Level",
        max = "MAX",
        upgrade = "Upgrade",
        need = "Need",
        attack = "Attack",
        hp = "HP",
        defense = "Defense",
        diceBonus = "Dice bonus",
        autoDamage = "Auto damage"
    )
}

private fun upgradeBonusLabel(item: EquipmentItem, labels: UpgradeLabels): String = when (item.slot) {
    EquipmentSlot.WEAPON -> "${labels.attack} +${item.level * Constants.EquipmentRules.WEAPON_ATTACK_PER_LEVEL}"
    EquipmentSlot.ARMOR  -> "${labels.hp} +${item.level * Constants.EquipmentRules.ARMOR_HP_PER_LEVEL}, ${labels.defense} +${item.level}"
    EquipmentSlot.CHARM  -> "${labels.diceBonus} +${(item.level * Constants.EquipmentRules.CHARM_DICE_BONUS_PER_LEVEL * 100).toInt()}%"
    EquipmentSlot.BOOTS  -> "${labels.autoDamage} +${item.level * Constants.EquipmentRules.BOOTS_AUTO_DAMAGE_PER_LEVEL}"
}

@Composable
private fun RankUpSuccessPanel(
    itemName: String,
    oldLevel: Int,
    newLevel: Int,
    slot: EquipmentSlot,
    labels: UpgradeLabels
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            SunburstBackground(modifier = Modifier.size(320.dp))
            GameIcon(
                icon = GameIconRole.ARROW_UP,
                fontSize = 240f,
                modifier = Modifier
                    .alpha(0.6f)
                    .pulseGlow()
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            GoldRibbonTitle(if (labels.title == "강화") "승급 성공" else "Rank Up!")

            Box(
                modifier = Modifier
                    .size(148.dp)
                    .floatBobbing(amount = 6.dp, durationMs = 1500),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .width(118.dp)
                        .height(24.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color(0x55000000))
                )
                PurpleItemCard(
                    modifier = Modifier.size(116.dp),
                    rarity = equipRarity(newLevel),
                    level = newLevel
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        GameIcon(slotIconUpgrade(slot), fontSize = 64f)
                    }
                }
            }

            Text(
                text = "$itemName  LV.$oldLevel  ➜  LV.$newLevel",
                color = ColorTextOnDark,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .cartoonShadow(5.dp, ColorInk, RoundedCornerShape(18.dp))
                    .cartoonBorder(3.dp, ColorSecondaryTop, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(ColorChrome.copy(alpha = 0.94f))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                upgradeStatRows(slot, oldLevel, newLevel, labels).forEach { row ->
                    RankUpStatRow(row = row)
                }
            }
        }
    }
}

@Composable
private fun GoldRibbonTitle(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .cartoonShadow(4.dp, ColorInk, RoundedCornerShape(10.dp))
                .cartoonBorder(3.dp, ColorInk, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.verticalGradient(listOf(ColorSecondaryTop, ColorSecondaryBottom)))
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text, color = ColorInk, fontSize = 30.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun RankUpStatRow(row: UpgradeStatDelta) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .cartoonBorder(2.dp, ColorPanelBrownLight, shape)
            .clip(shape)
            .background(ColorChrome)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GameIcon(row.icon, fontSize = 28f)
        Text(row.label, color = ColorSecondaryTop, fontSize = 16.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(74.dp))
        Text(row.oldValue, color = ColorTextOnDark, fontSize = 20.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text("»", color = ColorSecondaryTop, fontSize = 26.sp, fontWeight = FontWeight.Black)
        Text(row.newValue, color = ColorTextOnDark, fontSize = 20.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text("+${row.gain}", color = ColorPrimaryTop, fontSize = 20.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(64.dp), textAlign = TextAlign.End)
    }
}

private data class UpgradeStatDelta(
    val icon: GameIconRole,
    val label: String,
    val oldValue: String,
    val newValue: String,
    val gain: String
)

private fun upgradeStatRows(
    slot: EquipmentSlot,
    oldLevel: Int,
    newLevel: Int,
    labels: UpgradeLabels
): List<UpgradeStatDelta> = when (slot) {
    EquipmentSlot.WEAPON -> statRow(
        GameIconRole.ATTACK,
        labels.attack,
        oldLevel * Constants.EquipmentRules.WEAPON_ATTACK_PER_LEVEL,
        newLevel * Constants.EquipmentRules.WEAPON_ATTACK_PER_LEVEL
    )
    EquipmentSlot.ARMOR -> listOf(
        statRow(
            GameIconRole.HEAL,
            labels.hp,
            oldLevel * Constants.EquipmentRules.ARMOR_HP_PER_LEVEL,
            newLevel * Constants.EquipmentRules.ARMOR_HP_PER_LEVEL
        ).first(),
        statRow(GameIconRole.DEFEND, labels.defense, oldLevel, newLevel).first()
    )
    EquipmentSlot.CHARM -> {
        val oldPct = (oldLevel * Constants.EquipmentRules.CHARM_DICE_BONUS_PER_LEVEL * 100).toInt()
        val newPct = (newLevel * Constants.EquipmentRules.CHARM_DICE_BONUS_PER_LEVEL * 100).toInt()
        listOf(UpgradeStatDelta(GameIconRole.CHARM, labels.diceBonus, "$oldPct%", "$newPct%", "${newPct - oldPct}%"))
    }
    EquipmentSlot.BOOTS -> statRow(
        GameIconRole.BOOTS,
        labels.autoDamage,
        oldLevel * Constants.EquipmentRules.BOOTS_AUTO_DAMAGE_PER_LEVEL,
        newLevel * Constants.EquipmentRules.BOOTS_AUTO_DAMAGE_PER_LEVEL
    )
}

private fun statRow(icon: GameIconRole, label: String, oldValue: Int, newValue: Int): List<UpgradeStatDelta> =
    listOf(UpgradeStatDelta(icon, label, oldValue.toString(), newValue.toString(), (newValue - oldValue).toString()))

private fun slotIconUpgrade(slot: EquipmentSlot): GameIconRole = when (slot) {
    EquipmentSlot.WEAPON -> GameIconRole.WEAPON
    EquipmentSlot.ARMOR -> GameIconRole.ARMOR
    EquipmentSlot.CHARM -> GameIconRole.CHARM
    EquipmentSlot.BOOTS -> GameIconRole.BOOTS
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun UpgradeScreenPreview() {
    UpgradeScreen(
        meta      = createDefaultMeta().copy(coins = 100),
        onUpgrade = {}
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812, name = "Upgrade Korean")
@Composable
private fun UpgradeScreenKoreanPreview() {
    UpgradeScreen(
        meta = createDefaultMeta().copy(coins = 2_000),
        language = AppLanguage.KOREAN,
        onUpgrade = {}
    )
}
