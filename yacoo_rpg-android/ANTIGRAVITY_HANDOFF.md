# Yacoo RPG — Antigravity 작업 핸드오프

> 이 문서를 Antigravity에 그대로 던지면 됩니다.
> 맨 아래 "Antigravity용 프롬프트" 섹션을 복사해서 사용하세요.

---

## 1. 프로젝트 개요

**Yacoo RPG** — Kotlin + Jetpack Compose 안드로이드 캐주얼 RPG (주사위 야츠제 전투 시스템)

- 패키지: `com.yacoo.rpg`
- 타겟: 안드로이드 minSdk 26, targetSdk 35
- 빌드: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug`
- 언어: 한국어/영어 토글 지원 (반드시 보존)
- 게임 로직 변경 금지, 네비게이션 변경 금지, safe-area 처리 보존

---

## 2. Antigravity가 읽어야 할 파일 (우선순위 순)

### 필수 (먼저 읽기)

| 순서 | 파일 | 이유 |
|---|---|---|
| 1 | `ART_DIRECTION_OVERHAUL_BRIEF.md` | 아트 방향성 최종 목표. 스크린별 비주얼 요구사항 정의 |
| 2 | `IMPLEMENTATION_OVERHAUL_BRIEF.md` | 구현 전략. Phase 1-7로 나뉜 코딩 작업 지시서 |
| 3 | `app/src/main/java/com/yacoo/rpg/ui/theme/Color.kt` | 전체 색상 토큰. 모든 색상 참조의 출처 |
| 4 | `app/src/main/java/com/yacoo/rpg/ui/theme/Type.kt` | 타이포그래피 토큰 |
| 5 | `app/src/main/java/com/yacoo/rpg/ui/components/Shell.kt` | 메인 레이아웃 셸. Top HUD, Bottom Nav, 배경 통합 |
| 6 | `app/src/main/java/com/yacoo/rpg/ui/components/GameComponents.kt` | 공유 컴포넌트 키트 (버튼, 카드, 칩, 배지 등) |
| 7 | `app/src/main/java/com/yacoo/rpg/ui/components/GameArt.kt` | 게임 아트 렌더링 (히어로, 몬스터, 아레나 배경, 페이퍼돌) |
| 8 | `app/src/main/java/com/yacoo/rpg/ui/theme/GameIcons.kt` | 아이콘 추상화 시스템. PNG 우선 로드, emoji 폴백 |
| 9 | `app/src/main/java/com/yacoo/rpg/ui/components/DiceView.kt` | 주사위 컴포넌트 |

### 화면별 (해당 화면 작업 시 읽기)

| 파일 | 화면 |
|---|---|
| `app/src/main/java/com/yacoo/rpg/ui/screens/HomeScreen.kt` | 홈/모험 |
| `app/src/main/java/com/yacoo/rpg/ui/screens/CombatScreen.kt` | 전투 |
| `app/src/main/java/com/yacoo/rpg/ui/screens/EquipmentScreen.kt` | 장비 인벤토리 |
| `app/src/main/java/com/yacoo/rpg/ui/screens/UpgradeScreen.kt` | 강화 |
| `app/src/main/java/com/yacoo/rpg/ui/screens/GachaScreen.kt` | 뽑기/가챠 |
| `app/src/main/java/com/yacoo/rpg/ui/screens/ResultScreen.kt` | 전투 결과 |
| `app/src/main/java/com/yacoo/rpg/ui/screens/RunMapScreen.kt` | 런 맵 |
| `app/src/main/java/com/yacoo/rpg/ui/screens/RewardPickScreen.kt` | 보상 선택 |
| `app/src/main/java/com/yacoo/rpg/ui/screens/RunResultScreen.kt` | 런 결과 |

### 게임 로직 (읽기만, 수정 금지)

| 파일 | 내용 |
|---|---|
| `game/Types.kt` | 야츠제 공격 카테고리 |
| `game/EquipmentModels.kt` | 장비 데이터 모델 (WEAPON, ARMOR, CHARM, BOOTS) |
| `game/RunModels.kt` | 런 노드 타입 (BATTLE, ELITE, TREASURE, REST, BOSS), 보상 종류 (HEAL, SCRAP, DICE, REROLL) |
| `game/Constants.kt` | 게임 상수 (히어로 스탯, 장비 룰, 시작 장비) |
| `navigation/NavGraph.kt` | 네비게이션 그래프, 언어/화면 연결 |

---

## 3. 현재 구현 상태 (이미 완료된 것)

### 아트 에셋 (PNG) — 이미 존재함

`app/src/main/res/drawable/` 폴더에 다음 에셋이 있음:

**캐릭터:**
- `hero_body.png` — 히어로 (치비 베어 캐릭터)
- `monster_slime.png` — 일반 전투 몬스터
- `monster_elite.png` — 엘리트 몬스터
- `monster_beholder.png` — 보스 몬스터

**장비 아이콘:**
- `icon_weapon.png`, `icon_armor.png`, `icon_charm.png`, `icon_boots.png`

**네비게이션 아이콘:**
- `icon_nav_home.png`, `icon_nav_battle.png`, `icon_nav_gear.png`, `icon_nav_upgrade.png`, `icon_nav_draw.png`

**리소스 아이콘:**
- `icon_gold_3d.png`, `icon_gem_3d.png`, `icon_energy_3d.png`, `icon_power_3d.png`

**배경/이펙트:**
- `bg_home_adventure.png`, `bg_combat_arena.png`, `bg_map_parchment.png`
- `chest_treasure.png`, `effect_sunburst.png`, `banner_ribbon_gold.png`

**폰트:**
- `quicksand_bold.ttf`, `quicksand_semibold.ttf`, `quicksand_regular.ttf`

### UI 컴포넌트 — 이미 구현됨

- `Shell.kt`: Floating HUD pill badges, 튀어나온 중심 CTA 버튼, floating bottom nav
- `GameComponents.kt`: 3D 탕틸 버튼, 카툰 카드, 래리티 배지, 풍선형 체력바, 선버스트 이펙트
- `GameArt.kt`: PNG 기반 히어로/몬스터 렌더링, Canvas 아레나 배경, 레이어드 페이퍼돌
- `GameIcons.kt`: PNG 우선 / emoji 폴백 아이콘 시스템
- `DiceView.kt`: 애니메이션 3D 카툰 주사위
- `AdventureBackground`: 풀스크린 하늘+구름+태양+그라운드 배경

---

## 4. 남은 작업 (Antigravity가 해야 할 것)

### 작업 A: 이모지 폴백 아이콘 PNG 교체

`GameIcons.kt`의 `GameIconRole` enum에서 아직 PNG가 없는 것들:

| Role | 현재 폴백 이모지 | 필요한 PNG |
|---|---|---|
| `SCRAP` | 🔩 | 고철/스크랩 아이콘 |
| `ATTACK` | ⚔️ | 공격 아이콘 (전투 UI용, nav 아이콘과 별개) |
| `DEFEND` | 🛡 | 방어 아이콘 |
| `DICE` | 🎲 | 주사위 아이콘 (nav용과 별개) |
| `HEAL` | ❤️ | 힐/체력 아이콘 |
| `TREASURE` | 📦 | 보물 상자 (이미 `chest_treasure.png` 사용 중이나 확인 필요) |
| `REWARD` | 🎁 | 보상 선물 아이콘 |
| `SETTINGS` | ⚙️ | 설정 톱니바퀴 |
| `CLOSE` | ✕ | 닫기 X |
| `BACK` | ← | 뒤로가기 화살표 |
| `RESET` | 🔄 | 리셋 아이콘 |
| `CONFIRM` | ✓ | 확인 체크 |
| `DEFEAT` | 💀 | 패배 해골 |
| `LOCK` | 🔒 | 자물쇠 |
| `STAR` | ⭐ | 별 |
| `ARROW_UP` | ⬆ | 상향 화살표 (강화 표시용) |

**요구사항:**
- 기존 에셋 스타일과 통일: 두꺼운 카툰 아웃라인, 스티커 스타일, 투명 배경
- 배경 완전 투명 (alpha=0)
- `GameIcons.kt`의 `GameIcon()` 함수에 `R.drawable.xxx` 매핑 추가
- 해상도: 256x256px 이상, 필요시 512x512px

### 작업 B: 화면별 비주얼 폴리시 (ART_DIRECTION_OVERHAUL_BRIEF 기준)

각 화면을 `ART_DIRECTION_OVERHAUL_BRIEF.md`의 타겟 비전과 대조하여 부족한 점 보완:

**B1. HomeScreen** — 히어로가 배경 그라운드 위에 서 있는지, 거대한 START 버튼이 bottom nav 위에 떠 있는지 확인. 좌측/우측 플로팅 사이드 아이콘(퀘스트, 상점 자리) 추가 여부.

**B2. CombatScreen** — 주사위 패널이 둥근 플로팅 보드인지, HP 바가 파일형으로 캐릭터 아래에 떠 있는지, 데미지 숫자가 크고 바운스하는지 확인.

**B3. EquipmentScreen** — 다크 오버레이 위에 크림/프로스티드 패널이 떠 있는지, 페이퍼돌 렌더링이 깨끗한지 (흰 박스 잔상 없는지), 장비 슬롯 카드의 레벨 배지가 모서리에 겹쳐 있는지 확인.

**B4. UpgradeScreen** — 글로우 화살표, 선버스트 성공 이펙트, 골드 부족/맥스레벨 상태의 시각적 구분 확인.

**B5. GachaScreen** — 중앙의 글로우 트레져 체스트, 거대한 바운스 뽑기 버튼 확인.

**B6. ResultScreen / RunResultScreen** — 승리 배너, 보상 패널, CTA 버튼의 시각적 위계 확인.

**B7. RunMapScreen** — 노드 타입별 아이콘 구분 (BATTLE, ELITE, TREASURE, REST, BOSS), 진행 커넥터 라인 스타일 확인.

**B8. RewardPickScreen** — 보상 카드 (HEAL, SCRAP, DICE, REROLL) 아이콘 확인.

### 작업 C: 하드코딩된 색상/크기 정리

`Shell.kt`와 일부 화면에 아직 하드코딩된 `Color(0x...)`, `.dp`, `.sp`가 남아있음. 이것들을 `Color.kt`의 토큰으로 교체:

```
검색: Color(0x  →  Color.kt 토큰 사용
검색: [0-9]+\.dp  →  GameSpacing 토큰 사용
검색: [0-9]+\.sp  →  GameTypography 토큰 사용
```

### 작업 D: 배경 교체

현재 `AdventureBackground` (Shell.kt 하단)가 Canvas 코드로 그리는 하늘/구름/태양/그라운드임. `bg_home_adventure.png`가 존재하므로, Canvas 코드를 PNG Image로 교체하여 더 풍부한 배경 구현.

마찬가지로 CombatScreen도 `bg_combat_arena.png`를 사용하도록 교체.

RunMapScreen은 `bg_map_parchment.png` 사용 확인.

---

## 5. 절대 하면 안 되는 것 (Guardrails)

1. **게임 로직 변경 금지** — `game/` 폴더 내 파일 수정 없음 (Combat.kt, Yahtzee.kt, Run.kt, Rewards.kt, Equipment.kt, Dice.kt 등)
2. **네비게이션 라우트 변경 금지** — `NavGraph.kt`의 라우트 구조 유지
3. **언어 토글 변경 금지** — `AppLanguage` (KOREAN/ENGLISH) 글로벌 상태 보존
4. **Safe-area 처리 변경 금지** — `WindowInsets.safeDrawing` padding 유지
5. **외부 카피라이트 에셋 사용 금지** — Habby/Archero/Legend of Slime 등의 에셋 복사 불가. 참고용으로만 스타일 참조
6. **게임 수치/밸런스 변경 금지** — `Constants.kt`의 모든 값 유지
7. **새로운 게임 메커니즘 추가 금지** — 확률, 보상, 과금, 백엔드 추가 없음

---

## 6. 빌드 및 검증

빌드 명령:
```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug
```

검증 체크리스트:
- [ ] `BUILD SUCCESSFUL` 확인
- [ ] 한국어/영어 토글 정상 동작
- [ ] safe-area (노치/네비게이션바) 처리 유지
- [ ] 게임 로직 파일 diff 없음
- [ ] 이모지가 PNG 아이콘으로 교체됨
- [ ] Canvas 배경이 PNG로 교체됨

---

## 7. Antigravity용 프롬프트

아래 텍스트를 그대로 Antigravity에 복사해서 붙여넣으세요:

---

```
You are working on Yacoo RPG, a Kotlin + Jetpack Compose Android casual RPG game.

Project root: /Users/a1/Desktop/manus/yacoo_rpg-android

READ THESE FILES FIRST (in order):
1. ART_DIRECTION_OVERHAUL_BRIEF.md — target art direction
2. IMPLEMENTATION_OVERHAUL_BRIEF.md — implementation strategy
3. app/src/main/java/com/yacoo/rpg/ui/theme/Color.kt — color tokens
4. app/src/main/java/com/yacoo/rpg/ui/theme/Type.kt — typography tokens
5. app/src/main/java/com/yacoo/rpg/ui/theme/GameIcons.kt — icon system
6. app/src/main/java/com/yacoo/rpg/ui/components/Shell.kt — main layout shell
7. app/src/main/java/com/yacoo/rpg/ui/components/GameComponents.kt — shared components
8. app/src/main/java/com/yacoo/rpg/ui/components/GameArt.kt — game art rendering
9. app/src/main/java/com/yacoo/rpg/ui/components/DiceView.kt — dice component
10. app/src/main/java/com/yacoo/rpg/navigation/NavGraph.kt — navigation + language wiring

Then read each screen file under app/src/main/java/com/yacoo/rpg/ui/screens/.

YOUR TASKS (do all of them):

TASK A — Replace emoji fallback icons with PNG assets:
- In GameIcons.kt, the GameIcon() composable loads PNGs for some roles but falls back to emoji for others (SCRAP, ATTACK, DEFEND, DICE, HEAL, REWARD, SETTINGS, CLOSE, BACK, RESET, CONFIRM, DEFEAT, LOCK, STAR, ARROW_UP).
- Generate or provide matching PNG icons in res/drawable/ for each of these roles.
- Style: thick cartoon outline, sticker aesthetic, transparent background, consistent with existing icons (icon_weapon.png, icon_gold_3d.png, etc.).
- Update GameIcon() to load the new PNGs.
- Resolution: 256x256px minimum.

TASK B — Polish all screens to match ART_DIRECTION_OVERHAUL_BRIEF.md:
- HomeScreen: hero standing on background ground, massive floating START button above bottom nav, floating side icons.
- CombatScreen: floating rounded dice board (not blocky rectangle), pill-shaped HP bars under combatants, bouncy damage numbers.
- EquipmentScreen: frosted/cream floating panel over dark overlay, clean paperdoll rendering, corner-overlapping level badges on equipment cards.
- UpgradeScreen: glowing arrows, sunburst success effect, clear insufficient-gold and max-level visual states.
- GachaScreen: centered glowing treasure chest, massive bouncy draw buttons.
- ResultScreen/RunResultScreen: clear visual hierarchy for victory/defeat banners, rewards panel, CTA.
- RunMapScreen: visually distinct node type icons, styled progress connectors.
- RewardPickScreen: polished reward cards with icons for HEAL, SCRAP, DICE, REROLL.

TASK C — Replace hardcoded colors/sizes with design tokens:
- Search for Color(0x in ui/ files and replace with tokens from Color.kt.
- Search for raw .dp and .sp values and replace with GameSpacing / GameTypography tokens where practical.

TASK D — Use PNG backgrounds instead of Canvas code:
- Replace AdventureBackground Canvas code in Shell.kt with bg_home_adventure.png Image.
- Ensure CombatScreen uses bg_combat_arena.png.
- Ensure RunMapScreen uses bg_map_parchment.png.

STRICT RULES — DO NOT BREAK:
- Do NOT modify any file under game/ (game logic).
- Do NOT change navigation routes in NavGraph.kt.
- Do NOT break Korean/English language toggle.
- Do NOT remove or alter WindowInsets.safeDrawing handling.
- Do NOT copy copyrighted assets from any existing game.
- Do NOT change game balance values in Constants.kt.

BUILD AND VERIFY:
Run: JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug
- Must output BUILD SUCCESSFUL.
- No gameplay file changes (git diff -- app/src/main/java/com/yacoo/rpg/game/).
- Language toggle still works.
- Safe-area handling preserved.
```
