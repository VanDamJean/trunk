# Habby-Style Casual RPG Design System Overhaul

## TL;DR

> **Quick Summary**: Establish a reusable Habby-inspired casual RPG design system for the Kotlin Jetpack Compose app, then apply it across all major screens without changing gameplay, navigation behavior, language state, or safe-area handling.
>
> **Deliverables**:
> - Centralized design tokens for palette, typography, spacing, radius, elevation, gradients, rarity, and motion guidance.
> - Reusable polished Compose components for game buttons, cards, HUD chips, item slots, panels, banners, and screen surfaces.
> - Asset replacement pipeline/spec so emoji placeholders become temporary, not permanent.
> - Full major-screen visual overhaul plan: Shell/HUD/nav, Home, Combat, Gear, Upgrade, Draw/Gacha, Result, RunMap, RewardPick, RunResult, GameArt, DiceView.
>
> **Estimated Effort**: Large
> **Parallel Execution**: YES - 4 implementation waves + final verification
> **Critical Path**: 1 → 4 → 5 → 6/7/8/9/10/11/12/13 → 14 → F1-F4

---

## Context

### Original Request
User said the app's art direction feels ambiguous and the UI is not polished/sleek enough. User requested a design-system plan before further work.

### Interview Summary
**Key Decisions**:
- Target style: Habby-style casual mobile RPG.
- Scope: Design system first, then all major screens.
- Icons/art: Include an asset creation/replacement plan; emoji placeholders must not be permanent.
- Language: Preserve current Korean/English global toggle and parity.
- Safety: Preserve notch/punch-hole safe-area handling.

**Research Findings**:
- Theme files exist: `app/src/main/java/com/yacoo/rpg/ui/theme/Color.kt`, `Type.kt`.
- Direct sweep found 29 Kotlin files under `app/src/main/java/com/yacoo/rpg`.
- Many UI files still use hardcoded `Color(0x...)`, `.dp`, `.sp`, local gradients, local card shapes, and emoji icons.
- AST-grep was unavailable (`Not connected`), and CLI `sg` was missing; grep/rg findings are the current structural basis.

### Metis Review
**Identified Gaps** (addressed):
- Avoid copying Habby-owned UI/assets; use inspiration only.
- Preserve gameplay, navigation, localization, and safe-area behavior.
- Lock out backend, monetization, gacha probabilities, analytics, and gameplay balance changes.
- Include font scale, Korean text expansion, small-screen/notch, and missing-asset fallback QA.

---

## Work Objectives

### Core Objective
Create and apply a cohesive Habby-inspired visual system that makes the existing RPG UI feel like one polished mobile game while preserving current app behavior.

### Concrete Deliverables
- `ui/theme/Color.kt` and `Type.kt` updated/expanded into coherent design-system tokens.
- New or updated shared UI component APIs in `ui/components/`.
- Updated major screen UI implementations using shared components/tokens.
- Asset pipeline/spec documentation embedded in implementation files or plan-followup notes without adding final copyrighted/external assets.

### Definition of Done
- [ ] `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug` succeeds.
- [ ] All major screens compile and render through existing navigation.
- [ ] Korean/English toggle still applies globally to touched screens.
- [ ] Safe-area handling in Shell/HUD/bottom nav remains intact.
- [ ] Emoji usage is reduced or wrapped behind named asset/icon abstraction with fallback.

### Must Have
- Habby-inspired, not Habby-copied, visual direction.
- Central tokens before screen rewrites.
- Reusable components instead of one-off per-screen styling.
- Screen-by-screen application across Shell, Home, Combat, Gear, Upgrade, Draw/Gacha, Result, RunMap, RewardPick, RunResult.
- Agent-executed QA for every task.

### Must NOT Have (Guardrails)
- No direct copying of Habby-owned characters, icons, logos, layouts, or exact assets.
- No gameplay logic changes, balance changes, gacha probability systems, backend, monetization, analytics, or real-money flows.
- No breaking Korean/English language state.
- No breaking safe-area/notch/punch-hole behavior.
- No permanent emoji dependency for core RPG UI icons.
- No vague “make polished” changes without token/component reference.

---

## Verification Strategy (MANDATORY)

> **ZERO HUMAN INTERVENTION** - ALL verification is agent-executed.

### Test Decision
- **Infrastructure exists**: YES - Gradle Android test dependencies exist, but this plan focuses on visual implementation QA.
- **Automated tests**: Tests-after only if executor finds safe small tests; primary verification is build + emulator/screenshot/manual-agent QA.
- **Framework**: Gradle/Compose/Android instrumentation available; exact test additions are optional unless implementation changes logic.

### QA Policy
Every task below includes agent-executed QA scenarios. Evidence saved to `.omo/evidence/task-{N}-{scenario-slug}.{ext}`.

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Foundation):
├── 1. Design token inventory and palette expansion [quick]
├── 2. Typography, spacing, radius, elevation tokens [quick]
├── 3. Asset/icon pipeline specification and fallback map [writing]
└── 4. Shared game UI component kit [visual-engineering] depends: 1,2,3

Wave 2 (Shell + high-traffic screens):
├── 5. Shell/HUD/bottom navigation polish [visual-engineering] depends: 1,2,4
├── 6. Home screen visual overhaul [visual-engineering] depends: 4,5
├── 8. Gear inventory visual overhaul [visual-engineering] depends: 4
├── 9. Upgrade screen visual overhaul [visual-engineering] depends: 4
├── 10. Draw/Gacha screen visual overhaul [visual-engineering] depends: 4
└── 13. GameArt and DiceView style alignment [visual-engineering] depends: 1,3,4

Wave 3 (Battle + progression/result screens):
├── 7. Combat/Battle screen visual overhaul [visual-engineering] depends: 4,13
├── 11. Result and RunResult screens visual overhaul [visual-engineering] depends: 4,13
└── 12. RunMap and RewardPick screens visual overhaul [visual-engineering] depends: 4,13

Wave 4 (Integration QA after all screen work):
└── 14. Localization, safe-area, responsive QA integration [unspecified-high] depends: 5-13

Wave FINAL:
├── F1. Plan compliance audit (oracle)
├── F2. Code quality review (unspecified-high)
├── F3. Real manual QA (unspecified-high)
└── F4. Scope fidelity check (deep)
```

### Dependency Matrix

- **1**: blocks 4,5,13,14; blocked by none; wave 1
- **2**: blocks 4,5,14; blocked by none; wave 1
- **3**: blocks 4,13,14; blocked by none; wave 1
- **4**: blocks 5,6,7,8,9,10,11,12,13,14; blocked by 1,2,3; wave 1
- **5**: blocks 6,14; blocked by 1,2,4; wave 2
- **6**: blocks 14; blocked by 4,5; wave 2
- **7**: blocks 14; blocked by 4,13; wave 3
- **8**: blocks 14; blocked by 4; wave 2
- **9**: blocks 14; blocked by 4; wave 2
- **10**: blocks 14; blocked by 4; wave 2
- **11**: blocks 14; blocked by 4,13; wave 3
- **12**: blocks 14; blocked by 4,13; wave 3
- **13**: blocks 7,11,12,14; blocked by 1,3,4; wave 2
- **14**: blocks F1-F4; blocked by 5-13; wave 4

### Agent Dispatch Summary

- **Wave 1**: 1 → `quick`, 2 → `quick`, 3 → `writing`, 4 → `visual-engineering`
- **Wave 2**: 5/6/8/9/10/13 → `visual-engineering`
- **Wave 3**: 7/11/12 → `visual-engineering`
- **Wave 4**: 14 → `unspecified-high`
- **Final**: F1 → `oracle`, F2 → `unspecified-high`, F3 → `unspecified-high`, F4 → `deep`

---

## TODOs

> Implementation + verification = ONE task. Every task has exact references and QA.

- [~] 1. Design token inventory and palette expansion

  **What to do**:
  - Audit current palette in `Color.kt` and hardcoded colors found in screen/component files.
  - Create/rename tokens for Habby-style roles: background, surface, outline, primary, secondary, danger, rarity, HUD, card, disabled, text.
  - Keep current art colors only if they fit the new token taxonomy.

  **Must NOT do**:
  - Do not change gameplay, navigation, or strings.
  - Do not copy Habby palettes exactly.

  **Recommended Agent Profile**:
  - **Category**: `quick` — focused token-file and reference cleanup.
  - **Skills**: []
  - **Skills Evaluated but Omitted**: `rayden-code` — React-only, not Compose.

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 with 2,3
  - **Blocks**: 4,5,13,14
  - **Blocked By**: None

  **References**:
  - `app/src/main/java/com/yacoo/rpg/ui/theme/Color.kt` - current color source.
  - `app/src/main/java/com/yacoo/rpg/ui/components/Shell.kt` - HUD/nav local colors to absorb into tokens.
  - `app/src/main/java/com/yacoo/rpg/ui/screens/*.kt` - hardcoded screen palettes found by grep/rg.

  **Acceptance Criteria**:
  - [ ] `Color.kt` contains named role tokens for RPG UI and rarity treatment.
  - [ ] New screen/component changes can reference tokens instead of raw `Color(0x...)`.
  - [ ] `./gradlew assembleDebug` can compile after token changes.

  **QA Scenarios**:
  ```
  Scenario: Token file compiles
    Tool: Bash
    Preconditions: Token edits complete.
    Steps:
      1. Run JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug
      2. Confirm output contains BUILD SUCCESSFUL.
    Expected Result: Build succeeds.
    Failure Indicators: Kotlin compile errors or unresolved color references.
    Evidence: .omo/evidence/task-1-token-build.txt

  Scenario: Raw color reduction check
    Tool: Bash
    Preconditions: Token edits complete.
    Steps:
      1. Run rg -n "Color\\(0x" app/src/main/java/com/yacoo/rpg/ui
      2. Confirm remaining raw colors are either in theme/art token files or documented intentional exceptions.
    Expected Result: No unexplained raw colors in newly touched screen code.
    Evidence: .omo/evidence/task-1-raw-color-check.txt
  ```

  **Commit**: YES
  - Message: `style(design-system): define rpg color tokens`
  - Files: `ui/theme/Color.kt`
  - Pre-commit: Gradle assemble

- [~] 2. Typography, spacing, radius, and elevation tokens

  **What to do**:
  - Expand `Type.kt` and/or add theme token holders for sizes, spacing, corner radii, stroke widths, and elevation/shadow roles.
  - Define scale for: screen title, section title, button, chip, stat label, body, caption.
  - Define token names usable across Compose screens.

  **Must NOT do**:
  - Do not rewrite all screens in this task.
  - Do not introduce external font dependencies unless already available.

  **Recommended Agent Profile**:
  - **Category**: `quick` — foundational theme changes.
  - **Skills**: []
  - **Skills Evaluated but Omitted**: `performance-optimizer` — not a runtime bottleneck task.

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 with 1,3
  - **Blocks**: 4,5,14
  - **Blocked By**: None

  **References**:
  - `app/src/main/java/com/yacoo/rpg/ui/theme/Type.kt` - current typography scale.
  - `app/src/main/java/com/yacoo/rpg/ui/components/Shell.kt:263-297` - button heights/radii currently hardcoded.
  - `app/src/main/java/com/yacoo/rpg/ui/screens/EquipmentScreen.kt` - dense bespoke spacing/radii.

  **Acceptance Criteria**:
  - [ ] Typography/spacing/radius/elevation tokens exist and are named by role.
  - [ ] Shared components can reference token names.
  - [ ] Build succeeds.

  **QA Scenarios**:
  ```
  Scenario: Token usage compiles
    Tool: Bash
    Preconditions: Token definitions complete.
    Steps:
      1. Run JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug
    Expected Result: BUILD SUCCESSFUL.
    Evidence: .omo/evidence/task-2-token-build.txt

  Scenario: Magic-number audit baseline
    Tool: Bash
    Preconditions: Tokens complete.
    Steps:
      1. Run rg -n "[0-9]+\\.dp|[0-9]+\\.sp" app/src/main/java/com/yacoo/rpg/ui
      2. Save output as baseline for later screen tasks.
    Expected Result: Baseline captured; token files explain future replacements.
    Evidence: .omo/evidence/task-2-magic-number-baseline.txt
  ```

  **Commit**: YES
  - Message: `style(design-system): add typography and layout tokens`
  - Files: `ui/theme/Type.kt`, optional token file
  - Pre-commit: Gradle assemble

- [~] 3. Asset/icon pipeline specification and fallback map

  **What to do**:
  - Create a Kotlin-safe abstraction or documented mapping for core RPG icons: home, battle, gear, upgrade, draw, weapon, armor, charm, boots, gold, gem, energy, reward, boss.
  - Specify asset target style: rounded, high-contrast, chunky casual RPG icons; original assets only.
  - Define emoji as temporary fallback, not final visual source.

  **Must NOT do**:
  - Do not import copyrighted or Habby-owned assets.
  - Do not require final asset production in this implementation wave.

  **Recommended Agent Profile**:
  - **Category**: `writing` — asset spec and mapping clarity.
  - **Skills**: []
  - **Skills Evaluated but Omitted**: `python-pptx-generator` — not presentation generation.

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 with 1,2
  - **Blocks**: 4,13,14
  - **Blocked By**: None

  **References**:
  - `app/src/main/java/com/yacoo/rpg/ui/components/Shell.kt:107-117` - bottom nav emoji source.
  - `app/src/main/java/com/yacoo/rpg/ui/components/Shell.kt:330-335` - equipment slot emoji helper.
  - `app/src/main/java/com/yacoo/rpg/ui/screens/RewardPickScreen.kt` - reward emoji mapping.

  **Acceptance Criteria**:
  - [ ] A named icon/fallback map exists or is documented in a code-adjacent way suitable for implementation.
  - [ ] Core UI can migrate away from raw inline emoji.
  - [ ] No external copyrighted assets are added.

  **QA Scenarios**:
  ```
  Scenario: Icon fallback map covers core UI
    Tool: Bash
    Preconditions: Mapping/spec complete.
    Steps:
      1. Search rg -n "🏠|⚔|🛡|🎲|💰|💎|👟|🎁" app/src/main/java/com/yacoo/rpg/ui
      2. Confirm every remaining emoji has mapped fallback rationale.
    Expected Result: No unmapped core emoji remains.
    Evidence: .omo/evidence/task-3-icon-map.txt

  Scenario: No asset copying
    Tool: Bash
    Preconditions: Any asset directories touched.
    Steps:
      1. Run find-like listing through Glob for image/vector files under app/src/main.
      2. Confirm no new files use Habby/Archero/Survivor/Slime names.
    Expected Result: No copied-brand asset names.
    Evidence: .omo/evidence/task-3-asset-scope.txt
  ```

  **Commit**: YES
  - Message: `docs(art): define icon asset fallback plan`
  - Files: icon map/spec file(s)
  - Pre-commit: grep/rg checks

- [~] 4. Shared game UI component kit

  **What to do**:
  - Add/update shared components for `GameScreenSurface`, `GameCard`, `GameButton`, `HudChip`, `SectionHeader`, `ItemSlotCard`, `RarityBadge`, and `GameTab`.
  - Use tokens from tasks 1-3.
  - Preserve existing `PrimaryButton`, `SecondaryButton`, and `HpBar` compatibility or migrate them safely.

  **Must NOT do**:
  - Do not change navigation or gameplay callbacks.
  - Do not make screen-specific components that only work for one page.

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering` — reusable UI component design.
  - **Skills**: []
  - **Skills Evaluated but Omitted**: `rayden-code` — React/Rayden not Compose.

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 1 after 1-3
  - **Blocks**: 5-14
  - **Blocked By**: 1,2,3

  **References**:
  - `app/src/main/java/com/yacoo/rpg/ui/components/Shell.kt` - existing shared buttons and HP bar.
  - `app/src/main/java/com/yacoo/rpg/ui/components/DiceView.kt` - existing reusable visual component.
  - `app/src/main/java/com/yacoo/rpg/ui/screens/EquipmentScreen.kt` - repeated card/chip patterns to extract.

  **Acceptance Criteria**:
  - [ ] Shared components compile and are previewable where practical.
  - [ ] Components support Korean text expansion by allowing flexible width/height where needed.
  - [ ] Components avoid raw colors/sizes unless intentionally tokenized.

  **QA Scenarios**:
  ```
  Scenario: Shared kit compile
    Tool: Bash
    Preconditions: Component kit implemented.
    Steps:
      1. Run JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug
    Expected Result: BUILD SUCCESSFUL.
    Evidence: .omo/evidence/task-4-component-build.txt

  Scenario: Component token check
    Tool: Bash
    Preconditions: Component kit implemented.
    Steps:
      1. Run rg -n "Color\\(0x|[0-9]+\\.dp|[0-9]+\\.sp" app/src/main/java/com/yacoo/rpg/ui/components
      2. Confirm remaining literals are token definitions or documented exceptions.
    Expected Result: Component code is token-driven.
    Evidence: .omo/evidence/task-4-component-token-check.txt
  ```

  **Commit**: YES
  - Message: `style(ui): add shared game component kit`
  - Files: `ui/components/*`, token files
  - Pre-commit: Gradle assemble

- [~] 5. Shell/HUD/bottom navigation polish

  **What to do**:
  - Apply design-system components to `TopStatsBar`, `PlayerPill`, `StatPill`, and `YacooBottomNav`.
  - Preserve `WindowInsets.safeDrawing` behavior.
  - Replace raw nav emoji usage with icon abstraction/fallback from task 3.
  - Keep global Korean/English labels working.

  **Must NOT do**:
  - Do not remove safe-area padding.
  - Do not change route selection behavior.

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering` — high-impact UI polish.
  - **Skills**: []
  - **Skills Evaluated but Omitted**: `bug-hunter` — no bug reproduction needed unless build fails.

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 with 8,9,10,13
  - **Blocks**: 6,14
  - **Blocked By**: 1,2,4

  **References**:
  - `app/src/main/java/com/yacoo/rpg/ui/components/Shell.kt` - target shell implementation.
  - `app/src/main/java/com/yacoo/rpg/navigation/NavGraph.kt` - shell language/screen wiring.
  - `app/src/main/java/com/yacoo/rpg/game/Types.kt:Screen` - nav enum.

  **Acceptance Criteria**:
  - [ ] Shell compiles and bottom nav has 5 polished tabs.
  - [ ] Top HUD remains below notch/punch-hole and bottom nav remains above navigation bar.
  - [ ] Korean/English nav labels still switch.

  **QA Scenarios**:
  ```
  Scenario: Shell build and route labels
    Tool: Bash
    Preconditions: Shell polish complete.
    Steps:
      1. Run Gradle assembleDebug with Android Studio JBR.
      2. Search for language-driven label path in Shell.kt.
    Expected Result: Build succeeds and Shell still consumes AppLanguage.
    Evidence: .omo/evidence/task-5-shell-build.txt

  Scenario: Safe-area preservation
    Tool: Bash
    Preconditions: Shell polish complete.
    Steps:
      1. Run rg -n "WindowInsets.safeDrawing|windowInsetsPadding" app/src/main/java/com/yacoo/rpg/ui/components/Shell.kt
      2. Confirm top and bottom inset handling remain.
    Expected Result: Both top and bottom safeDrawing padding present.
    Evidence: .omo/evidence/task-5-safe-area.txt
  ```

  **Commit**: YES
  - Message: `style(shell): polish hud and bottom navigation`
  - Files: `Shell.kt`, optional icon map
  - Pre-commit: Gradle assemble

- [~] 6. Home screen visual overhaul

  **What to do**:
  - Rebuild Home around the shared screen surface, hero stage panel, polished start CTA, compact gear summary, and settings button.
  - Preserve language popup and global `AppLanguage` wiring.
  - Preserve callbacks: start combat, navigate, reset.

  **Must NOT do**:
  - Do not add new gameplay systems.
  - Do not move Draw/Gacha back into Home; Draw remains bottom-tab page.

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering` — main art-direction pilot screen.
  - **Skills**: []
  - **Skills Evaluated but Omitted**: `performance-optimizer` — static UI polish first.

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 after 5
  - **Blocks**: 14
  - **Blocked By**: 4,5

  **References**:
  - `app/src/main/java/com/yacoo/rpg/ui/screens/HomeScreen.kt` - target screen.
  - `app/src/main/java/com/yacoo/rpg/ui/components/GameArt.kt` - arena/hero art source.
  - `app/src/main/java/com/yacoo/rpg/navigation/NavGraph.kt` - Home callbacks/language.

  **Acceptance Criteria**:
  - [ ] Home uses shared components/tokens for background, cards, CTAs, chips.
  - [ ] Settings language popup still opens and changes global language.
  - [ ] Gear/Upgrade buttons still navigate correctly.

  **QA Scenarios**:
  ```
  Scenario: Home compiles and keeps callbacks
    Tool: Bash
    Preconditions: Home overhaul complete.
    Steps:
      1. Run Gradle assembleDebug.
      2. Search HomeScreen.kt for onStartCombat, onNavigate, onReset usage.
    Expected Result: Build succeeds and callbacks remain wired.
    Evidence: .omo/evidence/task-6-home-build.txt

  Scenario: Language popup preserved
    Tool: Bash
    Preconditions: Home overhaul complete.
    Steps:
      1. Search HomeScreen.kt for AlertDialog or replacement settings popup.
      2. Confirm onLanguageChange(AppLanguage) is still called.
    Expected Result: Global language change path remains.
    Evidence: .omo/evidence/task-6-language-popup.txt
  ```

  **Commit**: YES
  - Message: `style(home): apply casual rpg home design`
  - Files: `HomeScreen.kt`
  - Pre-commit: Gradle assemble

- [~] 7. Combat/Battle screen visual overhaul

  **What to do**:
  - Apply shared components to battle header, arena, HP bars, dice area, roll button, and attack list.
  - Improve readability and hierarchy while keeping Yahtzee/combat logic unchanged.
  - Align hero/monster art with task 13 outputs.

  **Must NOT do**:
  - Do not change dice, combat, enemy, HP, reward, or turn logic.
  - Do not alter result routing.

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering` — dense game UI polish.
  - **Skills**: []
  - **Skills Evaluated but Omitted**: `bug-hunter` — only use if regressions appear.

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 3 with 11,12
  - **Blocks**: 14
  - **Blocked By**: 4,13

  **References**:
  - `app/src/main/java/com/yacoo/rpg/ui/screens/CombatScreen.kt` - target battle UI.
  - `app/src/main/java/com/yacoo/rpg/ui/components/DiceView.kt` - dice components.
  - `app/src/main/java/com/yacoo/rpg/game/Combat.kt`, `Yahtzee.kt` - logic to preserve.

  **Acceptance Criteria**:
  - [ ] Combat screen compiles and all existing callbacks remain.
  - [ ] Roll/attack UI is visually grouped and readable.
  - [ ] No game logic files change unless strictly necessary for labels.

  **QA Scenarios**:
  ```
  Scenario: Combat UI compile and logic untouched
    Tool: Bash
    Preconditions: Combat overhaul complete.
    Steps:
      1. Run Gradle assembleDebug.
      2. Run git diff -- app/src/main/java/com/yacoo/rpg/game/Combat.kt app/src/main/java/com/yacoo/rpg/game/Yahtzee.kt.
    Expected Result: Build succeeds; no gameplay diff unless explicitly justified.
    Evidence: .omo/evidence/task-7-combat-build.txt

  Scenario: Battle controls still present
    Tool: Bash
    Preconditions: Combat overhaul complete.
    Steps:
      1. Search CombatScreen.kt for roll button callback and finish callback references.
      2. Confirm onFinish remains wired.
    Expected Result: Controls and result path remain.
    Evidence: .omo/evidence/task-7-combat-callbacks.txt
  ```

  **Commit**: YES
  - Message: `style(combat): polish battle screen hierarchy`
  - Files: `CombatScreen.kt`, optional components
  - Pre-commit: Gradle assemble

- [~] 8. Gear inventory visual overhaul

  **What to do**:
  - Apply design system to Habby-style gear inventory already present.
  - Replace bespoke card/chip styles with shared item slot, rarity badge, and detail panel components.
  - Keep current limitation clear: only equipped/current items exist; no unequip bag model yet.

  **Must NOT do**:
  - Do not add unequip/remove logic.
  - Do not change equipment data model.

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering` — inventory screen polish.
  - **Skills**: []
  - **Skills Evaluated but Omitted**: `api-endpoint-builder` — no API work.

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 with 5,9,10,13
  - **Blocks**: 14
  - **Blocked By**: 4

  **References**:
  - `app/src/main/java/com/yacoo/rpg/ui/screens/EquipmentScreen.kt` - target gear UI.
  - `app/src/main/java/com/yacoo/rpg/game/Equipment.kt` - equipment model/logic to preserve.
  - `app/src/main/java/com/yacoo/rpg/ui/components/GameArt.kt` - hero paperdoll source.

  **Acceptance Criteria**:
  - [ ] Gear screen uses shared item/card components where practical.
  - [ ] Korean/English labels still switch.
  - [ ] No equipment model or upgrade logic changes.

  **QA Scenarios**:
  ```
  Scenario: Gear compile and model untouched
    Tool: Bash
    Preconditions: Gear polish complete.
    Steps:
      1. Run Gradle assembleDebug.
      2. Run git diff -- app/src/main/java/com/yacoo/rpg/game/Equipment.kt.
    Expected Result: Build succeeds; equipment logic unchanged.
    Evidence: .omo/evidence/task-8-gear-build.txt

  Scenario: Gear localization preserved
    Tool: Bash
    Preconditions: Gear polish complete.
    Steps:
      1. Search EquipmentScreen.kt for AppLanguage and labels mapping.
      2. Confirm Korean and English label sets still exist.
    Expected Result: Gear still consumes global language.
    Evidence: .omo/evidence/task-8-gear-localization.txt
  ```

  **Commit**: YES
  - Message: `style(gear): align inventory with design system`
  - Files: `EquipmentScreen.kt`, optional shared components
  - Pre-commit: Gradle assemble

- [~] 9. Upgrade screen visual overhaul

  **What to do**:
  - Apply shared cards, stat badges, CTA buttons, and affordability states to Upgrade.
  - Keep Korean/English labels and bonus label mapping.
  - Make insufficient-gold and max-level states visually distinct.

  **Must NOT do**:
  - Do not change upgrade costs, level cap, or coin deduction logic.
  - Do not add new currencies.

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering` — small screen but visual-state heavy.
  - **Skills**: []
  - **Skills Evaluated but Omitted**: `performance-optimizer` — no performance issue.

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 with 5,8,10,13
  - **Blocks**: 14
  - **Blocked By**: 4

  **References**:
  - `app/src/main/java/com/yacoo/rpg/ui/screens/UpgradeScreen.kt` - target screen.
  - `app/src/main/java/com/yacoo/rpg/game/Equipment.kt:upgradeEquipment` - logic to preserve.
  - `app/src/main/java/com/yacoo/rpg/navigation/NavGraph.kt` - language wiring.

  **Acceptance Criteria**:
  - [ ] Upgrade screen uses design-system card/button states.
  - [ ] Existing can-upgrade, need-coins, max-level behavior is preserved.
  - [ ] Korean/English still applies.

  **QA Scenarios**:
  ```
  Scenario: Upgrade build and logic untouched
    Tool: Bash
    Preconditions: Upgrade polish complete.
    Steps:
      1. Run Gradle assembleDebug.
      2. Run git diff -- app/src/main/java/com/yacoo/rpg/game/Equipment.kt.
    Expected Result: Build succeeds; upgrade logic unchanged.
    Evidence: .omo/evidence/task-9-upgrade-build.txt

  Scenario: Upgrade state labels present
    Tool: Bash
    Preconditions: Upgrade polish complete.
    Steps:
      1. Search UpgradeScreen.kt for max, need, upgrade label mapping.
      2. Confirm AppLanguage labels are still passed from NavGraph.
    Expected Result: Upgrade states are localized.
    Evidence: .omo/evidence/task-9-upgrade-localization.txt
  ```

  **Commit**: YES
  - Message: `style(upgrade): polish upgrade card states`
  - Files: `UpgradeScreen.kt`
  - Pre-commit: Gradle assemble

- [~] 10. Draw/Gacha screen visual overhaul

  **What to do**:
  - Apply design system to Gacha page with weapon/armor tabs.
  - Make the page visually ready for future draws while disabled/placeholder mechanics remain explicit.
  - Use asset/icon abstraction for weapon/armor draw visuals.

  **Must NOT do**:
  - Do not implement real draw probabilities, rewards, purchases, or backend.
  - Do not add real-money wording.

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering` — RPG shop-like UI without monetization logic.
  - **Skills**: []
  - **Skills Evaluated but Omitted**: `api-endpoint-builder` — no API endpoint.

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 with 5,8,9,13
  - **Blocks**: 14
  - **Blocked By**: 4

  **References**:
  - `app/src/main/java/com/yacoo/rpg/ui/screens/GachaScreen.kt` - target page.
  - `app/src/main/java/com/yacoo/rpg/ui/components/Shell.kt` - Draw bottom tab.
  - `app/src/main/java/com/yacoo/rpg/navigation/NavGraph.kt` - language and route wiring.

  **Acceptance Criteria**:
  - [ ] Gacha page uses polished tabs/cards/resource chips.
  - [ ] Weapon/Armor tabs remain one page.
  - [ ] Mechanics remain disabled/placeholder only.

  **QA Scenarios**:
  ```
  Scenario: Gacha compile and no mechanics added
    Tool: Bash
    Preconditions: Gacha polish complete.
    Steps:
      1. Run Gradle assembleDebug.
      2. Search GachaScreen.kt for probability, purchase, backend, API, random reward terms.
    Expected Result: Build succeeds; no real mechanics added.
    Evidence: .omo/evidence/task-10-gacha-scope.txt

  Scenario: Gacha localization preserved
    Tool: Bash
    Preconditions: Gacha polish complete.
    Steps:
      1. Search GachaScreen.kt for AppLanguage and Korean/English labels.
    Expected Result: Draw page remains localized.
    Evidence: .omo/evidence/task-10-gacha-localization.txt
  ```

  **Commit**: YES
  - Message: `style(draw): polish equipment draw page`
  - Files: `GachaScreen.kt`
  - Pre-commit: Gradle assemble

- [~] 11. Result and RunResult screens visual overhaul

  **What to do**:
  - Apply victory/defeat/result screen tokens, banners, rewards panel, and CTA components.
  - Create clear visual hierarchy for win, loss, coins, duplicate item bonus, and return actions.
  - Align icons with asset fallback map.

  **Must NOT do**:
  - Do not change reward calculation, claim logic, or route behavior.
  - Do not add new result types.

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering` — celebration/failure UI polish.
  - **Skills**: []
  - **Skills Evaluated but Omitted**: `bug-hunter` — no known logic bug.

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 3 with 7,12
  - **Blocks**: 14
  - **Blocked By**: 4,13

  **References**:
  - `app/src/main/java/com/yacoo/rpg/ui/screens/ResultScreen.kt` - combat result UI.
  - `app/src/main/java/com/yacoo/rpg/ui/screens/RunResultScreen.kt` - run result UI.
  - `app/src/main/java/com/yacoo/rpg/game/AppState.kt` - reward/result state to preserve.

  **Acceptance Criteria**:
  - [ ] Result and RunResult use shared result/card/CTA components.
  - [ ] Reward values display unchanged.
  - [ ] Build succeeds.

  **QA Scenarios**:
  ```
  Scenario: Result screens compile
    Tool: Bash
    Preconditions: Result polish complete.
    Steps:
      1. Run Gradle assembleDebug.
      2. Search ResultScreen.kt and RunResultScreen.kt for existing reward value fields.
    Expected Result: Build succeeds; displayed values still sourced from existing result/meta data.
    Evidence: .omo/evidence/task-11-result-build.txt

  Scenario: Result logic untouched
    Tool: Bash
    Preconditions: Result polish complete.
    Steps:
      1. Run git diff -- app/src/main/java/com/yacoo/rpg/game/AppState.kt app/src/main/java/com/yacoo/rpg/game/Rewards.kt
    Expected Result: No reward/result logic changes unless explicitly justified.
    Evidence: .omo/evidence/task-11-result-logic.txt
  ```

  **Commit**: YES
  - Message: `style(result): polish win loss and run result screens`
  - Files: `ResultScreen.kt`, `RunResultScreen.kt`
  - Pre-commit: Gradle assemble

- [~] 12. RunMap and RewardPick screens visual overhaul

  **What to do**:
  - Apply design-system map nodes, progress connectors, reward choice cards, and CTA states.
  - Make node types visually distinct with tokenized icon/rarity treatment.
  - Keep current run/reward pick behavior unchanged.

  **Must NOT do**:
  - Do not change map generation, reward selection, healing, dice, reroll, or scrap logic.
  - Do not add new node types.

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering` — progression-map and reward card UI.
  - **Skills**: []
  - **Skills Evaluated but Omitted**: `api-endpoint-builder` — no backend/API.

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 3 with 7,11
  - **Blocks**: 14
  - **Blocked By**: 4,13

  **References**:
  - `app/src/main/java/com/yacoo/rpg/ui/screens/RunMapScreen.kt` - map UI.
  - `app/src/main/java/com/yacoo/rpg/ui/screens/RewardPickScreen.kt` - reward choices.
  - `app/src/main/java/com/yacoo/rpg/game/Run.kt`, `Rewards.kt` - logic to preserve.

  **Acceptance Criteria**:
  - [ ] RunMap and RewardPick use shared card/chip/icon components.
  - [ ] Node/reward logic remains untouched.
  - [ ] Empty/no-reward state is visually handled.

  **QA Scenarios**:
  ```
  Scenario: Run screens compile
    Tool: Bash
    Preconditions: RunMap/RewardPick polish complete.
    Steps:
      1. Run Gradle assembleDebug.
      2. Search RunMapScreen.kt for onStartNode and RewardPickScreen.kt for onPickReward.
    Expected Result: Build succeeds; callbacks remain.
    Evidence: .omo/evidence/task-12-run-screens-build.txt

  Scenario: Run logic untouched
    Tool: Bash
    Preconditions: RunMap/RewardPick polish complete.
    Steps:
      1. Run git diff -- app/src/main/java/com/yacoo/rpg/game/Run.kt app/src/main/java/com/yacoo/rpg/game/Rewards.kt
    Expected Result: No run/reward logic changes unless explicitly justified.
    Evidence: .omo/evidence/task-12-run-logic.txt
  ```

  **Commit**: YES
  - Message: `style(run): polish map and reward screens`
  - Files: `RunMapScreen.kt`, `RewardPickScreen.kt`
  - Pre-commit: Gradle assemble

- [~] 13. GameArt and DiceView style alignment

  **What to do**:
  - Align `GameArt.kt` and `DiceView.kt` with new tokens and asset direction.
  - Improve outline/stroke/shadow consistency for hero, monster, arena, dice, and rarity/held states.
  - Keep Canvas-based placeholders acceptable but name them as transitional art.

  **Must NOT do**:
  - Do not replace with copied external sprites.
  - Do not change dice values or held/rolling semantics.

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering` — visual-art component consistency.
  - **Skills**: []
  - **Skills Evaluated but Omitted**: `performance-optimizer` — revisit only if Canvas perf regresses.

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 with 5,8,9,10
  - **Blocks**: 7,11,12,14
  - **Blocked By**: 1,3,4

  **References**:
  - `app/src/main/java/com/yacoo/rpg/ui/components/GameArt.kt` - Canvas art.
  - `app/src/main/java/com/yacoo/rpg/ui/components/DiceView.kt` - dice visuals.
  - `app/src/main/java/com/yacoo/rpg/ui/theme/Color.kt` - art/token palette.

  **Acceptance Criteria**:
  - [ ] Art components use consistent tokenized outlines/shadows/fills.
  - [ ] DiceView remains functionally identical.
  - [ ] Build succeeds.

  **QA Scenarios**:
  ```
  Scenario: Art component compile
    Tool: Bash
    Preconditions: GameArt/DiceView alignment complete.
    Steps:
      1. Run Gradle assembleDebug.
    Expected Result: BUILD SUCCESSFUL.
    Evidence: .omo/evidence/task-13-art-build.txt

  Scenario: Dice semantics preserved
    Tool: Bash
    Preconditions: DiceView alignment complete.
    Steps:
      1. Search DiceView.kt for DieValue, held, rolling parameters.
      2. Confirm public composable parameters are still available.
    Expected Result: Existing dice callers remain compatible.
    Evidence: .omo/evidence/task-13-dice-api.txt
  ```

  **Commit**: YES
  - Message: `style(art): align canvas art and dice visuals`
  - Files: `GameArt.kt`, `DiceView.kt`, token files
  - Pre-commit: Gradle assemble

- [~] 14. Localization, safe-area, responsive QA integration

  **What to do**:
  - Perform final integration pass across touched screens.
  - Check Korean/English labels, text expansion, font-scale risks, safe-area behavior, small-screen layout, and missing icon fallback.
  - Remove or document remaining hardcoded visual literals.

  **Must NOT do**:
  - Do not introduce new UI features.
  - Do not bypass previous screen task decisions.

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high` — cross-screen QA/integration.
  - **Skills**: []
  - **Skills Evaluated but Omitted**: `security-review` — no security scope.

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 4 final integration
  - **Blocks**: F1-F4
  - **Blocked By**: 5,6,7,8,9,10,11,12,13

  **References**:
  - `app/src/main/java/com/yacoo/rpg/navigation/NavGraph.kt` - global wiring.
  - `app/src/main/java/com/yacoo/rpg/viewmodel/GameViewModel.kt` - global language state.
  - `app/src/main/java/com/yacoo/rpg/ui/components/Shell.kt` - safe-area and shell behavior.
  - All touched screen files.

  **Acceptance Criteria**:
  - [ ] Build succeeds.
  - [ ] Korean/English maps exist for touched localized screens.
  - [ ] Shell still uses safeDrawing insets.
  - [ ] Hardcoded visual literals outside token/art exceptions are minimized and documented.

  **QA Scenarios**:
  ```
  Scenario: Full integration build
    Tool: Bash
    Preconditions: All screen tasks complete.
    Steps:
      1. Run JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug
      2. Confirm BUILD SUCCESSFUL.
    Expected Result: Full app compiles.
    Evidence: .omo/evidence/task-14-full-build.txt

  Scenario: Localization and safe-area grep audit
    Tool: Bash
    Preconditions: All screen tasks complete.
    Steps:
      1. Run rg -n "AppLanguage|WindowInsets.safeDrawing|windowInsetsPadding" app/src/main/java/com/yacoo/rpg
      2. Confirm language wiring and safe-area handling remain.
    Expected Result: Global language and safe-area paths remain visible.
    Evidence: .omo/evidence/task-14-localization-safearea.txt

  Scenario: Visual literal final audit
    Tool: Bash
    Preconditions: All screen tasks complete.
    Steps:
      1. Run rg -n "Color\\(0x|[0-9]+\\.dp|[0-9]+\\.sp" app/src/main/java/com/yacoo/rpg/ui
      2. Classify remaining results as token files, art exceptions, or issues.
    Expected Result: No unexplained one-off visual literals in polished screen code.
    Evidence: .omo/evidence/task-14-visual-literal-audit.txt
  ```

  **Commit**: YES
  - Message: `style(ui): verify responsive localized design system`
  - Files: all touched UI files as needed
  - Pre-commit: Gradle assemble

---

## Final Verification Wave (MANDATORY — after ALL implementation tasks)

- [~] F1. **Plan Compliance Audit** — `oracle`
  Read this plan end-to-end. Verify every Must Have appears in implementation. Verify every Must NOT Have is absent via code search. Check evidence files exist. Output: `Must Have [N/N] | Must NOT Have [N/N] | Tasks [N/N] | VERDICT: APPROVE/REJECT`.
  **Acceptance Criteria**:
  - [ ] All Must Have items map to implemented files or evidence.
  - [ ] All Must NOT Have items are absent by grep/diff inspection.
  - [ ] Every task evidence path exists or rejection cites missing evidence.
  - [ ] Verdict is `APPROVE` only when all checks pass.

- [~] F2. **Code Quality Review** — `unspecified-high`
  Run build and inspect changed Kotlin files for unused imports, hardcoded visual literals left outside token files, broken localization patterns, and excessive one-off components. Output: `Build [PASS/FAIL] | Files [N clean/N issues] | VERDICT`.
  **Acceptance Criteria**:
  - [ ] Gradle build passes.
  - [ ] No unresolved imports or compile warnings introduced by touched files.
  - [ ] New hardcoded visual literals are limited to token/art exception files or are rejected with file:line.
  - [ ] Verdict is `PASS` only when build and file review pass.

- [~] F3. **Real Manual QA** — `unspecified-high`
  Run app on emulator if available. Navigate Home → Battle → Gear → Upgrade → Draw → Result/Run flows. Capture screenshots to `.omo/evidence/final-qa/`. Verify Korean/English and safe-area behavior. Output: `Screens [N/N pass] | Localization [PASS/FAIL] | Safe Area [PASS/FAIL] | VERDICT`.
  **Acceptance Criteria**:
  - [ ] Screenshots or terminal evidence exist for every navigated screen.
  - [ ] Korean and English states are both exercised.
  - [ ] Safe-area screenshots/evidence show HUD/nav not overlapping system bars.
  - [ ] Verdict is `PASS` only when all listed screens pass smoke QA.

- [~] F4. **Scope Fidelity Check** — `deep`
  Compare actual diff to this plan. Reject gameplay/backend/monetization/gacha probability changes. Verify no Habby asset copying. Output: `Tasks [N/N compliant] | Scope Creep [CLEAN/N issues] | VERDICT`.
  **Acceptance Criteria**:
  - [ ] Every changed file maps to one or more plan tasks.
  - [ ] No gameplay/backend/monetization/probability changes are present.
  - [ ] No copied Habby/third-party branded asset names or references appear.
  - [ ] Verdict is `PASS` only when all changed files are in scope.

---

## Commit Strategy

- **Wave 1**: `style(design-system): add casual rpg visual tokens and components`
- **Wave 2**: `style(ui): apply design system to shell and primary screens`
- **Wave 3**: `style(ui): polish battle and progression screens`
- **Final**: `chore(qa): verify design-system overhaul`

---

## Success Criteria

### Verification Commands
```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug
```

### Final Checklist
- [ ] Build succeeds.
- [ ] Shell/HUD/nav remain safe-area aware.
- [ ] Korean/English toggle still works across touched screens.
- [ ] Design tokens are the source of truth for new colors/sizes/radii.
- [ ] Major screens use shared components where practical.
- [ ] Emoji/icon usage has named fallback strategy.
- [ ] No gameplay, backend, monetization, or probability changes.
