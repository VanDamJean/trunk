## 2026-06-03 Task: task-3-visual-system

- Added reusable original inline SVG/CSS art exports in `src/components/GameArt.tsx`: `RoundAnimalHero`, `ForestMonster`, `LayeredArena`, `CoinBadge`, `EquipmentBadge`, `RedDotMarker`, `DamagePop`, `RewardBurst`, and `ArtStack`.
- Re-exported art from `src/components/ui.tsx` and added `ArtCard` plus `ChunkyBadge` while preserving existing `Shell`, `Card`, `PrimaryButton`, `StatPill`, `ProgressBar`, `Dice`, and `RewardBadge` exports.
- Expanded `src/styles.css` into a tokenized casual RPG foundation: saturated gradients, thick outlines, chunky card/button shadows, 375x812-friendly shell sizing, one-hand mobile fallback, button/nav/dice focus states, art sizing classes, arena layers, badge glyph styling, red-dot markers, damage pops, and reward burst animation.
- Minimal screen compatibility updates replaced emoji placeholders with shared art components in Home, Combat, Equipment, and Result without changing combat/game mechanics or remaking screen hierarchy.
- Verification evidence written: `.omo/evidence/habby-yahtzee-task-3-build.txt`, `.omo/evidence/habby-yahtzee-task-3-no-external-assets.txt`, and `.omo/evidence/habby-yahtzee-task-3-manual-qa.txt`.

## 2026-06-03 Task: task-1-category-mechanics-general-agent

- Added selectable Yahtzee attack category APIs alongside existing best-hand APIs so current UI/tests can keep using `YahtzeeHand`, `HANDS`, `evaluateHand`, and `calculateDiceDamage`.
- `getValidAttackCategories` and `isAttackCategoryValid` intentionally require exactly 5 dice; incomplete rolls return no valid categories, including `chance`.
- Selected category damage uses the chosen category multiplier, so the same four-kind dice can resolve as `pair` at x1.2 or `fourKind` at x3.2.
- Verification evidence written: `.omo/evidence/habby-yahtzee-task-1-category-tests.txt` and `.omo/evidence/habby-yahtzee-task-1-damage-tests.txt`.

## 2026-06-03 Task: task-2-combat-category-selection-general

- Replaced CombatScreen's auto-best dice resolution with direct `ATTACK_CATEGORIES` selection under `Choose Attack Hand`.
- Category buttons stay visible before rolling but are disabled with `Roll first`; after a complete roll, valid categories from `getValidAttackCategories` are enabled and invalid categories show `Not matched`.
- Selected attacks call `calculateSelectedCategoryDamage(equipment, category)`, so fixtures like `[2,2,2,2,5]` can resolve as `Pair x1.2` instead of forced four-kind damage.
- Resolving a category closes the dice panel, clears dice/holds/rolls, consumes dice charge, records the selected category for combat completion, and resumes auto-battle.
- Replaced `Force Yahtzee` with DEV `Force Pair Fixture` and `Force Full House Fixture` buttons that open the panel with rolls set to 1 and holds cleared.
- Verification evidence written: `.omo/evidence/habby-yahtzee-task-2-build.txt` and `.omo/evidence/habby-yahtzee-task-2-selected-flow.txt`.

## 2026-06-04 Task: task-5-selected-category-tests

- Strengthened `src/game/yahtzee.test.ts` with deterministic selected-category validity fixtures for `[2,2,2,2,5]`, `[2,2,2,3,3]`, `[1,2,3,4,5]`, and incomplete dice.
- Expanded `src/game/combat.test.ts` selected damage coverage so pair damage on four-kind dice stays at the selected pair multiplier and does not collapse to four-kind damage.
- Added `src/App.test.tsx` behavior coverage for pre-roll disabled categories, invalid disabled categories after `Force Pair Fixture`, selected pair attack messaging/reset, category reuse, all-held reroll preservation, third-roll lockout, and absence of the old `Resolve Attack` flow.
- Verification passed with `npm test -- --run`: 6 files and 38 tests passed.
- Verification evidence written: `.omo/evidence/habby-yahtzee-task-5-tests.txt` and `.omo/evidence/habby-yahtzee-task-5-no-old-flow.txt`.


## 2026-06-04 Task: task-4-screen-hierarchy

- Remade Home around a compact RPG hub: hero arena, stage route, power number, one primary `Start Combat` CTA, secondary Equipment/Upgrade actions, and a computed red-dot upgrade prompt via `canUpgrade`.
- Remade Combat around the existing selected-category flow with `LayeredArena`, hero/enemy staging, damage feedback, compact HP bars, and the preserved `Choose Attack Hand` board plus DEV fixture labels.
- Remade Equipment and Upgrade with Task 3 item badges, chunky status badges, selectable item cards, and stronger upgrade readiness affordances without changing equipment helpers.
- Remade Result with `RewardBurst`, large coins-earned reward panel, and selected attack wording instead of `Best hand`.
- Added only token-based CSS composition classes on top of the existing Task 3 visual system; no new routes, dependencies, assets, product systems, or monetization text were added.


## 2026-06-04 Task: task-4-scope-fix

- Removed the unrequested `tsconfig.app.json` test exclusion so `npm run build` type-checks the included `src` test files again.
- Fixed the exposed TypeScript issue in `src/game/yahtzee.test.ts` by typing the selected-category fixture cases as `YahtzeeAttackCategory[]`, preserving Task 5 behavior tests.
- Copied Task 4 browser screenshots to the expected evidence names: `.omo/evidence/habby-yahtzee-task-4-hub.png` and `.omo/evidence/habby-yahtzee-task-4-result.png`.

## 2026-06-04 Task: task-6-qa-docs-general

- Updated `scripts/qa.mjs` to prove the direct selected-category combat path with `Force Pair Fixture`, disabled `Full House attack x2.5 Not matched`, selected `Pair attack x1.2`, and selected `Full House attack x2.5` from `Force Full House Fixture`.
- Kept browser QA coverage for result claim, coin grant, Twig Wand upgrade, reload persistence, and reset while adding fatal console/page error gating.
- Updated `docs/README.ko.md` to describe roll/hold/reroll, enabled category selection, selected-category damage, reusable categories, and original CSS/SVG visuals.

## 2026-06-04 Task: task-7-full-regression

- Ran final regression: `npm test -- --run` passed with 6 files and 38 tests, `npm run build` passed, and `npm run qa` passed against a clean `npm run dev -- --host 127.0.0.1` server on port 5173.
- Stopped a stale workspace Vite process that was occupying port 5173 before the clean QA rerun; confirmed no process remained listening on 5173 afterward.
- Verified old-flow searches: no runtime `Habby`/`Capybara Go`, no runtime external image refs/assets in `src`, no runtime `Best hand`, and no runtime `Resolve Attack` path.
- Documented that `Resolve Attack` appears only in negative absence assertions in `src/App.test.tsx`, not as a success path.
- Confirmed Task 1-7 evidence files exist and wrote `.omo/evidence/habby-yahtzee-task-7-full-regression.txt` plus `.omo/evidence/habby-yahtzee-task-7-old-flow-removed.txt`.
