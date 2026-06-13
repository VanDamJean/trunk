import { EQUIPMENT_ORDER, EQUIPMENT_RULES, HERO_BASE, STARTING_EQUIPMENT } from './constants';
import type { EquipmentItem, EquipmentSet, EquipmentSlot, GameSave, MetaSave, HeroStats } from './types';

export function cloneEquipment(equipment: EquipmentSet = STARTING_EQUIPMENT): EquipmentSet {
  return {
    weapon: { ...equipment.weapon },
    armor: { ...equipment.armor },
    charm: { ...equipment.charm },
    boots: { ...equipment.boots }
  };
}

export function createDefaultSave(): GameSave {
  return {
    stage: HERO_BASE.stage,
    coins: HERO_BASE.coins,
    equipment: cloneEquipment()
  };
}

export function createDefaultMeta(): MetaSave {
  return {
    version: 2,
    coins: HERO_BASE.coins,
    equipment: cloneEquipment(),
    bestChapter: 1,
    totalRuns: 0
  };
}

export function upgradeCost(item: EquipmentItem): number {
  return EQUIPMENT_RULES.costPerCurrentLevel * item.level;
}

export function canUpgrade(item: EquipmentItem, coins: number): boolean {
  return item.level < EQUIPMENT_RULES.levelCap && coins >= upgradeCost(item);
}

export function upgradeEquipment(save: GameSave, slot: EquipmentSlot): GameSave;
export function upgradeEquipment(save: MetaSave, slot: EquipmentSlot): MetaSave;
export function upgradeEquipment(save: GameSave | MetaSave, slot: EquipmentSlot): GameSave | MetaSave {
  const item = save.equipment[slot];
  if (!canUpgrade(item, save.coins)) {
    return save;
  }

  return {
    ...save,
    coins: save.coins - upgradeCost(item),
    equipment: {
      ...save.equipment,
      [slot]: { ...item, level: item.level + 1 }
    }
  };
}

export function getHeroStats(equipment: EquipmentSet): HeroStats {
  const maxHp = HERO_BASE.hp + equipment.armor.level * EQUIPMENT_RULES.armorHpPerLevel;
  const attack = HERO_BASE.attack + equipment.weapon.level * EQUIPMENT_RULES.weaponAttackPerLevel;
  const defense = equipment.armor.level * EQUIPMENT_RULES.armorDefensePerLevel;
  const autoBonus = equipment.boots.level * EQUIPMENT_RULES.bootsAutoDamagePerLevel;
  const diceBonus = equipment.charm.level * EQUIPMENT_RULES.charmDiceBonusPerLevel;
  return {
    maxHp,
    attack,
    defense,
    autoBonus,
    diceBonus,
    power: Math.round(maxHp / 5 + attack * 6 + defense * 5 + autoBonus * 4 + diceBonus * 100)
  };
}

export function equipmentBonusLabel(item: EquipmentItem): string {
  switch (item.slot) {
    case 'weapon':
      return `Attack +${item.level * EQUIPMENT_RULES.weaponAttackPerLevel}`;
    case 'armor':
      return `HP +${item.level * EQUIPMENT_RULES.armorHpPerLevel}, Defense +${item.level}`;
    case 'charm':
      return `Dice bonus +${Math.round(item.level * EQUIPMENT_RULES.charmDiceBonusPerLevel * 100)}%`;
    case 'boots':
      return `Auto damage +${item.level * EQUIPMENT_RULES.bootsAutoDamagePerLevel}`;
  }
}

export function getEquipmentItems(equipment: EquipmentSet): EquipmentItem[] {
  return EQUIPMENT_ORDER.map((slot) => equipment[slot]);
}
