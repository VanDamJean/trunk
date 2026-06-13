# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Workspace Overview

This is a multi-project workspace. Each subdirectory is an independent project:

| Directory | Type | Status |
|---|---|---|
| `gemsin/` | Vanilla HTML/CSS/JS web mockup of Gemsin gaming app | Complete prototype |
| `gemsin-android/` | Kotlin + Jetpack Compose Android port of Gemsin | Functional, no real backend |
| `medi/silent_moon/` | React + TypeScript + Vite meditation app (Silent Moon) | Web mockup, bugs fixed |
| `medi-android/` | Kotlin + Jetpack Compose Android port of Silent Moon | Build-ready, no real audio backend |
| `word-game-community/` | React + TypeScript + Vite word puzzle game (Kyrgyz) | Active dev project |
| `tetris/` | Python + pygame neon Tetris | Standalone script |
| `designed_mob_apli/` | Loose TSX component files for a community mobile app | Reference/prototype |
| `russiaQuiz/` | Loose TSX/HTML quiz files | Reference/prototype |

---

## word-game-community (React/Vite)

Stack: React 19, TypeScript, Tailwind CSS v4, Vite 7, pnpm

```bash
cd word-game-community
pnpm dev          # dev server at localhost:5173
pnpm build        # production build
pnpm preview      # preview production build
```

Path alias `@/` maps to `src/`. No test runner configured.

**Architecture:** Single-page app. `src/App.tsx` renders `src/pages/Home.tsx` directly. `Home.tsx` owns all game state — riddle progression, gem economy, slot/grid mechanics. `src/components/Confetti.tsx` is the only extracted component. No router.

**Game logic:** `RIDDLES` array is hardcoded in `Home.tsx`. Tile placement uses a `gridPerm` permutation array (indices into `tiles`) so shuffling doesn't move selected tiles. `slotTileId` holds tile IDs in answer slots (null = empty).

---

## gemsin (Vanilla Web Mockup)

No build step. Open `gemsin/index.html` directly in a browser or serve with any static file server:

```bash
cd gemsin
python3 -m http.server 8080
```

Mock data lives in `mock-data.js`. `app.js` handles all view switching and interactions. Design constraints are documented in `DESIGN_FREEZE_SPEC.md` — pixel values, colors, and spacing must not deviate from the Figma source (node `1:3`, file key `YwNL6NSg776AyOAPuCBMsp`).

---

## gemsin-android (Kotlin + Jetpack Compose)

Build with Android Studio or Gradle CLI. JVM heap is set to 8192m in `gradle.properties` (required to avoid OOM during Gradle daemon startup).

```bash
cd gemsin-android
./gradlew assembleDebug
./gradlew installDebug   # requires connected device/emulator
```

Key versions (from `gradle/libs.versions.toml`): AGP 9.2.0, Kotlin 2.2.10, Compose BOM 2024.12.01, Navigation Compose 2.8.5.

**Architecture:** Single-activity (`MainActivity.kt`) with Navigation Compose. `NavGraph.kt` defines routes for 10 app screens + 4 mini-game screens. `data/MockData.kt` contains all static data as Kotlin data classes. `ui/theme/Theme.kt` defines the dark Gemsin brand color scheme.

**Mini-games** (`ui/screens/`): All four games (`JumpManScreen`, `FlappyBirdScreen`, `UlarAngkaScreen`, `TetrisScreen`) use a `LaunchedEffect` + `delay(16L)` game loop (~60fps), `BoxWithConstraints` for responsive sizing, and an IDLE/RUNNING/DEAD state machine. Graphics are Canvas-drawn shapes (no sprite assets yet).

---

## tetris (Python)

Requires `pygame`. Run directly:

```bash
cd tetris
python3 neon_tetris.py
```

---

## Gemsin Design Freeze Rules

When implementing any Gemsin UI (web or Android), these design values must not change:
- Button sizes, corner radii, and colors
- Typography (family, size, weight, line-height, alignment)
- Card dimensions, padding, and spacing
- Icon sizes, positions, and colors
- Bottom tab bar height and active color
- Base screen dimensions: `375×812`

The authoritative frame list is `gemsin/screen_frames.csv` (40 screens, `name=Group` filter). Use frame `id` values as the implementation reference.
