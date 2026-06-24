# Yacoo RPG — Total UI Rebuild Directive for Antigravity

이 문서는 Antigravity에게 전달할 **전면 리디자인/전면 리빌드 지시서**다.

이전 문서가 "기존 UI를 개선"하는 느낌이었다면, 이 문서는 다르다.  
목표는 **현재 Yacoo RPG의 화면 껍데기를 거의 전부 버리고, 게임 로직만 유지한 채 상용 모바일 게임처럼 화면 구조를 새로 짜는 것**이다.

---

## 0. Absolute Mandate

**Do not polish the existing UI. Rebuild the shell.**

현재 화면의 카드, 리스트, 배경, 버튼, 배치, 정보 밀도, 여백, Material 느낌을 대부분 폐기한다.  
남겨야 하는 것은 아래뿐이다.

- Kotlin + Jetpack Compose 프로젝트 구조
- Navigation routes
- GameViewModel / state flow
- Game logic under `game/`
- Save/storage/localization
- Existing drawable assets only as temporary raw material

다음은 보존 대상이 아니다.

- 현재 Home 레이아웃
- 현재 Combat 레이아웃
- 현재 RewardPick 레이아웃
- 현재 Equipment/Gacha/Upgrade 화면 구조
- 현재 Material Card/Button 스타일
- 현재 큰 배경 일러스트 중심 구성
- 현재 앱처럼 보이는 spacing/typography

Antigravity가 이 작업을 "theme update"로 끝내면 실패다.  
성공 기준은 앱을 실행했을 때 **완전히 다른 상용 모바일 게임 UI처럼 보여야 한다**는 것이다.

---

## 1. Source References

작업 전 반드시 읽어라.

### 분석 문서

- `/Users/a1/Desktop/video_test/UI_DEEP_ANALYSIS.md`
- `/Users/a1/Desktop/video_test/DPMZ6716_UI_DEEP_ANALYSIS.md`
- `/Users/a1/Desktop/video_test/NMPL1791_UI_DEEP_ANALYSIS.md`

### 대표 시트

- `/Users/a1/Desktop/video_test/analysis_frames/core_reference_sheet.jpg`
- `/Users/a1/Desktop/video_test/analysis_DPMZ6716/core_reference_sheet.jpg`
- `/Users/a1/Desktop/video_test/analysis_NMPL1791/core_reference_sheet.jpg`

### 핵심 해석

Yacoo는 세 레퍼런스를 섞되, 가장 강하게 가져갈 것은 다음이다.

- CFGI8241: 주사위 RPG 전투, 하단 주사위 콘솔, 보상/룰렛/결과 연출
- DPMZ6716: 상단 진행 HUD, 3카드 선택, 실패 보상, 짧고 강한 전투 UI
- NMPL1791: 하단 큰 CTA, 튜토리얼 손가락, NPC 말풍선, 해금 배너, 보드게임식 전장

---

## 2. Final Product Shape

Yacoo RPG를 아래 형태로 바꾼다.

**Vertical dice roguelite board RPG**

화면 구조:

```text
┌──────────────────────────────┐
│ compact top HUD              │
│ stage / enemy / resource      │
├──────────────────────────────┤
│                              │
│ board-like battle field       │
│ hero, enemy, hit effects      │
│                              │
├──────────────────────────────┤
│ bottom dice/action console    │
│ dice tray + hand grid + CTA   │
└──────────────────────────────┘
```

메타 구조:

```text
Home hub
  ↓ Start
Combat board
  ↓ Reward modal
Run map / treasure / rest
  ↓ Boss / clear / defeat
Result with rewards
  ↓ Upgrade / Gear / Draw
Retry
```

---

## 3. Hard Visual Rules

### 3.1 No generic app UI

금지:

- Default Material card feel
- Long admin-style lists
- Form-like screens
- Plain rounded rectangles with text only
- Generic gradient backgrounds
- Big empty hero/marketing layout
- Screen sections that look like dashboard cards

필수:

- Game board surfaces
- Thick outlines
- Layered HUD
- Diegetic panels: parchment, metal, dice tray, reward chest, board tiles
- Clear bottom CTA
- Strong banners
- Reward particles

### 3.2 No AI-looking scenery

배경은 예쁜 그림이 아니라 **게임 보드**여야 한다.

Preferred:

- tiled board
- floating platform
- parchment map
- grass/stone lanes
- dice table
- node path
- simple repeated patterns

Avoid:

- painterly fantasy forest
- blurred magical landscape
- soft gradient with decorative particles
- random scenery that does not explain gameplay

### 3.3 Every screen needs a primary action

각 화면은 1초 안에 유저가 무엇을 누를지 보여야 한다.

- Home: Start Run
- Combat: Roll / Confirm / Pick hand
- RewardPick: Pick one card
- RunMap: Enter current node
- Result: Continue / Upgrade / Retry
- Equipment: Equip / Upgrade
- Gacha: Draw
- Upgrade: Upgrade selected stat

Primary CTA is always bottom-centered and yellow/gold unless the action is utility reroll, which can be blue/cyan.

---

## 4. Files To Rebuild

Antigravity는 아래 파일을 "부분 수정"이 아니라 **화면 재작성 대상으로 간주**한다.

### Shell and foundation

- `app/src/main/java/com/yacoo/rpg/ui/components/Shell.kt`
- `app/src/main/java/com/yacoo/rpg/ui/theme/Color.kt`
- `app/src/main/java/com/yacoo/rpg/ui/theme/Type.kt`
- `app/src/main/java/com/yacoo/rpg/ui/components/GameComponents.kt`
- `app/src/main/java/com/yacoo/rpg/ui/components/GameArt.kt`

### Gameplay screens

- `app/src/main/java/com/yacoo/rpg/ui/screens/HomeScreen.kt`
- `app/src/main/java/com/yacoo/rpg/ui/screens/CombatScreen.kt`
- `app/src/main/java/com/yacoo/rpg/ui/screens/RewardPickScreen.kt`
- `app/src/main/java/com/yacoo/rpg/ui/screens/RunMapScreen.kt`
- `app/src/main/java/com/yacoo/rpg/ui/screens/RunResultScreen.kt`
- `app/src/main/java/com/yacoo/rpg/ui/screens/ResultScreen.kt`

### Meta screens

- `app/src/main/java/com/yacoo/rpg/ui/screens/EquipmentScreen.kt`
- `app/src/main/java/com/yacoo/rpg/ui/screens/GachaScreen.kt`
- `app/src/main/java/com/yacoo/rpg/ui/screens/UpgradeScreen.kt`

Do not preserve old layouts for emotional safety. If old components fight the target, delete or bypass them.

---

## 5. New Global Component Kit

Build a new visual kit before rebuilding screens.

Required components:

- `GameScreenFrame`
- `TopRunHud`
- `BottomActionConsole`
- `DiceTray`
- `HandAttackGrid`
- `YellowGameButton`
- `BlueUtilityButton`
- `RibbonBanner`
- `UnlockBanner`
- `DimOverlay`
- `ThreeChoiceModal`
- `RewardCard`
- `ResultRewardRow`
- `ChestRewardPanel`
- `TutorialHandPointer`
- `NpcSpeechBubble`
- `BoardArenaBackground`
- `ParchmentNodeMap`
- `InventoryGrid`
- `EquipmentSlot`
- `RarityFrame`

These must not look like ordinary Material components.

Style:

- dark outline 2-4dp
- slight bevel/shadow
- saturated CTA colors
- thick text shadow
- compact mobile density
- Korean-safe text sizing

---

## 6. Screen Rebuild Specs

## 6.1 HomeScreen — replace with stage hub

Current home must be thrown away.

New structure:

```text
top: profile + resources
center: current stage board / hero pedestal
sides: small stacked feature buttons
lower center: current run preview / reward preview
bottom: massive START button
bottom nav: integrated game tabbar
```

Required visual:

- board/map, not scenic poster
- side buttons like mobile game event buttons
- bottom yellow `시작` / `Start`
- small status chips for chapter, power, best floor
- no giant stat list
- no generic "dashboard card"

User should see this and immediately think: "this is a mobile game lobby."

## 6.2 CombatScreen — replace with dice battle board

Current combat layout must be rebuilt.

New structure:

```text
top 12%:
  pause/exit, chapter, enemy HP, reward counters

middle 50%:
  board arena
  hero on lower half
  enemy on upper half
  hit flashes, dice/projectile effects

bottom 38%:
  dice tray
  selected hand/combo
  2-3 column hand attack grid
  roll/confirm CTA
```

Required:

- Bottom nav hidden.
- All Yahtzee hand choices visible in compact grid.
- Dice look physical and clickable.
- Selected dice glow.
- Current combo/multiplier has a large readable badge.
- Enemy HP and hero HP are always visible.
- Hit feedback exists: floating numbers, small screen shake, sparkle.

If there is only one screen to make excellent, make this one excellent.

## 6.3 RewardPickScreen — replace with commercial 3-card modal

New structure:

```text
dimmed combat/map background
top ribbon: 보상 선택 / Skill Choice
three cards
bottom mini rail / current status
```

Cards:

- big icon
- title
- one-line effect
- rarity color
- stars/grade
- selected card flash

Do not use a plain list of rewards.

## 6.4 RunMapScreen — replace with node board

New structure:

- parchment/board background
- connected nodes
- current node glowing
- completed nodes stamped
- locked nodes dimmed
- bottom yellow Enter/Continue CTA

No list-like layout.

## 6.5 Result screens — victory and defeat both reward

Victory:

- large ribbon
- objective stars
- reward row
- chest/sunburst
- yellow continue

Defeat:

- not a dead end
- show run duration/floor
- show rewards gained
- show next actions: Upgrade / Gear / Draw / Retry
- yellow primary continue

## 6.6 EquipmentScreen — rebuild as mobile RPG inventory

New structure:

```text
top: hero paperdoll
around hero: 4 equipped slots
middle: power/stat strip
bottom: inventory grid / locked slots
selected item: detail panel
CTA: equip/upgrade
```

Use only existing equipment model types:

- WEAPON
- ARMOR
- CHARM
- BOOTS

Do not add new game model types.

## 6.7 GachaScreen — rebuild as recruit/draw event

New structure:

- shop/recruit backdrop
- central chest/portal/dice altar
- draw 1 / draw 10
- result cards in rarity frames
- no z-order overlap
- no bottom nav clipping

Use NMPL's recruit flow and CFGI's chest reward energy.

## 6.8 UpgradeScreen — rebuild as growth board

New structure:

- large current power/stat
- upgrade cards/nodes
- cost chips
- green upgrade CTA
- unlock banner for new systems

No settings-form look.

---

## 7. Commercial UI Patterns To Force In

The redesign must include these patterns somewhere:

- Full-screen dim overlay
- 3-card choice modal
- Big yellow bottom CTA
- Unlock banner
- Reward chest or reward row
- Defeat-with-rewards
- Compact top HUD
- Bottom-first action console
- Tutorial hand pointer
- NPC speech bubble or short coach bubble
- Rarity color frames
- Board/tile background

If a pattern is missing, the redesign is incomplete.

---

## 8. Theme Direction

Use a hybrid palette:

```kotlin
val Ink = Color(0xFF151923)
val Overlay = Color(0xCC080C12)
val Cream = Color(0xFFF2E3BC)
val Brown = Color(0xFF5B3E31)
val Gold = Color(0xFFFFD84A)
val GoldDark = Color(0xFFD58A18)
val Cyan = Color(0xFF7FE6FF)
val Blue = Color(0xFF3F7EE8)
val Green = Color(0xFF60D96A)
val Orange = Color(0xFFE97824)
val Red = Color(0xFFBE3434)
val Purple = Color(0xFF7659C9)
val BoardGreen = Color(0xFF2E4016)
val TileGray = Color(0xFFA1A0B2)
```

Use role colors consistently:

- Yellow/gold: primary CTA, reward, confirm
- Blue/cyan: dice, reroll, utility
- Green: heal, upgrade, success
- Orange/red: attack, danger, boss
- Purple: rare/special/draw
- Cream/brown: meta panels

---

## 9. Animation Requirements

Add real game juice.

Minimum:

- button press scale
- dice roll animation
- selected dice glow
- modal dim fade
- card drop/scale entry
- selected reward flash
- unlock banner slide/pop
- floating damage number
- chest reward sparkle
- result reward count-up or reveal

Timing:

- button press: 80-120ms
- modal in: 180-220ms
- card in: 220-350ms
- unlock banner: 1.2-2.0s
- result/chest: 1.0-1.8s

---

## 10. Things That Must Be Deleted Or Replaced

Search the UI and remove the visual effect of:

- plain Material cards
- full-width boring list rows
- huge static stat blocks on Home
- generic rounded rectangle buttons
- empty decorative background areas
- unstyled default dialogs
- text-heavy panels without icons
- bottom nav covering content
- z-order issues in Gacha
- long Combat hand list

Do not merely recolor these. Replace them with game components.

---

## 11. Verification

Run:

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug
```

If fast:

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew test
```

Check at least these viewport classes if emulator/screenshot tooling exists:

- 360x800
- 393x852
- 411x891

Manual visual checklist:

- Does it look like a new game shell, not a themed old app?
- Are primary actions bottom-centered?
- Is Combat visibly rebuilt?
- Is RewardPick a 3-card modal?
- Does defeat still give rewards?
- Are backgrounds board/tile/map-based?
- Is yellow used consistently for CTA?
- Is text readable in Korean?
- Does bottom nav avoid covering controls?
- Is Combat bottom nav hidden?

---

## 12. Antigravity Prompt

Copy this prompt into Antigravity:

```text
You are working in /Users/a1/Desktop/manus/yacoo_rpg-android.

Read ANTIGRAVITY_TOTAL_REDESIGN_REBUILD_DIRECTIVE.md first. Treat it as the source of truth. It supersedes all previous UI/art direction briefs where there is conflict.

This is not a polish pass. Do not theme the existing screens. Rebuild the UI shell and screen layouts while preserving game logic, navigation routes, save/localization behavior, and the rule that bottom nav is hidden during Combat.

Read the three video analysis documents referenced in the directive:
- /Users/a1/Desktop/video_test/UI_DEEP_ANALYSIS.md
- /Users/a1/Desktop/video_test/DPMZ6716_UI_DEEP_ANALYSIS.md
- /Users/a1/Desktop/video_test/NMPL1791_UI_DEEP_ANALYSIS.md

Implement a total commercial-mobile redesign:
- Home becomes a stage hub with a big bottom Start CTA.
- Combat becomes a dice battle board with compact top HUD and bottom dice/action console.
- RewardPick becomes a dimmed 3-card choice modal.
- RunMap becomes a parchment/node board.
- Result screens show rewards even on defeat.
- Equipment becomes a real inventory shell.
- Gacha becomes a recruit/draw event shell.
- Upgrade becomes a growth board.

Delete or bypass old Material-looking layouts where necessary. Build shared game components first: RibbonBanner, DimOverlay, ThreeChoiceModal, DiceTray, BottomActionConsole, YellowGameButton, UnlockBanner, TutorialHandPointer, NpcSpeechBubble, ResultPanel, InventoryGrid, and board backgrounds.

Do not copy external game assets, logos, characters, icons, or exact text. Only apply layout, timing, modal, reward, CTA, and visual-system patterns.

Verify with:
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug

Report changed files, what was fully rebuilt, and any remaining limitations.
```
