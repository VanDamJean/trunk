# Yacoo RPG — 3-Reference UI Redesign Directive for Antigravity

이 문서는 Antigravity에게 그대로 전달할 **최종 리디자인/리배치 지시서**다.

목표는 Yacoo RPG Android를 “조금 예쁘게” 고치는 것이 아니라, 사용자가 분석한 3개 상용 모바일 게임 영상에서 공통적으로 검증된 UI 문법을 Yacoo의 야츠제 주사위 RPG 시스템에 맞게 재구성하는 것이다.

> 중요: 외부 게임의 캐릭터, 로고, 아이콘, 텍스트, 에셋을 복사하지 않는다.  
> 가져올 것은 **레이아웃 문법, 상호작용 리듬, 모달 구조, 보상 피드백 방식, 화면 밀도**다.

---

## 0. Source Of Truth

이 문서는 아래 기존 문서들과 충돌할 경우 우선한다.

- `ANTIGRAVITY_OPUS_UI_REDESIGN_BRIEF.md`
- `ART_DIRECTION_OVERHAUL_BRIEF.md`
- `IMPLEMENTATION_OVERHAUL_BRIEF.md`
- `docs/ART_DIRECTION_OVERHAUL_BRIEF.md`
- `docs/IMPLEMENTATION_OVERHAUL_BRIEF.md`

기존 문서를 삭제하지 말고, 구현 판단은 이 문서를 우선한다.

---

## 1. Project Context

프로젝트:

- Path: `/Users/a1/Desktop/manus/yacoo_rpg-android`
- Stack: Kotlin + Jetpack Compose Android
- Package: `com.yacoo.rpg`
- Game: Yahtzee/dice-based roguelite RPG

Build command:

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug
```

Do not change:

- Core game logic in `app/src/main/java/com/yacoo/rpg/game/` unless strictly required for UI state plumbing.
- Save models / storage semantics.
- Navigation destinations and localization support.
- Rule that bottom nav is hidden during `Screen.COMBAT`.

Allowed:

- Full UI layout overhaul.
- Theme/token replacement.
- Compose Canvas backgrounds/effects.
- Shared component refactor.
- Rebuilding screen hierarchy inside existing screens.
- Replacing placeholder-looking visuals with stylized game UI.

---

## 2. Reference Analysis Inputs

Before coding, read these analysis documents and image sheets.

### Reference A — CFGI8241 / dice roguelite RPG

- Analysis doc: `/Users/a1/Desktop/video_test/UI_DEEP_ANALYSIS.md`
- Reference sheet: `/Users/a1/Desktop/video_test/analysis_frames/core_reference_sheet.jpg`

What to extract:

- Bottom dice tray and main action zone.
- Dim overlay + central skill/reward choice.
- Dice/skill cards with strong rarity colors.
- Treasure chest, roulette, boss cut-in, clear/defeat result.
- Equipment/codex modal structure.
- Bright fantasy reward feedback.

### Reference B — DPMZ6716 / Survivor.io-style action

- Analysis doc: `/Users/a1/Desktop/video_test/DPMZ6716_UI_DEEP_ANALYSIS.md`
- Reference sheet: `/Users/a1/Desktop/video_test/analysis_DPMZ6716/core_reference_sheet.jpg`

What to extract:

- Compact top timer/progress HUD.
- Repeated 3-card skill selection modal.
- Failure result that still gives rewards.
- Stage/growth map after failure.
- Direct, low-noise, readable mobile action HUD.
- Tile-based background that does not look AI-generated.

### Reference C — NMPL1791 / random summon defense

- Analysis doc: `/Users/a1/Desktop/video_test/NMPL1791_UI_DEEP_ANALYSIS.md`
- Reference sheet: `/Users/a1/Desktop/video_test/analysis_NMPL1791/core_reference_sheet.jpg`

What to extract:

- Large yellow bottom CTA.
- NPC tutorial speech bubble + giant hand pointer.
- Unlock banners: "강화 해금", "신규 기능 오픈", etc.
- Recruit/gacha reward flow.
- Board-game-like battlefield instead of soft AI illustration.
- Cream paper panels + brown frames + yellow buttons.

---

## 3. One-Line Target

Turn Yacoo RPG into a commercial-looking vertical mobile dice roguelite shell:

**top progress HUD + living battle board + bottom dice/action tray + dimmed 3-card reward modal + strong result/reward screens + stage/growth/gacha/equipment meta loop.**

---

## 4. Design Principles

### 4.1 Bottom-first mobile control

All primary actions must live in the bottom 25-35% of the screen.

Yacoo mapping:

- Combat: dice tray, roll button, hand/attack controls.
- Home: large Start/Adventure CTA.
- Reward: confirm/pick button.
- Gacha: draw button.
- Equipment: equip/upgrade CTA.

Avoid placing primary actions in the top half.

### 4.2 Progress runs in the background, choices interrupt briefly

Combat should feel continuous. Major choices should interrupt with:

1. background dim,
2. central title/banner,
3. 3 cards or reward panel,
4. immediate glow/haptic feedback,
5. return to flow.

Use this for:

- `RewardPickScreen`
- run reward choices,
- rest/treasure node choices,
- post-combat reward picks,
- gacha/recruit results.

### 4.3 Yellow is always "good to press"

Use yellow/gold only for positive CTA or reward-confirming actions.

Examples:

- Start
- Roll
- Pick
- Confirm
- Open Chest
- Draw
- Equip
- Continue

Do not use yellow for passive labels.

### 4.4 Color communicates function before text

Apply this consistently:

- Blue/cyan: dice, utility, reroll, safe choice.
- Orange/red: attack, danger, boss, damage.
- Green: heal, growth, upgrade, success.
- Purple: rare/special/gacha.
- Gold/yellow: reward/CTA/legendary/confirm.
- Cream/brown: meta panels, tutorial, parchment maps.
- Dark navy/black overlay: modal focus.

### 4.5 Avoid AI-looking backgrounds

Do not rely on soft, generic, blurred fantasy backgrounds. They read as AI-generated.

Preferred background construction:

- Repeatable board/tile layers.
- Clear roads/lanes/fields/platforms.
- Hand-authored simple shapes.
- Compose Canvas patterns.
- Hard-edged silhouettes.
- Purposeful gameplay zones.

Good directions:

- Dice battlefield: floating board/platform with lane markers.
- Run map: parchment path nodes.
- Home: compact stage hub with character pedestal and side menu.
- Combat arena: visible play board, not vague scenery.

Bad directions:

- Blurry gradients.
- Generic magical forest image.
- Overly smooth AI landscape.
- Decorative orbs or bokeh blobs.
- Big empty hero marketing page.

---

## 5. Required Screen Redesigns

### 5.1 Shell.kt — global mobile game shell

Read:

- `app/src/main/java/com/yacoo/rpg/ui/components/Shell.kt`
- `app/src/main/java/com/yacoo/rpg/navigation/NavGraph.kt`

Implement:

- Top HUD must be compact and floating.
- Resource pills should be small, dark/cream, high contrast.
- Bottom nav must be integrated with safe area and not float awkwardly as a separate Material bar.
- Center tab can protrude, but the bottom bar must visually attach to the bottom edge.
- Options should be a game-style panel, not a default Material dialog.

Rules:

- Bottom nav hidden on Combat.
- No text clipping in Korean or English.
- No nested card-in-card look.

### 5.2 HomeScreen.kt — stage hub / adventure entry

Read:

- `app/src/main/java/com/yacoo/rpg/ui/screens/HomeScreen.kt`
- `app/src/main/java/com/yacoo/rpg/ui/components/GameArt.kt`

Target:

Home should become a playable stage hub, not a static menu.

Layout:

- Top 0-12%: resource/profile HUD.
- Center 12-58%: character/pedestal/stage board.
- Side edges 20-55%: stacked event/quest/shop buttons.
- Lower 58-78%: run preview, current chapter, rewards.
- Bottom 78-88%: large yellow Start CTA.
- Bottom 88-100%: integrated nav.

Use references:

- NMPL stage map/recruit loop for the big yellow CTA and onboarding tone.
- CFGI result/reward rhythm for treasure and stage previews.
- DPM map/growth screen for after-failure motivation.

Remove or demote:

- Large generic stat rows on the home screen.
- Overly decorative background art that does not explain gameplay.
- Huge empty scenic areas.

Home success criteria:

- User instantly sees "Start run" as the main action.
- Stage/chapter progress is visible.
- Meta features are side buttons, not giant sections.
- The screen reads as a mobile game lobby within 1 second.

### 5.3 CombatScreen.kt — dice combat board

Read:

- `app/src/main/java/com/yacoo/rpg/ui/screens/CombatScreen.kt`
- `app/src/main/java/com/yacoo/rpg/ui/components/CombatArena.kt`
- `app/src/main/java/com/yacoo/rpg/ui/components/CombatDiceBoard.kt`
- `app/src/main/java/com/yacoo/rpg/ui/components/DiceView.kt`
- `app/src/main/java/com/yacoo/rpg/ui/components/CombatCards.kt`

Target:

Combat should combine:

- CFGI bottom dice tray,
- DPM compact top progress HUD,
- NMPL board-game clarity.

Layout:

- Top 0-12%:
  - pause/exit,
  - floor/wave/turn,
  - enemy HP/progress,
  - run resource counters.

- Middle 12-62%:
  - enemy/hero arena.
  - attack lanes/effects.
  - visible board/platform, not a vague background.
  - damage numbers and short reward particles.

- Bottom 62-100%:
  - dice tray,
  - roll/confirm CTA,
  - compact hand attack grid,
  - reroll/reward chips.

Required changes:

- Hand attacks must be compact 2-3 column grid, not long list.
- Dice must look physical: bright face, thick border, shadow, pips.
- Current usable dice/hand must glow.
- Roll/Confirm button must be large yellow or blue depending state:
  - Yellow: positive proceed/confirm.
  - Blue/cyan: roll/reroll utility.
- Enemy HP, hero HP, and current combo multiplier must be immediately readable.

Combat effects:

- 200-400ms hit flash.
- Floating damage numbers.
- Small coin/scrap/gem pickup particles.
- Boss/elite warning banner.
- Screen shake only on big hits, subtle.

Combat success criteria:

- No bottom nav.
- No overlapping text/buttons.
- All 9 Yahtzee hand attacks visible or reachable without awkward scroll.
- Bottom 35% feels like a polished dice/action console.

### 5.4 RewardPickScreen.kt — 3-card choice modal

Read:

- `app/src/main/java/com/yacoo/rpg/ui/screens/RewardPickScreen.kt`
- `app/src/main/java/com/yacoo/rpg/game/Rewards.kt`
- `app/src/main/java/com/yacoo/rpg/game/RunModels.kt`

Target:

This should become the most commercial-feeling screen in the game.

Layout:

- Full-screen dim overlay.
- Top title ribbon: "보상 선택" / "Skill Choice".
- Optional confetti/particles around title.
- 3 large vertical cards.
- Each card:
  - icon,
  - title,
  - short effect,
  - rarity/role color,
  - small star/grade row.
- Bottom:
  - current dice/status mini rail if relevant.

Reference:

- DPM 3-card skill selection for card density.
- CFGI skill selection for glow/reward feel.

Card color mapping:

- HEAL: green.
- SCRAP/GOLD: yellow/bronze.
- DICE/REROLL: blue/cyan.
- ATTACK/elite reward: orange/red.
- rare/special: purple.

Animation:

- Dim in: 200ms.
- Title/card drop-in: 250-350ms.
- Selected card flash: 300ms.
- Return to previous flow immediately.

### 5.5 RunResultScreen.kt / ResultScreen.kt — clear and defeat

Read:

- `app/src/main/java/com/yacoo/rpg/ui/screens/RunResultScreen.kt`
- `app/src/main/java/com/yacoo/rpg/ui/screens/ResultScreen.kt`

Target:

Both victory and defeat must give feedback and rewards.

Victory:

- Large ribbon title.
- Stars/objectives.
- Reward icons in a row.
- Yellow Continue CTA.
- Chest/sunburst effect.

Defeat:

- Blue or purple "Defeat / 아쉽네요" title.
- Run duration/floor.
- Rewards still shown.
- Growth suggestion:
  - upgrade,
  - equipment,
  - draw,
  - retry.

Reference:

- CFGI clear/defeat result.
- DPM failure result.
- NMPL 종료 result panel.

Success criteria:

- Defeat never feels like a dead end.
- User has an obvious next action.
- Reward icons are readable and not hidden by bottom nav.

### 5.6 RunMapScreen.kt — node map / growth path

Read:

- `app/src/main/java/com/yacoo/rpg/ui/screens/RunMapScreen.kt`
- `app/src/main/java/com/yacoo/rpg/game/RunModels.kt`

Target:

Make it feel like a stage/growth map rather than a form/list.

Layout:

- Parchment/board background.
- Nodes connected by thick path.
- Node icons:
  - Battle,
  - Elite,
  - Treasure,
  - Rest,
  - Boss.
- Current node glows.
- Completed nodes stamped.
- Locked nodes dimmed.
- Bottom CTA: Continue/Enter.

Reference:

- DPM orange stage/growth map.
- NMPL vertical lobby stage road.
- CFGI hex growth board.

Success criteria:

- It is visually clear where the player is and where to go.
- No list-like admin UI.

### 5.7 EquipmentScreen.kt — inventory shell

Read:

- `app/src/main/java/com/yacoo/rpg/ui/screens/EquipmentScreen.kt`
- `app/src/main/java/com/yacoo/rpg/game/EquipmentModels.kt`
- `app/src/main/java/com/yacoo/rpg/game/Equipment.kt`

Target:

Build a commercial mobile gear screen:

- top character/paperdoll,
- 4 equipped slots,
- stat strip,
- inventory grid,
- selected item detail,
- equip/upgrade CTA.

Use current 4 slots only:

- WEAPON
- ARMOR
- CHARM
- BOOTS

Do not add new equipment types to the game model.

Visual language:

- Cream/brown panel from NMPL.
- Blue/purple rarity card from CFGI.
- Inventory grid clarity from mobile RPGs.

Success criteria:

- The screen reads as "equipment inventory" immediately.
- Placeholder locked slots are visually clear if there is no real bag model.
- Bottom nav does not cover the grid.

### 5.8 GachaScreen.kt — draw/recruit shell

Read:

- `app/src/main/java/com/yacoo/rpg/ui/screens/GachaScreen.kt`

Target:

Reframe gacha as a recruit/draw event:

- Full-screen dim or shop backdrop.
- Central chest/portal/recruit board.
- Draw 1 / Draw 10 buttons.
- Result cards with rarity colors.
- Yellow CTA for draw/confirm.

Reference:

- NMPL 영웅 모집 + 획득.
- CFGI treasure chest.
- DPM result reward row.

Required:

- Fix z-order and bottom nav overlap.
- Cards must never hide behind panels.
- Draw result must be readable at 360x800 and 411x891.

### 5.9 UpgradeScreen.kt — meta growth

Read:

- `app/src/main/java/com/yacoo/rpg/ui/screens/UpgradeScreen.kt`

Target:

Make upgrade feel like a growth board, not a settings form.

Layout:

- Large current power/stat at top.
- Upgrade nodes/cards.
- Cost chips.
- Green success/upgrade CTA.
- Unlock banner when a major upgrade becomes available.

Reference:

- NMPL 강화 해금.
- CFGI hex growth board.
- DPM growth map.

---

## 6. Shared Components To Build Or Refactor

Create or refactor components in `GameComponents.kt` / `GameArt.kt` as needed.

Suggested components:

- `GameDimOverlay`
- `RibbonTitle`
- `YellowCtaButton`
- `GameResourcePill`
- `RewardCard`
- `SkillChoiceCard`
- `BottomActionTray`
- `DiceConsole`
- `UnlockBanner`
- `TutorialHandPointer`
- `NpcSpeechBubble`
- `RewardIconRow`
- `ResultPanel`
- `BoardBackground`
- `StageNode`
- `InventorySlot`

Use local patterns. Do not over-abstract if a component is only used once.

---

## 7. Theme Tokens

Update `Color.kt` and related tokens.

Suggested palette:

```kotlin
val GameInk = Color(0xFF151923)
val GameOverlay = Color(0xCC080C12)
val GamePanelCream = Color(0xFFF2E3BC)
val GamePanelBrown = Color(0xFF5B3E31)
val GameGold = Color(0xFFFFD84A)
val GameGoldDark = Color(0xFFD58A18)
val GameCyan = Color(0xFF7FE6FF)
val GameBlue = Color(0xFF3F7EE8)
val GameGreen = Color(0xFF60D96A)
val GameOrange = Color(0xFFE97824)
val GameRed = Color(0xFFBE3434)
val GamePurple = Color(0xFF7659C9)
val GameTileGray = Color(0xFFA1A0B2)
val GameForestDark = Color(0xFF223418)
```

Typography:

- Keep existing game fonts if readable.
- Korean labels need strong weight and shadow.
- Avoid tiny Korean text below 11sp unless it is decorative.
- Buttons must support Korean and English without clipping.

---

## 8. Animation/Haptics Spec

Use short, game-like animation. Do not make slow app transitions.

- Button press: 80-120ms scale down/up.
- Modal dim in: 180-220ms.
- Card entry: 220-350ms.
- Unlock banner: 1.2-2.0s.
- Wave/boss/result banner: 1.0-3.0s.
- Reward sparkle: 0.5-1.2s.
- Damage float: 0.5-0.8s.
- Chest open: 1.0-1.8s.

Haptics:

- Light haptic on tap.
- Medium haptic on reward selection.
- Strong haptic on boss/clear/rare draw, if existing haptic helper supports it.

---

## 9. Tutorial / Onboarding Layer

Add a reusable tutorial overlay pattern, even if only wired to placeholder states initially.

Pattern:

- Background dim.
- Target UI remains highlighted.
- Large hand pointer points/taps target.
- NPC speech bubble explains one sentence.
- Avoid long paragraphs.

Use cases:

- First Start button.
- First Roll.
- First reward pick.
- First equipment equip.
- First gacha draw.

Reference:

- NMPL tutorial hand and king speech bubble.

---

## 10. IP Safety

Do not copy:

- Game names/logos from references.
- Character designs.
- Exact icon art.
- Exact skill names.
- Exact UI art assets.
- Screenshots as in-app assets.

Allowed:

- Layout ratios.
- Timing and modal behavior.
- Color-role conventions.
- CTA placement.
- Reward/result UX patterns.
- Generic concepts like chest, dice, cards, nodes, banners.

---

## 11. Implementation Order

Do this in phases.

### Phase 1 — Audit and screenshot current state

Read required files. Identify current composables for:

- shell,
- home,
- combat,
- reward pick,
- result,
- run map,
- equipment,
- gacha,
- upgrade.

Take screenshots before changing if tooling allows.

### Phase 2 — Theme + shared components

Implement tokens and shared components first.

No screen should keep default Material-looking cards/buttons if a game component exists.

### Phase 3 — Combat + RewardPick

These define the gameplay feel.

- Combat board.
- Bottom dice console.
- 3-card reward modal.
- Hit/reward feedback.

### Phase 4 — Home + RunMap

Build the outer loop:

- stage hub,
- start CTA,
- node map,
- side buttons.

### Phase 5 — Result + Equipment + Gacha + Upgrade

Build the meta loop:

- failure still rewards,
- gear screen,
- draw/recruit screen,
- growth screen.

### Phase 6 — Polish

- text fit,
- safe areas,
- z-order,
- animations,
- Korean/English labels,
- no overlapping bottom nav.

### Phase 7 — Verification

Run:

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug
```

If tests are relevant and fast:

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew test
```

If emulator/screenshot tooling exists, verify:

- 360x800
- 393x852
- 411x891

---

## 12. Acceptance Criteria

The redesign is successful only if all are true:

- The app no longer looks like a generic Compose/Material prototype.
- Primary controls are bottom-first and thumb-friendly.
- Combat has a clear top HUD, board/arena center, and bottom dice console.
- Reward selection uses a polished dimmed 3-card modal.
- Yellow CTA meaning is consistent.
- Result screens show reward progress even on defeat.
- Equipment/Gacha/Upgrade look like game screens, not settings pages.
- Backgrounds are structured boards/maps, not vague AI-like scenery.
- No text overlaps or clips in Korean.
- Bottom nav never covers critical controls/cards.
- `Screen.COMBAT` still hides bottom nav.
- Build passes.

---

## 13. Antigravity Prompt

Copy this section into Antigravity:

```text
You are working in /Users/a1/Desktop/manus/yacoo_rpg-android.

Read ANTIGRAVITY_3_REFERENCE_UI_REDESIGN_DIRECTIVE.md first and treat it as the source of truth. It supersedes older art direction briefs where they conflict.

Also read the three video-analysis docs referenced inside it:
- /Users/a1/Desktop/video_test/UI_DEEP_ANALYSIS.md
- /Users/a1/Desktop/video_test/DPMZ6716_UI_DEEP_ANALYSIS.md
- /Users/a1/Desktop/video_test/NMPL1791_UI_DEEP_ANALYSIS.md

Redesign the app shell and screens around the shared commercial mobile UI patterns from those references:
- bottom-first controls,
- compact top HUD,
- dimmed 3-card reward/skill modal,
- yellow CTA convention,
- color-coded reward/rarity roles,
- result screens that still reward defeat,
- board/tile backgrounds instead of AI-looking scenery,
- tutorial hand/NPC bubble where useful,
- unlock/reward banners.

Do not copy any reference game assets, logos, characters, exact text, or IP. Only use layout, interaction, timing, and visual-system patterns.

Preserve Yacoo's game logic, save models, navigation destinations, localization support, and the rule that bottom nav is hidden in Combat.

Start by auditing Shell.kt, HomeScreen.kt, CombatScreen.kt, RewardPickScreen.kt, RunResultScreen.kt, RunMapScreen.kt, EquipmentScreen.kt, GachaScreen.kt, UpgradeScreen.kt, Color.kt, Type.kt, GameComponents.kt, GameArt.kt, DiceView.kt, and CombatDiceBoard.kt.

Implement in phases:
1. theme tokens and shared game components,
2. Combat + RewardPick,
3. Home + RunMap,
4. Result + Equipment + Gacha + Upgrade,
5. polish safe areas, z-order, text fit, and animations.

Verify with:
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug

Deliver a concise summary of changed files and any remaining limitations.
```
