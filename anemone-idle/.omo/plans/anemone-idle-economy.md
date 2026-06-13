# Anemone Idle Economy: Dynamic 뿅 + True Idle Income

## TL;DR
> **Summary**: Convert the current fixed `뿅! +3` feel into a production-scaled idle loop: fresh/legacy starts produce automatically, the pulse button scales from production rate, and offline rewards remain capped and verified.
> **Deliverables**:
> - Dynamic plankton pulse formula: `max(3, ceil(3 + planktonPerSecond * 10))`
> - Immediate passive income from fresh starts and zero-producer legacy saves via 1 starter `driftPolyps`
> - UI/feedback/notice text using computed pulse amount instead of fixed `+3`
> - Vitest TDD coverage for pulse scaling, starter idle income, online/offline production, caps, and save compatibility
> **Effort**: Short
> **Parallel**: NO - shared economy/state files create merge risk
> **Critical Path**: Task 1 → Task 2 → Task 3 → Task 4 → Final Verification

## Context
### Original Request
User asked why 뽕 is fixed at `+3` and why money only increases when pressing the 뽕 button, noting that an idle growth game should increase automatically.

### Interview Summary
- Economy structure: both 뽕 gain and passive money/resource generation should increase.
- Idle scope: include online passive income and offline rewards.
- Pulse scaling: production-rate-based.
- Test strategy: TDD.
- UI scope: update existing button/feedback/notice only; no redesign or new modal.

### Metis Review (gaps addressed)
- Locked fresh-start automatic income: new games and zero-producer legacy saves receive 1 starter `driftPolyps`.
- Locked exact pulse formula: `max(3, ceil(3 + getTotalRates(economy).plankton * 10))`.
- Locked resource scope: pulse remains plankton-only; other resources continue through producers/upgrades.
- Locked offline UX: preserve existing notice-based feedback.
- Guardrails: preserve save compatibility, `MAX_OFFLINE_SECONDS`, existing architecture, and avoid full rebalance/redesign.

## Work Objectives
### Core Objective
Make Anemone Idle feel like an idle/incremental game from the first session: resources increase without pressing 뿅, and pressing 뿅 scales with production progress instead of staying fixed at `+3`.

### Deliverables
- `src/gameState.js`: replace fixed pulse gain usage with a pure computed helper.
- `src/economy.js` and/or `src/gameState.js`: ensure initial/legacy zero-producer states receive starter passive production.
- `src/main.js`: render computed pulse amount in button burst and feedback.
- Tests in `tests/gameState.test.js`, `tests/economy.test.js`, and `tests/storage.test.js`.

### Definition of Done (verifiable conditions with commands)
- `npm test` passes.
- `npm run build` passes.
- Fresh game state gains plankton after advancing 10 seconds without pressing 뿅.
- Fresh game pulse gain is greater than the old fixed `3` because starter production contributes to the formula.
- A higher plankton production rate produces a strictly higher pulse gain.
- Offline load still grants capped production once and does not bypass `MAX_OFFLINE_SECONDS`.
- UI button and click feedback display the computed dynamic amount, not a hardcoded `+3`.

### Must Have
- Use TDD: add/update failing tests before implementation in each task.
- Keep production paths through existing `advanceGame()`, `applyProduction()`, `loadGame()`, and `getTotalRates()`.
- Keep minimum pulse at `3` for safety/familiarity.
- Keep pulse plankton-only.
- Preserve legacy save loading.

### Must NOT Have
- Do not add prestige, new resources, new producers, new screens, or broad balance redesign.
- Do not remove or raise `MAX_OFFLINE_SECONDS`.
- Do not create duplicate timers or a second production engine.
- Do not hardcode dynamic UI amount separately from the game-state helper.
- Do not rename resources or rewrite Korean/English copy beyond amount correctness.

## Verification Strategy
> ZERO HUMAN INTERVENTION - all verification is agent-executed.
- Test decision: TDD with Vitest (`npm test`), plus production build (`npm run build`).
- QA policy: Every task has agent-executed scenarios.
- Evidence: `.omo/evidence/task-{N}-{slug}.{ext}`

## Execution Strategy
### Parallel Execution Waves
> Target: sequential because tasks touch shared economy/state/UI files and order matters.

Wave 1: Task 1 (`quick`) - dynamic pulse helper and state-level tests.
Wave 2: Task 2 (`quick`) - starter passive income and legacy zero-producer compatibility.
Wave 3: Task 3 (`quick`) - UI/feedback dynamic amount wiring.
Wave 4: Task 4 (`unspecified-high`) - offline/online regression hardening and full verification.

### Dependency Matrix (full, all tasks)
- Task 1: no blockers; blocks Tasks 3 and 4.
- Task 2: no blockers; blocks Task 4.
- Task 3: blocked by Task 1; blocks Task 4.
- Task 4: blocked by Tasks 1, 2, and 3.

### Agent Dispatch Summary (wave → task count → categories)
- Wave 1 → 1 task → `quick`
- Wave 2 → 1 task → `quick`
- Wave 3 → 1 task → `quick`
- Wave 4 → 1 task → `unspecified-high`

## TODOs
> Implementation + Test = ONE task. Never separate.
> EVERY task MUST have: Agent Profile + Parallelization + QA Scenarios.

- [x] 1. Add production-scaled pulse helper with TDD coverage

  **What to do**:
  1. In `tests/gameState.test.js`, update the current fixed pulse test at `tests/gameState.test.js:49-57` into TDD coverage for a computed pulse amount.
  2. Add tests that assert:
     - a deliberately zero-rate economy still has minimum pulse gain `3`; construct it by creating an economy/state and setting every `economy.producers[*] = 0` in the test, so this assertion remains valid after Task 2 adds a starter producer;
     - a state with positive plankton production returns `max(3, ceil(3 + rate * 10))`;
     - a larger `driftPolyps` count produces a strictly larger pulse gain;
     - `pulseReefInState()` adds exactly the computed amount to `resources.plankton` and `lifetimeEarned.plankton`.
  3. In `src/gameState.js`, replace `export const PULSE_PLANKTON_GAIN = 3` at `src/gameState.js:18` with:
     - `export const MIN_PULSE_PLANKTON_GAIN = 3;`
     - `export const PULSE_PRODUCTION_SECONDS = 10;`
     - `export function getPulsePlanktonGain(economy) { return Math.max(MIN_PULSE_PLANKTON_GAIN, Math.ceil(MIN_PULSE_PLANKTON_GAIN + getTotalRates(economy).plankton * PULSE_PRODUCTION_SECONDS)); }`
  4. Update `pulseReefInState()` at `src/gameState.js:67-74` to compute `const amount = getPulsePlanktonGain(state.economy)` and use that amount in `addResources()` and `noticeArgs`.
  5. Import `getTotalRates` from `./economy.js` in `src/gameState.js`; keep imports sorted consistently with existing style.

  **Must NOT do**:
  - Do not make pulse grant pearls or tide energy.
  - Do not leave `PULSE_PLANKTON_GAIN` as a public fixed amount used by UI.
  - Do not use random, time-based, or click-count-based scaling.

  **Recommended Agent Profile**:
  - Category: `quick` - Reason: targeted tests and helper change in existing state/economy code.
  - Skills: [] - No specialized skill needed.
  - Omitted: [`performance-optimizer`, `api-endpoint-builder`] - not relevant to a small client-side economy helper.

  **Parallelization**: Can Parallel: NO | Wave 1 | Blocks: [3, 4] | Blocked By: []

  **References**:
  - Pattern: `src/gameState.js:18` - current fixed pulse constant to replace.
  - Pattern: `src/gameState.js:67-74` - current fixed pulse state mutation.
  - API/Type: `src/economy.js:116-123` - `getTotalRates()` returns per-second resource rates.
  - Test: `tests/gameState.test.js:49-57` - existing pulse test to evolve.

  **Acceptance Criteria**:
  - [ ] `npm test -- tests/gameState.test.js` passes.
  - [ ] Tests prove minimum pulse is exactly `3` when production rate is zero.
  - [ ] Tests prove pulse gain is `>3` and formula-based when plankton/sec is positive.
  - [ ] Tests prove `pulseReefInState()` adds the computed helper value to both current and lifetime plankton.

  **QA Scenarios**:
  ```
  Scenario: Dynamic pulse helper scales from production
    Tool: Bash
    Steps: Run `npm test -- tests/gameState.test.js`.
    Expected: Tests for zero-rate minimum, positive-rate formula, and larger-rate scaling pass.
    Evidence: .omo/evidence/task-1-dynamic-pulse-test.txt

  Scenario: Invalid no-production edge case remains safe
    Tool: Bash
    Steps: Run the zero-production helper test in `tests/gameState.test.js` as part of the same Vitest command.
    Expected: Computed pulse remains exactly `3`; no NaN/Infinity appears.
    Evidence: .omo/evidence/task-1-dynamic-pulse-edge.txt
  ```

  **Commit**: NO | Message: `fix(economy): scale pulse gain from production` | Files: [`src/gameState.js`, `tests/gameState.test.js`]

- [x] 2. Add starter passive income for fresh and zero-producer legacy states

  **What to do**:
  1. Add failing tests first:
     - In `tests/economy.test.js`, assert `createInitialEconomy().producers.driftPolyps === 1` and `getTotalRates(createInitialEconomy()).plankton > 0`.
     - Update existing exact-count tests that assumed zero starting producers. Example: `tests/economy.test.js:78-87` should assert the purchased producer count increased by `1` from its previous count, not that it equals `1`.
     - In `tests/gameState.test.js` or `tests/economy.test.js`, assert `advanceGame(createInitialState(0), 10_000)` increases plankton without calling `pulseReefInState()`.
     - In `tests/storage.test.js`, add a legacy zero-producer save fixture and assert `loadGame()` normalizes it to at least 1 `driftPolyps` and grants offline production after elapsed time.
  2. In `src/economy.js`, make `createInitialEconomy()` initialize `producers` with one starter `driftPolyps` while preserving initial resources/lifetime values.
  3. For legacy saves, add a small normalization helper in the state load path so a loaded economy with all producer counts equal to `0` receives one `driftPolyps`.
     - Preferred location: `sanitizeState()` in `src/gameState.js:176-203`, immediately after `cloneEconomy()` creates `economy`.
     - Helper behavior: if `Object.values(economy.producers).every((count) => count === 0)`, set `economy.producers.driftPolyps = 1`.
     - Do not modify saves that already own any producer.
  4. Keep `lastSavedAt`, `lastTickAt`, resources, missions, and progression untouched except for refresh side effects already performed by `refreshStateProgression()`.

  **Must NOT do**:
  - Do not reduce the first producer cost or alter producer cost growth.
  - Do not auto-buy/spend plankton to grant the starter producer.
  - Do not grant pearls or tide energy at start.
  - Do not mark missions claimed automatically.

  **Recommended Agent Profile**:
  - Category: `quick` - Reason: small initialization and migration behavior with tests.
  - Skills: [] - No specialized skill needed.
  - Omitted: [`performance-optimizer`] - no performance bottleneck work.

  **Parallelization**: Can Parallel: NO | Wave 2 | Blocks: [4] | Blocked By: []

  **References**:
  - Pattern: `src/economy.js:45-60` - initial economy shape.
  - Pattern: `src/gameState.js:176-203` - save/state sanitization path.
  - Pattern: `src/gameState.js:36-43` - online advance path that should now produce from fresh state.
  - Test: `tests/storage.test.js:42-49` and `tests/storage.test.js:105-120` - corrupt/legacy save fixture style.

  **Acceptance Criteria**:
  - [ ] `npm test -- tests/economy.test.js tests/gameState.test.js tests/storage.test.js` passes.
  - [ ] Fresh state has at least 1 `driftPolyps` without spending starting plankton.
  - [ ] Fresh state gains plankton after 10 seconds without a pulse click.
  - [ ] Legacy zero-producer saves are normalized to starter passive income.
  - [ ] Saves with existing producers are not overwritten.

  **QA Scenarios**:
  ```
  Scenario: Fresh game earns without pressing 뿅
    Tool: Bash
    Steps: Run `npm test -- tests/gameState.test.js`.
    Expected: A fresh `createInitialState(0)` advanced to `10_000` has more plankton than at creation.
    Evidence: .omo/evidence/task-2-fresh-idle-income.txt

  Scenario: Legacy zero-producer save receives starter production safely
    Tool: Bash
    Steps: Run `npm test -- tests/storage.test.js`.
    Expected: Legacy fixture loads with `driftPolyps >= 1`, applies elapsed offline production, and does not throw.
    Evidence: .omo/evidence/task-2-legacy-starter.txt
  ```

  **Commit**: NO | Message: `fix(economy): start idle income immediately` | Files: [`src/economy.js`, `src/gameState.js`, `tests/economy.test.js`, `tests/gameState.test.js`, `tests/storage.test.js`]

- [x] 3. Wire dynamic pulse amount through UI button, burst text, and feedback

  **What to do**:
  1. Update `src/main.js` imports at `src/main.js:4-17` to import `getPulsePlanktonGain` and stop importing the removed fixed constant.
  2. In the pulse click handler at `src/main.js:185-199`, compute the expected amount before state update using the same pre-click economy that `pulseReefInState()` uses.
     - Required value: `const pulseAmount = getPulsePlanktonGain(state.economy)` before assigning `state = pulseReefInState(state)`.
     - Use `pulseAmount` for `burst()` amount text.
  3. In `renderReefPanel()` at `src/main.js:421-470`, compute `const pulseAmount = getPulsePlanktonGain(state.economy)` and render `pulseButton` with that amount at `src/main.js:437-439`.
  4. Change `createFeedFeedback()` at `src/main.js:502-510` to accept the computed amount as an argument: `createFeedFeedback(locale, previousLifetime, nextState, pulseAmount)`, then set `amount: pulseAmount` in the returned object.
  5. If any import or test still references `PULSE_PLANKTON_GAIN`, replace it with helper/minimum constants only where semantically correct.
  6. Do not change Korean copy except dynamic interpolation continues to use `pulseButton: '뿅! +{amount} {icon}'` at `src/i18n.js:200`.

  **Must NOT do**:
  - Do not duplicate the formula in `main.js`; UI must call the helper.
  - Do not show stale `+3` after buying producers or upgrades.
  - Do not change tab navigation, producer cards, or settings layout.

  **Recommended Agent Profile**:
  - Category: `quick` - Reason: targeted UI data wiring.
  - Skills: [] - No specialized skill needed.
  - Omitted: [`frontend-ui-ux`] - no visual redesign requested.

  **Parallelization**: Can Parallel: NO | Wave 3 | Blocks: [4] | Blocked By: [1]

  **References**:
  - Pattern: `src/main.js:185-199` - pulse click and burst text.
  - Pattern: `src/main.js:437-439` - pulse button amount display.
  - Pattern: `src/main.js:502-510` - feedback card amount source.
  - Text: `src/i18n.js:193-200` - Korean per-second and pulse interpolation.

  **Acceptance Criteria**:
  - [ ] `npm test -- tests/gameState.test.js` passes after import updates.
  - [ ] `npm run build` passes with no missing export/import errors.
  - [ ] Source search for `PULSE_PLANKTON_GAIN` returns no stale UI usage; only intentional renamed minimum constants remain.
  - [ ] Browser shows pulse button amount from `getPulsePlanktonGain(state.economy)`.

  **QA Scenarios**:
  ```
  Scenario: Pulse button displays dynamic amount
    Tool: Playwright
    Steps: Start dev server, open the app, select Korean if needed, inspect the pulse button text in the reef tab.
    Expected: Button text matches `뿅! +{computed amount}` where computed amount is greater than 3 on the starter state.
    Evidence: .omo/evidence/task-3-dynamic-pulse-ui.png

  Scenario: Pulse feedback uses same dynamic amount
    Tool: Playwright
    Steps: Click the pulse button once and inspect the feedback card/stat text.
    Expected: Floating/burst/feedback amount equals the button amount from before the click; no `+3` stale value appears.
    Evidence: .omo/evidence/task-3-dynamic-pulse-feedback.png
  ```

  **Commit**: NO | Message: `fix(ui): show computed pulse amount` | Files: [`src/main.js`, `src/i18n.js` only if interpolation keys require no semantic copy change]

- [x] 4. Harden online/offline regression coverage and run full verification

  **What to do**:
  1. Add/update regression tests before final fixes:
     - `tests/storage.test.js`: offline load from starter passive state increases plankton, caps at `MAX_OFFLINE_SECONDS`, and repeated save/load does not double-grant the same elapsed time.
     - `tests/storage.test.js`: future timestamp / negative elapsed still grants no negative or bonus production.
     - `tests/balanceSimulation.test.js`: update expected rates only if starter `driftPolyps` changes early numbers; keep finite-number assertions.
     - `tests/economy.test.js`: update early-game balance assertions at `tests/economy.test.js:143-151` only as required by starter production and dynamic pulse changes.
  2. If tests reveal double-grant behavior, fix only timestamp handling in `saveGame()` / `loadGame()` while preserving current design:
     - `saveGame()` at `src/storage.js:4-17` must advance once then set `lastSavedAt` and `lastTickAt` to `now`.
     - `loadGame()` at `src/storage.js:19-44` must use capped elapsed seconds and return `lastSavedAt: now`.
  3. Run the full test and build commands.
  4. Capture command output into evidence files.

  **Must NOT do**:
  - Do not weaken finite-number safeguards.
  - Do not raise balance ceilings solely to hide runaway production unless the new starter producer mathematically requires a tiny documented adjustment.
  - Do not remove offline cap or notice behavior.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: cross-file regression and balance verification across save/load/economy tests.
  - Skills: [] - No specialized skill needed.
  - Omitted: [`performance-optimizer`] - verification and regression, not benchmark tuning.

  **Parallelization**: Can Parallel: NO | Wave 4 | Blocks: [] | Blocked By: [1, 2, 3]

  **References**:
  - Pattern: `src/storage.js:4-17` - save path that advances pending production.
  - Pattern: `src/storage.js:19-44` - offline load/cap path.
  - Config: `src/config.js:15` - `MAX_OFFLINE_SECONDS` must remain unchanged.
  - Test: `tests/storage.test.js:13-40` - existing offline progress and cap tests.
  - Test: `tests/balanceSimulation.test.js:4-14` - finite/cap safety style.

  **Acceptance Criteria**:
  - [ ] `npm test` passes.
  - [ ] `npm run build` passes.
  - [ ] Offline cap remains exactly `MAX_OFFLINE_SECONDS`.
  - [ ] Repeated load/save cannot repeatedly grant the same offline interval.
  - [ ] No formatted amount shows `Infinity` or `NaN`.

  **QA Scenarios**:
  ```
  Scenario: Full automated test suite validates economy behavior
    Tool: Bash
    Steps: Run `npm test`.
    Expected: All Vitest suites pass, including new dynamic pulse and starter idle tests.
    Evidence: .omo/evidence/task-4-full-tests.txt

  Scenario: Production build catches import/export and syntax regressions
    Tool: Bash
    Steps: Run `npm run build`.
    Expected: Vite build exits 0 with no missing `PULSE_PLANKTON_GAIN` export or syntax errors.
    Evidence: .omo/evidence/task-4-build.txt
  ```

  **Commit**: NO | Message: `test(economy): cover idle income regressions` | Files: [`tests/storage.test.js`, `tests/balanceSimulation.test.js`, `tests/economy.test.js`, `src/storage.js` only if regression fix required]

## Final Verification Wave (MANDATORY — after ALL implementation tasks)
> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.
> **Do NOT auto-proceed after verification. Wait for user's explicit approval before marking work complete.**
> **Never mark F1-F4 as checked before getting user's okay.** Rejection or user feedback -> fix -> re-run -> present again -> wait for okay.
- [x] F1. Plan Compliance Audit — oracle
- [x] F2. Code Quality Review — oracle/code self-review
- [x] F3. Real Manual QA — Playwright
- [x] F4. Scope Fidelity Check — context search + docs update

## Commit Strategy
- User did not request commits. Do not commit unless explicitly asked.
- If commits are later requested, use one atomic commit after all tests/build/QA pass: `fix(economy): scale idle income and pulse rewards`.
- Stage only intended files: `src/gameState.js`, `src/economy.js`, `src/main.js`, `tests/gameState.test.js`, `tests/economy.test.js`, `tests/storage.test.js`, `tests/balanceSimulation.test.js`, and `src/storage.js` only if a regression fix was necessary.

## Success Criteria
- Fresh game visibly earns resources without pressing 뿅.
- 뿅 amount is not fixed at `+3`; it grows with plankton/sec production.
- Offline rewards continue to work through existing capped save/load path.
- UI displays the same computed amount used by state mutation.
- `npm test` and `npm run build` pass.
- Final verification agents approve and the user explicitly approves completion.
