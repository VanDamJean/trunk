# F4 Browser QA Evidence

Checkbox item: `- [ ] F4. Real Browser QA — unspecified-high (+ Playwright)`

## Commands

- Read `scripts/qa.mjs` and prior Task 6 / Task 7 evidence.
- Grep confirmed `scripts/qa.mjs` contains no `Resolve Attack` path.
- Ran `npm run dev -- --host 127.0.0.1` with a cleanup trap.
- Ran `npm run qa` against `http://127.0.0.1:5173`.
- `npm run qa` exited 0.
- Ran `lsof -nP -iTCP:5173 -sTCP:LISTEN || true` after cleanup; output was empty.

## Flow Proof

- `scripts/qa.mjs` line 25 clicks `Force Pair Fixture`.
- `scripts/qa.mjs` line 27 locates `Pair attack x1.2` by role/name.
- `scripts/qa.mjs` line 28 locates `Full House attack x2.5 Not matched` by role/name.
- `scripts/qa.mjs` line 31 asserts `Pair attack x1.2` is enabled for the pair fixture.
- `scripts/qa.mjs` line 32 asserts `Full House attack x2.5 Not matched` is disabled for the pair fixture.
- `scripts/qa.mjs` line 34 clicks `Pair attack x1.2`.
- `scripts/qa.mjs` line 35 waits for `Pair attack x1.2 dealt`.
- `scripts/qa.mjs` line 37 clicks `Force Full House Fixture`.
- `scripts/qa.mjs` line 38 locates `Full House attack x2.5` by role/name.
- `scripts/qa.mjs` line 40 asserts `Full House attack x2.5` is enabled for the full-house fixture.
- `scripts/qa.mjs` line 42 clicks `Full House attack x2.5`.
- `scripts/qa.mjs` line 43 waits for `Full House attack x2.5 dealt`.
- `scripts/qa.mjs` lines 46-62 cover result, reward claim, coin grant, Twig Wand upgrade, reload persistence, and reset.
- Grep found no `Resolve Attack` match in `scripts/qa.mjs`; QA does not pass through the old auto-best-only path.

Generated screenshot evidence from the successful QA run:

- `.omo/evidence/habby-yahtzee-task-6-pair-category.png`
- `.omo/evidence/habby-yahtzee-task-6-full-house-category.png`
- `.omo/evidence/task-7-dice-resolve.png`
- `.omo/evidence/task-8-upgrade-success.png`
- `.omo/evidence/task-9-persistence.png`
- `.omo/evidence/f3-manual-qa.png`

## Console Summary

From regenerated `.omo/evidence/habby-yahtzee-task-6-browser-qa.md`:

- Console lines: 6
- Page errors: 0
- Fatal console errors: 0
- Console lines were Vite connect/disconnect debug messages and React DevTools info messages.
- `scripts/qa.mjs` lines 69 and 103-105 gate fatal console errors and page errors before allowing success.

## Server Cleanup

- The dev server was started by this review with `npm run dev -- --host 127.0.0.1`.
- The shell wrapper installed a cleanup trap and stopped the dev server after QA.
- Explicit post-run check `lsof -nP -iTCP:5173 -sTCP:LISTEN || true` produced no output, confirming no listener remained on port 5173.

## Verdict

Browser QA proves direct category selection, disabled invalid Full House on the pair fixture, selected Pair, selected Full House, result claim, coin grant, Twig Wand upgrade, reload persistence, and reset. It does not use the old `Resolve Attack` flow, and the run had no page errors or fatal console errors.

VERDICT: APPROVE
