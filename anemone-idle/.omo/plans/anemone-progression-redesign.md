# Anemone Idle Progression, Balance, and Quest Redesign Plan

## TL;DR
> **Summary**: Convert the current resource-clicker prototype into a chapter-driven idle/adventure progression loop by adding declarative content, explicit quest states, versioned migration, balance simulations, and UI next-goal guidance while preserving the existing vanilla JS/Vite/Vitest stack.
> **Deliverables**:
> - Chapter 1 progression model targeting a satisfying 0-15 minute loop.
> - Declarative conditions/rewards schema for quests, chapters, unlocks, and progression guidance.
> - Save migration from current `version: 1` saves.
> - Balance report and quest graph validators proving reachability and no softlocks.
> - UI surfacing for chapters, active goals, next unlocks, and producer-spawn progress.
> **Effort**: Large
> **Parallel**: YES - 7 waves
> **Critical Path**: Task 1 → Task 2 → Task 4 → Task 7 → Task 8 → Task 9 → Task 11 → Final Verification

## Context

### Original Request
The user said the current game does not progress like the originally planned “adventure 코뮤니스타” concept and specifically called out missing/weak balancing, quests, and game progression. They asked to properly understand the system structure, decide what to do, and save the plan as Markdown.

### Interview Summary
- This is not a one-off UI bug; it is an architecture-level progression design pass.
- Interpret “adventure 코뮤니스타” as the desired feel of community/adventure chapters, not a mandatory full retheme or rename.
- Use the existing project: vanilla JavaScript, direct DOM rendering, Vite, Vitest, localStorage saves.
- MVP target is Chapter 1: a complete 0-15 minute loop that can later extend to longer chapters.

### Current-State Findings
- `src/config.js:1-18` defines 3 resources and global balance constants; `src/config.js:27-95` defines 6 producers, including both resource producers and `producesProducer` auto-hire tiers.
- `src/config.js:99-123` defines 3 upgrades; `src/config.js:125-175` defines 7 flat missions with no chapter, unlock, active, completed, or claimed state model.
- `src/economy.js:98-105` calculates producer costs as `baseCost * costGrowth^owned`; `src/economy.js:197-216` applies resource production and producer spawning.
- `src/logic/production.js:29-47` preserves fractional producer-spawn progress and increments `stats.autoHires` when lower-tier workers are generated.
- `src/missions.js:4-7` stores only claimed mission IDs; `src/missions.js:28-36` shows the first 3 unclaimed missions; `src/missions.js:39-55` claims rewards once.
- `src/gameState.js:17-30` creates `version: 1` state; `src/gameState.js:160-182` sanitizes existing saves but does not implement versioned migrations.
- `src/storage.js:19-44` loads localStorage saves, applies offline progress, and caps offline time via `MAX_OFFLINE_SECONDS` from `src/config.js:15`.
- `src/main.js:366-388` renders top-strip missions directly from `getVisibleMissions`; `src/main.js:406-453` renders all producers directly; there is no chapter dashboard or next-goal guidance.
- `docs/advanced_roadmap.md:95-105` defines the intended emotional curve; `docs/advanced_roadmap.md:127-137` defines numeric 0-12 minute targets; `docs/advanced_roadmap.md:272-283` defines release-grade numeric QA checks.
- `tests/economy.test.js:66-75` already contains a simple active-start simulation; `tests/economy.test.js:143-151` checks 5-minute/15-minute growth bounds, but there is no balance report or quest graph validator.

### Metis Review (gaps addressed)
- Added explicit MVP scope and out-of-scope boundaries to avoid theme rewrite, backend, ads, prestige, and framework migration creep.
- Defaulted target progression to a 15-minute Chapter 1 loop because `docs/advanced_roadmap.md` already defines that window.
- Required executable acceptance criteria, numeric simulation targets, quest graph validation, and save migration checks.
- Required explicit handling for old saves, duplicate rewards, cyclic dependencies, impossible unlocks, offline jumps, and end-of-content states.

### Oracle Review (gates addressed)
- Oracle phase 0 architecture advice: define loops and content schema first, then let UI consume pure state/action logic.
- Oracle phase 1 verdict: GO. Existing research and assumptions are sufficient to generate this plan without more user questions.

## Work Objectives

### Core Objective
Make the game feel like a structured idle/adventure progression instead of a loose accumulation toy: players should always know the current chapter goal, the next unlock, the bottleneck, and why hiring/upgrading matters.

### Deliverables
1. Declarative progression content model for chapters, quests, conditions, rewards, and guidance.
2. State model upgrade from flat claimed missions to locked/active/completed/claimed quest states.
3. Versioned save migration from existing v1 saves.
4. Chapter 1 content using the current office/anemone theme and “community/adventure progression” feel.
5. Balance simulation/report proving the 0-15 minute loop is reachable and not explosive.
6. UI updates showing current chapter, active quests, next unlock, and producer-spawn progress.
7. Tests for migration, quest graph validity, balance targets, and UI smoke behavior.

### Definition of Done (verifiable conditions with commands)
- `npm test` passes with all existing tests plus new progression, migration, quest graph, and balance simulation tests.
- `npm run build` succeeds.
- `node scripts/balance-report.mjs --scenario chapter1-active` exits `0` and writes `.omo/evidence/balance-chapter1-active.json`.
- `node scripts/validate-progression-content.mjs` exits `0` and writes `.omo/evidence/progression-content-validation.json`.
- Browser QA confirms active Korean progression UI shows chapter title, active quests, next unlock, and no empty mission strip during Chapter 1.

### Must Have
- Keep stable IDs for all resources, producers, upgrades, quests, chapters, zones, conditions, and rewards.
- Preserve existing resources for MVP: `plankton`, `pearls`, `tideEnergy`.
- Preserve existing producer-spawner mechanic and fractional progress from `src/logic/production.js:29-47`.
- Preserve existing localStorage saves by migrating current `version: 1` data.
- Keep implementation in vanilla JS/Vite/Vitest; no new framework.
- Use numeric targets from `docs/advanced_roadmap.md:127-137` and `docs/advanced_roadmap.md:272-283` as the Chapter 1 balance baseline.
- Provide an end-of-content state after Chapter 1 so players are not left with an empty quest list.

### Must NOT Have
- No full rename/retheme to “코뮤니스타” unless separately approved.
- No React/Redux/ECS/TypeScript migration.
- No backend/database/real ads/AdMob integration in this MVP.
- No prestige/reset loop in this MVP.
- No mission logic embedded directly in `src/main.js`.
- No vague “improve balance” task without numeric pass/fail targets.
- No reward duplication on reload, old-save migration, or repeated claim attempts.

## Verification Strategy
> Agent-executed verification only. Final completion still waits for the user's explicit approval as required in the Final Verification Wave.
- Test decision: tests-after for architecture and content work using existing Vitest. Add red tests before each implementation task when practical.
- QA policy: Every task has agent-executed happy path and failure/edge scenarios.
- Evidence: `.omo/evidence/task-{N}-{slug}.{ext}`.
- Browser QA: Use Playwright for visible progression UI and no-console-error checks.
- Command QA: Use `npm test`, `npm run build`, `node scripts/validate-progression-content.mjs`, and `node scripts/balance-report.mjs --scenario chapter1-active`.

## Execution Strategy

### Parallel Execution Waves
> Target: 5-8 tasks per wave. This plan has fewer tasks because state migration and progression graph work are tightly coupled; parallelism is used only where dependencies are safe.

Wave 1: Task 1, Task 2, Task 3
Wave 2: Task 4, Task 5, Task 6
Wave 3: Task 7
Wave 4: Task 8
Wave 5: Task 9, Task 10
Wave 6: Task 11
Wave 7: Task 12

### Dependency Matrix
| Task | Depends On | Blocks |
|---:|---|---|
| 1 | none | 4, 5, 6, 7, 8, 10 |
| 2 | none | 4, 7, 8 |
| 3 | none | 5, 9, 11 |
| 4 | 1, 2 | 7, 8, 10 |
| 5 | 1, 3 | 9, 10 |
| 6 | 1 | 7, 8, 10 |
| 7 | 1, 2, 4, 6 | 8, 9, 10, 11 |
| 8 | 7 | 9, 10 |
| 9 | 3, 5, 7, 8 | 11 |
| 10 | 1, 4, 5, 6, 7, 8 | 11, 12 |
| 11 | 7, 9, 10 | 12 |
| 12 | 10, 11 | Final Verification |

### Agent Dispatch Summary
| Wave | Task Count | Categories |
|---|---:|---|
| 1 | 3 | quick, deep |
| 2 | 3 | deep, quick |
| 3 | 1 | deep |
| 4 | 1 | visual-engineering |
| 5 | 2 | visual-engineering, quick |
| 6 | 1 | deep |
| 7 | 1 | writing |

## TODOs

- [ ] 1. Extract progression content schema and validators

  **What to do**: Create declarative content modules for progression without changing runtime behavior yet.
  - Add `src/progression/content.js` exporting `CHAPTER_DEFINITIONS`, `QUEST_DEFINITIONS`, `CONDITION_TYPES`, and `REWARD_TYPES`.
  - Add `src/progression/validators.js` with pure validation functions for ID existence, duplicate IDs, missing references, circular quest prerequisites, unreachable chapter entry quests, invalid rewards, and finite numeric targets.
  - Use stable IDs. Chapter 1 ID must be `chapter1-office-reef`. Initial quest IDs must include `quest-first-interns`, `quest-first-shrimp`, `quest-first-capsule`, `quest-first-upgrade`, `quest-first-crab`, and `quest-chapter1-complete`.
  - Do not remove existing `MISSION_DEFINITIONS` yet; this task is additive.

  **Must NOT do**: Do not alter current mission UI or save format in this task. Do not introduce JSON schema libraries.

  **Recommended Agent Profile**:
  - Category: `deep` - Reason: schema design affects every later system.
  - Skills: `[]` - Existing JS/Vitest patterns are sufficient.
  - Omitted: [`api-endpoint-builder`, `performance-optimizer`] - No API or measured performance bottleneck.

  **Parallelization**: Can Parallel: YES | Wave 1 | Blocks: 4, 5, 6, 7, 8, 10 | Blocked By: none

  **References**:
  - Pattern: `src/config.js:125-175` - Existing flat mission definition shape to evolve from.
  - Pattern: `src/missions.js:10-26` - Existing condition evaluation for producer/resource/stat goals.
  - External: `docs/advanced_roadmap.md:95-105` - Emotional curve for Chapter 1 goals.

  **Acceptance Criteria**:
  - [ ] `npm test -- tests/progression.test.js` passes after tests are added.
  - [ ] Validator rejects a duplicate quest ID fixture with an error containing `duplicate quest id`.
  - [ ] Validator rejects a missing producer/resource reference fixture with an error containing the missing ID.

  **QA Scenarios**:
  ```
  Scenario: Valid Chapter 1 content passes
    Tool: Bash
    Steps: Run `npm test -- tests/progression.test.js`
    Expected: Test file passes and includes a case named `validates chapter 1 progression content`
    Evidence: .omo/evidence/task-1-progression-validator.txt

  Scenario: Invalid content fails fast
    Tool: Bash
    Steps: Run validator unit test that injects a quest requiring `missingProducerId`
    Expected: Assertion sees validation error mentioning `missingProducerId`
    Evidence: .omo/evidence/task-1-progression-validator-error.txt
  ```

  **Commit**: NO | Reason: current project is not a git repository. If a repo is initialized later, use message `feat(progression): add content schema validation` for files `src/progression/content.js`, `src/progression/validators.js`, `tests/progression.test.js`.

- [ ] 2. Define quest/chapter runtime state model

  **What to do**: Add pure progression state helpers that can represent `locked`, `active`, `completed`, and `claimed` without UI coupling.
  - Add `src/progression/state.js` with `createProgressionState()`, `cloneProgressionState()`, `evaluateCondition()`, `evaluateConditions()`, `refreshProgressionState()`, and `claimQuestReward()`.
  - Quest statuses must be exactly `locked`, `active`, `completed`, `claimed`.
  - Chapter statuses must be exactly `locked`, `active`, `completed`.
  - Completed-but-unclaimed quests must remain visible and claimable after reload.
  - Claimed quest rewards must never be paid twice.
  - Use existing reward logic through `addResources()` from `src/economy.js:187-195`.

  **Must NOT do**: Do not delete `src/missions.js` yet. Do not mutate DOM. Do not add narrative branching beyond Chapter 1.

  **Recommended Agent Profile**:
  - Category: `deep` - Reason: state machine and reward idempotency are high-risk.
  - Skills: `[]` - Pure JS logic only.
  - Omitted: [`frontend-ui-ux`] - No UI in this task.

  **Parallelization**: Can Parallel: YES | Wave 1 | Blocks: 4, 7, 8 | Blocked By: none

  **References**:
  - Pattern: `src/missions.js:39-55` - One-time reward claim behavior to preserve.
  - Pattern: `src/gameState.js:92-101` - State action wrapper style.
  - API/Type: `src/economy.js:187-195` - Resource reward application.

  **Acceptance Criteria**:
  - [ ] `npm test -- tests/progressionState.test.js` passes.
  - [ ] Tests prove a locked quest becomes active only when unlock conditions pass.
  - [ ] Tests prove completed quest remains completed until claimed.
  - [ ] Tests prove second claim attempt returns `claimed: false` and pays no resources.

  **QA Scenarios**:
  ```
  Scenario: Quest state advances through lifecycle
    Tool: Bash
    Steps: Run `npm test -- tests/progressionState.test.js -t "quest lifecycle"`
    Expected: locked -> active -> completed -> claimed assertions pass
    Evidence: .omo/evidence/task-2-quest-lifecycle.txt

  Scenario: Duplicate claim is blocked
    Tool: Bash
    Steps: Run `npm test -- tests/progressionState.test.js -t "blocks duplicate rewards"`
    Expected: second claim keeps resources unchanged
    Evidence: .omo/evidence/task-2-duplicate-claim.txt
  ```

  **Commit**: NO | Reason: current project is not a git repository. If a repo is initialized later, use message `feat(progression): add quest state machine` for files `src/progression/state.js`, `tests/progressionState.test.js`.

- [ ] 3. Add balance target definitions from the roadmap

  **What to do**: Capture numeric progression targets in code so balancing is testable.
  - Add `src/progression/balanceTargets.js` exporting Chapter 1 target windows.
  - Targets must include: 60s intern minimum 3, 180s shrimp 2-4, 300s crab price visible but not repeatedly affordable, 900s at least 1 crab, production cap under 20,000/s at 15 minutes.
  - Use `docs/advanced_roadmap.md:127-137` and `docs/advanced_roadmap.md:272-283` as source comments.
  - Define the MVP end condition: `quest-chapter1-complete` after first crab + 2 upgrades + one capsule claim, with later chapters shown as “준비 중”.

  **Must NOT do**: Do not tune producer numbers in this task.

  **Recommended Agent Profile**:
  - Category: `quick` - Reason: small constants file and tests.
  - Skills: `[]` - No specialist skill required.
  - Omitted: [`performance-optimizer`] - Not measuring runtime performance.

  **Parallelization**: Can Parallel: YES | Wave 1 | Blocks: 5, 9, 11 | Blocked By: none

  **References**:
  - Pattern: `docs/advanced_roadmap.md:127-137` - Balance table.
  - Pattern: `docs/advanced_roadmap.md:272-283` - Numeric QA criteria.
  - Test: `tests/economy.test.js:143-151` - Existing 5/15-minute simulation assertion style.

  **Acceptance Criteria**:
  - [ ] `npm test -- tests/balanceTargets.test.js` passes.
  - [ ] Each target has `id`, `timeSeconds`, `metric`, `min` and/or `max`, and `source` fields.
  - [ ] Test proves no target has `NaN`, `Infinity`, negative time, or missing source.

  **QA Scenarios**:
  ```
  Scenario: Balance targets are finite and sourced
    Tool: Bash
    Steps: Run `npm test -- tests/balanceTargets.test.js`
    Expected: all targets have finite values and roadmap source labels
    Evidence: .omo/evidence/task-3-balance-targets.txt

  Scenario: Invalid target fixture is rejected
    Tool: Bash
    Steps: Run unit test with `timeSeconds: Infinity`
    Expected: validation fails with `invalid balance target`
    Evidence: .omo/evidence/task-3-balance-targets-error.txt
  ```

  **Commit**: NO | Reason: current project is not a git repository. If a repo is initialized later, use message `test(balance): codify chapter one targets` for files `src/progression/balanceTargets.js`, `tests/balanceTargets.test.js`.

- [ ] 4. Migrate saves from v1 mission state to progression state

  **What to do**: Add versioned migration while preserving old saves.
  - Update `createInitialState()` in `src/gameState.js:17-30` to create `version: 2` and a `progression` field from `createProgressionState()`.
  - Add migration helpers, either in `src/gameState.js` or `src/progression/migrations.js`, with `migrateState(raw, now)`.
  - `sanitizeState()` must accept v1 saves that have `missions.claimed` only and initialize progression statuses without paying rewards again.
  - Existing `missions` field may remain during transition, but runtime source of truth after migration must be `state.progression`.
  - Preserve `economy.producerProgress` so fractional auto-hires are not lost.
  - Keep invalid localStorage behavior from `src/storage.js:45-47`: bad saves fall back safely.

  **Must NOT do**: Do not reset user resources, producers, upgrades, capsule, locale, ad buff, timestamps, or fractional producer progress.

  **Recommended Agent Profile**:
  - Category: `deep` - Reason: save migration is irreversible and high-risk.
  - Skills: `bug-hunter` - Useful for edge cases and regression prevention.
  - Omitted: [`frontend-ui-ux`] - No UI work.

  **Parallelization**: Can Parallel: NO | Wave 2 | Blocks: 7, 8, 10 | Blocked By: 1, 2

  **References**:
  - Pattern: `src/gameState.js:160-182` - Existing sanitize path.
  - Pattern: `src/storage.js:19-44` - Load/offline flow that calls sanitize.
  - Test: `tests/storage.test.js` - Existing storage compatibility patterns.

  **Acceptance Criteria**:
  - [ ] `npm test -- tests/storage.test.js tests/progressionMigration.test.js` passes.
  - [ ] v1 save fixture with `missions.claimed: ['first-bloom']` loads as v2 with equivalent claimed quest state and no duplicate resources.
  - [ ] v1 save fixture preserving `economy.producerProgress.cleanerShrimp = 0.75` loads with same fractional progress.
  - [ ] invalid save JSON still returns initial state and does not throw.

  **QA Scenarios**:
  ```
  Scenario: Old v1 save migrates safely
    Tool: Bash
    Steps: Run `npm test -- tests/progressionMigration.test.js -t "migrates v1 saves"`
    Expected: state.version is 2 and progression claimed status matches old mission claim
    Evidence: .omo/evidence/task-4-v1-migration.txt

  Scenario: Duplicate rewards are not paid during migration
    Tool: Bash
    Steps: Run `npm test -- tests/progressionMigration.test.js -t "does not duplicate claimed rewards"`
    Expected: resources equal pre-migration fixture values
    Evidence: .omo/evidence/task-4-no-duplicate-rewards.txt
  ```

  **Commit**: NO | Reason: current project is not a git repository. If a repo is initialized later, use message `feat(save): migrate progression state` for files `src/gameState.js`, `src/progression/migrations.js`, `tests/progressionMigration.test.js`, `tests/storage.test.js`.

- [ ] 5. Build Chapter 1 balance simulator and report script

  **What to do**: Add deterministic simulation tooling for the active 0-15 minute loop.
  - Add `scripts/balance-report.mjs`.
  - Script scenario `chapter1-active` must simulate: pulse once per second, claim available quests, buy affordable useful producers/upgrades using a deterministic priority, start/claim capsules when possible, and advance time to 900 seconds.
  - Write `.omo/evidence/balance-chapter1-active.json` with timeline checkpoints at 60, 180, 300, 420, 720, and 900 seconds.
  - Exit nonzero if any balance target from Task 3 fails.
  - Do not require browser or localStorage.

  **Must NOT do**: Do not tune numbers inside the script; it should report failures, not hide them.

  **Recommended Agent Profile**:
  - Category: `deep` - Reason: deterministic simulation must match game logic and catch softlocks.
  - Skills: `performance-optimizer` - Useful to keep simulation deterministic and cheap if expanded.
  - Omitted: [`frontend-ui-ux`] - No UI work.

  **Parallelization**: Can Parallel: YES | Wave 2 | Blocks: 9, 10 | Blocked By: 1, 3

  **References**:
  - Pattern: `tests/economy.test.js:66-75` - Existing active-start simulation helper.
  - Pattern: `src/gameState.js:32-39` - Time advancement behavior.
  - Pattern: `src/storage.js:28-36` - Offline advancement flow to keep simulation consistent.

  **Acceptance Criteria**:
  - [ ] `node scripts/balance-report.mjs --scenario chapter1-active` runs and writes `.omo/evidence/balance-chapter1-active.json`; exit may be nonzero before Task 11 tuning if targets fail.
  - [ ] Report includes checkpoint fields: `seconds`, `resources`, `producers`, `rates`, `activeQuests`, `completedQuests`, `failedTargets`.
  - [ ] If a test fixture makes `driftPolyps.baseRate = 0`, report exits nonzero and names failed target.

  **QA Scenarios**:
  ```
  Scenario: Chapter 1 active simulation passes
    Tool: Bash
    Steps: Run `node scripts/balance-report.mjs --scenario chapter1-active`
    Expected: evidence JSON is written with checkpoints; target failures, if any, are listed in `failedTargets` for Task 11
    Evidence: .omo/evidence/task-5-balance-report.txt

  Scenario: Broken balance fails loudly
    Tool: Bash
    Steps: Run unit test/mocked scenario with zero early production
    Expected: nonzero result or assertion containing failed 60s/180s target
    Evidence: .omo/evidence/task-5-balance-report-error.txt
  ```

  **Commit**: NO | Reason: current project is not a git repository. If a repo is initialized later, use message `test(balance): add chapter one simulation report` for files `scripts/balance-report.mjs`, `tests/balanceSimulation.test.js`.

- [ ] 6. Create compatibility adapter from old missions to new progression

  **What to do**: Keep existing app stable while new progression replaces flat missions.
  - Add `src/progression/legacyMissionAdapter.js` mapping current mission IDs from `src/config.js:125-175` to new quest IDs.
  - Ensure old mission IDs: `first-bloom`, `shrimp-shift`, `pearl-cache`, `branch-boss`, `tidal-gift`, `whale-boardroom`, `prismatic-growth` each have explicit mapping or explicit deprecation status.
  - Adapter must expose `getVisibleProgressionQuests(state, limit = 3)` matching the current UI needs but backed by progression statuses.
  - Keep `src/missions.js` tests passing until UI fully switches.

  **Must NOT do**: Do not silently drop claimed old missions; do not rename producer IDs.

  **Recommended Agent Profile**:
  - Category: `quick` - Reason: compatibility bridge and tests.
  - Skills: `[]` - Straightforward adapter.
  - Omitted: [`api-endpoint-builder`] - No API.

  **Parallelization**: Can Parallel: YES | Wave 2 | Blocks: 7, 8, 10 | Blocked By: 1

  **References**:
  - Pattern: `src/missions.js:28-36` - Current visible mission contract.
  - Pattern: `src/config.js:125-175` - Legacy mission IDs.
  - Test: `tests/missions.test.js:14-37` - Existing claim behavior to preserve until migration completes.

  **Acceptance Criteria**:
  - [ ] `npm test -- tests/progressionAdapter.test.js tests/missions.test.js` passes.
  - [ ] Adapter returns no more than 3 visible active/completed quests by default.
  - [ ] Adapter never returns claimed quests.
  - [ ] Each legacy mission ID has a tested mapping or explicit deprecation entry.

  **QA Scenarios**:
  ```
  Scenario: Adapter returns current active quests
    Tool: Bash
    Steps: Run `npm test -- tests/progressionAdapter.test.js -t "visible progression quests"`
    Expected: max 3 quests, no claimed quests, completed quests claimable
    Evidence: .omo/evidence/task-6-adapter-visible.txt

  Scenario: Legacy mission map is complete
    Tool: Bash
    Steps: Run `npm test -- tests/progressionAdapter.test.js -t "maps legacy mission ids"`
    Expected: all 7 old IDs are mapped or explicitly deprecated
    Evidence: .omo/evidence/task-6-adapter-legacy.txt
  ```

  **Commit**: NO | Reason: current project is not a git repository. If a repo is initialized later, use message `feat(progression): bridge legacy missions` for files `src/progression/legacyMissionAdapter.js`, `tests/progressionAdapter.test.js`.

- [ ] 7. Wire progression actions into game state

  **What to do**: Make `gameState` refresh progression after every relevant action.
  - Update `advanceGame()`, `buyProducerInState()`, `pulseReefInState()`, `purchaseUpgradeInState()`, `claimCapsule()`, and `claimRewardedAdBuffInState()` so returned state has refreshed progression statuses.
  - Add `claimQuestInState(state, questId)` to replace or sit alongside `claimMissionInState()`.
  - Preserve old `claimMissionInState()` as a compatibility wrapper during this plan unless all call sites are changed in the same task.
  - Progression refresh must be pure and deterministic.
  - Completed quests should be claimable; claimed quests should not appear again.

  **Must NOT do**: Do not call DOM functions from game state. Do not pay rewards during mere refresh.

  **Recommended Agent Profile**:
  - Category: `deep` - Reason: central state transitions and idempotency.
  - Skills: `bug-hunter` - Regression-heavy area.
  - Omitted: [`frontend-ui-ux`] - UI comes later.

  **Parallelization**: Can Parallel: NO | Wave 3 | Blocks: 9, 10, 11 | Blocked By: 1, 2, 4, 6

  **References**:
  - Pattern: `src/gameState.js:32-39` - Advance action shape.
  - Pattern: `src/gameState.js:52-69` - Buy/pulse action shape.
  - Pattern: `src/gameState.js:92-101` - Claim mission wrapper shape.

  **Acceptance Criteria**:
  - [ ] `npm test -- tests/gameState.test.js tests/progressionState.test.js` passes.
  - [ ] Buying the first 5 `driftPolyps` causes `quest-first-interns` to become completed.
  - [ ] Claiming `quest-first-interns` pays reward once and transitions it to claimed.
  - [ ] Advancing time and offline loading refresh quest completion without auto-claiming rewards.

  **QA Scenarios**:
  ```
  Scenario: Producer purchase updates quest completion
    Tool: Bash
    Steps: Run `npm test -- tests/gameState.test.js -t "updates progression after producer buys"`
    Expected: relevant quest status is completed after fifth intern
    Evidence: .omo/evidence/task-7-progress-after-buy.txt

  Scenario: Refresh does not auto-pay rewards
    Tool: Bash
    Steps: Run `npm test -- tests/gameState.test.js -t "does not auto claim progression rewards"`
    Expected: resources unchanged until claimQuestInState is called
    Evidence: .omo/evidence/task-7-no-auto-claim.txt
  ```

  **Commit**: NO | Reason: current project is not a git repository. If a repo is initialized later, use message `feat(game-state): refresh progression actions` for files `src/gameState.js`, `tests/gameState.test.js`, `tests/progressionState.test.js`.

- [ ] 8. Replace flat mission visibility with progression quest visibility

  **What to do**: Move mission strip data source from flat `getVisibleMissions()` to progression-backed visible quests.
  - Update `src/main.js:366-388` to render active/completed progression quests with status labels.
  - Claim buttons must call `claimQuestInState()` or compatibility handler, not direct old mission claim, once Task 7 is complete.
  - Empty visible list must show an end-of-content card: `1장 업무 산호초 완료 — 다음 커뮤니티 원정 준비 중`.
  - Keep at most 3 compact cards in the top strip for mobile layout.
  - Include progress display for condition types: producer count, resource earned, stat, upgrade count, quest completion, chapter completion.

  **Must NOT do**: Do not change bottom tab structure. Do not remove capsule/upgrades/settings tabs.

  **Recommended Agent Profile**:
  - Category: `visual-engineering` - Reason: visible UI state and mobile card behavior.
  - Skills: `frontend-ui-ux` - Must preserve chunky playful UI style.
  - Omitted: [`rayden-code`] - Project is not Rayden/React.

  **Parallelization**: Can Parallel: NO | Wave 4 | Blocks: 9, 10 | Blocked By: 7

  **References**:
  - Pattern: `src/main.js:366-388` - Current mission strip markup.
  - Pattern: `src/styles.css` mission card styles - Extend existing visual language, do not rewrite.
  - Text: `src/i18n.js` - Add all new copy for English/Korean and maintain coverage.

  **Acceptance Criteria**:
  - [ ] `npm test -- tests/i18n.test.js tests/progressionAdapter.test.js` passes.
  - [ ] Browser QA sees no old flat mission list after progression is initialized.
  - [ ] Completed quest card button is enabled and claiming it updates resources and card state.
  - [ ] All quests completed shows the end-of-content card rather than an empty strip.

  **QA Scenarios**:
  ```
  Scenario: Active progression quests appear in Korean
    Tool: Playwright
    Steps: Open app, switch to Korean, inspect mission strip
    Expected: chapter/quest cards display Korean titles and progress values
    Evidence: .omo/evidence/task-8-korean-progress-quests.png

  Scenario: End-of-content guidance appears
    Tool: Playwright
    Steps: Seed localStorage with Chapter 1 completed state and reload
    Expected: card text includes `1장 업무 산호초 완료` and no console errors
    Evidence: .omo/evidence/task-8-end-content.png
  ```

  **Commit**: NO | Reason: current project is not a git repository. If a repo is initialized later, use message `feat(ui): show progression quests` for files `src/main.js`, `src/i18n.js`, `src/styles.css`, `tests/i18n.test.js`.

- [ ] 9. Add chapter dashboard and next-goal guidance

  **What to do**: Make progression understandable from the main reef screen.
  - Add a compact chapter dashboard above or below the pulse panel in `renderReefPanel()` at `src/main.js:406-453`.
  - Dashboard must show: current chapter name, chapter progress percentage, next unlock, current bottleneck, and current production rate.
  - For Chapter 1, Korean title must be `1장: 업무 산호초 창업`. English title can be `Chapter 1: Office Reef Startup`.
  - Next unlock examples: first intern, first shrimp, first capsule, first upgrade, first crab, chapter complete.
  - Producer-spawner progress must be visible for producer-spawners after they are owned: e.g. `새우 자동 결재 42%` for crab progress.

  **Must NOT do**: Do not create new tabs. Do not make UI depend on hardcoded quest indexes.

  **Recommended Agent Profile**:
  - Category: `visual-engineering` - Reason: UI information architecture and mobile layout.
  - Skills: `frontend-ui-ux` - Visual clarity required.
  - Omitted: [`api-endpoint-builder`] - No API.

  **Parallelization**: Can Parallel: NO | Wave 5 | Blocks: 11 | Blocked By: 3, 5, 7, 8

  **References**:
  - Pattern: `src/main.js:352-364` - Existing resource/rate render style.
  - Pattern: `src/main.js:406-453` - Reef panel insertion point.
  - Pattern: `src/logic/production.js:37-40` - Fractional producer progress to surface.

  **Acceptance Criteria**:
  - [ ] Browser QA at 430px width shows dashboard without horizontal overflow.
  - [ ] Dashboard updates after hiring, quest claim, and capsule claim without reload.
  - [ ] Dashboard never shows `undefined`, `NaN`, or blank next unlock.
  - [ ] Reduced-motion media behavior remains respected by existing CSS rule.

  **QA Scenarios**:
  ```
  Scenario: Dashboard guides next action
    Tool: Playwright
    Steps: Start fresh Korean save, click 뿅 enough to buy first intern, observe dashboard before and after purchase
    Expected: next unlock changes from first intern guidance to first mission/shrimp guidance
    Evidence: .omo/evidence/task-9-dashboard-next-goal.png

  Scenario: Producer-spawn progress is visible
    Tool: Playwright
    Steps: Seed state with one crabBranchBoss and partial cleanerShrimp progress, reload
    Expected: dashboard shows auto-hire progress percentage, no NaN/undefined
    Evidence: .omo/evidence/task-9-spawn-progress.png
  ```

  **Commit**: NO | Reason: current project is not a git repository. If a repo is initialized later, use message `feat(ui): add chapter guidance dashboard` for files `src/main.js`, `src/styles.css`, `src/i18n.js`, `tests/i18n.test.js`.

- [ ] 10. Validate progression graph and balance in CI-friendly commands

  **What to do**: Provide executable checks for future tuning.
  - Add `scripts/validate-progression-content.mjs` that imports validators and content, writes `.omo/evidence/progression-content-validation.json`, and exits nonzero on errors.
  - Add or update tests so `npm test` covers graph reachability, migration, reward idempotency, balance target validity, and finite economy values.
  - Ensure both scripts create `.omo/evidence` if missing.
  - Document commands in `docs/guide.md:124-131` without changing broader guide content.

  **Must NOT do**: Do not require network, browser, or manual input for these scripts.

  **Recommended Agent Profile**:
  - Category: `quick` - Reason: command wrappers and tests.
  - Skills: `jq` optional for checking JSON evidence if needed.
  - Omitted: [`playwright`] - This task is command-only.

  **Parallelization**: Can Parallel: YES | Wave 5 | Blocks: 11, 12 | Blocked By: 1, 4, 5, 6, 7, 8

  **References**:
  - Pattern: `package.json:6-12` - Existing script command style.
  - Pattern: `docs/guide.md:124-131` - Current verification command docs.
  - Test: `tests/economy.test.js:160-177` - Existing finite-value tests.

  **Acceptance Criteria**:
  - [ ] `node scripts/validate-progression-content.mjs` exits `0` and writes JSON with `valid: true`.
  - [ ] `node scripts/balance-report.mjs --scenario chapter1-active` writes JSON with checkpoint data; exit may be nonzero until Task 11 tuning removes `failedTargets`.
  - [ ] `npm test` passes.
  - [ ] `npm run build` passes.

  **QA Scenarios**:
  ```
  Scenario: CI-friendly validation commands pass
    Tool: Bash
    Steps: Run `node scripts/validate-progression-content.mjs`, then run `node scripts/balance-report.mjs --scenario chapter1-active`, then run `npm test && npm run build`
    Expected: validation, tests, and build exit 0; balance report writes JSON and may list failedTargets for Task 11
    Evidence: .omo/evidence/task-10-ci-validation.txt

  Scenario: Validation evidence JSON is parseable
    Tool: Bash
    Steps: Run `node -e "JSON.parse(require('fs').readFileSync('.omo/evidence/progression-content-validation.json','utf8'))"`
    Expected: command exits 0
    Evidence: .omo/evidence/task-10-evidence-json.txt
  ```

  **Commit**: NO | Reason: current project is not a git repository. If a repo is initialized later, use message `test(progression): add validation scripts` for files `scripts/validate-progression-content.mjs`, `scripts/balance-report.mjs`, `docs/guide.md`, tests as needed.

- [ ] 11. Tune Chapter 1 numbers against the simulator

  **What to do**: Adjust existing numeric definitions only as needed to satisfy Chapter 1 targets.
  - Tune only `src/config.js` producer/upgrade numbers, `src/progression/content.js` quest rewards, legacy mission reward numbers, and `REWARDED_AD_BUFF` constants if tests prove current values miss targets.
  - Keep formulas unchanged unless a failing target cannot be solved by data tuning.
  - Use these target ranges from roadmap as hard constraints: 60s interns >=3, 180s shrimp between 2 and 4, 300s shows first crab wall, 900s at least 1 crab, production <=20,000/s.
  - Preserve `PULSE_PLANKTON_GAIN = 3` from `src/gameState.js:15` unless explicitly changed in a separate user-approved plan.
  - If a target fails, update report notes explaining which number changed and why.

  **Must NOT do**: Do not add monetization/real ad logic. Do not inflate early rewards so Chapter 1 completes before 5 minutes.

  **Recommended Agent Profile**:
  - Category: `deep` - Reason: balancing requires iterative evidence.
  - Skills: `performance-optimizer` - Use measure-before/after discipline.
  - Omitted: [`frontend-ui-ux`] - Data tuning only.

  **Parallelization**: Can Parallel: NO | Wave 6 | Blocks: 12 | Blocked By: 7, 9, 10

  **References**:
  - Pattern: `src/config.js:27-95` - Producer numbers.
  - Pattern: `src/config.js:99-123` - Upgrade numbers.
  - Pattern: `src/config.js:125-175` - Mission rewards to tune or supersede.
  - Pattern: `src/progression/content.js` - New quest rewards to tune after Task 1 creates the file.
  - External: `docs/advanced_roadmap.md:127-137` - Target curve.

  **Acceptance Criteria**:
  - [ ] `node scripts/balance-report.mjs --scenario chapter1-active` exits `0` with all targets passing.
  - [ ] Report proves Chapter 1 does not softlock if user skips capsule for first 5 minutes.
  - [ ] Report proves 4-hour offline cap does not produce `Infinity`, `NaN`, or producer-spawner explosion.
  - [ ] `npm test -- tests/economy.test.js tests/balanceSimulation.test.js` passes.

  **QA Scenarios**:
  ```
  Scenario: Target curve passes after tuning
    Tool: Bash
    Steps: Run `node scripts/balance-report.mjs --scenario chapter1-active`
    Expected: all target IDs pass and `failedTargets` is empty
    Evidence: .omo/evidence/task-11-balanced-curve.json

  Scenario: Offline cap stays finite
    Tool: Bash
    Steps: Run `npm test -- tests/balanceSimulation.test.js -t "offline cap remains finite"`
    Expected: no NaN/Infinity and producer counts remain below configured safety cap
    Evidence: .omo/evidence/task-11-offline-finite.txt
  ```

  **Commit**: NO | Reason: current project is not a git repository. If a repo is initialized later, use message `balance(progression): tune chapter one loop` for files `src/config.js`, `src/progression/content.js`, balance tests/reports as needed.

- [ ] 12. Update player-facing guide and developer notes

  **What to do**: Document the new progression model after implementation stabilizes.
  - Update `docs/guide.md` to explain Chapter 1, active quests, next unlock guidance, and the end-of-content state.
  - Add a short developer note section listing validation commands: `npm test`, `npm run build`, `node scripts/validate-progression-content.mjs`, `node scripts/balance-report.mjs --scenario chapter1-active`.
  - If `docs/advanced_roadmap.md` remains aspirational, add a note that Chapter 1 implemented subset is now validated by scripts.
  - Ensure Korean terminology matches `src/i18n.js` keys added during UI work.

  **Must NOT do**: Do not document unimplemented prestige, backend, real ads, multiplayer, or future chapters as live features.

  **Recommended Agent Profile**:
  - Category: `writing` - Reason: documentation and terminology consistency.
  - Skills: `[]` - No specialist needed.
  - Omitted: [`api-endpoint-builder`] - No API docs.

  **Parallelization**: Can Parallel: NO | Wave 7 | Blocks: Final Verification | Blocked By: 10, 11

  **References**:
  - Pattern: `docs/guide.md:14-23` - Existing basic goal section.
  - Pattern: `docs/guide.md:57-68` - Existing mission explanation.
  - Pattern: `docs/guide.md:124-131` - Existing verification command section.

  **Acceptance Criteria**:
  - [ ] `grep -n "1장: 업무 산호초 창업" docs/guide.md` finds the Chapter 1 section.
  - [ ] `grep -n "validate-progression-content" docs/guide.md` finds the validation command.
  - [ ] `npm test` and `npm run build` pass after docs updates.
  - [ ] Docs do not claim prestige/backend/real ads are implemented.

  **QA Scenarios**:
  ```
  Scenario: Guide documents implemented progression only
    Tool: Bash
    Steps: Search docs for Chapter 1 and validation commands
    Expected: implemented features documented; unimplemented prestige/backend not described as live
    Evidence: .omo/evidence/task-12-guide-check.txt

  Scenario: Docs change does not break build/test
    Tool: Bash
    Steps: Run `npm test && npm run build`
    Expected: both commands exit 0
    Evidence: .omo/evidence/task-12-docs-verification.txt
  ```

  **Commit**: NO | Reason: current project is not a git repository. If a repo is initialized later, use message `docs: document chapter progression loop` for files `docs/guide.md`, optionally `docs/advanced_roadmap.md`.

## Final Verification Wave (MANDATORY — after ALL implementation tasks)
> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.
> **Do NOT auto-proceed after verification. Wait for user's explicit approval before marking work complete.**
> **Never mark F1-F4 as checked before getting user's okay.** Rejection or user feedback -> fix -> re-run -> present again -> wait for okay.
- [ ] F1. Plan Compliance Audit — oracle
- [ ] F2. Code Quality Review — unspecified-high
- [ ] F3. Real Manual QA — unspecified-high (+ playwright)
- [ ] F4. Scope Fidelity Check — deep

## Change Tracking Strategy
- This project directory is currently not a git repository, so tasks must not attempt `git commit` by default.
- If the user later initializes or moves this project into a git repository, use each task's suggested commit message and file list.
- Never include `node_modules`, `dist`, screenshots, `.omo/evidence`, or temporary server logs in commits unless explicitly requested.

## Success Criteria
- The game has a visible Chapter 1 progression loop instead of only flat accumulation.
- Players always see active goals and next unlock guidance.
- Quest state supports locked/active/completed/claimed and avoids duplicate rewards.
- Old saves load safely and preserve existing progress.
- Balance is proven by repeatable simulation, not by eyeballing.
- The implementation remains vanilla JS + Vite + Vitest.
