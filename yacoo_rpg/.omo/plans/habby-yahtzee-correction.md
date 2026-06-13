# Habby-Style Yahtzee Combat Correction

## TL;DR
> **Summary**: Rework the current prototype so it no longer feels like a generic pastel card app and no longer auto-resolves the best Yahtzee hand. The combat dice phase becomes a direct player choice loop: roll, hold, reroll, then choose the attack category.
> **Deliverables**:
> - Explicit Yahtzee category selection mechanics in combat
> - CSS/SVG original casual RPG visual remake
> - Updated unit/component/Playwright QA proving selected category drives attack
> - Updated Korean docs describing the corrected gameplay
> **Effort**: Medium
> **Parallel**: YES - 5 waves
> **Critical Path**: Task 1 + Task 3 → Task 2 → Task 4 + Task 5 → Task 6 → Task 7 → Final Verification

## Context

### Original Correction Request
User rejected the completed prototype: graphics do not feel like Habby game-company style, and the Yahtzee interaction is wrong because the player should directly match/use Yahtzee logic instead of a weird automatic best-hand attack.

### Confirmed Decisions
- Rework scope: full rework of combat UX + Yahtzee system + visuals.
- Yahtzee interaction: after rolling, player directly chooses an enabled category/hand to attack with.
- Category lockout: categories are reusable every dice charge; no per-combat/per-stage scorecard lockout.
- Visual scope: CSS/SVG original remake; no copyrighted asset copying.
- Keep web prototype only.

### Metis Review (gaps addressed)
- Defined exact category list, category validity, and damage formula.
- Added guardrail to remove old `Resolve Attack`-only primary flow.
- Added QA proving selected category, not auto-best-hand, drives damage.
- Added edge cases: category before first roll, max rolls, all-held reroll, invalid disabled category, post-attack reset, reusable categories.

### Oracle Review
- Phase 1 initial verdict: `NO-GO` because damage/scoring rules per category were unspecified.
- Fix applied: exact formula and multiplier table frozen.
- Phase 1 retry verdict: `GO`.

## Work Objectives

### Core Objective
Make the prototype feel like a deliberate casual mobile RPG combat game, and make Yahtzee interaction player-driven: the user must roll/hold/reroll and choose the attack category themselves.

### Deliverables
- `src/game/yahtzee.ts`: category validity helpers and selected-category support.
- `src/game/combat.ts`: selected-category damage path.
- `src/screens/CombatScreen.tsx`: category selection board replacing auto-best `Resolve Attack` flow.
- `src/components/ui.tsx` and/or new `src/components/GameArt.tsx`: SVG/CSS art components.
- `src/styles.css`: stronger mobile RPG presentation.
- Tests covering selected category mechanics.
- `scripts/qa.mjs`: browser QA for direct category choice.
- `docs/README.ko.md`: corrected gameplay explanation.

### Definition of Done
- `npm test -- --run` exits `0`.
- `npm run build` exits `0`.
- `npm run dev -- --host 127.0.0.1` launches app.
- `npm run qa` exits `0` after dev server is running.
- Combat dice panel shows category buttons after first roll.
- Player can choose `Pair`, `Three of a Kind`, `Full House`, etc. only when valid; `Chance` is always available after first roll.
- Damage uses selected category multiplier, not automatic highest hand.
- Category remains available on the next dice charge.
- Screenshots/evidence saved under `.omo/evidence/`.

### Must Have
- 5 dice.
- Up to 3 rolls per dice charge.
- Hold/unhold individual dice.
- Category buttons shown with clear enabled/disabled states.
- Player can attack after roll 1, 2, or 3.
- Invalid categories disabled, not selectable.
- `chance` enabled after first roll.
- Dice state resets after selected attack or cancel.
- Auto-battle pauses while dice panel is open and resumes after attack/cancel.
- CSS/SVG original visuals: chunky buttons, layered battle scene, reward burst, red-dot upgrade prompt, damage number feedback.

### Must NOT Have
- No backend, auth, cloud save, ads, IAP, shop, gacha, battle pass, native app, copyrighted asset copying.
- No full 13-category Yahtzee scorecard unless explicitly requested later.
- No category lockouts; user chose reusable categories.
- No old primary flow where app auto-picks best hand and user only clicks `Resolve Attack`.
- No new routing/global state libraries.

## Frozen Corrected Mechanics

### Attack Categories
Use these 9 RPG attack categories:

| Category | Valid When | Multiplier |
|---|---|---:|
| `chance` | any 5 dice after first roll | 1.0 |
| `pair` | at least one pair | 1.2 |
| `twoPair` | at least two distinct pairs | 1.5 |
| `threeKind` | at least three of same value | 1.8 |
| `smallStraight` | 1-2-3-4, 2-3-4-5, or 3-4-5-6 | 2.1 |
| `fullHouse` | exactly 3 + 2 | 2.5 |
| `fourKind` | at least four of same value | 3.2 |
| `largeStraight` | 1-2-3-4-5 or 2-3-4-5-6 | 3.8 |
| `yahtzee` | five of a kind | 6.0 |

### Damage Formula
`floor((heroAttack + weaponAttackBonus) * selectedCategoryMultiplier * (1 + charmDiceBonus))`

Selected category controls multiplier. If dice qualify for multiple categories, player may choose any enabled category. Example: `[2,2,2,2,5]` enables `chance`, `pair`, `threeKind`, and `fourKind`; choosing `pair` must apply `1.2`, choosing `fourKind` must apply `3.2`.

### Dice Phase State Machine
1. Dice charge becomes ready after existing charge timer.
2. Player opens dice panel; combat timers pause.
3. Roll count starts at `0`; no category enabled before first roll.
4. Player clicks `Roll Dice`; 5 dice appear; roll count `1`.
5. Player can hold/unhold dice by clicking individual dice.
6. Player may reroll unheld dice until roll count `3`.
7. After any roll, category board shows enabled/disabled categories.
8. Player clicks enabled category button: damage applies immediately using selected multiplier.
9. Dice panel closes; dice/holds/roll count/category state resets; dice charge consumed; auto-battle resumes.
10. Next dice charge repeats with all categories reusable.

## Verification Strategy
> ZERO HUMAN INTERVENTION FOR QA EXECUTION - all verification is agent-executed.
- Unit tests: category validity, selected damage, reroll/hold edge cases.
- Component tests: Combat selected category flow where feasible.
- Browser QA: direct category choice, visual state, persistence loop.
- Evidence: `.omo/evidence/habby-yahtzee-task-{N}-{slug}.{ext}`.

## Execution Strategy

### Parallel Execution Waves
Wave 1: Task 1 mechanics helpers, Task 3 visual art tokens/components.
Wave 2: Task 2 combat selected-category flow.
Wave 3: Task 4 screen visual remake, Task 5 tests.
Wave 4: Task 6 QA/docs.
Wave 5: Task 7 cleanup/regression.
Final Verification Wave: F1-F4 in parallel.

### Dependency Matrix
| Task | Depends On | Blocks |
|---|---|---|
| 1 | none | 2,5,7 |
| 2 | 1 | 5,6,7 |
| 3 | none | 4,6,7 |
| 4 | 2,3 | 6,7 |
| 5 | 1,2 | 7 |
| 6 | 2,4,5 | 7 |
| 7 | 4,5,6 | Final Verification |

### Agent Dispatch Summary
- Wave 1 → 2 tasks → `unspecified-high`, `visual-engineering`
- Wave 2 → 1 task → `unspecified-high`
- Wave 3 → 2 tasks → `visual-engineering`, `unspecified-high`
- Wave 4 → 1 task → `writing`
- Wave 5 → 1 task → `unspecified-high`

## TODOs

- [x] 1. Add selectable Yahtzee category mechanics

  **What to do**: Update `src/game/types.ts`, `src/game/constants.ts`, `src/game/yahtzee.ts`, and `src/game/combat.ts`. Add `YahtzeeAttackCategory` if needed, `ATTACK_CATEGORIES`, `getValidAttackCategories(dice)`, `isAttackCategoryValid(dice, category)`, and `calculateSelectedCategoryDamage(equipment, category)`. Keep existing evaluator if useful, but it must not drive combat attack automatically.
  **Must NOT do**: Do not add full 13-category scorecard. Do not add category lockouts. Do not use auto-best hand as attack resolver.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - core deterministic mechanics.
  - Skills: [] - no specialized skill required.
  - Omitted: [`api-endpoint-builder`] - no backend.

  **Parallelization**: Can Parallel: YES | Wave 1 | Blocks: 2,5,7 | Blocked By: none

  **References**:
  - Current evaluator: `src/game/yahtzee.ts:37` - currently returns highest hand only.
  - Current multipliers: `src/game/constants.ts:33` - reuse exact table.
  - Current damage: `src/game/combat.ts:18` - adapt to selected category.

  **Acceptance Criteria**:
  - [ ] `npm test -- --run src/game/yahtzee.test.ts src/game/combat.test.ts` exits `0`.
  - [ ] `[2,2,2,2,5]` enables `chance`, `pair`, `threeKind`, `fourKind`, but not `fullHouse`, `largeStraight`, `yahtzee`.
  - [ ] `[2,2,2,3,3]` enables `chance`, `pair`, `twoPair`, `threeKind`, `fullHouse`.
  - [ ] `[1,2,3,4,5]` enables `chance`, `smallStraight`, `largeStraight`.
  - [ ] Selected `pair` on `[2,2,2,2,5]` uses `1.2`, not automatic `3.2`.

  **QA Scenarios**:
  ```
  Scenario: Category validity is deterministic
    Tool: Bash
    Steps: Run `npm test -- --run src/game/yahtzee.test.ts`.
    Expected: Exact enabled/disabled category fixtures pass.
    Evidence: .omo/evidence/habby-yahtzee-task-1-category-tests.txt

  Scenario: Selected category controls damage
    Tool: Bash
    Steps: Run `npm test -- --run src/game/combat.test.ts`.
    Expected: Same dice fixture can produce lower/higher damage depending on selected category.
    Evidence: .omo/evidence/habby-yahtzee-task-1-damage-tests.txt
  ```

  **Commit**: NO | Message: `feat(game): add selectable yahtzee attack categories` | Files: `src/game/types.ts`, `src/game/constants.ts`, `src/game/yahtzee.ts`, `src/game/combat.ts`, `src/game/*.test.ts`

- [x] 2. Replace Combat dice panel with direct category selection

  **What to do**: Update `src/screens/CombatScreen.tsx`. Replace `Best hand` + `Resolve Attack` as the primary action with a category board titled `Choose Attack Hand`. Each category button shows label, multiplier, and disabled reason. After first roll, enabled categories are clickable. On click, apply selected-category damage, show message like `Pair attack x1.2 dealt 16`, close/reset dice, consume charge, resume combat. Keep `Cancel Dice`. Add DEV-only deterministic fixture controls: `Force Pair Fixture` sets dice to `[2,2,2,2,5]`, rolls to `1`, opens dice panel, and clears holds; `Force Full House Fixture` sets dice to `[2,2,2,3,3]`, rolls to `1`, opens dice panel, and clears holds. These controls must be hidden in production because they are gated by `import.meta.env.DEV`.
  **Must NOT do**: Do not leave the old flow where the user only clicks `Resolve Attack` and the app picks the highest hand. Do not allow category click before first roll. Do not allow invalid category click.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - timer/state-heavy React interaction.
  - Skills: [] - no specialized skill required.
  - Omitted: [`performance-optimizer`] - no measured bottleneck.

  **Parallelization**: Can Parallel: NO | Wave 2 | Blocks: 5,6,7 | Blocked By: 1

  **References**:
  - Current auto-resolve: `src/screens/CombatScreen.tsx:27`, `src/screens/CombatScreen.tsx:91`, `src/screens/CombatScreen.tsx:135-137`.
  - Dice hold UI: `src/components/ui.tsx:88`.
  - Frozen state machine: `Frozen Corrected Mechanics > Dice Phase State Machine`.

  **Acceptance Criteria**:
  - [ ] `npm run build` exits `0`.
  - [ ] Category board appears after dice panel opens.
  - [ ] Before first roll, all category buttons disabled.
  - [ ] After first roll, `Chance` enabled.
  - [ ] Invalid categories disabled with visible disabled styling.
  - [ ] Selected category button applies damage and resets dice state.
  - [ ] Next dice charge can choose same category again.
  - [ ] DEV-only `Force Pair Fixture` and `Force Full House Fixture` controls exist during `npm run dev` and are absent from production build UI.

  **QA Scenarios**:
  ```
  Scenario: Player selects category instead of auto-best hand
    Tool: Playwright
    Steps: Start dev server; Start Combat; click `Force Pair Fixture`; click `Pair x1.2`.
    Expected: Message says Pair attack; damage matches pair multiplier, not Four of a Kind multiplier.
    Evidence: .omo/evidence/habby-yahtzee-task-2-selected-pair.png

  Scenario: Invalid category cannot be clicked
    Tool: Playwright
    Steps: Click `Force Pair Fixture`; inspect `Full House x2.5` and `Yahtzee x6.0`.
    Expected: Both disabled; clicking does not apply damage or close panel.
    Evidence: .omo/evidence/habby-yahtzee-task-2-invalid-disabled.png
  ```

  **Commit**: NO | Message: `feat(combat): require selected yahtzee attack hand` | Files: `src/screens/CombatScreen.tsx`, `src/components/ui.tsx`

- [x] 3. Create original CSS/SVG casual RPG visual system

  **What to do**: Add or update `src/components/ui.tsx` and optionally create `src/components/GameArt.tsx`. Replace emoji-only hero/enemy presentation with inline SVG/CSS originals: round animal hero, forest monster, layered arena background, coin/equipment badges, red-dot notification marker. Update `src/styles.css` tokens for saturated gradients, thick outlines, chunky shadows, one-hand mobile layout, damage pop styling, and reward burst.
  **Must NOT do**: Do not copy Habby/Capybara Go characters, icons, exact UI, or downloaded assets. Do not add external asset pipeline.

  **Recommended Agent Profile**:
  - Category: `visual-engineering` - visual/UI system remake.
  - Skills: [] - no specialized skill required.
  - Omitted: [`rayden-code`] - no Rayden UI.

  **Parallelization**: Can Parallel: YES | Wave 1 | Blocks: 4,6,7 | Blocked By: none

  **References**:
  - Current CSS: `src/styles.css:36-175` - simple shell/cards/buttons/dice.
  - Current UI components: `src/components/ui.tsx:18-105`.
  - Research principle: juicy feedback, chunky buttons, progress bars, reward bursts, red-dot prompts, clean mobile hierarchy.

  **Acceptance Criteria**:
  - [ ] `npm run build` exits `0`.
  - [ ] Home/Combat no longer rely on large emoji as main character/enemy art.
  - [ ] Mobile frame still fits 375×812 without horizontal overflow.
  - [ ] Buttons retain accessible text and focus styles.
  - [ ] Visuals are original CSS/SVG; no external image URLs/assets.

  **QA Scenarios**:
  ```
  Scenario: Original art renders on mobile
    Tool: Playwright
    Steps: Open Home and Combat at 375x812; capture screenshots.
    Expected: Screens show SVG/CSS hero/enemy/arena elements, not emoji-only art.
    Evidence: .omo/evidence/habby-yahtzee-task-3-art-mobile.png

  Scenario: No copied/external assets
    Tool: Bash
    Steps: Search `src` for `http`, `.png`, `.jpg`, `.webp`, and brand terms `Habby`, `Capybara Go` in runtime UI.
    Expected: No external assets or copied brand UI references in source runtime.
    Evidence: .omo/evidence/habby-yahtzee-task-3-no-external-assets.txt
  ```

  **Commit**: NO | Message: `feat(ui): add original casual rpg visual system` | Files: `src/components/ui.tsx`, `src/components/GameArt.tsx`, `src/styles.css`

- [x] 4. Remake screens around game-like mobile hierarchy

  **What to do**: Update `src/screens/HomeScreen.tsx`, `src/screens/CombatScreen.tsx`, `src/screens/EquipmentScreen.tsx`, `src/screens/UpgradeScreen.tsx`, and `src/screens/ResultScreen.tsx`. Home should feel like a compact RPG hub with hero card, stage path/CTA, power number, red-dot upgrade prompt when coins can upgrade. Combat should show layered battle scene, damage pop/message, dice board. Equipment/Upgrade should use item cards and stronger upgrade affordance. Result should show reward burst and clear next action.
  **Must NOT do**: Do not add new screens/systems. Do not add shop/gacha/ads/offers.

  **Recommended Agent Profile**:
  - Category: `visual-engineering` - screen-level UX remake.
  - Skills: [] - no specialized skill required.
  - Omitted: [`api-endpoint-builder`] - no API.

  **Parallelization**: Can Parallel: YES | Wave 3 | Blocks: 6,7 | Blocked By: 2,3

  **References**:
  - Current Home: `src/screens/HomeScreen.tsx:16-47`.
  - Current Combat: `src/screens/CombatScreen.tsx:102-149`.
  - Current Upgrade: `src/screens/UpgradeScreen.tsx:13-40`.
  - Current Result: `src/screens/ResultScreen.tsx:21-36`.

  **Acceptance Criteria**:
  - [ ] `npm run build` exits `0`.
  - [ ] Home has one primary CTA: `Start Combat`.
  - [ ] Upgrade prompt/red-dot appears when player has enough coins for at least one gear upgrade.
  - [ ] Combat category board is visible and readable on 375×812.
  - [ ] Result screen shows coins earned and a reward burst visual.

  **QA Scenarios**:
  ```
  Scenario: Hub communicates RPG progression
    Tool: Playwright
    Steps: Grant 100 coins in DEV; return Home.
    Expected: Home shows power/stage, Start Combat primary CTA, and visible upgrade prompt/red-dot.
    Evidence: .omo/evidence/habby-yahtzee-task-4-hub.png

  Scenario: Reward screen feels game-like without extra systems
    Tool: Playwright
    Steps: Force Win; open Result.
    Expected: Reward burst/coins visible; no shop/gacha/ad/IAP text appears.
    Evidence: .omo/evidence/habby-yahtzee-task-4-result.png
  ```

  **Commit**: NO | Message: `feat(screens): remake casual rpg flow` | Files: `src/screens/*.tsx`, `src/styles.css`

- [x] 5. Update tests for selected-category Yahtzee combat

  **What to do**: Expand `src/game/yahtzee.test.ts`, `src/game/combat.test.ts`, and `src/App.test.tsx`. Add deterministic fixtures for category validity, chosen-category damage, first-roll category enablement, disabled invalid category, and reusable category behavior where component-test feasible.
  **Must NOT do**: Do not only update snapshots/screenshots. Do not leave old tests that imply auto-best attack is accepted primary behavior.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - test strategy and interaction coverage.
  - Skills: [] - no specialized skill required.
  - Omitted: [`bug-hunter`] - no specific runtime bug, this is planned regression coverage.

  **Parallelization**: Can Parallel: YES | Wave 3 | Blocks: 7 | Blocked By: 1,2

  **References**:
  - Current tests: `src/game/yahtzee.test.ts:5-34`, `src/App.test.tsx:6-24`.
  - Selected damage rule: `Frozen Corrected Mechanics > Damage Formula`.

  **Acceptance Criteria**:
  - [ ] `npm test -- --run` exits `0`.
  - [ ] Tests fail if selected `pair` on four-kind dice uses four-kind multiplier.
  - [ ] Tests fail if invalid category can be selected.
  - [ ] Tests cover all-held reroll not changing dice.
  - [ ] Tests cover max third roll disables further rolling.

  **QA Scenarios**:
  ```
  Scenario: Unit and component tests enforce corrected behavior
    Tool: Bash
    Steps: Run `npm test -- --run`.
    Expected: All tests pass and include selected-category assertions.
    Evidence: .omo/evidence/habby-yahtzee-task-5-tests.txt

  Scenario: Old auto-best flow is not tested as success path
    Tool: Bash
    Steps: Search tests for `Resolve Attack` success-only flow.
    Expected: Tests target category selection buttons, not only old Resolve Attack.
    Evidence: .omo/evidence/habby-yahtzee-task-5-no-old-flow.txt
  ```

  **Commit**: NO | Message: `test(combat): cover selected yahtzee attack flow` | Files: `src/**/*.test.ts`, `src/**/*.test.tsx`

- [x] 6. Update Playwright QA and Korean docs

  **What to do**: Update `scripts/qa.mjs` to run corrected full flow: Home → Combat → `Force Pair Fixture` → verify `Pair x1.2` enabled and `Full House x2.5` disabled → click `Pair x1.2` → verify selected category message/damage → trigger/claim Result → Upgrade → refresh → reset. Also run a second dice interaction using `Force Full House Fixture` if combat is still active or via a new combat to prove `Full House x2.5` can be selected. Update `docs/README.ko.md` to explain direct category selection and CSS/SVG visual remake.
  **Must NOT do**: Do not leave QA clicking only `Resolve Attack`. Do not describe old auto-best flow in docs.

  **Recommended Agent Profile**:
  - Category: `writing` - docs plus QA script wording/evidence.
  - Skills: [] - no specialized skill required.
  - Omitted: [`python-pptx-generator`] - no deck.

  **Parallelization**: Can Parallel: NO | Wave 4 | Blocks: 7 | Blocked By: 2,4,5

  **References**:
  - Current QA: `scripts/qa.mjs:17-25` - currently clicks `Resolve Attack`.
  - Current Korean docs: `docs/README.ko.md`.

  **Acceptance Criteria**:
  - [ ] `npm run qa` exits `0` with dev server running.
  - [ ] QA evidence says category chosen by player, e.g. `Pair x1.2` or `Full House x2.5`.
  - [ ] Docs explain: roll up to 3 times, hold dice, choose enabled category, selected category affects damage.
  - [ ] Docs do not claim Habby assets were copied.

  **QA Scenarios**:
  ```
  Scenario: Browser QA proves selected category path
    Tool: Playwright
    Steps: Run `npm run qa` while dev server is active.
    Expected: QA clicks named category button and evidence markdown records selected category + resulting attack.
    Evidence: .omo/evidence/habby-yahtzee-task-6-browser-qa.md

  Scenario: Korean docs match corrected game
    Tool: Bash
    Steps: Read `docs/README.ko.md`; search for old `자동 최고 족보` or old-only `Resolve Attack` wording.
    Expected: Docs describe direct category selection and corrected run commands.
    Evidence: .omo/evidence/habby-yahtzee-task-6-docs.txt
  ```

  **Commit**: NO | Message: `docs(app): document corrected yahtzee combat` | Files: `scripts/qa.mjs`, `docs/README.ko.md`

- [x] 7. Run full regression and remove old-flow leftovers

  **What to do**: Run full checks, inspect source for old-flow leftovers, and capture evidence. Commands: `npm test -- --run`, `npm run build`, `npm run dev -- --host 127.0.0.1`, `npm run qa`. Search app source for stale `Best hand`, stale primary `Resolve Attack`, copied brand terms, external assets. If `Resolve Attack` remains as text, it must not be the primary auto-best action; preferred label is category button text.
  **Must NOT do**: Do not mark complete if QA still passes via old auto-best flow. Do not ignore disabled invalid-category UX.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - cross-cutting regression closure.
  - Skills: [] - no specialized skill required.
  - Omitted: [`codebase-audit-pre-push`] - no push requested.

  **Parallelization**: Can Parallel: NO | Wave 5 | Blocks: Final Verification | Blocked By: 4,5,6

  **References**:
  - Old flow locations: `src/screens/CombatScreen.tsx:135-137`, `scripts/qa.mjs:24`.
  - Final evidence policy: `.omo/evidence/`.

  **Acceptance Criteria**:
  - [ ] `npm test -- --run` exits `0`.
  - [ ] `npm run build` exits `0`.
  - [ ] `npm run qa` exits `0` with dev server running.
  - [ ] Source search finds no runtime `Habby`/`Capybara Go` copying references.
  - [ ] Source search finds no old auto-best-only `Best hand` primary flow.
  - [ ] Evidence exists for Tasks 1-7.

  **QA Scenarios**:
  ```
  Scenario: Full verification suite passes
    Tool: Bash
    Steps: Run `npm test -- --run`; run `npm run build`; start dev server; run `npm run qa`; stop dev server.
    Expected: All commands exit 0.
    Evidence: .omo/evidence/habby-yahtzee-task-7-full-regression.txt

  Scenario: Old rejected behavior is gone
    Tool: Bash
    Steps: Search `src` and `scripts/qa.mjs` for old auto-best-only flow markers.
    Expected: No `Best hand` auto-resolve primary path; QA uses category button selection.
    Evidence: .omo/evidence/habby-yahtzee-task-7-old-flow-removed.txt
  ```

  **Commit**: NO | Message: `chore(app): verify corrected yahtzee rpg prototype` | Files: `src/**`, `scripts/qa.mjs`, `docs/README.ko.md`, `.omo/evidence/**`

## Final Verification Wave
> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit `okay` before completing.
> Do not mark F1-F4 checked before user approval.

- [x] F1. Plan Compliance Audit — oracle
  - Verify implementation matches this correction plan: direct category selection, reusable categories, selected-category damage, CSS/SVG original visual remake.
  - Pass criteria: evidence says `APPROVE`; no excluded systems added.
  - Evidence: `.omo/evidence/habby-yahtzee-f1-plan-compliance.md`.

- [x] F2. Yahtzee Mechanics Review — unspecified-high
  - Review category validity, multiplier mapping, dice state reset, roll/hold edge cases.
  - Pass criteria: selected category determines damage; invalid categories disabled; categories reusable next charge.
  - Evidence: `.omo/evidence/habby-yahtzee-f2-mechanics.md`.

- [x] F3. Visual/UX Review — visual-engineering
  - Review 375×812 Home/Combat/Result screenshots for original casual RPG feel, hierarchy, reward juice, no copied assets.
  - Pass criteria: reviewer approves direction as stronger game-like mobile RPG UI and non-infringing.
  - Evidence: `.omo/evidence/habby-yahtzee-f3-visual.md` plus screenshots.

- [x] F4. Real Browser QA — unspecified-high (+ Playwright)
  - Execute full browser flow with selected category attack and upgrade loop.
  - Pass criteria: QA script proves direct category selection and no old auto-best-only path.
  - Evidence: `.omo/evidence/habby-yahtzee-f4-browser-qa.md`.

## Commit Strategy
- Current project is not a git repo; tasks specify `Commit: NO`.
- If git is initialized later by explicit user request, use logical commit groups:
  1. `feat(game): add selectable yahtzee categories`
  2. `feat(combat): rework dice attack choice`
  3. `feat(ui): remake casual rpg visuals`
  4. `test(app): verify corrected yahtzee flow`

## Success Criteria
- User can see a game-like mobile RPG presentation instead of generic pastel/emoji cards.
- User directly chooses Yahtzee attack category after rolling/holding/rerolling.
- Selected category, not automatic best hand, controls attack damage.
- Invalid categories are disabled and understandable.
- Categories are reusable on every dice charge.
- Build, tests, and browser QA pass.
