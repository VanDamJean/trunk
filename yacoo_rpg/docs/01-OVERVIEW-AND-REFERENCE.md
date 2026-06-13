# 01 — 개요 · 레퍼런스 · 현재 상태 · 갭 분석

## 1. 비전

기본 야추(Yahtzee) 주사위 룰(주사위 5개, 굴리기, 홀드, 포커식 족보)을 **전투의 핵심 메커니즘**으로 삼아,
**하비(Habby) "딸깍 다이스"** 스타일의 가볍고 중독성 있는 모바일 RPG를 만든다.

핵심 감성:
- 한 손으로, 세로 화면에서, 짧게 즐기는 캐주얼 RPG
- "주사위를 굴려 좋은 족보를 만들어 적을 때리는" 손맛
- 로그라이크 런(run)의 짜릿함 + 영구 성장의 누적감

## 2. 레퍼런스: 딸깍 다이스(Habby) 핵심 메커니즘

> 메커니즘만 참고한다. 자산/코드 복사 금지.

- 최대 **5개의 주사위**를 굴려 **포커 족보**(원페어, 투페어, 트리플, 스트레이트, 풀하우스, 포카인드, 야추)를 만들어 몬스터를 공격.
- **턴제 + 로그라이크 + RPG** 혼합. 가볍게 플레이 가능.
- 주사위는 처음엔 일부만 열려 있고, **캐릭터 레벨업으로 최대 5개까지 확장**.
- 매 턴 전체 주사위가 새로 굴러가고, **보유한 리롤(리셋) 횟수만큼** 주사위를 다시 굴릴 수 있음.
- **높은 숫자가 유리**하고, **숫자 조합(족보)에 따라 보너스 배율** 적용. → 즉 데미지에 "주사위 눈값"과 "족보 배율"이 함께 작용.
- **무기 다수(약 5종)**, 각 무기는 **특정 족보/플레이 스타일에 최적화**.
- **특성(패시브 카드)**: 일반-희귀-에픽-전설 등급. 시작 주사위 수 증가, 특정 숫자/조합 공격력 증가 등. 강화 가능.
- **보물**: 각 조합의 공격력 배율, 특정 숫자의 공격력을 올려줌. 강화 가능.
- 2D 픽셀 아트, 세로 화면, 한 손 플레이.

## 3. 목표 게임 루프

```
[메타 / 런 밖]
  Home → 영구 장비 강화 · 특성 해금 · (무기 선택) → "런 시작"
        ↑                                                  │
        │ 메타 재화 정산(런 성과 → 코인/해금)              ▼
[런 / 로그라이크]                                    챕터 맵 진입
  챕터 맵의 노드를 따라 이동
    └ 노드 = 전투(하이브리드: 오토배틀 평타 + 주사위 필살기)
         └ 승리 → 보상 3택1 (특성 / 보물 / 회복·재화)
              └ 다음 노드 … → 보스 노드
                   ├ 보스 클리어 → 다음(더 깊은) 챕터
                   └ HP 0(사망) → 런 종료 → 메타 정산 → Home
```

- **런 내부 = 로그라이크**: HP는 전투 사이에 유지되고, 특성·보물은 이번 런에만 누적, 사망하면 전부 폐기.
- **런 외부 = 영구 메타**: 코인, 장비 레벨, 해금한 특성/무기, 최고 기록은 계속 남는다.
- 메타 성장이 다음 런을 더 강하게 만들어 "한 번 더" 동기를 만든다.

## 4. 현재 상태 (yacoo_rpg, 검증 완료)

### 4.1 기술 스택 / 구조
- React 19 + Vite 7 + TypeScript 5.9, npm, vitest + playwright + @testing-library.
- `index.html` → `src/main.tsx` → `src/App.tsx`.
- 화면 5개(`Screen` 타입): `home`, `combat`, `equipment`, `upgrade`, `result`.

### 4.2 폴더 구조
```
src/
  App.tsx                  # 화면 라우팅(상태 기반 조건 렌더), 상단 스탯
  main.tsx
  styles.css
  game/                    # ★ 순수 TS 로직 (부수효과 없음) — 안드로이드 포팅의 핵심
    types.ts               # 모든 타입
    constants.ts           # 밸런스 상수, 족보 정의, 시작 장비
    yahtzee.ts             # 주사위 굴림, 족보 판정/평가
    combat.ts              # 적 생성, 데미지 계산
    equipment.ts           # 장비/영웅 스탯, 강화 비용, 기본 세이브
    rewards.ts             # 승/패 보상
    appState.ts            # 전투 종료/보상 수령/강화 상태 전이
    storage.ts             # localStorage 로드/저장/검증/리셋
  hooks/
    useGameState.ts        # React 상태 + game/ 로직 연결
  screens/                 # 5개 화면 컴포넌트
    HomeScreen.tsx, CombatScreen.tsx, EquipmentScreen.tsx,
    UpgradeScreen.tsx, ResultScreen.tsx
  components/
    ui.tsx                 # Shell, BottomNav, Card, PrimaryButton, ProgressBar, Dice, ...
    GameArt.tsx            # RoundAnimalHero, ForestMonster, EquipmentBadge, DamagePop, ...
  test/setup.ts
scripts/qa.mjs             # playwright 기반 브라우저 QA
docs/                      # ← 이 핸드오프 문서
```

### 4.3 이미 구현된 것
- **주사위**: 5개, 최대 3롤, 홀드, 족보 9종 판정/평가(`yahtzee.ts`).
- **족보 배율**(`constants.ts` `HANDS` / `ATTACK_CATEGORIES`): chance 1.0 / pair 1.2 / twoPair 1.5 / threeKind 1.8 / smallStraight 2.1 / fullHouse 2.5 / fourKind 3.2 / largeStraight 3.8 / yahtzee 6.0.
- **전투**(`CombatScreen.tsx`): 오토배틀(영웅 1000ms마다 평타, 적 1200ms마다 공격) + 8000ms마다 "주사위 충전" → Open Dice → 굴려서 족보 선택 → 강공격.
- **영웅/장비 스탯**(`equipment.ts`): 기본 HP 120, 공격 10. 장비 4종(무기/방어구/부적/신발), 레벨업(cap 10, 비용 `25 × 현재레벨`).
- **적**(`combat.ts`): `maxHp = 80 + 30×(stage-1)`, `attack = 6 + 2×(stage-1)`, 5스테이지마다 보스.
- **보상**(`rewards.ts`): 승리 `30 + 10×stage` 코인, 30% 확률 중복 아이템(+20 코인). 패배 10 코인.
- **진행/저장**: 승리 시 `stage + 1` 영구 진행. localStorage 키 `yacoo-rpg-save-v1`. `GameSave { stage, coins, equipment, lastResult }`.
- **테스트**: `combat / equipment / rewards / storage / yahtzee / App` 단위 테스트 + playwright QA.

### 4.4 재사용 가능한 UI 컴포넌트 인벤토리
새 화면을 만들 때 새로 그리지 말고 아래를 우선 재사용한다.

- `ui.tsx`: `Shell`, `TopStatsBar`, `BottomNav`, `Card`, `ArtCard`, `PrimaryButton`, `StatPill`, `ProgressBar`, `Dice`, `RewardBadge`, `ChunkyBadge`
- `GameArt.tsx`: `RoundAnimalHero`, `ForestMonster`, `LayeredArena`, `CoinBadge`, `EquipmentBadge`, `RedDotMarker`, `DamagePop`, `RewardBurst`, `ArtStack`

## 5. 갭 분석 (현재 → 딸깍 다이스)

| 요소 | 현재 yacoo_rpg | 목표(딸깍 다이스화) | 작업 필요 |
|---|---|---|---|
| 주사위 5개 + 홀드 + 3롤 | O | O | 유지 |
| 족보 9종 → 배율 | O | O | 유지 (보정 추가) |
| **데미지에 주사위 눈값 반영** | ✗ (`attack × 배율`만) | 눈값 합 + 족보 배율 | **필수 (M1)** |
| **Meta/Run 데이터 분리** | ✗ (단일 `GameSave`) | `MetaSave` + `RunState` | **필수 (M1)** |
| **로그라이크 런 루프** | ✗ (stage 영구 증가만) | 챕터/노드/사망/정산 | **필수 (M2)** |
| **보상 3택1** | ✗ | 특성/보물/회복 선택 | **필수 (M2)** |
| **특성(패시브 카드)** | ✗ | 등급별 풀 + 효과 스택 | **필수 (M3)** |
| **보물** | ✗ | 조합 배율↑/숫자 데미지↑ | **필수 (M3)** |
| **무기별 족보 특화** | ✗ (공격력만) | `specialtyHand` 보너스 | **필수 (M3)** |
| **주사위 개수/리롤 성장** | ✗ (5개·3롤 고정) | 런 중 2~5개로 확장 | **필수 (M3)** |
| 필살기 게이지 시각화/연출 | 부분(텍스트) | 게이지·애니·사운드·햅틱 | M4 |
| 콘텐츠(적/보스/챕터) 다양성 | 최소 | 확장 | M4 |
| 안드로이드 포팅 | ✗ | Kotlin/Compose | M5 |

자세한 작업 분해는 `04-ROADMAP-AND-TASKS.md` 참고.
