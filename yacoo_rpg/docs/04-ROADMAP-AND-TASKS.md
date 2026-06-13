# 04 — 로드맵 & 작업 체크리스트

> 마일스톤 **M1 → M5 순서대로** 진행한다. 각 마일스톤은 끝날 때 **빌드/테스트/QA 3종이 모두 통과**해야 한다.
> 체크박스는 작업 단위다. 한 항목을 끝내면 `[x]`로 바꾼다.
> 타입/공식은 `02`, `03` 문서를 근거로 한다. 코딩 규칙은 `05` 문서를 반드시 먼저 읽는다.

## 공통 완료 기준 (모든 마일스톤)
- [ ] `npm run build` 통과 (tsc -b 포함)
- [ ] `npm test -- --run` 통과 (기존 + 신규 테스트)
- [ ] `npm run qa` 통과 (playwright 브라우저 QA, 콘솔 에러 없음)
- [ ] `game/` 폴더 순수성 유지 (DOM/React/전역 Math.random/Date.now 직접 사용 금지)
- [ ] 새 순수 모듈마다 단위 테스트 추가

---

## M1 — 기반 재설계 (데이터 모델 분리 + 눈값 데미지)
**목표:** 전투 흐름은 그대로 두고, (1) `GameSave`를 `MetaSave`+`RunState`로 분리, (2) 필살기 데미지에 주사위 눈값 반영. 가장 안전한 첫 단계.

### 데이터 모델
- [ ] `types.ts`: `MetaSave`, `RunState`, `ChapterMap`, `MapNode`, `NodeType` 추가 (`03` 문서 기준)
- [ ] `types.ts`: `EquipmentItem`에 `specialtyHand?: YahtzeeHand` 필드 추가 (값 사용은 M3, 필드만 미리)
- [ ] `types.ts`: `Screen`에 `runMap`, `rewardPick`, `runResult` 추가
- [ ] `equipment.ts`: `createDefaultMeta(): MetaSave` 추가 (기존 `createDefaultSave` 대체/위임)
- [ ] `run.ts`(신규): `createRun(meta, rng): RunState` — 시작 HP/주사위/리롤 초기화

### 데미지 공식
- [ ] `combat.ts`: 눈값 합 반영한 `calculateUltimateDamage({ dice, hand, equipment, run })` 추가 (`02` §1.4, 특성/보물/특화 항은 중립값 1/0)
- [ ] `combat.ts`: 기존 `calculateSelectedCategoryDamage`를 신규 함수로 대체하거나 위임 (호출부 `CombatScreen` 갱신)
- [ ] 단위 테스트: 동일 족보라도 눈값에 따라 데미지가 달라짐을 검증

### 저장 / 마이그레이션
- [ ] `storage.ts`: `isValidMetaSave`, `loadMeta`, `saveMeta`, `resetMeta` 작성
- [ ] `storage.ts`: v1(`GameSave`) → v2(`MetaSave`) 마이그레이션 구현 (`03` §4)
- [ ] 단위 테스트: v1 샘플 마이그레이션, 손상 데이터 폴백

### 상태/연결
- [ ] `appState.ts`: meta/run 분리에 맞춰 상태 전이 함수 시그니처 정리(임시로 단일 노드 전투 유지 가능)
- [ ] `hooks/useGameState.ts`: `meta`/`run` 상태로 재구성, 저장 effect를 `saveMeta`로 변경
- [ ] `CombatScreen.tsx`: 주사위 개수/리롤을 `run`에서 받도록(이번 단계는 5/3 그대로 넣어도 됨), 데미지 표시를 신규 공식으로
- [ ] 기존 화면들이 새 상태 구조로 정상 렌더되는지 확인 (Home/Equipment/Upgrade/Result)

**M1 완료 기준:** 기존과 동일하게 플레이 가능 + 저장이 `MetaSave`로 저장됨 + 필살기 데미지가 눈값에 반응함.

---

## M1.5 — 전투 모델 전환 (오토배틀 → 수동 턴제)
**목표:** 시간 기반 오토배틀을 폐기하고 순수 수동 턴제로 교체. 평타는 매 턴 선택지로만 유지.
**사양:** 반드시 `06-COMBAT-MANUAL-TURN-SPEC.md`를 먼저 읽고 그대로 구현할 것.

- [ ] `constants.ts`: `COMBAT_TIMING`에서 `heroAttackMs`/`enemyAttackMs`/`diceChargeMs`/`maxDiceCharges` 제거, `enemyTurnDelayMs`(기본 500, 테스트 0) 추가, `maxRolls`/`diceCount` 유지
- [ ] `CombatScreen.tsx`: 타이머 3개 `useEffect`와 `diceReady`/`diceOpen`/`openDice`/`closeDice` 제거
- [ ] `CombatScreen.tsx`: `phase`('choosing'|'enemyTurn'|'over') 상태머신 + `startTurn`/`reroll`/`diceAttack`/`basicAttack`/`enemyTurn` 구현 (`06` §3)
- [ ] `basicAttack`: `calculateHeroAutoDamage` 고정 데미지 연결
- [ ] 족보 버튼에 예상 데미지 표기(권장), 주사위 항상 표시
- [ ] `App.test.tsx`: 전투 관련 테스트를 턴제 흐름으로 재작성
- [ ] `scripts/qa.mjs`: "Open Dice"/"Force Dice Charge" 단계 제거, 턴제 흐름으로 갱신
- [ ] `combat.ts`: 적 공격이 "턴당 1회"로 바뀐 것에 맞춰 `createEnemy` 밸런스 1차 조정

**M1.5 완료 기준:** 타이머 없이 턴제로 한 전투를 끝까지 진행 가능 + 빌드/테스트/QA 3종 통과.

---

## M1.6 — 비주얼 & UX 폴리시 (껍데기 / 필드·주사위 연출 / 홈 / 장비 그래픽)
**목표:** 로직은 그대로 두고 "요즘 모바일 게임처럼" 보이고 만지게 만든다. 4개 영역: (A)셸 현대화 (B)필드 진행+주사위 던지기 연출 (C)홈 한 화면 (D)장비 페이퍼돌.
**사양:** 반드시 `07-VISUAL-AND-UX-POLISH.md`를 먼저 읽고 그대로 구현할 것. `game/` 폴더는 건드리지 않는다.
**권장 순서:** C → A → D → B.

- [ ] A. 셸: 상단 HUD 알약화, 하단 아이콘 독, 프레임/전환 마이크로 인터랙션 (`ui.tsx`, `GameArt.tsx`, `styles.css`)
- [ ] B1. 필드 진행: 패럴랙스 스크롤 배경 + 전진/처치/등장 연출(더미 진행 허용, M2 데이터 대비)
- [ ] B2. 주사위 던지기: throw-in → 통통 → settle 3단계 연출, 핍/3D 면, 착지 이펙트. **테스트 즉시정착(`VITEST` → `ROLL_ANIM_MS=0`) 유지**
- [ ] C. 홈 한 화면: 375×812 무스크롤 플렉스 레이아웃, 영웅 주역화 + CTA 엄지존
- [ ] D. 장비 페이퍼돌: `HeroPaperdoll`로 슬롯별 파츠 그래픽 장착, 장비/홈 화면 연계
- [ ] 접근성 라벨/텍스트(§6 목록) 유지 또는 변경 시 테스트·`qa.mjs` 동반 갱신
- [ ] `prefers-reduced-motion` 존중

**M1.6 완료 기준:** 첫인상이 "모바일 게임"으로 바뀌고 홈/전투가 무스크롤. 빌드/테스트/QA 3종 통과.

---

## M2 — 로그라이크 런 루프 (챕터/노드/사망/정산 + 보상 3택1)
**목표:** "런" 레이어를 실제로 돌게 만든다. 챕터 맵을 돌고, 죽으면 런이 끝나고 메타로 정산.

### 챕터 / 노드
- [ ] `chapter.ts`(신규): `createChapterMap(chapter, rng): ChapterMap` (선형 시퀀스 + 마지막 boss)
- [ ] `combat.ts`: `createEnemy(chapter, nodeType, rng)`로 확장 (HP/공격 스케일링; elite/boss 가중)
- [ ] 단위 테스트: 맵 생성 결정성(같은 seed→같은 맵), 노드 구성 검증

### 런 진행 / 사망 / 정산
- [ ] `run.ts`: `advanceNode(run)`, `applyCombatResult(run, outcome)`(HP 반영), `isRunOver(run)`
- [ ] `meta.ts`(신규): `settleRun(meta, run): MetaSave` — 런 성과 → 코인/해금/최고기록 환산
- [ ] `run.ts`: 챕터 클리어 시 다음 챕터 진입(`nextChapter(run, rng)`)
- [ ] 단위 테스트: 사망/클리어/정산 경로

### 화면
- [ ] `screens/RunMapScreen.tsx`(신규): 현재 챕터 노드 표시, 다음 노드 진입
- [ ] `screens/RunResultScreen.tsx`(신규): 런 종료 정산 결과 + Home 복귀
- [ ] `HomeScreen.tsx`: "Start Run"(런 시작) 및 진행 중 런 "Continue" 분기
- [ ] `App.tsx`: `runMap`/`runResult`/`rewardPick` 라우팅 추가
- [ ] `useGameState.ts`: 런 시작/노드 진입/전투 종료/정산 액션 연결

### 보상 3택1
- [ ] `rewards.ts`: `rollRewardChoices(run, meta, rng): RewardChoice[]` (3개, 가중치)
- [ ] `screens/RewardPickScreen.tsx`(신규): 후보 3개 표시, 선택 시 `run` 반영
- [ ] 단위 테스트: 후보 생성 결정성/가중치, 선택 적용 결과

**M2 완료 기준:** Home → 런 시작 → 노드 전투 → 보상 선택 → … → 보스/사망 → 정산 → Home 의 전체 루프가 동작.

---

## M3 — 콘텐츠 시스템 (특성 / 보물 / 무기 특화 / 주사위 성장)
**목표:** 딸깍 다이스의 "빌드 쌓는 맛". 데미지 공식의 중립 항들을 실제 시스템으로 채운다.

### 특성(Trait)
- [ ] `traits.ts`(신규): `TRAIT_POOL: TraitDef[]` (등급별 다수), `applyTraits(base, traits)` 순수 함수
- [ ] 데미지/스탯 계산에 `traitDamageBonus`, `startDice`, `rerolls`, `maxHp` 등 반영
- [ ] 메타 해금 연동(`unlockedTraitIds` → 런 풀 필터)
- [ ] 단위 테스트: 효과 스택, 해금 필터

### 보물(Treasure)
- [ ] `treasures.ts`(신규): `TREASURE_POOL: TreasureDef[]`, 데미지 공식에 `treasureHandMultiplier`/`flat`/`face` 합류
- [ ] 단위 테스트: 보물 효과가 데미지에 정확히 반영

### 무기 특화
- [ ] `constants.ts`: 무기 5종 정의 + 각 `specialtyHand`
- [ ] `combat.ts`: 공격 족보 == 무기 특화 시 `specialtyMultiplier` 적용
- [ ] (선택) 무기 선택/해금 UI 또는 메타 연동
- [ ] 단위 테스트: 특화 일치/불일치 데미지 차이

### 주사위 개수/리롤 성장
- [ ] `constants.ts`: `diceCount` 고정 상수 제거 → `run.diceCount` 사용으로 전환
- [ ] `yahtzee.ts`: 가변 주사위 개수(2~5) 족보 판정 일반화 (`02` §1.5)
- [ ] `CombatScreen.tsx`: 주사위 그리드를 `run.diceCount`로 렌더, 리롤 한도 `run.maxRolls`
- [ ] 보상/특성으로 `diceCount`/`maxRolls` 증가가 실제 반영되는지 확인
- [ ] 단위 테스트: 주사위 2/3/4/5개 각각의 족보 판정 고정

**M3 완료 기준:** 런 중 특성·보물을 모아 빌드가 강해지고, 무기 특화/주사위 성장이 데미지에 반영됨.

---

## M4 — 완성도 (연출 / 콘텐츠 / 밸런스 / 한 손 UX)
**목표:** "게임처럼" 느껴지게.

- [ ] 턴 전환 연출(내 턴/적 턴 표시) + 공격 발동 연출
- [ ] 주사위 굴림/홀드/족보 성립 애니메이션
- [ ] 데미지 팝업 강화(`DamagePop` 활용), 승리/사망 연출
- [ ] 사운드/햅틱: 플랫폼 추상화 인터페이스 통해 연결 (`05` 참고). 웹은 Vibration/Audio
- [ ] 적/보스/챕터 콘텐츠 확장(이름, 스탯 곡선, 보스 패턴 등)
- [ ] 밸런스 패스: 데미지/HP/보상/강화 비용 튜닝 (수치는 `constants.ts` 집중)
- [ ] 한 손 UX 정리: 하단 엄지존 배치, 터치 타겟 ≥44px, 세로 375×812 점검
- [ ] (선택) 간단한 튜토리얼/온보딩

**M4 완료 기준:** 처음 보는 사람이 설명 없이 한 손으로 한 런을 끝까지 즐길 수 있음.

---

## M5 — 안드로이드 포팅
**목표:** Kotlin + Jetpack Compose 포팅(워크스페이스의 다른 `*-android` 프로젝트 패턴).

- [ ] `game/` 순수 로직을 Kotlin으로 1:1 포팅 (타입/공식/RNG 주입 그대로 → "사양서"로 사용)
- [ ] 저장: `MetaSave` JSON ↔ DataStore/SharedPreferences 매핑
- [ ] Compose UI: 5~8개 화면을 Compose로 재구성 (세로 375dp 기준 스케일)
- [ ] 사운드/햅틱 네이티브 구현
- [ ] 단위 테스트 포팅(데미지/족보/마이그레이션 등 핵심 로직)

**M5 완료 기준:** 안드로이드에서 웹과 동일한 코어 루프가 동작.

---

## 진행 추적 표

| 마일스톤 | 상태 | 핵심 산출물 |
|---|---|---|
| M1 기반 재설계 | ☑ | Meta/Run 분리, 눈값 데미지, 마이그레이션 (완료) |
| M1.5 전투 전환 | ☑ | 오토배틀 폐기 → 수동 턴제 (`06` 문서) (완료) |
| M1.6 비주얼/UX | ☐ | 셸/필드·주사위 연출/홈/장비 그래픽 (`07` 문서) |
| M2 런 루프 | ☐ | 챕터/노드/사망/정산, 보상 3택1 |
| M3 콘텐츠 | ☐ | 특성/보물/무기특화/주사위 성장 |
| M4 완성도 | ☐ | 연출/사운드/밸런스/한손 UX |
| M5 안드로이드 | ☐ | Kotlin/Compose 포팅 |
