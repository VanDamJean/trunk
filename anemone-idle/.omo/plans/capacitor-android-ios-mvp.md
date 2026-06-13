# Capacitor Android+iOS MVP Port Plan

## TL;DR
> **Summary**: Package the existing vanilla Vite `anemone-idle` game as a shared Capacitor mobile MVP for Android and iOS without rewriting gameplay/UI. Keep `localStorage` for MVP speed, generate native projects, harden mobile layout, add iOS scripts/docs, and verify with existing tests plus native sync/build/launch checks where SDKs exist.
> **Deliverables**:
> - Android native project generated under `android/`
> - iOS native project generated under `ios/`
> - `@capacitor/ios` installed and mobile scripts updated
> - Mobile safe-area/touch/overflow fixes for phone portrait
> - Mobile runbook documenting build/sync/open commands and MVP risks
> - Evidence files under `.omo/evidence/` for every verification step
> **Effort**: Medium
> **Parallel**: YES - 4 waves
> **Critical Path**: Task 1 → Task 2 → Task 3 → Task 7 → Task 8 → Final Verification

> **Active Plan Declaration**: This is the active plan for the Android+iOS MVP port. Other existing files in `.omo/plans/` (`anemone-idle-economy.md`, `anemone-progression-redesign.md`) are unrelated prior plans and MUST NOT be executed for this request.

## Context
### Original Request
User asked in Korean to first create a documentation/work plan because it is time to move the project to Android and make it work on both Android and Apple/iOS.

### Interview Summary
- Direction chosen: shared app approach.
- Target chosen: MVP port, not full production/store release.
- Technical direction: Capacitor web-first packaging, not React Native/Flutter/native rewrite.
- Storage decision: keep current `localStorage` for MVP.
- Test decision: tests-after.

### Metis Review (gaps addressed)
- Added explicit environment prerequisites and blocker evidence behavior for missing Xcode/Android SDK/JDK.
- Added guardrails against rewriting, Preferences migration, AdMob, backend/auth/cloud save, and app-store release creep.
- Added concrete acceptance commands for Vitest, Vite build, content validation, balance report, Capacitor add/sync, Gradle/Xcode checks.
- Added safe-area, touch target, no-horizontal-scroll, small-screen, stale-`dist`, and asset-path edge cases.

## Work Objectives
### Core Objective
Make `/Users/a1/Desktop/manus/anemone-idle` build and package as a Capacitor mobile MVP for both Android and iOS from the existing Vite app.

### Deliverables
- Updated dependency/scripts in `package.json` and `package-lock.json`.
- Generated `android/` and `ios/` native projects.
- Mobile layout hardening in `index.html` and/or `src/styles.css` only where required for Capacitor phone portrait.
- Documentation/runbook under `docs/` for mobile MVP commands, risks, and exclusions.
- Agent-generated evidence under `.omo/evidence/`.

### Definition of Done (verifiable conditions with commands)
- `npm test` exits 0.
- `npm run build` exits 0 and creates `dist/`.
- `node scripts/validate-progression-content.mjs` exits 0.
- `node scripts/balance-report.mjs --scenario chapter1-active` exits 0 and writes expected evidence JSON.
- `npm run mobile:sync` exits 0 after build.
- `android/` and `ios/` directories exist with Capacitor-generated native projects.
- If Android SDK/JDK exists: Android Gradle debug build exits 0; otherwise `.omo/evidence/mobile-android-sdk-blocker.txt` records exact missing command/tool.
- If Xcode exists: iOS simulator/build check exits 0; otherwise `.omo/evidence/mobile-ios-xcode-blocker.txt` records exact missing command/tool.
- Mobile viewport QA at `375×667`, `375×812`, and `390×844` shows no horizontal scroll, no bottom-nav overlap, tappable main controls, and visible game loop.

### Must Have
- Use Capacitor 7 workflow around existing `webDir: dist`.
- Preserve `capacitor.config.json` app ID `com.bloopoffice.anemoneidle` unless implementation discovers a hard Capacitor conflict.
- Preserve existing gameplay, economy, progression, translations, save schema, and visual identity.
- Keep `localStorage` in MVP and document mobile persistence risk.
- Treat missing Xcode/Android SDK as explicit documented blockers, not silent failure.

### Must NOT Have (guardrails, AI slop patterns, scope boundaries)
- MUST NOT rewrite in React Native, Flutter, Kotlin UI, SwiftUI, React, or another SPA framework.
- MUST NOT migrate storage to Capacitor Preferences in MVP.
- MUST NOT add backend, auth, cloud save, push notifications, analytics, AdMob production, signing/provisioning, TestFlight, Play Console, or App Store release tasks.
- MUST NOT refactor `src/main.js` merely because it is large.
- MUST NOT alter game balance/content except if a mobile blocker is directly proven.
- MUST NOT mark simulator/device verification passed when the local SDK is absent; record blocker evidence instead.

## Verification Strategy
> ZERO HUMAN INTERVENTION - all verification is agent-executed.
- Test decision: tests-after using existing Vitest + Vite build + content/balance scripts, then Capacitor sync/native checks.
- QA policy: Every task has agent-executed scenarios.
- Evidence: `.omo/evidence/task-{N}-{slug}.{ext}`

## Execution Strategy
### Parallel Execution Waves
> Target: 5-8 tasks per wave. <3 per wave (except final) = under-splitting.
> Extract shared dependencies as Wave-1 tasks for max parallelism.

Wave 1: Tasks 1-2 — baseline audit and Capacitor iOS dependency/scripts foundation.
Wave 2: Tasks 3-5 — native project generation, mobile layout hardening, storage MVP QA.
Wave 3: Tasks 6-7 — docs/runbook and native build/open verification after native projects/layout exist.
Wave 4: Task 8 — consolidated regression evidence after docs/storage/native verification complete.

### Dependency Matrix (full, all tasks)
| Task | Depends On | Blocks |
|---:|---|---|
| 1 | none | 2, 3, 7, 8 |
| 2 | 1 | 3, 7, 8 |
| 3 | 2 | 7, 8 |
| 4 | 1 | 7, 8 |
| 5 | 1 | 8 |
| 6 | 1, 2, 3 | 8 |
| 7 | 3, 4 | 8 |
| 8 | 5, 6, 7 | Final Verification |

### Agent Dispatch Summary (wave → task count → categories)
| Wave | Count | Categories |
|---|---:|---|
| 1 | 2 | quick, unspecified-high |
| 2 | 3 | unspecified-high, visual-engineering |
| 3 | 2 | writing, unspecified-high |
| 4 | 1 | deep |

## TODOs
> Implementation + Test = ONE task. Never separate.
> EVERY task MUST have: Agent Profile + Parallelization + QA Scenarios.

- [x] 1. Baseline mobile readiness audit

  **What to do**: Run existing repo verification commands before changing mobile files. Capture current status for `npm test`, `npm run build`, progression validation, balance report, current Capacitor config, and presence/absence of `android/` and `ios/`. Create evidence files only; do not change source files in this task.
  **Must NOT do**: Do not install packages, generate native projects, or edit code in this task.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: needs command execution, environment diagnosis, and evidence capture.
  - Skills: [] - no special skill needed.
  - Omitted: [`bug-hunter`] - no defect is being debugged yet.

  **Parallelization**: Can Parallel: NO | Wave 1 | Blocks: [2, 3, 7, 8] | Blocked By: []

  **References**:
  - Pattern: `package.json:6-13` - existing scripts: `dev`, `build`, `mobile:sync`, `mobile:android`, `preview`, `test`.
  - Pattern: `capacitor.config.json:1-9` - current app ID/name/webDir and Android scheme.
  - Test: `tests/gameState.test.js:1-21` - Vitest module import style and current logic coverage.
  - Test: `tests/storage.test.js:1-158` - current save/load/localStorage abstraction coverage.
  - Docs: `docs/guide.md:133-142` - existing verification command list.

  **Acceptance Criteria**:
  - [ ] `npm test` result captured to `.omo/evidence/task-1-npm-test.txt`.
  - [ ] `npm run build` result captured to `.omo/evidence/task-1-npm-build.txt` and `dist/` existence recorded.
  - [ ] `node scripts/validate-progression-content.mjs` result captured to `.omo/evidence/task-1-progression-validation.txt`.
  - [ ] `node scripts/balance-report.mjs --scenario chapter1-active` result captured to `.omo/evidence/task-1-balance-report.txt`.
  - [ ] `android/` and `ios/` absence/presence captured to `.omo/evidence/task-1-native-folder-audit.txt`.

  **QA Scenarios**:
  ```
  Scenario: Baseline commands execute
    Tool: Bash
    Steps: Run npm test; npm run build; node scripts/validate-progression-content.mjs; node scripts/balance-report.mjs --scenario chapter1-active
    Expected: Each command exit code is recorded; failures include exact stderr and are treated as blockers for dependent tasks.
    Evidence: .omo/evidence/task-1-baseline-commands.txt

  Scenario: Native folders are not accidentally overwritten
    Tool: Bash
    Steps: Check whether android/ and ios/ exist before generation
    Expected: Existing folders are listed if present; if absent, evidence states generation is safe.
    Evidence: .omo/evidence/task-1-native-folder-audit.txt
  ```

  **Commit**: NO | Message: `chore(mobile): capture baseline readiness` | Files: [.omo/evidence/*]

- [x] 2. Add iOS Capacitor dependency and mobile scripts

  **What to do**: Add `@capacitor/ios` at the same compatible Capacitor version family as existing `@capacitor/*` packages. Update `package.json` scripts to include iOS/shared mobile commands: keep `mobile:sync`, keep `mobile:android`, add `mobile:ios` for iOS run/open workflow, and add non-ambiguous open/build helper scripts if supported by local Capacitor CLI. Update `package-lock.json` through npm install, then run tests/build.
  **Must NOT do**: Do not run `npx cap add ios` or `npx cap add android` in this task. Do not change app ID/name.

  **Recommended Agent Profile**:
  - Category: `quick` - Reason: package/script change with straightforward verification.
  - Skills: [] - package manager operation only.
  - Omitted: [`api-endpoint-builder`] - no API work.

  **Parallelization**: Can Parallel: NO | Wave 1 | Blocks: [3, 7, 8] | Blocked By: [1]

  **References**:
  - Pattern: `package.json:14-20` - existing Capacitor dependency versions.
  - External: Capacitor docs `/ionic-team/capacitor-docs` - install `@capacitor/android @capacitor/ios`, then add platforms.
  - Pattern: `package.json:6-13` - script section to extend without deleting current scripts.

  **Acceptance Criteria**:
  - [ ] `package.json` includes `@capacitor/ios` compatible with `@capacitor/core`/`@capacitor/cli`/`@capacitor/android`.
  - [ ] `package-lock.json` is updated by npm, not hand-edited.
  - [ ] `npm test` exits 0 after dependency/script update.
  - [ ] `npm run build` exits 0 after dependency/script update.
  - [ ] Script list evidence captured to `.omo/evidence/task-2-mobile-scripts.txt`.

  **QA Scenarios**:
  ```
  Scenario: iOS dependency installed cleanly
    Tool: Bash
    Steps: Run npm install for @capacitor/ios, then inspect npm package metadata through npm ls @capacitor/ios
    Expected: @capacitor/ios appears with compatible 7.x version and no npm install error.
    Evidence: .omo/evidence/task-2-ios-dependency.txt

  Scenario: Existing web tests still pass
    Tool: Bash
    Steps: Run npm test and npm run build after package changes
    Expected: Both commands exit 0.
    Evidence: .omo/evidence/task-2-regression.txt
  ```

  **Commit**: YES | Message: `chore(mobile): add capacitor ios scripts` | Files: [`package.json`, `package-lock.json`]

- [~] 3. Generate Capacitor Android and iOS native projects

  **What to do**: Generate missing native platform projects using Capacitor CLI. If `android/` or `ios/` already exists from a prior attempt, do not overwrite blindly; inspect and either run sync only or record a blocker. For absent folders, run `npx cap add android` and `npx cap add ios` after `npm run build`. Then run `npx cap sync` or `npm run mobile:sync` and capture output.
  **Must NOT do**: Do not manually edit native Gradle/Xcode files unless Capacitor generation fails with a concrete, documented requirement. Do not add signing/provisioning/app-store setup.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: native project generation has environment and overwrite edge cases.
  - Skills: [] - Capacitor CLI workflow only.
  - Omitted: [`performance-optimizer`] - no performance measurement needed.

  **Parallelization**: Can Parallel: YES | Wave 2 | Blocks: [6, 7, 8] | Blocked By: [2]

  **References**:
  - API/Type: `capacitor.config.json:1-9` - app ID/name/webDir used by generated projects.
  - Pattern: `package.json:8-10` - build then sync/run mobile workflow.
  - External: Capacitor docs `/ionic-team/capacitor-docs` - `npx cap add android`, `npx cap add ios`, `npm run build`, `npx cap sync`.
  - Docs: `docs/advanced_roadmap.md:153-167` - project roadmap recommends Capacitor packaging with `dist` copied into native projects.

  **Acceptance Criteria**:
  - [ ] `android/` exists after generation or existing-folder blocker is recorded.
  - [ ] `ios/` exists after generation or existing-folder blocker is recorded.
  - [ ] `npm run build` exits 0 immediately before sync.
  - [ ] `npx cap sync` or `npm run mobile:sync` exits 0.
  - [ ] Generated-project evidence captured to `.omo/evidence/task-3-cap-add-sync.txt`.

  **QA Scenarios**:
  ```
  Scenario: Fresh native project generation
    Tool: Bash
    Steps: If android/ and ios/ are absent, run npm run build; npx cap add android; npx cap add ios; npx cap sync
    Expected: android/ and ios/ exist and sync exits 0.
    Evidence: .omo/evidence/task-3-cap-add-sync.txt

  Scenario: Existing folder guard
    Tool: Bash
    Steps: If android/ or ios/ exists before cap add, record directory state and skip destructive generation
    Expected: No native folder is overwritten; blocker or sync-only path is documented.
    Evidence: .omo/evidence/task-3-existing-folder-guard.txt
  ```

  **Commit**: YES | Message: `chore(mobile): generate capacitor native projects` | Files: [`android/**`, `ios/**`, `package.json`, `package-lock.json`, `capacitor.config.json`]

- [x] 4. Harden phone-portrait layout for Capacitor WebView

  **What to do**: Audit and adjust `index.html`/`src/styles.css` for mobile WebView basics: viewport meta correctness, safe-area padding for iOS notch/home indicator, bottom nav not overlapping content, no horizontal scroll, and minimum touch target behavior. Use current 430px shell as the visual baseline; do not redesign.
  **Must NOT do**: Do not change color palette, gameplay text, economy values, component hierarchy, or tab names.

  **Recommended Agent Profile**:
  - Category: `visual-engineering` - Reason: mobile CSS, safe areas, and viewport QA.
  - Skills: [] - no design skill beyond existing CSS constraints.
  - Omitted: [`frontend-ui-ux`] - avoid redesign; only hardening.

  **Parallelization**: Can Parallel: YES | Wave 2 | Blocks: [7, 8] | Blocked By: [1]

  **References**:
  - Pattern: `src/styles.css:152-159` - `.shell` width/min-height/padding baseline.
  - Pattern: `src/styles.css:183-197` - fixed `.resource-header` top layout.
  - Pattern: `src/styles.css:85-108` - current button/touch styling baseline.
  - Pattern: `src/main.js:35-61` - top shell, tab panel, and bottom nav DOM structure.

  **Acceptance Criteria**:
  - [ ] `npm run build` exits 0 after layout changes.
  - [ ] At viewport `375×667`, page has no horizontal scroll and bottom nav does not cover main tappable controls.
  - [ ] At viewport `375×812`, top resource header and bottom nav remain visible and content can scroll.
  - [ ] At viewport `390×844`, primary pulse button and tab navigation are tappable.
  - [ ] Evidence screenshots or text report captured under `.omo/evidence/task-4-mobile-layout-*`.

  **QA Scenarios**:
  ```
  Scenario: Small phone portrait layout
    Tool: Playwright
    Steps: Open Vite preview at 375x667; inspect document.scrollWidth <= window.innerWidth; click reef pulse button; click each bottom nav tab
    Expected: No horizontal scroll; clicks work; no fixed header/nav overlap prevents interaction.
    Evidence: .omo/evidence/task-4-375x667.png

  Scenario: iPhone-style safe-area layout
    Tool: Playwright
    Steps: Open Vite preview at 390x844; inspect top header, bottom nav, and scrollable panel; click capsule/upgrades/settings tabs
    Expected: Header/nav visible; tab panel content reachable; no content hidden behind bottom nav.
    Evidence: .omo/evidence/task-4-390x844.png
  ```

  **Commit**: YES | Message: `fix(mobile): harden capacitor viewport layout` | Files: [`index.html`, `src/styles.css`]

- [x] 5. Verify MVP localStorage behavior and document persistence risk

  **What to do**: Keep `localStorage` implementation for MVP. Add no Preferences plugin. Verify save/load still works through existing tests and one browser/mobile smoke scenario. If documentation is not handled by Task 6 yet, provide exact risk text for Task 6: mobile WebViews can clear localStorage under storage pressure; uninstall clears saves; Preferences migration is future hardening.
  **Must NOT do**: Do not install `@capacitor/preferences`, do not change save key, do not migrate schema.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: persistence verification and risk documentation.
  - Skills: [] - uses existing storage tests.
  - Omitted: [`api-endpoint-builder`] - no backend storage.

  **Parallelization**: Can Parallel: YES | Wave 2 | Blocks: [8] | Blocked By: [1]

  **References**:
  - Pattern: `src/main.js:22-28` - app loads state from `window.localStorage`.
  - API/Type: `src/storage.js:4-17` - save abstraction writes JSON to storage.
  - API/Type: `src/storage.js:19-48` - load abstraction handles empty/corrupt saves and offline progress.
  - Test: `tests/storage.test.js:12-158` - coverage for save/load, corrupt saves, quota failures, and legacy values.
  - External: Capacitor docs `/ionic-team/capacitor-docs` - Preferences recommended for stable native key/value storage, but out of MVP by user decision.

  **Acceptance Criteria**:
  - [ ] `npm test -- tests/storage.test.js` or equivalent focused Vitest command exits 0.
  - [ ] Full `npm test` exits 0.
  - [ ] No `@capacitor/preferences` dependency added.
  - [ ] Evidence captures localStorage smoke: save is created after one pulse/producer action and remains after reload.
  - [ ] Persistence risk text is included in docs/runbook from Task 6 or captured for insertion.

  **QA Scenarios**:
  ```
  Scenario: Browser save survives reload
    Tool: Playwright
    Steps: Open app; click primary reef pulse button; wait for state save; reload page; inspect resource count/locale/save presence in localStorage key anemone-idle-save-v1
    Expected: Save key exists and loaded state is not reset to corrupt/empty state.
    Evidence: .omo/evidence/task-5-localstorage-reload.txt

  Scenario: Preferences plugin stays out of MVP
    Tool: Bash
    Steps: Inspect package.json/package-lock for @capacitor/preferences after all changes
    Expected: Dependency is absent; docs mention Preferences only as future hardening.
    Evidence: .omo/evidence/task-5-no-preferences.txt
  ```

  **Commit**: YES | Message: `test(mobile): verify mvp localstorage persistence` | Files: [`docs/**`, `.omo/evidence/**`]

- [x] 6. Write mobile MVP runbook and scope boundaries

  **What to do**: Add or update a mobile runbook in `docs/` with exact commands for local web dev, build, `mobile:sync`, Android open/run, iOS open/run, SDK prerequisites, evidence expectations, localStorage MVP risk, and explicit exclusions. Use Korean tone consistent with existing docs where practical.
  **Must NOT do**: Do not claim store readiness, signing, AdMob production, or cloud save exists.

  **Recommended Agent Profile**:
  - Category: `writing` - Reason: technical documentation and runbook clarity.
  - Skills: [] - repo docs only.
  - Omitted: [`python-pptx-generator`] - no presentation deck.

  **Parallelization**: Can Parallel: YES | Wave 3 | Blocks: [8] | Blocked By: [1, 2, 3]

  **References**:
  - Docs: `docs/guide.md:5-13` - existing run instructions style.
  - Docs: `docs/guide.md:117-142` - existing save and verification sections.
  - Docs: `docs/advanced_roadmap.md:153-167` - existing mobile packaging roadmap.
  - Pattern: `package.json:6-13` - commands to document after scripts are updated.
  - Pattern: `capacitor.config.json:1-9` - app ID/name/webDir to document.

  **Acceptance Criteria**:
  - [ ] Docs include Android and iOS MVP command sequences.
  - [ ] Docs state `localStorage` is intentionally retained for MVP and list persistence risks.
  - [ ] Docs state explicit exclusions: store release/signing, AdMob production, backend/auth/cloud save, Preferences migration.
  - [ ] Docs include prerequisite checks for Node/npm, Android Studio/SDK/JDK, Xcode/CocoaPods if needed by generated project.
  - [ ] Docs include troubleshooting for missing SDKs and existing `android/`/`ios/` folders.

  **QA Scenarios**:
  ```
  Scenario: Android runbook is executable
    Tool: Bash
    Steps: Extract documented Android commands and compare against package.json scripts and generated android/ folder
    Expected: Every documented command exists or is an official Capacitor CLI command; no imaginary script names.
    Evidence: .omo/evidence/task-6-android-runbook-check.txt

  Scenario: iOS runbook is bounded to MVP
    Tool: Bash
    Steps: Search docs for store release/signing/TestFlight claims and Preferences migration wording
    Expected: Docs present them only as exclusions/future work, not MVP deliverables.
    Evidence: .omo/evidence/task-6-ios-scope-check.txt
  ```

  **Commit**: YES | Message: `docs(mobile): add capacitor mvp runbook` | Files: [`docs/**`]

- [~] 7. Verify native sync/build/open flows for Android and iOS

  **What to do**: Run the complete mobile chain after Tasks 2-4: `npm run build`, `npm run mobile:sync`, Android Gradle/debug build if SDK exists, and iOS build/simulator/open check if Xcode exists. When a native SDK is missing, write blocker evidence with exact missing command, platform, and next install action; do not fake pass.
  **Must NOT do**: Do not sign release builds, create store artifacts, or change native project settings unrelated to MVP debug launch.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: native build tooling and environment fallbacks.
  - Skills: [] - command execution and evidence capture.
  - Omitted: [`git-master`] - no git operation required unless executor is explicitly committing.

  **Parallelization**: Can Parallel: YES | Wave 3 | Blocks: [8] | Blocked By: [3, 4]

  **References**:
  - Pattern: `package.json:8-10` - existing build/sync/run chain.
  - External: Capacitor docs `/ionic-team/capacitor-docs` - build web, sync, then compile native binary.
  - Generated: `android/` - Gradle debug build target after Task 3.
  - Generated: `ios/` - Xcode workspace/project target after Task 3.

  **Acceptance Criteria**:
  - [ ] `npm run build` exits 0 immediately before native sync.
  - [ ] `npm run mobile:sync` exits 0.
  - [ ] Android debug build exits 0 if Android SDK/JDK exists, otherwise `.omo/evidence/mobile-android-sdk-blocker.txt` exists with exact blocker.
  - [ ] iOS build/open/simulator check exits 0 if Xcode exists, otherwise `.omo/evidence/mobile-ios-xcode-blocker.txt` exists with exact blocker.
  - [ ] Native verification summary captured to `.omo/evidence/task-7-native-verification-summary.md`.

  **QA Scenarios**:
  ```
  Scenario: Android native build path
    Tool: Bash
    Steps: Check java/gradle/android SDK availability; run npm run build; npm run mobile:sync; run Android debug build through generated Gradle project if SDK exists
    Expected: Debug build exits 0 or exact SDK/JDK blocker evidence is written.
    Evidence: .omo/evidence/task-7-android-build.txt

  Scenario: iOS native build path
    Tool: Bash
    Steps: Check xcodebuild availability; run npm run build; npm run mobile:sync; run iOS build/open/simulator-compatible check if Xcode exists
    Expected: iOS check exits 0 or exact Xcode blocker evidence is written.
    Evidence: .omo/evidence/task-7-ios-build.txt
  ```

  **Commit**: YES | Message: `chore(mobile): verify native debug builds` | Files: [`android/**`, `ios/**`, `.omo/evidence/**`]

- [~] 8. Consolidate regression and mobile QA evidence

  **What to do**: Re-run final web and mobile verification after all implementation tasks: full Vitest, Vite build, progression validation, balance report, Capacitor sync, localStorage smoke, and viewport QA. Create a final evidence index summarizing pass/fail/blocker status and commands used.
  **Must NOT do**: Do not close blockers by assumption. Do not delete evidence from prior tasks.

  **Recommended Agent Profile**:
  - Category: `deep` - Reason: final cross-check across tests, docs, native artifacts, and QA evidence.
  - Skills: [] - verification only.
  - Omitted: [`performance-optimizer`] - performance tuning is out of MVP unless a blocking regression appears.

  **Parallelization**: Can Parallel: NO | Wave 4 | Blocks: [Final Verification] | Blocked By: [5, 6, 7]

  **References**:
  - Docs: `docs/guide.md:133-142` - final verification commands.
  - Test: `tests/*.test.js` - existing Vitest suite.
  - Pattern: `package.json:6-13` - final script inventory.
  - Pattern: `capacitor.config.json:1-9` - final Capacitor config check.

  **Acceptance Criteria**:
  - [ ] `.omo/evidence/mobile-mvp-final-index.md` exists and lists every task evidence file.
  - [ ] Full `npm test` exits 0.
  - [ ] `npm run build` exits 0.
  - [ ] `node scripts/validate-progression-content.mjs` exits 0.
  - [ ] `node scripts/balance-report.mjs --scenario chapter1-active` exits 0.
  - [ ] `npm run mobile:sync` exits 0.
  - [ ] Any SDK-limited native verification is clearly labeled BLOCKED-BY-ENV, not PASS.

  **QA Scenarios**:
  ```
  Scenario: Final regression gate
    Tool: Bash
    Steps: Run npm test; npm run build; node scripts/validate-progression-content.mjs; node scripts/balance-report.mjs --scenario chapter1-active; npm run mobile:sync
    Expected: All non-SDK commands exit 0.
    Evidence: .omo/evidence/task-8-final-regression.txt

  Scenario: Final mobile interaction smoke
    Tool: Playwright
    Steps: Run Vite preview; open 375x812; click pulse button; switch reef/capsule/upgrades/settings tabs; reload; inspect localStorage key
    Expected: Interactions work, save key persists after reload, no horizontal scroll.
    Evidence: .omo/evidence/task-8-mobile-smoke.png
  ```

  **Commit**: YES | Message: `test(mobile): consolidate mvp verification` | Files: [`.omo/evidence/**`, `docs/**`]

## Final Verification Wave (MANDATORY — after ALL implementation tasks)
> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.
> **Do NOT auto-proceed after verification. Wait for user's explicit approval before marking work complete.**
> **Never mark F1-F4 as checked before getting user's okay.** Rejection or user feedback -> fix -> re-run -> present again -> wait for okay.
- [x] F1. Plan Compliance Audit — oracle
- [x] F2. Code Quality Review — unspecified-high
- [x] F3. Real Manual QA — unspecified-high (+ playwright for viewport/mobile interaction QA)
- [x] F4. Scope Fidelity Check — deep

## Commit Strategy
- Prefer one commit per completed implementation task when files changed.
- Commit messages:
  - `chore(mobile): add capacitor ios scripts`
  - `chore(mobile): generate capacitor native projects`
  - `fix(mobile): harden capacitor viewport layout`
  - `test(mobile): verify mvp localstorage persistence`
  - `docs(mobile): add capacitor mvp runbook`
  - `chore(mobile): verify native debug builds`
  - `test(mobile): consolidate mvp verification`
- Never commit environment-specific secrets, signing configs, provisioning profiles, derived build outputs, or local IDE junk.

## Success Criteria
- Android+iOS shared mobile MVP path exists through Capacitor with generated native projects.
- Existing game behavior and save schema remain intact.
- Mobile packaging and layout are verified with concrete command output and screenshots/text evidence.
- Store/production scope remains explicitly out of MVP.
- The executor can run `/start-work` and follow this plan without asking which framework, storage strategy, test strategy, or scope boundaries to use.
