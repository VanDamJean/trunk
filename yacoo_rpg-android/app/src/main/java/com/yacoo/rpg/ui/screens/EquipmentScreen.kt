package com.yacoo.rpg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.game.*
import com.yacoo.rpg.ui.components.*
import com.yacoo.rpg.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EquipmentScreen(
    equipment: EquipmentSet,
    coins: Int = 0,
    language: AppLanguage = AppLanguage.ENGLISH,
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedSlot by remember { mutableStateOf<EquipmentSlot?>(EquipmentSlot.entries.first()) }
    val items = getEquipmentItems(equipment)
    val labels = equipmentLabels(language)

    DarkOverlayPanel(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 80.dp, bottom = 120.dp) // Adjust for top HUD and bottom nav
        ) {
        item {
            EquipmentHeader(labels = labels)
        }

        item {
            HeroEquipmentSummary(
                equipment = equipment,
                coins = coins,
                selectedSlot = selectedSlot,
                onSlotClick = { slot -> selectedSlot = slot }
            )
        }

        selectedSlot?.let { slot ->
            item {
                EquipmentDetailPanel(item = equipment[slot], labels = labels)
            }
        }

        item {
            BrownSectionHeader(title = labels.inventory)
        }

        item {
            val visualItems = List(24) { index -> items[index % items.size] }
            val rowCount = (visualItems.size + 3) / 4
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (r in 0 until rowCount) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (c in 0 until 4) {
                            val index = r * 4 + c
                            if (index < visualItems.size) {
                                val item = visualItems[index]
                                val visualLevel = item.level + (index / 4) + (index % 2)
                                PurpleItemCard(
                                    modifier = Modifier.weight(1f).aspectRatio(1f).staggerSlideIn(delayMs = index * 50),
                                    rarity = equipRarity(visualLevel),
                                    level = visualLevel,
                                    onClick = { selectedSlot = item.slot }
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        GameIcon(icon = slotIconEquip(item.slot), fontSize = 32f)
                                        
                                        // Selected highlight
                                        if (selectedSlot == item.slot) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .border(3.dp, ColorSecondaryTop, RoundedCornerShape(12.dp))
                                                    .pulseGlow()
                                            )
                                        }
                                        Text(
                                            text = item.slot.name.take(1),
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(ColorChrome)
                                                .padding(horizontal = 4.dp, vertical = 1.dp),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = ColorTextOnDark
                                        )

                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(4.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(if (index < items.size) ColorPrimaryTop else ColorSecondaryBottom)
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = if (index < items.size) "E" else "+${index % 3 + 1}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = ColorInk
                                            )
                                        }
                                    }
                                }
                            } else {
                                // Empty slot placeholder
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .staggerSlideIn(delayMs = index * 50)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(ColorInkSoft.copy(alpha = 0.3f))
                                        .border(2.dp, ColorInkSoft, RoundedCornerShape(16.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
        
        item {
            // Close button
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                GameButton(
                    text = labels.close,
                    onClick = onClose,
                    variant = GameButtonVariant.SECONDARY,
                    modifier = Modifier.width(200.dp)
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
    }
}

@Composable
private fun EquipmentHeader(labels: EquipmentLabels) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    labels.title,
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp,
                    color = Color(0xFFFFFDF9)
                )
                Text(
                    labels.subtitle,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = ColorSecondaryTop
                )
            }
        }
    }
}

@Composable
private fun HeroEquipmentSummary(
    equipment: EquipmentSet,
    coins: Int,
    selectedSlot: EquipmentSlot?,
    onSlotClick: (EquipmentSlot) -> Unit
) {
    val summaryShape = RoundedCornerShape(24.dp)
    val heroStats = remember(equipment) { getHeroStats(equipment) }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp, end = 6.dp)
            .cartoonShadow(shadowOffset = 5.dp, color = ColorInk, shape = summaryShape)
            .cartoonBorder(strokeWidth = 3.dp, color = ColorInk, shape = summaryShape)
            .clip(summaryShape)
            .background(ColorSurfacePanel)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Showcase Layout: Left Character | Right Slots Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Large Hero Showcase
                Box(
                    modifier = Modifier
                        .weight(0.45f)
                        .aspectRatio(0.7f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(ColorCard.copy(alpha = 0.5f))
                        .border(2.dp, ColorInk.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    HeroPaperdollCanvas(
                        equipment = equipment,
                        highlightSlot = selectedSlot,
                        size = 140.dp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Right: Slots Grid
                Column(
                    modifier = Modifier.weight(0.55f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        EquippedSlotCard(
                            slot = EquipmentSlot.WEAPON, item = equipment.weapon,
                            isSelected = selectedSlot == EquipmentSlot.WEAPON,
                            showUpgradeIndicator = canUpgrade(equipment.weapon, coins),
                            onClick = { onSlotClick(EquipmentSlot.WEAPON) },
                            modifier = Modifier.weight(1f).aspectRatio(1f)
                        )
                        EquippedSlotCard(
                            slot = EquipmentSlot.ARMOR, item = equipment.armor,
                            isSelected = selectedSlot == EquipmentSlot.ARMOR,
                            showUpgradeIndicator = canUpgrade(equipment.armor, coins),
                            onClick = { onSlotClick(EquipmentSlot.ARMOR) },
                            modifier = Modifier.weight(1f).aspectRatio(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        EquippedSlotCard(
                            slot = EquipmentSlot.CHARM, item = equipment.charm,
                            isSelected = selectedSlot == EquipmentSlot.CHARM,
                            showUpgradeIndicator = canUpgrade(equipment.charm, coins),
                            onClick = { onSlotClick(EquipmentSlot.CHARM) },
                            modifier = Modifier.weight(1f).aspectRatio(1f)
                        )
                        EquippedSlotCard(
                            slot = EquipmentSlot.BOOTS, item = equipment.boots,
                            isSelected = selectedSlot == EquipmentSlot.BOOTS,
                            showUpgradeIndicator = canUpgrade(equipment.boots, coins),
                            onClick = { onSlotClick(EquipmentSlot.BOOTS) },
                            modifier = Modifier.weight(1f).aspectRatio(1f)
                        )
                    }
                }
            }
            
            // Stats Panel
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CombatPowerBadge(power = heroStats.power)
                HorizontalStatsBar(
                    hp = heroStats.maxHp,
                    attack = heroStats.attack,
                    defense = heroStats.defense
                )
            }
        }
    }
}

@Composable
private fun EquipmentDetailPanel(item: EquipmentItem, labels: EquipmentLabels) {
    val panelShape = RoundedCornerShape(20.dp)
    val itemRarity = equipRarity(item.level)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp, end = 5.dp)
            .cartoonShadow(shadowOffset = 4.dp, color = ColorInk, shape = panelShape)
            .cartoonBorder(strokeWidth = 3.dp, color = ColorInk, shape = panelShape)
            .clip(panelShape)
            .background(ColorChrome)
            .padding(14.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large item icon box using PurpleItemCard
                PurpleItemCard(
                    modifier = Modifier.size(64.dp),
                    rarity = itemRarity
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                         GameIcon(
                            icon = slotIconEquip(item.slot),
                            fontSize = 36f
                        )
                    }
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.name,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = itemRarity.color
                    )
                    Text(
                        "${labels.equipped} • ${equipmentSlotLabel(item.slot, labels)} ${labels.slot}",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = ColorTextSecondary
                    )
                }
                
                // Lv Capsule
                val lvShape = RoundedCornerShape(8.dp)
                Text(
                    "Lv ${item.level}",
                    modifier = Modifier
                        .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = lvShape)
                        .clip(lvShape)
                        .background(ColorPrimaryBottom)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = ColorInk
                )
            }

            // Stat list
            val innerShape = RoundedCornerShape(12.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = innerShape)
                    .clip(innerShape)
                    .background(ColorInkSoft.copy(alpha = 0.5f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    labels.bonus,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = ColorSecondaryTop
                )
                Text(
                    equipmentBonusLabel(item),
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = ColorCard
                )
            }
        }
    }
}

private fun slotIconEquip(slot: EquipmentSlot): GameIconRole = when (slot) {
    EquipmentSlot.WEAPON -> GameIconRole.WEAPON
    EquipmentSlot.ARMOR -> GameIconRole.ARMOR
    EquipmentSlot.CHARM -> GameIconRole.CHARM
    EquipmentSlot.BOOTS -> GameIconRole.BOOTS
}

private data class EquipmentLabels(
    val title: String,
    val subtitle: String,
    val current: String,
    val hint: String,
    val heroLoadout: String,
    val slotFilled: String,
    val slots: String,
    val avgLevel: String,
    val equipped: String,
    val slot: String,
    val bonus: String,
    val inventory: String,
    val noUnequip: String,
    val equippedNow: String,
    val open: String,
    val view: String,
    val close: String,
    val weapon: String,
    val armor: String,
    val charm: String,
    val boots: String
)

private fun equipmentLabels(language: AppLanguage): EquipmentLabels = when (language) {
    AppLanguage.KOREAN -> EquipmentLabels(
        title = "장비",
        subtitle = "착용 장비 관리",
        current = "현재 장착",
        hint = "착용 중인 장비를 눌러 능력치를 확인하세요.",
        heroLoadout = "영웅 장비",
        slotFilled = "현재 장비 세트의 4개 슬롯이 채워져 있습니다.",
        slots = "슬롯",
        avgLevel = "평균 Lv",
        equipped = "장착됨",
        slot = "슬롯",
        bonus = "장착 보너스",
        inventory = "내 장비",
        noUnequip = "아직 해제용 가방 모델이 없어 장착 아이템만 표시됩니다.",
        equippedNow = "현재 장착 중",
        open = "열림",
        view = "보기",
        close = "닫기",
        weapon = "무기",
        armor = "갑옷",
        charm = "부적",
        boots = "신발"
    )
    AppLanguage.ENGLISH -> EquipmentLabels(
        title = "Gear",
        subtitle = "Equipped inventory",
        current = "CURRENT",
        hint = "Tap any equipped item to inspect its RPG stats.",
        heroLoadout = "Hero Loadout",
        slotFilled = "4 slots are filled from the current equipment set.",
        slots = "Slots",
        avgLevel = "Avg Lv",
        equipped = "Equipped",
        slot = "slot",
        bonus = "Equip Bonus",
        inventory = "Inventory",
        noUnequip = "These are equipped items only — no unequip bag yet.",
        equippedNow = "Equipped now",
        open = "OPEN",
        view = "VIEW",
        close = "CLOSE",
        weapon = "Weapon",
        armor = "Armor",
        charm = "Charm",
        boots = "Boots"
    )
}

private fun equipmentSlotLabel(slot: EquipmentSlot, labels: EquipmentLabels): String = when (slot) {
    EquipmentSlot.WEAPON -> labels.weapon
    EquipmentSlot.ARMOR -> labels.armor
    EquipmentSlot.CHARM -> labels.charm
    EquipmentSlot.BOOTS -> labels.boots
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun EquipmentScreenPreview() {
    EquipmentScreen(equipment = Constants.STARTING_EQUIPMENT)
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812, name = "Equipment Korean")
@Composable
private fun EquipmentScreenKoreanPreview() {
    EquipmentScreen(equipment = Constants.STARTING_EQUIPMENT, coins = 5_000, language = AppLanguage.KOREAN)
}
