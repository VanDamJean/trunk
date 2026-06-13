import { STORAGE_KEY } from './constants';
import { createDefaultMeta, createDefaultSave } from './equipment';
import type { EquipmentItem, EquipmentSlot, GameSave, MetaSave } from './types';

const slots: EquipmentSlot[] = ['weapon', 'armor', 'charm', 'boots'];

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null;
}

function isValidEquipmentItem(item: unknown, slot: EquipmentSlot): item is EquipmentItem {
  return (
    isRecord(item) &&
    item.slot === slot &&
    typeof item.id === 'string' &&
    typeof item.name === 'string' &&
    typeof item.level === 'number' &&
    item.level >= 1
  );
}

function isValidEquipmentSet(equipment: unknown): boolean {
  if (!isRecord(equipment)) return false;
  return slots.every((slot) => isValidEquipmentItem(equipment[slot], slot));
}

// ── v1 (GameSave) validator — kept for migration ──────────────────────

export function isValidSave(value: unknown): value is GameSave {
  if (!isRecord(value) || typeof value.stage !== 'number' || typeof value.coins !== 'number') {
    return false;
  }
  return isValidEquipmentSet(value.equipment);
}

// ── v2 (MetaSave) validator ───────────────────────────────────────────

export function isValidMetaSave(value: unknown): value is MetaSave {
  if (
    !isRecord(value) ||
    value.version !== 2 ||
    typeof value.coins !== 'number' ||
    typeof value.bestChapter !== 'number' ||
    typeof value.totalRuns !== 'number'
  ) {
    return false;
  }
  return isValidEquipmentSet(value.equipment);
}

// ── Migration v1 → v2 ────────────────────────────────────────────────

export function migrateV1ToV2(v1: GameSave): MetaSave {
  return {
    version: 2,
    coins: v1.coins,
    equipment: v1.equipment,
    bestChapter: Math.max(1, Math.ceil(v1.stage / 6)),
    totalRuns: 0
  };
}

// ── Meta load / save / reset ─────────────────────────────────────────

export function loadMeta(storage: Storage = window.localStorage): MetaSave {
  const raw = storage.getItem(STORAGE_KEY);
  if (!raw) return createDefaultMeta();
  try {
    const parsed: unknown = JSON.parse(raw);
    if (isValidMetaSave(parsed)) return parsed;
    // Attempt v1 migration
    if (isValidSave(parsed)) return migrateV1ToV2(parsed);
    return createDefaultMeta();
  } catch {
    return createDefaultMeta();
  }
}

export function saveMeta(meta: MetaSave, storage: Storage = window.localStorage): void {
  storage.setItem(STORAGE_KEY, JSON.stringify(meta));
}

export function resetMeta(storage: Storage = window.localStorage): MetaSave {
  storage.removeItem(STORAGE_KEY);
  return createDefaultMeta();
}

// ── Legacy shims (kept so existing tests don't break) ─────────────────

export function loadSave(storage: Storage = window.localStorage): GameSave {
  const raw = storage.getItem(STORAGE_KEY);
  if (!raw) return createDefaultSave();
  try {
    const parsed: unknown = JSON.parse(raw);
    return isValidSave(parsed) ? parsed : createDefaultSave();
  } catch {
    return createDefaultSave();
  }
}

export function saveGame(save: GameSave, storage: Storage = window.localStorage): void {
  storage.setItem(STORAGE_KEY, JSON.stringify(save));
}

export function resetSave(storage: Storage = window.localStorage): GameSave {
  storage.removeItem(STORAGE_KEY);
  return createDefaultSave();
}
