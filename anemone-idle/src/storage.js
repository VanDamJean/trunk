import { MAX_OFFLINE_SECONDS, SAVE_KEY } from './config.js';
import { advanceGame, cloneState, createInitialState, sanitizeState } from './gameState.js';

export function saveGame(storage, state, now = Date.now(), key = SAVE_KEY) {
  const advanced = advanceGame(state, now);
  const payload = sanitizeState({
    ...cloneState(advanced),
    lastSavedAt: now,
    lastTickAt: now
  }, now);
  try {
    storage.setItem(key, JSON.stringify(payload));
  } catch {
    return payload;
  }
  return payload;
}

export function loadGame(storage, now = Date.now(), key = SAVE_KEY) {
  const empty = { state: createInitialState(now), offlineSeconds: 0, capped: false, loaded: false };
  try {
    const raw = storage.getItem(key);
    if (!raw) {
      return empty;
    }

    const parsed = sanitizeState(JSON.parse(raw), now);
    const elapsedSeconds = Math.max(0, (now - parsed.lastSavedAt) / 1000);
    const offlineSeconds = Math.min(elapsedSeconds, MAX_OFFLINE_SECONDS);
    const offlineBase = {
      ...parsed,
      lastTickAt: now - offlineSeconds * 1000
    };
    return {
      state: {
        ...advanceGame(offlineBase, now),
        lastSavedAt: now,
        notice: offlineSeconds > 0 ? 'noticeOffline' : parsed.notice,
        noticeArgs: offlineSeconds > 0 ? { minutes: Math.floor(offlineSeconds / 60) } : parsed.noticeArgs
      },
      offlineSeconds,
      capped: elapsedSeconds > MAX_OFFLINE_SECONDS,
      loaded: true
    };
  } catch {
    return empty;
  }
}

export function clearGame(storage, key = SAVE_KEY) {
  storage.removeItem(key);
}

export function createMemoryStorage(seed = {}) {
  const data = new Map(Object.entries(seed));
  return {
    getItem(key) {
      return data.has(key) ? data.get(key) : null;
    },
    setItem(key, value) {
      data.set(key, String(value));
    },
    removeItem(key) {
      data.delete(key);
    }
  };
}
