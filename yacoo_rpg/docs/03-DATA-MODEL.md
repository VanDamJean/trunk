# 03 — 데이터 모델 / 타입 명세

> 아래 타입은 **제안 명세**다. 필드명/구조는 이대로 가는 것을 권장하되, 합당한 이유가 있으면 조정 가능(단, Meta/Run 분리 원칙과 JSON 직렬화 가능성은 반드시 유지).
> 모든 타입은 `src/game/types.ts`에 둔다. 모든 저장 데이터는 **평면적이고 JSON 직렬화 가능**해야 한다(안드로이드 포팅 대비).

---

## 1. 현재 타입 (참고)

`src/game/types.ts` 현재 핵심:
```ts
export interface GameSave {
  stage: number;
  coins: number;
  equipment: EquipmentSet;
  lastResult?: CombatResult;
}
export type Screen = 'home' | 'combat' | 'equipment' | 'upgrade' | 'result';
export interface EquipmentItem { id: string; slot: EquipmentSlot; name: string; level: number; }
export type EquipmentSlot = 'weapon' | 'armor' | 'charm' | 'boots';
```
→ `GameSave`를 **`MetaSave` + `RunState`** 로 분리하는 것이 M1의 핵심.

---

## 2. 목표 타입 (제안)

### 2.1 영구 저장 — MetaSave
```ts
export interface MetaSave {
  version: 2;                 // 스키마 버전 (마이그레이션용)
  coins: number;              // 영구 재화 (장비 강화)
  equipment: EquipmentSet;    // 영구 장비 (기존 구조 유지)
  unlockedTraitIds: string[]; // 영구 해금한 특성 id
  unlockedWeaponIds: string[];// (선택) 영구 해금한 무기 id
  bestChapter: number;        // 최고 도달 챕터
  totalRuns: number;          // 누적 런 수 (통계/튜닝용)
  runInProgress?: RunState;   // 진행 중 런(이어하기). 없으면 메타 화면에서 새 런 시작.
}
```

### 2.2 런 상태 — RunState
```ts
export interface RunState {
  seed: number;               // 이 런의 RNG 시드 (재현/디버그)
  chapter: number;            // 현재 챕터
  map: ChapterMap;            // 현재 챕터 노드 맵
  nodeIndex: number;          // 현재 위치(클리어한 노드 수)
  hp: number;                 // 영웅 현재 HP (전투 사이 유지)
  maxHp: number;              // 이 런의 최대 HP (장비+특성 반영 스냅샷 또는 파생)
  diceCount: number;          // 2~5, 런 중 성장
  maxRolls: number;           // 리롤 횟수, 런 중 성장
  traits: OwnedTrait[];       // 이번 런 특성
  treasures: OwnedTreasure[]; // 이번 런 보물
  scrap: number;              // 런 내 임시 재화(보상/상점)
  pendingReward?: RewardChoice[]; // 보상 3택1 후보 (선택 대기)
}
```

### 2.3 챕터 / 노드
```ts
export type NodeType = 'battle' | 'elite' | 'treasure' | 'rest' | 'boss';

export interface MapNode {
  id: string;
  type: NodeType;
  cleared: boolean;
}

export interface ChapterMap {
  chapter: number;
  nodes: MapNode[];           // 단순 선형 시퀀스로 시작(후에 분기 확장 가능)
}
```

### 2.4 특성 / 보물
```ts
export type Rarity = 'common' | 'rare' | 'epic' | 'legendary';

export interface TraitDef {
  id: string;
  name: string;
  rarity: Rarity;
  description: string;
  // 효과는 데이터로 표현 (순수 함수 applyTraits에서 해석)
  effects: TraitEffect[];
}
export interface OwnedTrait {
  defId: string;
  level: number;              // 중복 획득 시 강화(선택)
}
export interface TraitEffect {
  kind:
    | 'startDice'             // 시작 주사위 수 +value
    | 'rerolls'               // 리롤 +value
    | 'damagePct'             // 전체 데미지 +value%
    | 'faceDamage'            // 특정 숫자(face) 데미지 +value
    | 'handMultiplier'        // 특정 족보 배율 +value
    | 'autoDamage'            // 평타 +value
    | 'maxHp'                 // 최대 HP +value
    | 'damageReductionPct';   // 받는 피해 -value%
  value: number;
  face?: DieValue;            // faceDamage용
  hand?: YahtzeeHand;         // handMultiplier용
}

export interface TreasureDef {
  id: string;
  name: string;
  rarity: Rarity;
  description: string;
  effects: TreasureEffect[];
}
export interface OwnedTreasure {
  defId: string;
  level: number;
}
export interface TreasureEffect {
  kind: 'handMultiplier' | 'faceFlatDamage' | 'flatDamage';
  value: number;
  hand?: YahtzeeHand;
  face?: DieValue;
}
```

### 2.5 보상 후보
```ts
export type RewardChoice =
  | { kind: 'trait'; defId: string }
  | { kind: 'treasure'; defId: string }
  | { kind: 'heal'; amount: number }
  | { kind: 'scrap'; amount: number }
  | { kind: 'dice'; amount: number }     // 즉시 diceCount +amount
  | { kind: 'reroll'; amount: number };  // 즉시 maxRolls +amount
```

### 2.6 무기 특화 (EquipmentItem 확장)
```ts
export interface EquipmentItem {
  id: string;
  slot: EquipmentSlot;
  name: string;
  level: number;
  specialtyHand?: YahtzeeHand; // 무기에만 사용. 일치 족보 사용 시 보너스 배율.
}
```

### 2.7 화면 추가
```ts
export type Screen =
  | 'home'        // 메타 허브
  | 'runMap'      // 챕터 노드 맵
  | 'combat'
  | 'rewardPick'  // 보상 3택1
  | 'equipment'
  | 'upgrade'
  | 'runResult'   // 런 종료 정산
  | 'result';     // (기존 전투 결과 — runResult로 통합하거나 유지)
```

---

## 3. 파생 스탯 계산 (순수 함수 시그니처 제안)

```ts
// 장비 + 특성 + 보물을 합산한 "유효 영웅 스탯"
getEffectiveStats(equipment: EquipmentSet, run: RunState): HeroStats

// 필살기 데미지 (02-GAME-DESIGN-SPEC §1.4 공식)
calculateUltimateDamage(args: {
  dice: DieValue[];
  hand: YahtzeeHand;
  equipment: EquipmentSet;
  run: RunState;
}): number
```
- 모든 보정(`traitDamageBonus`, `specialtyMultiplier`, treasure 항)은 이 함수들 안에서 합산한다.
- **부수효과 없이** 입력만으로 결과가 결정되어야 한다(테스트/포팅 용이).

---

## 4. 저장 스키마 & 마이그레이션

- 현재 저장 키: `yacoo-rpg-save-v1`, 값 = `GameSave`.
- 신규: **`MetaSave`(version: 2)** 로 저장. 키는 동일(`yacoo-rpg-save-v1`)을 재사용하되 내부 `version`으로 구분하거나, 새 키 `yacoo-rpg-save-v2`를 쓴다(구현자 선택, 단 일관성 유지).
- **마이그레이션 규칙(v1 → v2)**:
  - 기존 `coins`, `equipment` → `MetaSave.coins`, `MetaSave.equipment` 그대로 이전.
  - 기존 `stage` → `MetaSave.bestChapter`로 환산(예: `bestChapter = max(1, floor(stage / nodesPerChapter))`) 또는 단순 보존.
  - `lastResult`는 폐기(또는 무시).
  - `runInProgress`는 없음(undefined)으로 시작.
- `storage.ts`의 `isValidSave`를 **`isValidMetaSave`로 교체/확장**하고, v1 형태가 들어오면 마이그레이션 후 반환. 깨진 데이터는 기존처럼 기본값으로 폴백.
- **마이그레이션 단위 테스트 필수**: v1 샘플 → v2 변환 결과 검증, 손상 데이터 폴백 검증.

---

## 5. 불변식(invariants) — 테스트로 고정할 것

- `2 ≤ run.diceCount ≤ 5`, `run.maxRolls ≥ 1`.
- `0 ≤ run.hp ≤ run.maxHp`.
- 장비 레벨 `1 ≤ level ≤ EQUIPMENT_RULES.levelCap`.
- `MetaSave`/`RunState`는 **JSON.stringify → parse 후 동일**(직렬화 안정성).
- 데미지/스탯 계산 함수는 **동일 입력 → 동일 출력**(RNG 미사용 또는 주입된 RNG만 사용).
