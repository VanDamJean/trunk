# F3 Visual/UX Review - visual-engineering

Checkbox reviewed: `- [ ] F3. Visual/UX Review — visual-engineering`

## Screens Reviewed

- Fresh 375x812 Playwright screenshots captured during this review:
  - `.omo/evidence/habby-yahtzee-f3-home-375x812.png`
  - `.omo/evidence/habby-yahtzee-f3-home-upgrade-ready-375x812.png`
  - `.omo/evidence/habby-yahtzee-f3-combat-375x812.png`
  - `.omo/evidence/habby-yahtzee-f3-combat-board-375x812.png`
  - `.omo/evidence/habby-yahtzee-f3-result-375x812.png`
- Inherited evidence reviewed:
  - Task 3: `.omo/evidence/habby-yahtzee-task-3-manual-qa.txt`, `.omo/evidence/habby-yahtzee-task-3-no-external-assets.txt`
  - Task 4: `.omo/evidence/habby-yahtzee-task-4-hub.png`, `.omo/evidence/habby-yahtzee-task-4-result.png`, `.omo/evidence/habby-yahtzee-task-4-hub-verified.png`, `.omo/evidence/habby-yahtzee-task-4-result-verified.png`
  - Task 6: `.omo/evidence/habby-yahtzee-task-6-browser-qa.md`, `.omo/evidence/habby-yahtzee-task-6-pair-category.png`, `.omo/evidence/habby-yahtzee-task-6-full-house-category.png`
  - Task 7: `.omo/evidence/habby-yahtzee-task-7-full-regression.txt`, `.omo/evidence/habby-yahtzee-task-7-old-flow-removed.txt`, `.omo/evidence/task-7-dice-resolve.png`
- Required source read: `src/components/GameArt.tsx`, `src/components/ui.tsx`, `src/styles.css`, `src/screens/HomeScreen.tsx`, `src/screens/CombatScreen.tsx`, `src/screens/ResultScreen.tsx`, `src/screens/EquipmentScreen.tsx`, and `src/screens/UpgradeScreen.tsx`.

## UX Findings

- Home is a compact RPG hub, not a generic pastel/emoji card stack. It opens with stage/status pills, a chunky hero arena, power callout, stage route, and a dominant green `Start Combat` CTA. Secondary Equipment/Upgrade actions are visually subordinate.
- The upgrade-ready state is understandable at 375x812: after granting enough coins, Home shows a red-dot status row saying `Gear upgrade ready before the next fight.`, and Upgrade buttons/items use red-dot markers with accessible labels.
- Combat has a layered battle scene with custom hero/enemy SVG art, foreground arena, damage feedback, two readable HP bars, and a disabled `Open Dice` button that remains legible through muted styling and intact text.
- The Combat attack board remains readable on mobile. The `Choose Attack Hand` card shows five dice, roll count, enabled attack choices, and disabled invalid choices with visible `Not matched` wording. Fresh QA also asserted an invalid `Full House attack x2.5 Not matched` button is disabled.
- Result has strong reward hierarchy: animated-style `WIN` burst, `Victory!`, stage badge, large coins-earned panel, selected attack text, `Path cleared` status, and clear `Claim Reward` / `Back Home` actions.
- Original CSS/SVG components replace emoji-only main art: `RoundAnimalHero`, `ForestMonster`, `LayeredArena`, badge glyphs, `DamagePop`, and `RewardBurst` are composed through `ui.tsx` and styled by tokenized CSS.
- No shop, gacha, ad, or IAP visual system was introduced in the reviewed runtime source or inspected screenshots.

## Asset Safety

- Runtime source search: `rg -n -i '\b(habby|capybara go|capybara|shop|gacha|iap|ad|ads)\b' src` returned no matches.
- Runtime external asset search: `rg -n "https?://|url\(|\.png|\.jpg|\.jpeg|\.webp|\.gif|<img|background-image" src` found only local inline SVG gradient references such as `fill="url(#heroBody)"`; no HTTP image URLs, `<img>` tags, or raster art references were found.
- Emoji placeholder search across `src/components` and `src/screens` returned no matches.
- Reviewed art is inline SVG/CSS using generic original characters (`Dice Cub`, `Forest Grump`) and design tokens, not copied Habby/Capybara assets.

## Verification

- Started Vite locally at `127.0.0.1:5173` and drove the app with Playwright at a 375x812 viewport.
- Captured Home, upgrade-ready Home, Combat, Combat board, and Result screenshots listed above.
- Playwright assertions passed for `Start Combat`, disabled pre-charge `Open Dice`, disabled invalid category wording, upgrade prompt text, and Result rendering. Browser page errors: 0.
- Stopped the F3 dev server and confirmed no process remained listening on TCP 5173.

## Verdict

The corrected prototype meets the F3 visual/UX bar: stronger game-like mobile RPG hierarchy, readable 375x812 combat/category flow, reward juice, accessible button text/disabled states, upgrade red-dot affordance, original non-infringing SVG/CSS art, and no monetization/product-system creep.

VERDICT: APPROVE
