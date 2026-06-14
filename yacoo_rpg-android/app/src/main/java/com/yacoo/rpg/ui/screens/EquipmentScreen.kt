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
    var selectedSlot by remember { mutableStateOf<EquipmentSlot?>(null) }
    val items = getEquipmentItems(equipment)
    val labels = equipmentLabels(language)
    val bottomContentClearance = bottomNavContentClearance()

    DarkOverlayPanel(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TopStatsBar(
                stage = items.firstOrNull()?.level ?: 1, // approximate
                coins = coins,
                gems = 0,
                power = getHeroStats(equipment).power,
                language = language
            )

            EquipmentHeader(labels = labels, onClose = onClose)

            // Equipped slots directly on screen background
            EquippedShowcaseArea(
                equipment = equipment,
                coins = coins,
                selectedSlot = selectedSlot,
                onSlotClick = { slot -> selectedSlot = slot }
            )

            // Circular action sub-buttons row
            CircularSubButtonsRow(language = language)

            // Full-width brown section title
            FullWidthSectionHeader(title = labels.inventory)

            // Sort (cyan) & Forge (gold) buttons row
            SortAndForgeButtonsRow(language = language, onClose = onClose)

            val visualItems = remember(items) { List(25) { index -> items[index % items.size] } }
            val rowCount = (visualItems.size + 4) / 5
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = bottomContentClearance)
            ) {
                items(rowCount) { r ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (c in 0 until 5) {
                            val index = r * 5 + c
                            if (index < visualItems.size) {
                                val item = visualItems[index]
                                val visualLevel = item.level + (index / 5) + (index % 2)
                                val isEquipped = index < items.size
                                PurpleItemCard(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .staggerSlideIn(delayMs = index * 50),
                                    rarity = equipRarity(visualLevel),
                                    level = visualLevel,
                                    slot = item.slot,
                                    isEquipped = isEquipped,
                                    onClick = { selectedSlot = item.slot }
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        GameIcon(icon = slotIconEquip(item.slot), fontSize = 36f)
                                        
                                        // Selected highlight
                                        if (selectedSlot == item.slot) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .border(3.dp, ColorSecondaryTop, RoundedCornerShape(12.dp))
                                                    .pulseGlow()
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

        // Popup overlay for selected item details
        selectedSlot?.let { slot ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { selectedSlot = null },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clickable(enabled = false, onClick = {})
                ) {
                    EquipmentDetailPanel(item = equipment[slot], labels = labels)
                }
            }
        }
    }
}

@Composable
private fun EquipmentHeader(labels: EquipmentLabels, onClose: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    labels.title,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = Color(0xFFFFFDF9)
                )
                Text(
                    labels.subtitle,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = ColorSecondaryTop
                )
            }

            // X close button on the top-right
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .cartoonBorder(strokeWidth = 3.dp, color = ColorInk, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorDangerBottom)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                GameIcon(
                    icon = GameIcons.close,
                    fontSize = 20f
                )
            }
        }
    }
}

@Composable
private fun EquippedShowcaseArea(
    equipment: EquipmentSet,
    coins: Int,
    selectedSlot: EquipmentSlot?,
    onSlotClick: (EquipmentSlot) -> Unit
) {
    val heroStats = remember(equipment) { getHeroStats(equipment) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Column: WEAPON, Placeholder, BOOTS
            Column(
                modifier = Modifier.weight(0.13f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EquippedSlotCard(
                    slot = EquipmentSlot.WEAPON, item = equipment.weapon,
                    isSelected = selectedSlot == EquipmentSlot.WEAPON,
                    showUpgradeIndicator = canUpgrade(equipment.weapon, coins),
                    onClick = { onSlotClick(EquipmentSlot.WEAPON) },
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    iconSize = 28f
                )
                EmptyShowcaseSlot()
                EquippedSlotCard(
                    slot = EquipmentSlot.BOOTS, item = equipment.boots,
                    isSelected = selectedSlot == EquipmentSlot.BOOTS,
                    showUpgradeIndicator = canUpgrade(equipment.boots, coins),
                    onClick = { onSlotClick(EquipmentSlot.BOOTS) },
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    iconSize = 28f
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Center: Character Paperdoll
            Box(
                modifier = Modifier
                    .weight(0.52f)
                    .aspectRatio(1.2f),
                contentAlignment = Alignment.BottomCenter
            ) {
                HeroPaperdollCanvas(
                    equipment = equipment,
                    highlightSlot = selectedSlot,
                    size = 130.dp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Column: ARMOR, Placeholder, CHARM
            Column(
                modifier = Modifier.weight(0.13f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EquippedSlotCard(
                    slot = EquipmentSlot.ARMOR, item = equipment.armor,
                    isSelected = selectedSlot == EquipmentSlot.ARMOR,
                    showUpgradeIndicator = canUpgrade(equipment.armor, coins),
                    onClick = { onSlotClick(EquipmentSlot.ARMOR) },
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    iconSize = 28f
                )
                EmptyShowcaseSlot()
                EquippedSlotCard(
                    slot = EquipmentSlot.CHARM, item = equipment.charm,
                    isSelected = selectedSlot == EquipmentSlot.CHARM,
                    showUpgradeIndicator = canUpgrade(equipment.charm, coins),
                    onClick = { onSlotClick(EquipmentSlot.CHARM) },
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    iconSize = 28f
                )
            }
        }
        
        // Stats Panel
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
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

@Composable
private fun EmptyShowcaseSlot() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(bottom = 2.dp, end = 2.dp)
            .cartoonShadow(shadowOffset = 1.dp, color = ColorInk, shape = RoundedCornerShape(12.dp))
            .cartoonBorder(strokeWidth = 2.dp, color = ColorInkSoft, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(ColorInkSoft.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        GameIcon(icon = GameIconRole.LOCK, fontSize = 14f)
    }
}

@Composable
private fun CircularSubButtonsRow(language: AppLanguage) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularSubButton(icon = GameIconRole.PLAYER_AVATAR, label = if (language == AppLanguage.KOREAN) "펫" else "Pet")
        CircularSubButton(icon = GameIconRole.TREASURE, label = if (language == AppLanguage.KOREAN) "소장품" else "Collectible")
        CircularSubButton(icon = GameIconRole.BOSS, label = if (language == AppLanguage.KOREAN) "탈것" else "Mount")
        CircularSubButton(icon = GameIconRole.SCRAP, label = if (language == AppLanguage.KOREAN) "아티팩트" else "Artifact")
    }
}

@Composable
private fun CircularSubButton(
    icon: GameIconRole,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = RoundedCornerShape(17.dp))
                .clip(RoundedCornerShape(17.dp))
                .background(ColorSurfacePanel),
            contentAlignment = Alignment.Center
        ) {
            GameIcon(icon = icon, fontSize = 18f)
        }
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            color = ColorTextPrimary
        )
    }
}

@Composable
private fun FullWidthSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(6.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = shape)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(ColorPanelBrownLight, ColorPanelBrownDark)))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GameIcon(icon = GameIconRole.ARMOR, fontSize = 14f)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = ColorTextOnDark
        )
    }
}

@Composable
private fun SortAndForgeButtonsRow(
    language: AppLanguage,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "등급순" Button (Cyan color)
        Box(
            modifier = Modifier
                .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = RoundedCornerShape(6.dp))
                .clip(RoundedCornerShape(6.dp))
                .background(ColorSecondaryBottom) // cyan/purple
                .clickable { /* sort logic placeholder */ }
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (language == AppLanguage.KOREAN) "등급순" else "By Grade",
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                color = ColorInk
            )
        }

        // "공방" Button (Gold color with red dot notification)
        Box(
            modifier = Modifier
                .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = RoundedCornerShape(6.dp))
                .clip(RoundedCornerShape(6.dp))
                .background(ColorPrimaryBottom) // gold
                .clickable { /* forge logic placeholder */ }
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = if (language == AppLanguage.KOREAN) "공방" else "Forge",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = ColorInk
                )
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(RoundedCornerShape(2.5.dp))
                        .background(ColorDangerBottom)
                )
            }
        }
    }
}

@Composable
private fun EquipmentDetailPanel(item: EquipmentItem, labels: EquipmentLabels) {
    val panelShape = RoundedCornerShape(16.dp)
    val itemRarity = equipRarity(item.level)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp, end = 4.dp)
            .cartoonShadow(shadowOffset = 3.dp, color = ColorInk, shape = panelShape)
            .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = panelShape)
            .clip(panelShape)
            .background(ColorChrome)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Item Card (Small size: 44.dp)
            PurpleItemCard(
                modifier = Modifier.size(44.dp),
                rarity = itemRarity
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    GameIcon(
                        icon = slotIconEquip(item.slot),
                        fontSize = 32f
                    )
                }
            }
            
            // 2. Name & Slot (vertical stack)
            Column(modifier = Modifier.weight(0.4f)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = itemRarity.color,
                    maxLines = 1
                )
                Text(
                    text = "${labels.equipped} • ${equipmentSlotLabel(item.slot, labels)}",
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    color = ColorTextSecondary,
                    maxLines = 1
                )
            }
            
            // 3. Equip Bonus details (horizontal layout or small stack)
            Column(
                modifier = Modifier.weight(0.4f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = labels.bonus,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    color = ColorSecondaryTop
                )
                Text(
                    text = equipmentBonusLabel(item),
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = ColorTextPrimary
                )
            }
            
            // 4. Lv Capsule
            val lvShape = RoundedCornerShape(6.dp)
            Text(
                "Lv ${item.level}",
                modifier = Modifier
                    .cartoonBorder(strokeWidth = 2.dp, color = ColorInk, shape = lvShape)
                    .clip(lvShape)
                    .background(ColorPrimaryBottom)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                fontWeight = FontWeight.Black,
                fontSize = 10.sp,
                color = ColorInk
            )
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
