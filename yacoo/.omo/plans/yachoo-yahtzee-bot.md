# Yachoo Yahtzee Bot Web App

## TL;DR
> **Summary**: Build a fresh React + Vite + TypeScript web app for Korean Yacht/Yacht Dice with one local human player versus one deterministic basic strategic bot.
> **Deliverables**:
> - Fresh pnpm/Vite React app in `/Users/a1/Desktop/manus/yacoo`
> - Pure game logic modules for scoring, turn flow, and bot decisions
> - Simple single-page playable UI with Korean-first labels
> - Vitest unit tests plus Playwright browser verification
> **Effort**: Medium
> **Parallel**: YES - 4 waves
> **Critical Path**: Task 1 → Task 2 → Task 3 → Task 5 → Task 6 → Task 8

## Context
### Original Request
- User wants to start by building “야추” and include one bot.
- User noted GPT should be used for important orchestration/verification, and GLM can handle auxiliary coding if routing matters; if routing is automatic, skip manual weighting.

### Interview Summary
- Target: Web app.
- Ruleset: Korean Yacht/Yacht Dice.
- Bot: basic strategic bot, not random-only and not a strong/minimax/ML solver.
- Tests: set up tests for this new project.

### Metis Review (gaps addressed)
- Exact scoring variant pinned in this plan.
- Guardrails added against backend/auth/persistence/multiplayer/model-routing scope creep.
- Acceptance criteria use executable commands only.
- Logic must be separated from React for deterministic testing.
- Bot heuristic must include deterministic tie-breaking and never choose illegal moves.

## Work Objectives
### Core Objective
Create a complete local MVP where a human can play one full Korean Yacht match against one basic strategic bot in the browser.

### Deliverables
- Project scaffold: `package.json`, `pnpm-lock.yaml`, `vite.config.ts`, `tsconfig*.json`, `index.html`, `src/`.
- Test scaffold: Vitest config through Vite and Playwright config.
- Game logic: scoring, category metadata, game reducer/state machine, dice utilities.
- Bot logic: deterministic reroll/hold/category heuristic.
- UI: roll controls, holdable dice, scorecard, current turn, bot log, game-over summary, new game reset.
- Tests and QA evidence under `.omo/evidence/` during execution.

### Definition of Done (verifiable conditions with commands)
- `pnpm install` exits `0`.
- `pnpm exec playwright install chromium` exits `0` before browser tests.
- `pnpm test` exits `0` and covers scoring, turn flow, and bot legality.
- `pnpm build` exits `0`.
- `pnpm exec playwright test` exits `0` for at least one full browser flow.
- Browser UI can complete at least one deterministic human-vs-bot scoring sequence without console errors.

### Must Have
- Korean-first labels for game UI, with concise English-compatible code names.
- Human vs one bot only.
- No saved games; refresh resets state.
- Dice: five six-sided dice; max three rolls per turn; held dice persist across rerolls.
- Score categories:
  - `ones`, `twos`, `threes`, `fours`, `fives`, `sixes`: sum dice matching that face.
  - `choice`: sum all dice.
  - `fourKind`: sum all dice when any face appears at least four times, else `0`.
  - `fullHouse`: sum all dice only when counts are exactly `3+2`, else `0`; Yacht/five-of-kind does **not** count as full house.
  - `smallStraight`: `15` when dice contain any 4-long sequence: `1-2-3-4`, `2-3-4-5`, or `3-4-5-6`.
  - `largeStraight`: `30` only for exact set `1-2-3-4-5` or `2-3-4-5-6`.
  - `yacht`: `50` when all five dice match.
- No upper-section bonus in v1.
- Used categories cannot be selected again.
- Bot v1 heuristic:
  - During roll 1-2: evaluate keeping each face group and straight candidates; choose the hold set with highest simple priority.
  - Priority order for holds: existing Yacht/four-kind group → four-long straight candidate → full-house pair/triple candidates → highest repeated face → highest die.
  - Exact hold-set tie-breaks:
    - If dice already form Large Straight (`1-2-3-4-5` or `2-3-4-5-6`) or Yacht, hold all five dice.
    - Yacht/four-kind group: hold all dice matching the face with count `>=4`; if multiple faces somehow qualify, choose higher face.
    - Four-long straight candidate: evaluate sequences in order `[2,3,4,5]`, `[1,2,3,4]`, `[3,4,5,6]`; choose the first sequence where all four values are present; hold the lowest-index die for each sequence value.
    - Full-house candidates: if counts are exactly `3+2`, hold all five; if one triple exists, hold all dice of the highest triple face; else if two pairs exist, hold all dice from both pairs, choosing higher pair first only for log text; else if one pair exists, hold all dice of the highest pair face.
    - Highest repeated face: choose the face with highest count `>=2`, tie by higher face, and hold all dice of that face.
    - Highest die fallback: hold only the lowest-index die among dice showing the highest face.
  - On final roll: score the unused category with highest immediate score.
  - Tie-break category order: `yacht`, `largeStraight`, `fourKind`, `fullHouse`, `choice`, `smallStraight`, `sixes`, `fives`, `fours`, `threes`, `twos`, `ones`.
  - Bot decisions must be deterministic for the same dice, used categories, and roll count.

### Must NOT Have (guardrails, AI slop patterns, scope boundaries)
- No backend, auth, database, accounts, multiplayer networking, leaderboard, analytics, localStorage persistence, AI API calls, GLM/GPT runtime calls, minimax, ML, or advanced expected-value solver.
- Do not manually edit model-routing config unless a real config file already exists in `yacoo`; none exists now.
- Do not add router/global state library unless needed by acceptance criteria; it is not needed for v1.
- Do not hide rules inside React components; keep scoring/game/bot logic in pure TypeScript modules.
- Do not require human visual confirmation for acceptance.

## Verification Strategy
> ZERO HUMAN INTERVENTION - all verification is agent-executed.
- Test decision: tests-after with Vitest + Playwright; implement logic and tests in the same task where possible.
- QA policy: Every task has agent-executed scenarios.
- Evidence: `.omo/evidence/task-{N}-{slug}.{ext}`.

## Execution Strategy
### Parallel Execution Waves
> Target: 5-8 tasks per wave. <3 per wave (except final) = under-splitting.
> Extract shared dependencies as Wave-1 tasks for max parallelism.

Wave 1: Task 1 foundation scaffold.
Wave 2: Tasks 2-4 pure logic, tests, and bot.
Wave 3: Tasks 5-7 UI integration, styling, and browser tests.
Wave 4: Task 8 final app verification prep.

### Dependency Matrix (full, all tasks)
- Task 1 blocks all other tasks.
- Task 2 depends on Task 1; blocks Tasks 3, 5, 8.
- Task 3 depends on Tasks 1-2; blocks Tasks 5, 8.
- Task 4 depends on Tasks 1-2; blocks Task 6 and Task 8.
- Task 5 depends on Tasks 2-3; blocks Tasks 6-8.
- Task 6 depends on Tasks 4-5; blocks Task 8.
- Task 7 depends on Task 5; blocks Task 8.
- Task 8 depends on Tasks 1-7.

### Agent Dispatch Summary (wave → task count → categories)
- Wave 1 → 1 task → `unspecified-low` coding scaffold. If model routing is visible, GLM/lower-tier auxiliary coding is acceptable.
- Wave 2 → 3 tasks → `unspecified-high` for scoring/state correctness, `unspecified-low` for deterministic bot implementation.
- Wave 3 → 3 tasks → `visual-engineering` for UI, `unspecified-high` for Playwright tests.
- Wave 4 → 1 task → `unspecified-high` verification prep.
- Final Verification → 4 tasks → GPT/oracle/high-accuracy review agents; do not use GLM for final verification.

## TODOs
> Implementation + Test = ONE task. Never separate.
> EVERY task MUST have: Agent Profile + Parallelization + QA Scenarios.

- [x] 1. Scaffold React/Vite/TypeScript project with test tooling

  **What to do**: Initialize a fresh pnpm-based React + Vite + TypeScript app directly in `/Users/a1/Desktop/manus/yacoo`. Add scripts: `dev`, `build`, `preview`, `test`, `test:watch`, `e2e`. Add Vitest dependencies, Testing Library for React if needed, Playwright, and a minimal `playwright.config.ts`. Create `.gitignore`, `src/main.tsx`, `src/App.tsx`, `src/styles.css`, `src/game/`, `src/bot/`, `src/__tests__/`, and `tests/e2e/`.
  **Must NOT do**: Do not add backend, router, database, auth, localStorage, model-routing config, or complex UI frameworks. Do not overwrite `.omo/` artifacts.

  **Recommended Agent Profile**:
  - Category: `unspecified-low` - Reason: straightforward project scaffold and dependency wiring.
  - Skills: `[]` - no specialized skill required.
  - Omitted: `rayden-code`, `api-endpoint-builder` - no Rayden UI or API endpoint is in scope.

  **Parallelization**: Can Parallel: NO | Wave 1 | Blocks: 2, 3, 4, 5, 6, 7, 8 | Blocked By: none

  **References** (executor has NO interview context - be exhaustive):
  - Pattern: `/Users/a1/Desktop/manus/AGENTS.md:22-35` - workspace active web app convention uses React + TypeScript + Vite + pnpm and simple SPA architecture.
  - Create: `package.json` - must include scripts and dependencies.
  - Create: `vite.config.ts` - must configure React and Vitest test environment.
  - Create: `playwright.config.ts` - must run against local Vite dev server.

  **Acceptance Criteria** (agent-executable only):
  - [x] `pnpm install` exits `0`.
  - [x] `pnpm build` exits `0` with starter app.
  - [x] `pnpm test -- --run` exits `0`, even if only a smoke test exists at this stage.
  - [x] `pnpm exec playwright install chromium` exits `0`.
  - [x] `pnpm exec playwright test` exits `0` for a smoke test that verifies the page renders text `야추`.

  **QA Scenarios** (MANDATORY - task incomplete without these):
  ```
  Scenario: Scaffold builds
    Tool: Bash
    Steps: Run `pnpm install && pnpm build && pnpm test -- --run` from `/Users/a1/Desktop/manus/yacoo`.
    Expected: All commands exit 0; `dist/` is produced by build; no TypeScript errors.
    Evidence: .omo/evidence/task-1-scaffold-build.txt

  Scenario: Browser smoke test
    Tool: Playwright
    Steps: Run `pnpm exec playwright install chromium && pnpm exec playwright test`; test opens `/` and queries `text=야추`.
    Expected: Test exits 0 and screenshot/trace is available on failure only.
    Evidence: .omo/evidence/task-1-playwright-smoke.txt
  ```

  **Commit**: NO | Message: `chore(yachoo): scaffold web app` | Files: `package.json`, `pnpm-lock.yaml`, `vite.config.ts`, `playwright.config.ts`, `tsconfig*.json`, `index.html`, `src/**`, `tests/**`, `.gitignore`

- [x] 2. Implement Korean Yacht scoring rules with exhaustive unit tests

  **What to do**: Create `src/game/categories.ts`, `src/game/scoring.ts`, and `src/game/scoring.test.ts`. Export category IDs, Korean labels, scoring descriptions, `scoreCategory(category, dice)`, `isCategorySatisfied(category, dice)`, and `totalScore(scorecard)`. Implement the exact v1 rule variant from Must Have.
  **Must NOT do**: Do not include upper bonus. Do not make Yacht count as Full House. Do not depend on React or browser APIs.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: scoring edge cases are business-critical and must be exact.
  - Skills: `[]` - pure TypeScript implementation.
  - Omitted: `visual-engineering` - no UI work.

  **Parallelization**: Can Parallel: NO | Wave 2 | Blocks: 3, 5, 8 | Blocked By: 1

  **References**:
  - Requirement: `.omo/plans/yachoo-yahtzee-bot.md:51-65` - exact scoring variant and no upper bonus.
  - Create: `src/game/categories.ts` - category metadata and deterministic display order.
  - Create: `src/game/scoring.ts` - pure scoring functions.
  - Create: `src/game/scoring.test.ts` - Vitest coverage for all categories.

  **Acceptance Criteria**:
  - [x] `pnpm test -- --run src/game/scoring.test.ts` exits `0`.
  - [x] Tests cover Ones~Sixes, Choice, Four of a Kind success/failure, Full House success/failure, Yacht-not-Full-House, all straight variants, Yacht success/failure, and total score.
  - [x] TypeScript exports contain no `any` for public scoring APIs.

  **QA Scenarios**:
  ```
  Scenario: Happy scoring cases
    Tool: Bash
    Steps: Run `pnpm test -- --run src/game/scoring.test.ts`.
    Expected: Dice `[2,2,3,4,5]` scores twos=4, choice=16, smallStraight=15; `[1,2,3,4,5]` scores largeStraight=30; `[6,6,6,6,6]` scores yacht=50.
    Evidence: .omo/evidence/task-2-scoring-happy.txt

  Scenario: Failure scoring cases
    Tool: Bash
    Steps: Run `pnpm test -- --run src/game/scoring.test.ts`.
    Expected: `[6,6,6,6,6]` fullHouse=0; `[1,1,2,3,5]` smallStraight=0; `[1,2,3,4,6]` largeStraight=0; `[1,1,1,2,3]` fourKind=0.
    Evidence: .omo/evidence/task-2-scoring-edge.txt
  ```

  **Commit**: NO | Message: `feat(game): add yacht scoring rules` | Files: `src/game/categories.ts`, `src/game/scoring.ts`, `src/game/scoring.test.ts`

- [x] 3. Implement game state machine and turn reducer with tests

  **What to do**: Create `src/game/types.ts`, `src/game/dice.ts`, `src/game/state.ts`, and `src/game/state.test.ts`. Model players `human` and `bot`, scorecards, dice values, held flags, roll count, active player, phase (`ready`, `rolling`, `selectingCategory`, `gameOver`), and action log. Implement pure actions: `createInitialGame(seed?)`, `rollDice`, `toggleHold(index)`, `scoreTurn(category)`, `advanceTurn`, `isGameOver`, `getWinner`. Use injectable/randomizable dice roller for deterministic tests.
  **Must NOT do**: Do not call `Math.random()` directly inside hard-to-test reducer paths; wrap RNG in `rollDie(rng)` or pass dice in tests. Do not allow illegal state transitions silently.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: turn flow correctness affects all gameplay.
  - Skills: `[]` - pure TypeScript.
  - Omitted: `visual-engineering` - no UI work.

  **Parallelization**: Can Parallel: NO | Wave 2 | Blocks: 5, 8 | Blocked By: 1, 2

  **References**:
  - API/Type: `src/game/categories.ts` from Task 2 - category IDs and used-category checks.
  - API/Type: `src/game/scoring.ts` from Task 2 - score computation.
  - Create: `src/game/state.ts` - reducer/action implementation.
  - Create: `src/game/state.test.ts` - turn-flow and illegal-action tests.

  **Acceptance Criteria**:
  - [x] `pnpm test -- --run src/game/state.test.ts` exits `0`.
  - [x] Tests prove max three rolls, hold/unhold behavior, scoring only after at least one roll, used-category lockout, active-player switching, game-over after all categories filled, and tie handling.
  - [x] Illegal actions return a typed error/result or unchanged state with an explicit error message; no uncaught exceptions in normal UI paths.

  **QA Scenarios**:
  ```
  Scenario: Human turn happy path
    Tool: Bash
    Steps: Run `pnpm test -- --run src/game/state.test.ts`.
    Expected: Initial state rolls five dice, allows holding index 0, rerolls unheld dice only, scores an unused category, resets roll count, and switches to bot.
    Evidence: .omo/evidence/task-3-state-happy.txt

  Scenario: Illegal transitions
    Tool: Bash
    Steps: Run `pnpm test -- --run src/game/state.test.ts`.
    Expected: Fourth roll is rejected; scoring before first roll is rejected; selecting an already-used category is rejected with a test-asserted error.
    Evidence: .omo/evidence/task-3-state-errors.txt
  ```

  **Commit**: NO | Message: `feat(game): add turn state machine` | Files: `src/game/types.ts`, `src/game/dice.ts`, `src/game/state.ts`, `src/game/state.test.ts`

- [x] 4. Implement deterministic basic strategic bot with tests

  **What to do**: Create `src/bot/basicBot.ts` and `src/bot/basicBot.test.ts`. Export `chooseHoldsForBot(input: { dice, rollCount, usedCategories })` and `chooseCategoryForBot(input: { dice, usedCategories })` so this task depends only on category/scoring APIs, not React UI. Implement deterministic heuristic exactly: hold priority Yacht/four-kind group → four-long straight candidate → full-house pair/triple candidates → highest repeated face → highest die; use the exact hold-set tie-breaks in Must Have; final category chooses highest immediate score among unused categories; category score ties use order `yacht`, `largeStraight`, `fourKind`, `fullHouse`, `choice`, `smallStraight`, `sixes`, `fives`, `fours`, `threes`, `twos`, `ones`.
  **Must NOT do**: Do not add minimax, expected-value simulation, ML, network calls, GPT/GLM runtime calls, or random bot choices.

  **Recommended Agent Profile**:
  - Category: `unspecified-low` - Reason: bounded deterministic heuristic after scoring/state APIs exist.
  - Skills: `[]` - no specialized skill required.
  - Omitted: `performance-optimizer` - heuristic is intentionally simple.

  **Parallelization**: Can Parallel: YES | Wave 2 | Blocks: 6, 8 | Blocked By: 1, 2

  **References**:
  - API/Type: `src/game/scoring.ts` from Task 2 - score candidates.
  - API/Type: `src/game/categories.ts` from Task 2 - category IDs and tie-break category names.
  - Requirement: `.omo/plans/yachoo-yahtzee-bot.md:66-79` - bot heuristic and tie-break order.

  **Acceptance Criteria**:
  - [x] `pnpm test -- --run src/bot/basicBot.test.ts` exits `0`.
  - [x] Tests prove bot returns only legal holds and unused categories.
  - [x] Tests prove deterministic tie-breaking for equal scores.
  - [x] Tests prove hold-set tie-breaks for large straight, four-kind/Yacht, competing straight candidates, triple vs pairs, repeated faces, and highest-die fallback.
  - [x] Tests prove no bot function uses `fetch`, model APIs, or randomness for decisions.

  **QA Scenarios**:
  ```
  Scenario: Bot strategic happy path
    Tool: Bash
    Steps: Run `pnpm test -- --run src/bot/basicBot.test.ts`.
    Expected: For dice `[6,6,6,2,1]` before final roll, bot holds the three 6s; for `[1,2,3,4,6]`, bot holds one each of `1,2,3,4`; for `[1,2,3,4,5]`, bot holds all five.
    Evidence: .omo/evidence/task-4-bot-happy.txt

  Scenario: Bot legality edge cases
    Tool: Bash
    Steps: Run `pnpm test -- --run src/bot/basicBot.test.ts`.
    Expected: Bot never selects a used category; equal score ties follow the specified category order; `[1,2,3,4,5]` chooses the `[1,2,3,4,5]` large-straight hold-all rule over a four-long candidate; same input returns identical output.
    Evidence: .omo/evidence/task-4-bot-legal.txt
  ```

  **Commit**: NO | Message: `feat(bot): add deterministic yacht bot` | Files: `src/bot/basicBot.ts`, `src/bot/basicBot.test.ts`

- [x] 5. Build playable single-page game UI wired to pure state

  **What to do**: Implement the main playable UI in `src/App.tsx` plus small components under `src/components/`: `DiceRow.tsx`, `ScoreCard.tsx`, `TurnPanel.tsx`, `GameLog.tsx`, and `GameOver.tsx`. UI must show title `야추`, active player, roll count, five dice buttons with held state, roll button, scorecard buttons for available categories, current totals, bot log, winner/tie result, and “새 게임” reset. Wire human actions to pure reducer/state APIs from Tasks 2-3.
  **Must NOT do**: Do not duplicate scoring rules in React components. Do not make bot act in this task except through placeholders/hooks prepared for Task 6. Do not add persistence or backend calls.

  **Recommended Agent Profile**:
  - Category: `visual-engineering` - Reason: UI structure, accessibility, and state wiring.
  - Skills: `[]` - no external design system required.
  - Omitted: `rayden-code` - no Rayden UI components requested.

  **Parallelization**: Can Parallel: NO | Wave 3 | Blocks: 6, 7, 8 | Blocked By: 2, 3

  **References**:
  - Pattern: `/Users/a1/Desktop/manus/AGENTS.md:35-37` - simple SPA can keep app-level game state, but pure logic remains extracted.
  - API/Type: `src/game/state.ts` from Task 3 - reducer/actions.
  - API/Type: `src/game/categories.ts` from Task 2 - labels and category order.
  - Create: `src/components/DiceRow.tsx`, `src/components/ScoreCard.tsx`, `src/components/TurnPanel.tsx`, `src/components/GameLog.tsx`, `src/components/GameOver.tsx`.

  **Acceptance Criteria**:
  - [x] `pnpm test -- --run` exits `0` after UI tests/smoke tests.
  - [x] `pnpm build` exits `0`.
  - [x] UI contains accessible buttons named `굴리기`, `새 게임`, and category buttons with Korean labels.
  - [x] Held dice have an accessible state indicator such as `aria-pressed="true"`.

  **QA Scenarios**:
  ```
  Scenario: Human UI happy path
    Tool: Playwright
    Steps: Start dev server, open `/`, click `굴리기`, click first die button, click `굴리기` again, click an enabled score category button.
    Expected: Roll count updates, first die shows held state, scorecard updates, active player changes to bot or bot-pending state.
    Evidence: .omo/evidence/task-5-ui-human.png

  Scenario: Disabled UI edge cases
    Tool: Playwright
    Steps: Open `/` before rolling; inspect score category buttons and dice hold buttons.
    Expected: Scoring is disabled before first roll; hold buttons are disabled or no-op before dice exist; no console errors.
    Evidence: .omo/evidence/task-5-ui-disabled.txt
  ```

  **Commit**: NO | Message: `feat(ui): add playable yacht board` | Files: `src/App.tsx`, `src/components/**`, `src/styles.css`

- [x] 6. Integrate bot turn automation and visible bot action log

  **What to do**: Wire `basicBot` into the UI/game flow so the bot automatically completes its turn after the human scores. Bot should perform up to three rolls using `chooseHoldsForBot`, then score using `chooseCategoryForBot`, append readable Korean log entries, and return control to the human unless game is over. Keep automation deterministic in tests by allowing injected dice sequences or mocked roller.
  **Must NOT do**: Do not use timeouts that make tests flaky; if a visual delay is desired, keep it disabled or controllable in tests. Do not use AI APIs or random category choice.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: integration touches state machine, bot legality, and UI timing.
  - Skills: `[]` - no specialized skill required.
  - Omitted: `performance-optimizer` - no performance problem expected.

  **Parallelization**: Can Parallel: NO | Wave 3 | Blocks: 8 | Blocked By: 4, 5

  **References**:
  - API/Type: `src/bot/basicBot.ts` from Task 4 - bot decisions.
  - API/Type: `src/game/state.ts` from Task 3 - scoring and turn advancement.
  - UI: `src/components/GameLog.tsx` from Task 5 - display bot actions.

  **Acceptance Criteria**:
  - [x] `pnpm test -- --run` exits `0` with bot integration tests.
  - [x] `pnpm build` exits `0`.
  - [x] After a human scores, bot completes a legal turn without user input.
  - [x] Bot log includes at least roll/hold/category summary entries in Korean.

  **QA Scenarios**:
  ```
  Scenario: Bot auto-turn happy path
    Tool: Playwright
    Steps: Open `/`, complete one human category selection, wait for bot log entry containing `봇`, then inspect active player.
    Expected: Bot scores exactly one unused category and active player returns to human unless game over.
    Evidence: .omo/evidence/task-6-bot-autoturn.png

  Scenario: Bot exhausted category edge case
    Tool: Bash
    Steps: Run unit/integration test with all but one bot category pre-filled.
    Expected: Bot selects the only unused category, scores it, and game-over detection triggers when both scorecards are full.
    Evidence: .omo/evidence/task-6-bot-edge.txt
  ```

  **Commit**: NO | Message: `feat(bot): integrate automated opponent` | Files: `src/App.tsx`, `src/bot/**`, `src/game/**`, `src/components/GameLog.tsx`, `src/**/*.test.ts*`

- [x] 7. Add minimal responsive styling and accessibility polish

  **What to do**: Update `src/styles.css` and component markup for a clean single-page game layout. Use basic CSS only unless Task 1 already installed Tailwind; prefer no Tailwind to keep scope small. Ensure mobile-width usability, visible held dice, disabled states, focus outlines, readable scorecard, and game-over summary. Korean UI labels should remain primary.
  **Must NOT do**: Do not spend scope on animations, themes, design systems, sprites, sound, or pixel-perfect mockups.

  **Recommended Agent Profile**:
  - Category: `visual-engineering` - Reason: layout/accessibility polish.
  - Skills: `[]` - plain CSS is enough.
  - Omitted: `rayden-code` - no component library needed.

  **Parallelization**: Can Parallel: YES | Wave 3 | Blocks: 8 | Blocked By: 5

  **References**:
  - UI: `src/App.tsx` and `src/components/**` from Task 5.
  - Guardrail: `.omo/plans/yachoo-yahtzee-bot.md:74-80` - avoid overbuilding UI.

  **Acceptance Criteria**:
  - [x] `pnpm build` exits `0`.
  - [x] Playwright verifies title, roll button, dice buttons, scorecard, bot log, and reset button are visible at `375x812` and desktop viewport.
  - [x] No axe-style automated accessibility dependency is required, but basic ARIA labels and keyboard-focusable buttons must exist.

  **QA Scenarios**:
  ```
  Scenario: Mobile layout happy path
    Tool: Playwright
    Steps: Set viewport `375x812`, open `/`, capture screenshot after first roll.
    Expected: `야추`, dice row, scorecard, and `굴리기` button are visible without horizontal scrolling.
    Evidence: .omo/evidence/task-7-mobile-layout.png

  Scenario: Keyboard/accessibility edge case
    Tool: Playwright
    Steps: Use Tab to focus roll button and dice buttons; inspect held die `aria-pressed` after Space/Enter.
    Expected: Controls are keyboard reachable; held state is exposed; disabled buttons cannot be activated.
    Evidence: .omo/evidence/task-7-a11y.txt
  ```

  **Commit**: NO | Message: `style(ui): polish yacht game layout` | Files: `src/styles.css`, `src/App.tsx`, `src/components/**`

- [x] 8. Add full browser E2E flow and final implementation verification script

  **What to do**: Create or expand `tests/e2e/yachoo.spec.ts` to cover app load, human roll/hold/score, bot auto-turn, scorecard update, reset, and a controlled near-game-over scenario using a test-only preloaded state or deterministic dice helper. Add a documented verification command sequence in `README.md`: `pnpm install`, `pnpm exec playwright install chromium`, `pnpm test`, `pnpm build`, `pnpm exec playwright test`. Add `README.md` with scope, rules, bot heuristic, and non-goals.
  **Must NOT do**: Do not add CI, deployment, backend, or manual testing instructions as required acceptance; commands must be agent-executable.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: final test coverage and documentation must match implementation.
  - Skills: `[]` - no specialized skill required.
  - Omitted: `writing` - README is technical and tightly coupled to verification.

  **Parallelization**: Can Parallel: NO | Wave 4 | Blocks: Final Verification Wave | Blocked By: 1, 2, 3, 4, 5, 6, 7

  **References**:
  - Test: `tests/e2e/yachoo.spec.ts` from Task 1/5 - browser verification flow.
  - Docs: `README.md` - create project usage and rule summary.
  - Commands: `.omo/plans/yachoo-yahtzee-bot.md:44-50` - Definition of Done.

  **Acceptance Criteria**:
  - [x] `pnpm test -- --run` exits `0`.
  - [x] `pnpm build` exits `0`.
  - [x] `pnpm exec playwright install chromium` exits `0`.
  - [x] `pnpm exec playwright test` exits `0`.
  - [x] `README.md` states the exact v1 scoring rules, bot heuristic, run commands, and out-of-scope items.
  - [x] `.omo/evidence/task-8-final-commands.txt` contains command outputs or summarized pass/fail evidence.

  **QA Scenarios**:
  ```
  Scenario: Full app verification happy path
    Tool: Bash
    Steps: Run `pnpm test -- --run && pnpm build && pnpm exec playwright install chromium && pnpm exec playwright test`.
    Expected: All commands exit 0; Playwright confirms human action, bot action, and reset.
    Evidence: .omo/evidence/task-8-final-commands.txt

  Scenario: Reset/game-state edge case
    Tool: Playwright
    Steps: Open `/`, complete at least one human+bot round, click `새 게임`.
    Expected: Both scorecards clear, roll count resets to 0, active player is human, log returns to initial message, no console errors.
    Evidence: .omo/evidence/task-8-reset-flow.png
  ```

  **Commit**: NO | Message: `test(e2e): verify yacht game flow` | Files: `tests/e2e/yachoo.spec.ts`, `README.md`, `src/**`, `package.json`

## Final Verification Wave (MANDATORY — after ALL implementation tasks)
> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.
> **Do NOT auto-proceed after verification. Wait for user's explicit approval before marking work complete.**
> **Never mark F1-F4 as checked before getting user's okay.** Rejection or user feedback -> fix -> re-run -> present again -> wait for okay.
- [x] F1. Plan Compliance Audit — oracle
- [x] F2. Code Quality Review — unspecified-high
- [x] F3. Real Manual QA — unspecified-high (+ playwright)
- [x] F4. Scope Fidelity Check — deep

## Commit Strategy
- Commit only if the user explicitly asks for a commit.
- Suggested commit message if requested: `feat(yachoo): add playable yacht dice bot web app`.
- Include only project files under `/Users/a1/Desktop/manus/yacoo` and generated evidence if user wants evidence committed.

## Success Criteria
- All implementation TODOs complete with evidence paths populated.
- `pnpm test`, `pnpm build`, and `pnpm exec playwright test` all pass.
- Final verification agents F1-F4 all approve.
- User gives explicit “okay” after final verification summary.
