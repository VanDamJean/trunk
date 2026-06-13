# Yahtzee RPG Habby-Style Web Prototype

## TL;DR
> **Summary**: Build a greenfield React/Vite/TypeScript web prototype for a cute Capybara Go-like casual RPG where auto-battle is strengthened by player-triggered Yahtzee dice hands.
> **Deliverables**:
> - React/Vite app scaffold in `/Users/a1/Desktop/manus/yacoo_rpg`
> - Five-screen MVP: Home, Combat, Equipment, Upgrade, Result/Reward
> - Pure Yahtzee evaluator, damage calculator, equipment progression, reward, save/load logic
> - Tests-after suite for game logic and agent-executed browser QA evidence
> **Effort**: Medium
> **Parallel**: YES - 6 dependency-valid waves
> **Critical Path**: Task 1 → Task 2/3 → Task 4 → Task 7 → Task 9 → Task 10 → Final Verification Wave

## Context

### Original Request
User wants a game format like Archer-growing, Random Dice, Capybara Go, and Habby games, with RPG elements attached. Combat should use Yahtzee dice rules: when the player completes a Yahtzee-style hand, the character attacks more strongly.

### Interview Summary
- Platform: React/Vite web prototype.
- MVP scope: combat + growth/progression.
- Battle tempo: auto-battle baseline with player dice intervention.
- Yahtzee fidelity: near-original 5 dice, maximum 3 rolls, hold dice between rolls.
- Progression: equipment-centered.
- UX direction: cute, casual, reward-forward Capybara Go-style presentation.
- Screen scope: 5 screens — Home, Combat, Equipment, Upgrade, Result/Reward.
- Test strategy: tests-after for pure game logic; agent QA for UI/game loop.

### Metis Review (gaps addressed)
- Combat timing, dice timing, hand multipliers, equipment constants, persistence, and RNG determinism are frozen below as plan-defined defaults.
- MVP scope is guarded to 5 screens only.
- Pure logic must be separated from UI for reliable tests.
- Randomness must be injectable/seedable.
- Scope creep from pets, shops, gacha, ads, battle passes, cloud saves, and production art is explicitly excluded.

## Work Objectives

### Core Objective
Implement a playable MVP where the player enters combat, lets a hero auto-battle one enemy, triggers a Yahtzee dice intervention, resolves the best hand into bonus attack damage, wins/loses the stage, receives coins/equipment reward, upgrades gear, and sees increased power persist after refresh.

### Deliverables
- Greenfield React + TypeScript + Vite project at repo root.
- Static game constants and domain models.
- Pure logic modules:
  - `src/game/yahtzee.ts`
  - `src/game/combat.ts`
  - `src/game/equipment.ts`
  - `src/game/rewards.ts`
  - `src/game/storage.ts`
- UI screens/components for the 5-screen MVP.
- Vitest tests for Yahtzee, damage, equipment, rewards, and storage defaults.
- Browser QA evidence in `.omo/evidence/`.

### Definition of Done (verifiable conditions with commands)
- `npm install` completes.
- `npm run build` exits `0`.
- `npm test -- --run` exits `0`.
- `npm run dev -- --host 127.0.0.1` launches the app.
- Agent QA completes Home → Combat → Dice roll/hold/reroll/resolve → Result → Upgrade → Home loop and records evidence under `.omo/evidence/`.

### Must Have
- Mobile-first layout targeting 375×812 while remaining usable on desktop browser.
- Cute placeholder art using emoji/CSS shapes only; no asset search/generation.
- Simple local view-state navigation; no router.
- Static local data only.
- `localStorage` persistence under key `yacoo-rpg-save-v1`.
- Visible reset/debug control for QA.
- Seedable/injectable RNG for tests.
- Combat MVP: one hero vs one enemy per stage.
- Dice phase: player can trigger when charge is available; opening dice panel pauses combat timers; resolving dice resumes combat.
- If player ignores dice, auto-battle continues; only one dice charge can be stored.
- Dice cancel semantics: `Cancel Dice` consumes the stored charge, discards current dice/held/roll state, closes the panel, resumes combat timers, and starts the next 8000ms charge interval from zero.

### Must NOT Have (guardrails, AI slop patterns, scope boundaries)
- No backend, auth, cloud saves, analytics, multiplayer, PvP, real monetization, ads, IAP, daily quests, battle pass, gacha, shop, pets, skills, talents, chapters beyond numeric stages, merge systems, equipment set bonuses, production art pipeline, or native mobile work.
- No vague “polish later” placeholders in core logic.
- No business logic embedded only inside React components.
- No non-deterministic tests.
- No separate task that only writes tests without implementation; implementation + tests are paired per task.

## Frozen MVP Mechanics

### Balance Constants
- Hero starting stats: `hp=120`, `attack=10`, `stage=1`, `coins=0`.
- Enemy formula by stage `s`: `hp = 80 + 30 * (s - 1)`, `attack = 6 + 2 * (s - 1)`.
- Hero auto-attack: every `1000ms`, deals `heroAttack + bootsBonus`.
- Enemy auto-attack: every `1200ms`, deals `max(1, enemyAttack - armorDefenseBonus)`.
- Dice charge: becomes available every `8000ms` while combat is running; max stored charges `1`.
- Dice panel: pauses combat timers until resolved or cancelled.
- Dice rolls: 5 six-sided dice, maximum 3 rolls per intervention; held dice do not reroll.
- Resolving before first roll is disabled.
- Damage formula: `floor((heroAttack + weaponAttackBonus) * handMultiplier * (1 + charmDiceBonus))`.
- Combat clamps HP to minimum `0`.

### Yahtzee Hands and Ranking
Highest-ranked matching hand wins when multiple hands match.

| Rank | Hand | Detection | Multiplier |
|---:|---|---|---:|
| 9 | `yahtzee` | five of a kind | `6.0` |
| 8 | `largeStraight` | `1-2-3-4-5` or `2-3-4-5-6` | `3.8` |
| 7 | `fourKind` | four or more of same value | `3.2` |
| 6 | `fullHouse` | counts are `3 + 2` exactly | `2.5` |
| 5 | `smallStraight` | any 4-length straight | `2.1` |
| 4 | `threeKind` | three or more of same value | `1.8` |
| 3 | `twoPair` | two distinct pairs | `1.5` |
| 2 | `pair` | one pair | `1.2` |
| 1 | `chance` | fallback | `1.0` |

### Equipment Model
- Slots: `weapon`, `armor`, `charm`, `boots`.
- Starting gear: all slots have common level 1 gear.
- Currency: `coins` only.
- Upgrade cost: `25 * currentLevel` coins.
- Level cap: `10`.
- Weapon bonus: `+3 attack per level`.
- Armor bonus: `+15 maxHp per level` and `+1 defense per level`.
- Charm bonus: `+0.05 dice multiplier bonus per level`.
- Boots bonus: `+1 auto-attack damage per level`.
- Reward on win: `coins = 30 + 10 * stage`; 30% deterministic RNG chance to award one equipment duplicate.
- Duplicate equipment reward: converts to `20 coins`; no inventory/merge system in MVP.
- Loss reward: `10 coins`, stage does not advance.
- Win advances `stage += 1` after reward claim.

### Persistence
- Save shape:
  - `stage: number`
  - `coins: number`
  - `equipment: Record<slot, { id, slot, name, level }>`
  - `lastResult?: { outcome, stage, coinsEarned, handUsed? }`
- Storage key: `yacoo-rpg-save-v1`.
- Invalid/missing save loads default state.
- Reset button clears key and restores defaults.

## Verification Strategy
> ZERO HUMAN INTERVENTION FOR QA EXECUTION - all tests, browser QA, screenshots, and review evidence are agent-executed. Final completion still requires explicit user approval after agents present evidence.
- Test decision: tests-after with Vitest + React Testing Library for pure logic and selected component behavior.
- QA policy: Every task has agent-executed scenarios.
- Evidence: `.omo/evidence/task-{N}-{slug}.{ext}`.

## Execution Strategy

### Parallel Execution Waves
> Target: 5-8 tasks per wave. <3 per wave (except final) = under-splitting.
> Extract shared dependencies as Wave-1 tasks for max parallelism.

Wave 1: Task 1 scaffold.
Wave 2: Task 2 core combat/dice logic, Task 3 equipment/reward/storage logic, Task 5 visual system/layout.
Wave 3: Task 4 app state/navigation.
Wave 4: Task 6 Home/Result screens, Task 7 Combat screen, Task 8 Equipment/Upgrade screens.
Wave 5: Task 9 integration persistence and reset.
Wave 6: Task 10 tests/scripts/accessibility polish.
Final Verification Wave: F1-F4 in parallel after all tasks.

### Dependency Matrix (full, all tasks)
| Task | Depends On | Blocks |
|---|---|---|
| 1 | none | 2,3,4,5,6,7,8,9,10 |
| 2 | 1 | 4,7,10 |
| 3 | 1 | 4,6,8,9,10 |
| 4 | 1,2,3 | 6,7,8,9,10 |
| 5 | 1 | 6,7,8 |
| 6 | 4,5 | 9,10 |
| 7 | 2,4,5 | 9,10 |
| 8 | 3,4,5 | 9,10 |
| 9 | 3,4,6,7,8 | 10 |
| 10 | 2,3,6,7,8,9 | Final Verification |

### Agent Dispatch Summary (wave → task count → categories)
- Wave 1 → 1 task → `quick`
- Wave 2 → 3 tasks → `unspecified-high`, `unspecified-high`, `visual-engineering`
- Wave 3 → 1 task → `unspecified-high`
- Wave 4 → 3 tasks → `visual-engineering`, `unspecified-high`, `visual-engineering`
- Wave 5 → 1 task → `unspecified-high`
- Wave 6 → 1 task → `unspecified-high`

## TODOs
> Implementation + Test = ONE task. Never separate.
> EVERY task MUST have: Agent Profile + Parallelization + QA Scenarios.

- [x] 1. Scaffold React/Vite/TypeScript project and test tooling

  **What to do**: Create a greenfield React + TypeScript + Vite app at `/Users/a1/Desktop/manus/yacoo_rpg` without deleting `.omo/`. Add `package.json`, `index.html`, `vite.config.ts`, `tsconfig*.json`, `src/main.tsx`, `src/App.tsx`, `src/styles.css`, `src/game/`, `src/components/`, `src/screens/`, and `src/test/`. Install runtime deps `@vitejs/plugin-react`, `vite`, `typescript`, `react`, `react-dom`; dev/test deps `vitest`, `jsdom`, `@testing-library/react`, `@testing-library/jest-dom`, `@testing-library/user-event`, `playwright` if browser QA automation is used. Add scripts: `dev`, `build`, `test`.
  **Must NOT do**: Do not create backend files, router packages, state libraries, asset pipelines, or native app files.

  **Recommended Agent Profile**:
  - Category: `quick` - Reason: bounded scaffold/config task.
  - Skills: [] - no specialized skill needed.
  - Omitted: [`api-endpoint-builder`, `rayden-code`] - no API endpoints or Rayden components.

  **Parallelization**: Can Parallel: YES | Wave 1 | Blocks: 2,3,4,5,6,7,8,9,10 | Blocked By: none

  **References**:
  - Project state: `/Users/a1/Desktop/manus/yacoo_rpg/.omo/` - only existing directory; preserve it.
  - Draft: `.omo/drafts/yahtzee-rpg-habby-style.md` - confirmed stack and MVP scope.

  **Acceptance Criteria**:
  - [ ] `npm install` exits `0`.
  - [ ] `npm run build` exits `0`.
  - [ ] `npm test -- --run` exits `0` with at least one smoke test.
  - [ ] `.omo/` still exists and plan file remains intact.

  **QA Scenarios**:
  ```
  Scenario: App scaffold boots
    Tool: Bash
    Steps: Run `npm install`; run `npm run build`; run `npm test -- --run`.
    Expected: All commands exit 0; build output exists under `dist/`.
    Evidence: .omo/evidence/task-1-scaffold.txt

  Scenario: Existing OMO artifacts preserved
    Tool: Bash
    Steps: Verify `.omo/plans/yahtzee-rpg-habby-style.md` and `.omo/drafts/yahtzee-rpg-habby-style.md` still exist.
    Expected: Both files exist; no `.omo` deletion occurred.
    Evidence: .omo/evidence/task-1-omo-preserved.txt
  ```

  **Commit**: NO | Message: `chore(app): scaffold vite react prototype` | Files: `package.json`, `index.html`, `vite.config.ts`, `tsconfig*.json`, `src/**`

- [x] 2. Implement Yahtzee evaluator, combat constants, damage calculator, and tests

  **What to do**: Create `src/game/types.ts`, `src/game/constants.ts`, `src/game/yahtzee.ts`, and `src/game/combat.ts`. Define the frozen constants exactly as in this plan. Implement `evaluateHand(dice: DieValue[]): YahtzeeHandResult`, `rollDice(previous, held, rng)`, `calculateDiceDamage(hero, equipment, hand)`, `createEnemy(stage)`, and HP clamp helpers. Add Vitest tests in `src/game/yahtzee.test.ts` and `src/game/combat.test.ts`.
  **Must NOT do**: Do not put evaluator or damage rules in React components. Do not use `Math.random()` directly inside test-covered logic; accept RNG injection.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: core deterministic game logic with edge cases.
  - Skills: [] - no specialized skill needed.
  - Omitted: [`performance-optimizer`] - no measured performance bottleneck.

  **Parallelization**: Can Parallel: YES | Wave 2 | Blocks: 4,7,10 | Blocked By: 1

  **References**:
  - Mechanics: `Frozen MVP Mechanics` section in this plan - exact constants, hand ranking, damage formula.
  - Test strategy: `Verification Strategy` section - tests-after, deterministic logic.

  **Acceptance Criteria**:
  - [ ] `npm test -- --run src/game/yahtzee.test.ts src/game/combat.test.ts` exits `0`.
  - [ ] Tests include exact fixtures: `[1,1,1,1,1] => yahtzee`, `[2,2,2,3,3] => fullHouse`, `[1,2,3,4,5] => largeStraight`, `[1,1,2,3,4] => pair`, unordered `[4,2,3,1,6] => smallStraight`.
  - [ ] Highest-ranked matching hand wins for `[2,2,2,2,5] => fourKind`, not threeKind/pair.
  - [ ] Rolling cannot mutate held dice in test fixtures.
  - [ ] Damage formula test asserts `floor((10 + 3) * 2.5 * (1 + 0.05)) = 34` for level-1 weapon/charm fullHouse fixture.

  **QA Scenarios**:
  ```
  Scenario: Yahtzee hands classify deterministically
    Tool: Bash
    Steps: Run `npm test -- --run src/game/yahtzee.test.ts`.
    Expected: All hand fixtures pass exactly with expected hand IDs and multipliers.
    Evidence: .omo/evidence/task-2-yahtzee-tests.txt

  Scenario: Combat damage and HP clamps are safe
    Tool: Bash
    Steps: Run `npm test -- --run src/game/combat.test.ts`.
    Expected: Damage formula matches constants; enemy/hero HP never becomes negative after applying damage.
    Evidence: .omo/evidence/task-2-combat-tests.txt
  ```

  **Commit**: NO | Message: `feat(game): add yahtzee combat logic` | Files: `src/game/types.ts`, `src/game/constants.ts`, `src/game/yahtzee.ts`, `src/game/combat.ts`, `src/game/*.test.ts`

- [x] 3. Implement equipment, rewards, storage, and tests

  **What to do**: Create `src/game/equipment.ts`, `src/game/rewards.ts`, and `src/game/storage.ts`. Implement four equipment slots, level cap 10, upgrade cost `25 * currentLevel`, slot bonuses, win/loss rewards, duplicate conversion to 20 coins, default save, load validation, save, and reset. Add tests for upgrade success/failure, reward determinism with injected RNG, invalid save fallback, and reset.
  **Must NOT do**: Do not implement inventory lists, merge systems, rarity animations, shops, gacha, or additional currencies.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: progression math and persistence edge cases.
  - Skills: [] - no specialized skill needed.
  - Omitted: [`api-endpoint-builder`] - storage is local only.

  **Parallelization**: Can Parallel: YES | Wave 2 | Blocks: 4,6,8,9,10 | Blocked By: 1

  **References**:
  - Equipment model: `Frozen MVP Mechanics > Equipment Model` - exact slots, costs, bonuses, rewards.
  - Persistence: `Frozen MVP Mechanics > Persistence` - exact save key and shape.

  **Acceptance Criteria**:
  - [ ] `npm test -- --run src/game/equipment.test.ts src/game/rewards.test.ts src/game/storage.test.ts` exits `0`.
  - [ ] Upgrade with insufficient coins is rejected and does not change level/coins.
  - [ ] Level 10 equipment cannot upgrade.
  - [ ] Win reward for stage 3 returns `50` coins before duplicate conversion.
  - [ ] Invalid JSON or invalid save shape loads default state.

  **QA Scenarios**:
  ```
  Scenario: Upgrade rules are deterministic
    Tool: Bash
    Steps: Run `npm test -- --run src/game/equipment.test.ts`.
    Expected: Cost, level cap, slot bonuses, and insufficient-funds behavior match plan constants.
    Evidence: .omo/evidence/task-3-equipment-tests.txt

  Scenario: Save/load fallback works
    Tool: Bash
    Steps: Run `npm test -- --run src/game/storage.test.ts`.
    Expected: Missing/invalid saves return default state; reset removes `yacoo-rpg-save-v1`.
    Evidence: .omo/evidence/task-3-storage-tests.txt
  ```

  **Commit**: NO | Message: `feat(game): add equipment rewards storage` | Files: `src/game/equipment.ts`, `src/game/rewards.ts`, `src/game/storage.ts`, `src/game/*.test.ts`

- [x] 4. Implement app state machine and local navigation

  **What to do**: Replace placeholder `App.tsx` with a single app shell that manages screen state: `home`, `combat`, `equipment`, `upgrade`, `result`. Create `src/game/appState.ts` or `src/hooks/useGameState.ts` to load persisted state, expose actions (`startCombat`, `finishCombat`, `claimReward`, `upgradeSlot`, `resetSave`, `navigate`). Pass state/actions to screens. Keep navigation local; no router.
  **Must NOT do**: Do not install routing libraries or global state libraries. Do not duplicate business logic already implemented in `src/game/*`.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: integration layer coordinating mechanics and screens.
  - Skills: [] - no specialized skill needed.
  - Omitted: [`api-endpoint-builder`] - no server.

  **Parallelization**: Can Parallel: NO | Wave 3 | Blocks: 6,7,8,9,10 | Blocked By: 1,2,3

  **References**:
  - State shape: `Frozen MVP Mechanics > Persistence`.
  - Screen list: `Interview Summary` and `Work Objectives > Deliverables`.

  **Acceptance Criteria**:
  - [ ] `npm run build` exits `0`.
  - [ ] `App` can navigate to each of the five screen states via visible buttons/tabs.
  - [ ] Reset action clears persisted state and displays default stage 1 / 0 coins.
  - [ ] No route package appears in `package.json`.

  **QA Scenarios**:
  ```
  Scenario: Navigation reaches all five screens
    Tool: Playwright
    Steps: Launch dev server; click Home nav controls to visit Combat, Equipment, Upgrade, Result/Reward where available.
    Expected: Each screen displays a unique heading: `Home`, `Combat`, `Equipment`, `Upgrade`, `Result`.
    Evidence: .omo/evidence/task-4-navigation.png

  Scenario: Reset returns default state
    Tool: Playwright
    Steps: Modify state by upgrading or claiming reward if possible; click `Reset`; confirm reset.
    Expected: Home shows `Stage 1` and `Coins 0`.
    Evidence: .omo/evidence/task-4-reset.png
  ```

  **Commit**: NO | Message: `feat(app): add game state navigation` | Files: `src/App.tsx`, `src/hooks/**`, `src/game/appState.ts`

- [x] 5. Implement mobile-first visual system and reusable UI components

  **What to do**: Build `src/styles.css` and reusable components such as `Shell`, `TopStatsBar`, `BottomNav`, `Card`, `PrimaryButton`, `StatPill`, `ProgressBar`, `Dice`, and `RewardBadge`. Use mobile-first 375×812 framing, rounded cards, pastel/cute colors, emoji placeholders, clear touch targets, and reward-forward visual hierarchy. Ensure desktop centers the mobile frame.
  **Must NOT do**: Do not fetch/generate art assets. Do not copy copyrighted UI exactly; use reference-inspired original CSS.

  **Recommended Agent Profile**:
  - Category: `visual-engineering` - Reason: UI system and mobile-first styling.
  - Skills: [] - no specialized skill needed.
  - Omitted: [`rayden-code`] - not using Rayden UI components.

  **Parallelization**: Can Parallel: YES | Wave 2 | Blocks: 6,7,8 | Blocked By: 1

  **References**:
  - UX direction: `Interview Summary` - Capybara Go-like cute/casual/reward-forward.
  - Guardrails: `Must NOT Have` - placeholder art only, no asset pipeline.

  **Acceptance Criteria**:
  - [ ] `npm run build` exits `0`.
  - [ ] App frame is centered and no wider than `430px` on desktop.
  - [ ] Buttons have accessible text labels and visible focus styles.
  - [ ] Dice component can render values 1-6 and held state.

  **QA Scenarios**:
  ```
  Scenario: Mobile frame renders cleanly
    Tool: Playwright
    Steps: Open app at viewport 375x812; capture Home screen.
    Expected: No horizontal overflow; top stats and bottom navigation are visible.
    Evidence: .omo/evidence/task-5-mobile-frame.png

  Scenario: Components expose accessible labels
    Tool: Playwright
    Steps: Inspect buttons/dice controls by role/name.
    Expected: Primary actions are locatable by accessible name, e.g. `Start Combat`, `Roll Dice`, `Reset`.
    Evidence: .omo/evidence/task-5-accessibility.txt
  ```

  **Commit**: NO | Message: `feat(ui): add mobile visual system` | Files: `src/styles.css`, `src/components/**`

- [x] 6. Implement Home and Result/Reward screens

  **What to do**: Create `src/screens/HomeScreen.tsx` and `src/screens/ResultScreen.tsx`. Home shows hero name placeholder `Dice Cub`, stage, coins, power estimate, equipment summary, `Start Combat`, `Equipment`, `Upgrade`, and `Reset`. Result shows win/loss, stage, best hand used, coins earned, duplicate conversion if any, `Claim Reward`, and `Back Home`. Claiming a win advances stage; loss does not.
  **Must NOT do**: Do not add daily rewards, shops, gacha, ads, or extra screens.

  **Recommended Agent Profile**:
  - Category: `visual-engineering` - Reason: user-facing screens with game state bindings.
  - Skills: [] - no specialized skill needed.
  - Omitted: [`api-endpoint-builder`] - no server.

  **Parallelization**: Can Parallel: YES | Wave 4 | Blocks: 9,10 | Blocked By: 4,5

  **References**:
  - Screen scope: `Interview Summary` - Home and Result/Reward are required MVP screens.
  - Rewards: `Frozen MVP Mechanics > Equipment Model` - win/loss reward and stage advance rules.

  **Acceptance Criteria**:
  - [ ] `npm run build` exits `0`.
  - [ ] Home displays exact labels: `Stage`, `Coins`, `Power`, `Start Combat`.
  - [ ] Result win claim increments stage by 1 and adds reward coins.
  - [ ] Result loss claim adds 10 coins and keeps same stage.

  **QA Scenarios**:
  ```
  Scenario: Home presents core RPG status
    Tool: Playwright
    Steps: Open app; locate `Stage 1`, `Coins 0`, `Power`, and `Start Combat`.
    Expected: All labels are visible on the Home screen.
    Evidence: .omo/evidence/task-6-home.png

  Scenario: Result claim applies reward
    Tool: Playwright
    Steps: Before loading app, set `localStorage.yacoo-rpg-save-v1` to a valid save fixture with `lastResult: { outcome: "win", stage: 1, coinsEarned: 40, handUsed: "fullHouse" }`; open Result screen through app navigation or direct state control; click `Claim Reward`.
    Expected: Home shows increased coins and next stage.
    Evidence: .omo/evidence/task-6-result-claim.png
  ```

  **Commit**: NO | Message: `feat(screens): add home result screens` | Files: `src/screens/HomeScreen.tsx`, `src/screens/ResultScreen.tsx`

- [x] 7. Implement Combat screen with auto-battle and Yahtzee intervention

  **What to do**: Create `src/screens/CombatScreen.tsx` and combat UI components. Implement one hero vs one enemy. Timers: hero auto-attack every 1000ms, enemy auto-attack every 1200ms, dice charge every 8000ms. `Open Dice` pauses combat. Dice panel supports roll, hold/unhold individual dice, up to 3 rolls, best-hand preview, resolve, and cancel. Resolve applies calculated damage, stores `handUsed`, resumes combat, and completes result when either HP reaches 0.
  **Must NOT do**: Do not add multiple enemies, waves, skills, pets, projectiles, complex animations, or real-time action movement.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: timer/state-heavy UI and interaction logic.
  - Skills: [] - no specialized skill needed.
  - Omitted: [`performance-optimizer`] - simple MVP timers only.

  **Parallelization**: Can Parallel: NO | Wave 4 | Blocks: 9,10 | Blocked By: 2,4,5

  **References**:
  - Combat constants: `Frozen MVP Mechanics > Balance Constants`.
  - Yahtzee rules: `Frozen MVP Mechanics > Yahtzee Hands and Ranking`.
  - Pure logic: `src/game/yahtzee.ts`, `src/game/combat.ts` from Task 2.

  **Acceptance Criteria**:
  - [ ] `npm run build` exits `0`.
  - [ ] `Open Dice` is disabled until charge is available after the 8000ms interval.
  - [ ] Dice panel prevents more than 3 rolls.
  - [ ] Held dice do not change after reroll.
  - [ ] Resolving a Yahtzee hand applies `6.0` multiplier damage and can end combat.
  - [ ] HP bars never display below 0.

  **QA Scenarios**:
  ```
  Scenario: Dice hold and resolve happy path
    Tool: Playwright
    Steps: Start combat; wait up to 9000ms for dice charge; click `Open Dice`; click `Roll Dice`; hold at least one die; click `Roll Dice`; click `Resolve Attack`.
    Expected: Roll count decreases/updates, held die value remains stable, enemy HP decreases, combat resumes or result appears.
    Evidence: .omo/evidence/task-7-dice-resolve.png

  Scenario: Roll limit is enforced
    Tool: Playwright
    Steps: Open dice panel; roll three times; attempt a fourth roll.
    Expected: Fourth roll is disabled or blocked with clear UI text; `Resolve Attack` remains available after at least one roll.
    Evidence: .omo/evidence/task-7-roll-limit.png
  ```

  **Commit**: NO | Message: `feat(combat): add auto battle yahtzee intervention` | Files: `src/screens/CombatScreen.tsx`, `src/components/**`

- [x] 8. Implement Equipment and Upgrade screens

  **What to do**: Create `src/screens/EquipmentScreen.tsx` and `src/screens/UpgradeScreen.tsx`. Equipment screen displays the four slots, level, slot bonus, and cute equipment names: `Twig Wand` weapon, `Leaf Hoodie` armor, `Lucky Acorn` charm, `Tiny Boots` boots. Upgrade screen shows selected slot, current level, next bonus, cost, coin balance, and upgrade button. Disable upgrades if insufficient coins or level cap reached.
  **Must NOT do**: Do not create inventory grids, equipment swapping, rarity systems beyond static common gear, merge systems, or set bonuses.

  **Recommended Agent Profile**:
  - Category: `visual-engineering` - Reason: progression UI with clear feedback.
  - Skills: [] - no specialized skill needed.
  - Omitted: [`api-endpoint-builder`] - no API.

  **Parallelization**: Can Parallel: YES | Wave 4 | Blocks: 9,10 | Blocked By: 3,4,5

  **References**:
  - Equipment constants: `Frozen MVP Mechanics > Equipment Model`.
  - UI components: Task 5 component set.

  **Acceptance Criteria**:
  - [ ] `npm run build` exits `0`.
  - [ ] Equipment screen shows all four required gear names and slots.
  - [ ] Upgrade button deducts exact `25 * currentLevel` coins and increments level by 1.
  - [ ] Upgrade button is disabled with insufficient coins.
  - [ ] Level 10 displays `Max Level` and cannot upgrade.

  **QA Scenarios**:
  ```
  Scenario: Upgrade succeeds with enough coins
    Tool: Playwright
    Steps: Before loading app, set `localStorage.yacoo-rpg-save-v1` to a valid save fixture with `coins: 25`, `stage: 1`, and all four level-1 equipment items; open Upgrade; select `Twig Wand`; click `Upgrade`.
    Expected: Weapon level increases from 1 to 2 and coins decrease by 25.
    Evidence: .omo/evidence/task-8-upgrade-success.png

  Scenario: Upgrade blocked with insufficient coins
    Tool: Playwright
    Steps: Reset to 0 coins; open Upgrade; select any gear.
    Expected: Upgrade button is disabled and UI states insufficient coins.
    Evidence: .omo/evidence/task-8-upgrade-blocked.png
  ```

  **Commit**: NO | Message: `feat(screens): add equipment upgrade screens` | Files: `src/screens/EquipmentScreen.tsx`, `src/screens/UpgradeScreen.tsx`

- [x] 9. Integrate full loop persistence, debug controls, and deterministic QA fixtures

  **What to do**: Wire full flow: Home → Combat → Result → Claim Reward → Home/Upgrade. Persist state after reward claim, upgrade, and reset. Add unobtrusive QA controls gated by `import.meta.env.DEV`: grant 100 coins, force dice charge, force win, force loss, set deterministic dice seed. These controls must not appear in production build unless `DEV` is true.
  **Must NOT do**: Do not expose debug controls unconditionally in production UI. Do not add backend or cloud persistence.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: end-to-end state integration and QA determinism.
  - Skills: [] - no specialized skill needed.
  - Omitted: [`security-review`] - no sensitive backend surface in MVP.

  **Parallelization**: Can Parallel: NO | Wave 5 | Blocks: 10 | Blocked By: 3,4,6,7,8

  **References**:
  - Persistence: `Frozen MVP Mechanics > Persistence`.
  - QA policy: `Verification Strategy`.

  **Acceptance Criteria**:
  - [ ] `npm run build` exits `0`.
  - [ ] Refresh after upgrade preserves coins/equipment/stage.
  - [ ] Reset clears `localStorage` key `yacoo-rpg-save-v1`.
  - [ ] Dev QA controls exist during `npm run dev` and are absent from production build UI.
  - [ ] Full loop can be completed without human-only judgment.

  **QA Scenarios**:
  ```
  Scenario: Progress persists after refresh
    Tool: Playwright
    Steps: Start dev server; grant coins via QA control; upgrade `Twig Wand`; reload page.
    Expected: Weapon level and remaining coins persist after reload.
    Evidence: .omo/evidence/task-9-persistence.png

  Scenario: Full MVP loop completes
    Tool: Playwright
    Steps: Home click `Start Combat`; force dice charge; roll/resolve attack; force or play to win; click `Claim Reward`; go to Upgrade; upgrade gear; return Home.
    Expected: Home shows higher stage or coins/power after loop.
    Evidence: .omo/evidence/task-9-full-loop.png
  ```

  **Commit**: NO | Message: `feat(app): integrate full progression loop` | Files: `src/**`

- [x] 10. Complete tests-after suite, scripts, accessibility pass, and evidence capture

  **What to do**: Ensure all pure logic tests exist and pass. Add component tests only where useful for navigation/reset. Add `src/test/setup.ts` for jest-dom. Verify build, test, and manual QA commands. Improve accessible names, keyboard focus, disabled states, and error text for dice/upgrade controls. Capture final task evidence files under `.omo/evidence/`.
  **Must NOT do**: Do not broaden MVP scope or add new mechanics while polishing.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: cross-cutting verification and quality closure.
  - Skills: [] - no specialized skill needed.
  - Omitted: [`codebase-audit-pre-push`] - no push requested.

  **Parallelization**: Can Parallel: NO | Wave 6 | Blocks: Final Verification | Blocked By: 2,3,6,7,8,9

  **References**:
  - Definition of Done: `Work Objectives > Definition of Done`.
  - QA scenarios: all task QA sections above.

  **Acceptance Criteria**:
  - [ ] `npm run build` exits `0`.
  - [ ] `npm test -- --run` exits `0`.
  - [ ] Evidence files exist for Tasks 1-10 under `.omo/evidence/`.
  - [ ] Buttons and dice controls are keyboard reachable and have accessible names.
  - [ ] No excluded scope items appear in code or UI text.

  **QA Scenarios**:
  ```
  Scenario: Whole verification suite passes
    Tool: Bash
    Steps: Run `npm run build`; run `npm test -- --run`.
    Expected: Both commands exit 0.
    Evidence: .omo/evidence/task-10-build-test.txt

  Scenario: Keyboard and disabled states are usable
    Tool: Playwright
    Steps: Navigate with keyboard through Home, Combat dice panel, and Upgrade; inspect disabled roll/upgrade states.
    Expected: Focus is visible; disabled controls are announced/visibly disabled; no keyboard trap.
    Evidence: .omo/evidence/task-10-accessibility.png
  ```

  **Commit**: NO | Message: `test(app): complete mvp verification` | Files: `src/**`, `.omo/evidence/**`

## Final Verification Wave (MANDATORY — after ALL implementation tasks)
> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.
> **Do NOT auto-proceed after verification. Wait for user's explicit approval before marking work complete.**
> **Never mark F1-F4 as checked before getting user's okay.** Rejection or user feedback -> fix -> re-run -> present again -> wait for okay.
- [x] F1. Plan Compliance Audit — oracle
  - Verify every implemented feature maps to this plan and no excluded systems were added.
  - Pass criteria: Oracle verdict explicitly says `APPROVE`; any `REJECT` or missing evidence blocks completion.
  - Evidence: `.omo/evidence/f1-plan-compliance.md`.
- [x] F2. Code Quality Review — unspecified-high
  - Review separation between pure game logic and UI, deterministic RNG, state flow, and TypeScript clarity.
  - Pass criteria: Reviewer confirms build/test pass, no business logic is trapped only in components, and no critical TypeScript/runtime issue remains.
  - Evidence: `.omo/evidence/f2-code-quality.md`.
- [x] F3. Real Manual QA — unspecified-high (+ Playwright)
  - Execute full browser flow on 375×812 viewport: Home → Combat → Dice → Result → Upgrade → Refresh → Reset.
  - Pass criteria: QA evidence includes successful screenshots/logs for full loop, persistence after refresh, and reset to default state.
  - Evidence: `.omo/evidence/f3-manual-qa.png` and `.omo/evidence/f3-manual-qa.md`.
- [x] F4. Scope Fidelity Check — deep
  - Confirm the MVP stayed at 5 screens and did not include backend, monetization, gacha, pets, shops, battle pass, cloud save, native app, or production assets.
  - Pass criteria: Reviewer confirms no excluded scope appears in code, dependencies, UI labels, or package scripts.
  - Evidence: `.omo/evidence/f4-scope-fidelity.md`.

## Commit Strategy
- Repository is not currently a git repo, so tasks specify `Commit: NO`.
- If the executor initializes git later only after explicit user request, use these logical commit groups:
  1. `chore(app): scaffold vite react prototype`
  2. `feat(game): add deterministic combat progression logic`
  3. `feat(ui): add five screen mvp loop`
  4. `test(app): add logic tests and qa evidence`

## Success Criteria
- User can run the app locally and play the intended MVP loop.
- Yahtzee hand completion clearly produces stronger attacks.
- Equipment upgrades visibly improve power/combat outcomes.
- Progress persists through refresh and reset works.
- Tests and build pass with commands listed above.
- Final verification agents approve and user explicitly says okay before completion.
