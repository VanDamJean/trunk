# F2 Yahtzee Mechanics Review

Checkbox item reviewed: `- [ ] F2. Yahtzee Mechanics Review — unspecified-high`

## Source Review

- Required files read: `src/game/types.ts`, `src/game/constants.ts`, `src/game/yahtzee.ts`, `src/game/combat.ts`, `src/screens/CombatScreen.tsx`.
- Frozen categories are exactly 9 entries in `ATTACK_CATEGORIES`, in order: `chance`, `pair`, `twoPair`, `threeKind`, `smallStraight`, `fullHouse`, `fourKind`, `largeStraight`, `yahtzee`.
- Frozen labels and multipliers match the plan exactly: `Chance x1.0`, `Pair x1.2`, `Two Pair x1.5`, `Three of a Kind x1.8`, `Small Straight x2.1`, `Full House x2.5`, `Four of a Kind x3.2`, `Large Straight x3.8`, `Yahtzee x6.0`.
- `isAttackCategoryValid` requires exactly `COMBAT_TIMING.diceCount` dice, so incomplete or pre-roll dice enable no category, including `chance`.
- Validity mapping is selected-category based, not highest-hand based. `[2,2,2,2,5]` enables `chance`, `pair`, `threeKind`, and `fourKind`; selecting `pair` remains valid without forcing `fourKind` damage.
- `[2,2,2,3,3]` enables `chance`, `pair`, `twoPair`, `threeKind`, and `fullHouse`, with `fourKind`, straights, and `yahtzee` invalid.
- `chance` is always valid after a complete first roll because `isAttackCategoryValid(..., 'chance')` returns true only after the five-dice length guard passes.
- `CombatScreen` computes `hasCompleteRolledDice` as `dice.length === 5 && rolls > 0`; category buttons are disabled with `Roll first` before the first roll.
- Invalid categories are disabled in the UI through `disabled={Boolean(disabledReason)}` and guarded in `resolveCategory` by `if (!hasCompleteRolledDice || !validCategories.includes(category)) return`.
- Runtime combat damage uses the clicked category: `resolveCategory(category)` calls `calculateSelectedCategoryDamage(equipment, category)`, which looks up `ATTACK_CATEGORIES` metadata and applies that multiplier.
- Compatibility `evaluateHand` and `calculateDiceDamage` exports remain, but grep found no runtime `CombatScreen` attack path using `evaluateHand` or `calculateDiceDamage`; selected category damage drives runtime combat.
- Dice state resets on selected attack and cancel through `closeDice`, which clears `diceReady`, closes the panel, empties dice, resets holds to five false values, resets rolls to `0`, and updates the message.
- Categories are reusable because there is no scorecard or used-category state; after a selected attack `closeDice` resets transient dice state only, and tests reopen the same fixture and re-enable `pair`.
- Roll/hold behavior: `rollDice(previous, held)` preserves held dice when `held[index]` and a previous die exist; unheld dice roll through `rollDie`.
- Max rolls behavior: `rollCurrentDice` returns when `rolls >= COMBAT_TIMING.maxRolls`, and the `Roll Dice` button is disabled at max rolls.
- All-held reroll behavior is safe: if all dice are held and a reroll is allowed, `rollDice` returns the previous five values unchanged.

## Test Review

- Required test files read: `src/game/yahtzee.test.ts`, `src/game/combat.test.ts`, `src/App.test.tsx`.
- `yahtzee.test.ts` covers frozen category order/multipliers, incomplete dice disabling all categories, selected-category validity for `[2,2,2,2,5]`, `[2,2,2,3,3]`, and straight fixtures, plus held-dice reroll preservation.
- `combat.test.ts` covers selected-category damage from multipliers, including `[2,2,2,2,5]` where `pair` damage is `16` and `fourKind` damage is `43`, proving selected pair does not inherit four-kind multiplier.
- `App.test.tsx` covers no category enabled before first roll, invalid category disabled after pair fixture, selected pair damage messaging, dice panel reset after selected attack, category reuse after reopening, all-held reroll preservation, max-roll disablement, and absence of old `Resolve Attack` UI.
- Inherited Task 7 evidence reviewed: `.omo/evidence/habby-yahtzee-task-7-full-regression.txt` reports `npm test -- --run` passed with 6 files and 38 tests, plus build and browser QA passed.
- Inherited old-flow evidence reviewed: `.omo/evidence/habby-yahtzee-task-7-old-flow-removed.txt` reports no runtime `Best hand` or `Resolve Attack` path, with `Resolve Attack` only in negative test assertions.
- Final-wave rerun executed: `npm test -- --run`.
- Final-wave rerun result: 6 test files passed, 38 tests passed, exit code 0.

## Edge Cases

- `[2,2,2,2,5]`: `pair` and `fourKind` are both valid, but selected damage uses the selected category multiplier. Pair remains x1.2 and is not upgraded to x3.2.
- `[2,2,2,3,3]`: full house and lower matching categories are valid. This includes `chance`, `pair`, `twoPair`, `threeKind`, and `fullHouse`.
- Pre-roll/incomplete dice: no categories are valid and UI category buttons show disabled `Roll first` state.
- Invalid post-roll categories: UI disables them with `Not matched`, and `resolveCategory` returns without damage if called with an invalid category.
- `chance`: unavailable before a complete first roll, available after any complete five-dice roll.
- Cancel path: `Cancel Dice` calls `closeDice()`, consuming the charge and resetting dice, holds, rolls, and panel state.
- Selected attack path: successful category resolution calls `closeDice(...)`, resetting dice, holds, rolls, and panel state after applying selected damage.
- Category lockout: no persisted used-category structure exists, so categories are reusable on the next dice charge.
- LSP diagnostics: TypeScript LSP is configured but unavailable because `typescript-language-server` is not installed. Build evidence from Task 7 and the final `npm test -- --run` rerun substitute for available automated validation.

## Verdict

The Yahtzee category mechanics and combat state behavior meet the corrected RPG prototype requirements. Selected category determines damage, invalid categories are disabled and guarded, categories are reusable on future charges, dice state resets after selected attack or cancel, and regression tests pass.

VERDICT: APPROVE
