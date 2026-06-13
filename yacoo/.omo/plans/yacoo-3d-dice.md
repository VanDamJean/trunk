# Yacoo 3D Six-Sided Dice UI

## TL;DR

> **Quick Summary**: Replace the current flat numeric dice cards with five visible CSS 3D six-sided dice while preserving all Yacoo game logic, hold behavior, keyboard access, and tests.
>
> **Deliverables**:
> - Five cube-style dice in `src/components/DiceRow.tsx`, each visually showing pips/faces for values 1-6.
> - CSS 3D dice layout and thrown/rolling animation in `src/styles.css`.
> - Automated tests/e2e assertions proving five dice, pips, hold behavior, keyboard access, mobile layout, and build health.
>
> **Estimated Effort**: Short
> **Parallel Execution**: YES - 2 waves + final review
> **Critical Path**: 1 + 2 + 3 → 4 → F1-F4

---

## Context

### Original Request
User clarified the web Yacoo game should show five standard six-sided dice being thrown: "6면체 5개지".

### Interview Summary
**Key Discussions**:
- User wants standard six-sided dice, not a hexagon and not flat number-only cards.
- Browser/web implementation is possible; CSS 3D is sufficient and avoids dependency bloat.
- Existing `.omo/plans/dice-roll-animation.md` completed bounce/number-toggle animation but did not create visible cube dice.

**Research Findings**:
- `README.md` states rules use five six-sided dice.
- `src/game/dice.ts` already rolls exactly five `1 | 2 | 3 | 4 | 5 | 6` values.
- `src/components/DiceRow.tsx` owns dice rendering, hold toggles, rolling display values, and ARIA labels.
- `src/styles.css` owns `.dice-row`, `.die-button`, `.die-value`, `.die-state`, and `@keyframes dice-bounce`.
- Test infrastructure exists: Vitest, Testing Library, Playwright e2e, `pnpm test`, `pnpm build`, `pnpm e2e`.

### Metis Review
**Identified Gaps** (addressed):
- Held dice behavior locked: held dice stay static and visibly marked while unheld dice animate.
- Final value convention locked: visible/resting die must match game-state value `1-6`.
- Reduced-motion requirement added via `prefers-reduced-motion`.
- Scope creep locked out: no sound, physics engine, particles, full board redesign, dice themes, backend, persistence, or game logic changes.

---

## Work Objectives

### Core Objective
Make the Yacoo dice look and behave like five thrown standard six-sided dice in the browser, without changing game rules or state semantics.

### Concrete Deliverables
- `src/components/DiceRow.tsx`: cube/pip dice markup for five dice, preserving props and ARIA contract.
- `src/styles.css`: CSS 3D cube/pip styling, roll/tumble animation, held styling, reduced-motion fallback, mobile-safe layout.
- `src/__tests__/App.test.tsx` and/or `tests/e2e/yachoo.spec.ts`: tests proving dice UI remains usable and accessible.
- `.omo/evidence/`: agent-captured command outputs/screenshots from QA.

### Definition of Done
- [ ] `pnpm test -- --run` passes.
- [ ] `pnpm build` passes.
- [ ] `pnpm exec playwright test` passes or evidence records any environment/browser-install blocker.
- [ ] Browser QA shows exactly five dice, each with pip-based cube visuals.
- [ ] Held dice stay static during reroll; unheld dice animate.

### Must Have
- Five dice always rendered in the dice row.
- Each die visually represents standard six-sided dice values using pips/faces, not only a bare number.
- Roll animation looks like dice being thrown/tumbled.
- Final visible value matches actual game state value.
- Existing game logic, scoring, bot behavior, RNG semantics, and turn flow preserved.
- Keyboard and screen-reader accessibility preserved.
- Mobile 375px width has no horizontal overflow.

### Must NOT Have (Guardrails)
- No dependency additions.
- No Canvas, WebGL, Three.js, or physics engine.
- No changes to `src/game/dice.ts`, scoring, bot strategy, category rules, or game state model unless a test exposes a true bug unrelated to visuals.
- No sound effects, particle effects, dice skins/themes, settings panel, backend, persistence, or whole-board redesign.
- No `as any`, `@ts-ignore`, deleted tests, or suppressed type errors.
- No manual-only acceptance criteria.

---

## Verification Strategy (MANDATORY)

> **ZERO HUMAN INTERVENTION** - ALL verification is agent-executed.

### Test Decision
- **Infrastructure exists**: YES
- **Automated tests**: Tests-after
- **Framework**: Vitest + Testing Library + Playwright
- **TDD**: Not required; implementation is visual replacement with existing coverage.

### QA Policy
Every task must include agent-executed QA. Evidence saved to `.omo/evidence/task-{N}-{scenario-slug}.{ext}`.

- **Frontend/UI**: Use Playwright for rendering, interaction, screenshots, mobile viewport, and reduced-motion checks.
- **Library/Module**: Use `pnpm test -- --run` for unit/component checks.
- **Build**: Use `pnpm build` for TypeScript + Vite production validation.

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Start immediately - parallel UI/test concerns):
├── Task 1: DiceRow cube markup and accessibility contract [visual-engineering]
├── Task 2: CSS 3D dice faces, pips, roll animation, reduced motion [visual-engineering]
└── Task 3: Test/e2e assertions for five cube dice and hold behavior [quick]

Wave 2 (After Wave 1 - integration validation):
└── Task 4: Full validation pass and evidence capture [unspecified-high]

Wave FINAL (After ALL tasks — 4 parallel reviews, then user okay):
├── F1: Plan compliance audit (oracle)
├── F2: Code quality review (unspecified-high)
├── F3: Real manual QA via agent-executed browser scenarios (unspecified-high)
└── F4: Scope fidelity check (deep)
```

### Dependency Matrix

- **1**: depends None; blocks 3, 4
- **2**: depends None; blocks 3, 4
- **3**: depends 1, 2 for final pass but can draft assertions in parallel; blocks 4
- **4**: depends 1, 2, 3; blocks F1-F4
- **F1-F4**: depend 4

### Agent Dispatch Summary

- **Wave 1**: **3** - 1 → `visual-engineering`, 2 → `visual-engineering`, 3 → `quick`
- **Wave 2**: **1** - 4 → `unspecified-high`
- **FINAL**: **4** - F1 → `oracle`, F2 → `unspecified-high`, F3 → `unspecified-high`, F4 → `deep`

---

## TODOs

- [ ] 1. DiceRow cube markup and accessibility contract

  **What to do**:
  - Update `src/components/DiceRow.tsx` so each of the five dice buttons contains cube/pip markup instead of only `.die-value` text.
  - Preserve `DiceRowProps`: `dice`, `held`, `canToggle`, `isRolling`, `onToggle`.
  - Preserve five rendered buttons and existing ARIA label pattern such as `1번 주사위 1` so e2e tests and screen readers remain meaningful.
  - Add stable DOM hooks/classes for pip rendering, e.g. `die-cube`, `die-face`, `die-pip`, `data-value`, `data-rolling`, `data-held`.
  - Keep final visible value synchronized with `displayValues`/`realValues` so animation never changes game state.

  **Must NOT do**:
  - Do not touch scoring, bot, turn state, dice RNG, or category logic.
  - Do not remove button semantics or keyboard operability.

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: DOM structure, UI rendering, accessibility, and visual behavior.
  - **Skills**: []
  - **Skills Evaluated but Omitted**:
    - `rayden-code`: not using Rayden UI components.

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 with Tasks 2, 3
  - **Blocks**: 3, 4
  - **Blocked By**: None

  **References**:
  - `src/components/DiceRow.tsx:20-87` - Current component contract, roll display behavior, held handling, ARIA labels.
  - `src/game/categories.ts:17-18` - `DieValue` and `Dice` tuple types proving values are `1-6` and exactly five dice.
  - `tests/e2e/yachoo.spec.ts:23-32` - Existing roll/hold e2e flow relying on button labels.

  **Acceptance Criteria**:
  - [ ] Exactly five dice buttons render before and after roll.
  - [ ] Each rendered die has pip/cube DOM; no die is number-only visual content.
  - [ ] ARIA labels remain value-specific (`N번 주사위 V`).
  - [ ] Clicking a die after first roll toggles `aria-pressed` and held state.

  **QA Scenarios (MANDATORY)**:
  ```
  Scenario: Five cube dice render after deterministic roll
    Tool: Playwright
    Preconditions: Inject window.__YACOO_TEST_ROLLS__ = [[1,2,3,4,5]] before page load.
    Steps:
      1. Navigate to `/`.
      2. Click button role/name `굴리기`.
      3. Assert five buttons matching `/\d번 주사위 [1-6]/` exist.
      4. Assert each button contains `.die-cube` and at least one `.die-pip`.
    Expected Result: Five accessible cube dice appear with pips and matching labels.
    Failure Indicators: Fewer/more dice, missing pips, missing labels, numeric-only dice.
    Evidence: .omo/evidence/task-1-five-cube-dice.txt

  Scenario: Held toggle remains accessible
    Tool: Playwright
    Preconditions: Inject window.__YACOO_TEST_ROLLS__ = [[1,2,3,4,5]].
    Steps:
      1. Navigate to `/` and click `굴리기`.
      2. Click button `1번 주사위 1`.
      3. Assert button `1번 주사위 1` has `aria-pressed="true"` and `data-held="true"`.
    Expected Result: First die is held and remains a keyboard/clickable button.
    Evidence: .omo/evidence/task-1-held-toggle.txt
  ```

  **Evidence to Capture**:
  - [ ] `.omo/evidence/task-1-five-cube-dice.txt`
  - [ ] `.omo/evidence/task-1-held-toggle.txt`

  **Commit**: NO (group with Task 4)

- [ ] 2. CSS 3D dice faces, pips, roll animation, reduced motion

  **What to do**:
  - Update `src/styles.css` to style `.die-button` as a dice container with perspective-safe sizing.
  - Add cube/faces/pips styling so values 1-6 look like standard dice.
  - Replace or extend `dice-bounce` with a more dice-like tumble/throw animation for unheld dice only.
  - Add `@media (prefers-reduced-motion: reduce)` to disable or simplify animation.
  - Preserve mobile layout at 375px width without horizontal overflow.

  **Must NOT do**:
  - Do not add external assets, icon fonts, images, or dependencies.
  - Do not make dice too small to use on mobile.

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: CSS 3D, animation, responsive UI.
  - **Skills**: []
  - **Skills Evaluated but Omitted**:
    - `performance-optimizer`: CSS-only animation is small; no performance investigation needed beyond transform-only animation.

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 with Tasks 1, 3
  - **Blocks**: 3, 4
  - **Blocked By**: None

  **References**:
  - `src/styles.css:204-260` - Current dice row/card/bounce styles to replace or extend.
  - `src/styles.css:344-367` - Existing mobile constraints for dice size and score layout.
  - `README.md:Rules` - Dice are five standard six-sided dice.

  **Acceptance Criteria**:
  - [ ] Unheld dice with `data-rolling="true"` animate using transform-based CSS.
  - [ ] Held dice with `data-held="true"` are visibly distinct and do not tumble.
  - [ ] `prefers-reduced-motion: reduce` disables/simplifies roll animation.
  - [ ] At viewport `375x812`, `document.documentElement.scrollWidth <= window.innerWidth`.

  **QA Scenarios (MANDATORY)**:
  ```
  Scenario: Unheld dice animate on roll
    Tool: Playwright
    Preconditions: Inject deterministic rolls [[1,2,3,4,5],[6,6,6,6,6]].
    Steps:
      1. Navigate to `/`.
      2. Click `굴리기`.
      3. Immediately inspect first unheld die computed animation-name is not `none` OR `data-rolling="true"` during roll window.
      4. Wait 800ms and assert final label `1번 주사위 1` is present.
    Expected Result: Unheld dice enter rolling state then settle to deterministic values.
    Evidence: .omo/evidence/task-2-roll-animation.txt

  Scenario: Mobile has no horizontal overflow
    Tool: Playwright
    Preconditions: Viewport 375x812.
    Steps:
      1. Navigate to `/`.
      2. Click `굴리기`.
      3. Assert `document.documentElement.scrollWidth <= window.innerWidth`.
      4. Capture screenshot of dice row.
    Expected Result: Five dice fit in mobile layout without horizontal scrolling.
    Evidence: .omo/evidence/task-2-mobile-dice.png
  ```

  **Evidence to Capture**:
  - [ ] `.omo/evidence/task-2-roll-animation.txt`
  - [ ] `.omo/evidence/task-2-mobile-dice.png`

  **Commit**: NO (group with Task 4)

- [ ] 3. Test/e2e assertions for five cube dice and hold behavior

  **What to do**:
  - Update `src/__tests__/App.test.tsx` and/or `tests/e2e/yachoo.spec.ts` to assert the new dice DOM contract where useful.
  - Keep existing gameplay e2e assertions intact.
  - Add tests for five dice, deterministic labels, hold toggle, and mobile no-overflow if not already covered.
  - Avoid brittle pixel-perfect assertions; prefer role/label/data/pip-count checks.

  **Must NOT do**:
  - Do not delete tests to make suite pass.
  - Do not assert implementation details beyond stable dice UI hooks needed for regression safety.

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Focused test edits around existing test files.
  - **Skills**: []
  - **Skills Evaluated but Omitted**:
    - `bug-hunter`: no defect trace needed; this is planned regression coverage.

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 with Tasks 1, 2
  - **Blocks**: 4
  - **Blocked By**: 1 and 2 for final pass; can draft based on specified hooks.

  **References**:
  - `src/__tests__/App.test.tsx:1-10` - Current minimal render test.
  - `tests/e2e/yachoo.spec.ts:9-33` - Human roll/hold/score flow.
  - `tests/e2e/yachoo.spec.ts:46-57` - Mobile layout no-overflow assertion.

  **Acceptance Criteria**:
  - [ ] Automated tests verify five dice are rendered after roll.
  - [ ] Tests still verify held dice toggle with `aria-pressed`.
  - [ ] Existing gameplay flow remains covered.
  - [ ] Tests can run with deterministic `window.__YACOO_TEST_ROLLS__`.

  **QA Scenarios (MANDATORY)**:
  ```
  Scenario: Unit/e2e regression tests pass
    Tool: Bash
    Preconditions: Dependencies installed in `/Users/a1/Desktop/manus/yacoo`.
    Steps:
      1. Run `pnpm test -- --run`.
      2. Save stdout/stderr.
    Expected Result: Vitest exits 0 with all tests passing.
    Evidence: .omo/evidence/task-3-vitest.txt

  Scenario: E2E dice flow remains stable
    Tool: Bash / Playwright
    Preconditions: Chromium installed or install command available.
    Steps:
      1. Run `pnpm exec playwright test tests/e2e/yachoo.spec.ts`.
      2. Save stdout/stderr; if browser missing, record exact install blocker.
    Expected Result: Playwright exits 0 or blocker is explicit environment setup only.
    Evidence: .omo/evidence/task-3-e2e.txt
  ```

  **Evidence to Capture**:
  - [ ] `.omo/evidence/task-3-vitest.txt`
  - [ ] `.omo/evidence/task-3-e2e.txt`

  **Commit**: NO (group with Task 4)

- [ ] 4. Full validation pass and evidence capture

  **What to do**:
  - Run final TypeScript/build/test validation after Tasks 1-3.
  - Inspect changed files for guardrail violations: dependencies, game logic changes, deleted tests, `as any`, `@ts-ignore`, overbroad redesign.
  - Capture browser evidence for happy path, held reroll behavior, reduced motion, and mobile.
  - If failures occur, fix within allowed files and rerun relevant checks.

  **Must NOT do**:
  - Do not proceed with failing build/tests unless evidence shows external environment blocker.
  - Do not broaden scope to unrelated UI polish.

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Integration validation across component, CSS, tests, and e2e.
  - **Skills**: []
  - **Skills Evaluated but Omitted**:
    - `codebase-audit-pre-push`: full pre-push audit is too broad for this small UI change.

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 2 sequential integration
  - **Blocks**: F1-F4
  - **Blocked By**: 1, 2, 3

  **References**:
  - `package.json:6-12` - Validation scripts.
  - `README.md:검증` - Project verification commands.
  - `src/components/DiceRow.tsx` and `src/styles.css` - Primary changed files.

  **Acceptance Criteria**:
  - [ ] `pnpm test -- --run` passes.
  - [ ] `pnpm build` passes.
  - [ ] `pnpm exec playwright test` passes or browser-install blocker is documented.
  - [ ] Evidence files exist for all task scenarios.

  **QA Scenarios (MANDATORY)**:
  ```
  Scenario: Full command verification
    Tool: Bash
    Preconditions: Worktree has Tasks 1-3 complete.
    Steps:
      1. Run `pnpm test -- --run`.
      2. Run `pnpm build`.
      3. Run `pnpm exec playwright test`.
    Expected Result: Commands pass, or only Playwright browser-install blocker is recorded with exact message.
    Evidence: .omo/evidence/task-4-full-validation.txt

  Scenario: Guardrail scan
    Tool: Bash/Grep
    Preconditions: Worktree has Tasks 1-3 complete.
    Steps:
      1. Search changed files for `as any`, `@ts-ignore`, deleted test references, added dependencies, and changes outside allowed scope.
      2. Record git diff summary or file list.
    Expected Result: No guardrail violations.
    Evidence: .omo/evidence/task-4-guardrail-scan.txt
  ```

  **Evidence to Capture**:
  - [ ] `.omo/evidence/task-4-full-validation.txt`
  - [ ] `.omo/evidence/task-4-guardrail-scan.txt`

  **Commit**: YES
  - Message: `feat(dice): render animated 3d dice`
  - Files: `src/components/DiceRow.tsx`, `src/styles.css`, optional test files
  - Pre-commit: `pnpm test -- --run && pnpm build`

---

## Final Verification Wave (MANDATORY — after ALL implementation tasks)

> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.

- [ ] F1. **Plan Compliance Audit** — `oracle`
  Read this plan end-to-end. Verify every Must Have with file reads, test output, and browser/evidence files. Verify every Must NOT Have by searching changed files and `package.json`. Output: `Must Have [N/N] | Must NOT Have [N/N] | Tasks [N/N] | VERDICT: APPROVE/REJECT`.

- [ ] F2. **Code Quality Review** — `unspecified-high`
  Run `pnpm build` and `pnpm test -- --run`. Review changed files for `as any`, `@ts-ignore`, unused imports, over-abstraction, brittle CSS, deleted tests, and accessibility regressions. Output: `Build [PASS/FAIL] | Tests [N pass/N fail] | Files [N clean/N issues] | VERDICT`.

- [ ] F3. **Real Manual QA** — `unspecified-high`
  Use Playwright to execute every QA scenario from Tasks 1-4, including deterministic roll, held reroll, keyboard toggle, reduced motion, and mobile `375x812`. Save screenshots/logs to `.omo/evidence/final-qa/`. Output: `Scenarios [N/N pass] | Integration [N/N] | Edge Cases [N tested] | VERDICT`.

- [ ] F4. **Scope Fidelity Check** — `deep`
  Compare actual diff against this plan. Confirm only allowed files/concerns changed, no game logic/scoring/bot/dependency changes, and no unrelated redesign. Output: `Tasks [N/N compliant] | Contamination [CLEAN/N issues] | Unaccounted [CLEAN/N files] | VERDICT`.

---

## Commit Strategy

- **1**: `feat(dice): render animated 3d dice` - `src/components/DiceRow.tsx`, `src/styles.css`, optional tests - pre-commit `pnpm test -- --run && pnpm build`

---

## Success Criteria

### Verification Commands
```bash
pnpm test -- --run      # Expected: all Vitest tests pass
pnpm build              # Expected: TypeScript build and Vite build pass
pnpm exec playwright test # Expected: all e2e tests pass, unless browser install blocker documented
```

### Final Checklist
- [ ] Five standard six-sided dice appear as cube/pip dice.
- [ ] Rolling unheld dice tumble/throw visually.
- [ ] Held dice stay static and visually held.
- [ ] Final visual values match game state.
- [ ] Keyboard and screen-reader affordances preserved.
- [ ] Mobile has no horizontal overflow.
- [ ] No forbidden scope creep or dependency changes.
