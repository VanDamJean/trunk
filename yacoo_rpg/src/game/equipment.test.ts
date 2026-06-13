import { describe, expect, it } from 'vitest';
import { EQUIPMENT_RULES } from './constants';
import { canUpgrade, createDefaultSave, getHeroStats, upgradeCost, upgradeEquipment } from './equipment';

describe('equipment progression', () => {
  it('upgrades with exact cost and increases level', () => {
    const save = { ...createDefaultSave(), coins: 25 };
    const next = upgradeEquipment(save, 'weapon');
    expect(next.equipment.weapon.level).toBe(2);
    expect(next.coins).toBe(0);
  });

  it('rejects upgrades with insufficient coins', () => {
    const save = createDefaultSave();
    const next = upgradeEquipment(save, 'weapon');
    expect(next).toBe(save);
    expect(canUpgrade(save.equipment.weapon, save.coins)).toBe(false);
  });

  it('blocks upgrades at level cap', () => {
    const save = createDefaultSave();
    const capped = { ...save.equipment.weapon, level: EQUIPMENT_RULES.levelCap };
    expect(canUpgrade(capped, 9999)).toBe(false);
    expect(upgradeCost(save.equipment.weapon)).toBe(25);
  });

  it('derives hero stats from all gear slots', () => {
    const stats = getHeroStats(createDefaultSave().equipment);
    expect(stats.attack).toBe(13);
    expect(stats.maxHp).toBe(135);
    expect(stats.diceBonus).toBe(0.05);
  });
});
