import { EQUIPMENT_ORDER, EQUIPMENT_RULES } from './constants';
import type { EquipmentSet, RewardResult, Rng } from './types';

export function createWinReward(stage: number, equipment: EquipmentSet, rng: Rng = Math.random): RewardResult {
  const baseCoins = 30 + 10 * stage;
  if (rng() >= EQUIPMENT_RULES.duplicateChance) {
    return { coinsEarned: baseCoins };
  }

  const slotIndex = Math.min(EQUIPMENT_ORDER.length - 1, Math.floor(rng() * EQUIPMENT_ORDER.length));
  const item = equipment[EQUIPMENT_ORDER[slotIndex]];
  return {
    coinsEarned: baseCoins + EQUIPMENT_RULES.duplicateCoinValue,
    duplicateItemName: item.name
  };
}

export function createLossReward(): RewardResult {
  return { coinsEarned: 10 };
}
