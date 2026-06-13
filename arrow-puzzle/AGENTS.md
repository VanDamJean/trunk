# AGENTS.md

## Project Shape
- Vanilla ES module browser game: HTML5 Canvas + CSS + Vite. No framework, router, TypeScript, linter, formatter, CI, or repo-local OpenCode config.
- This directory is not a git repo. Do not rely on `git status`, blame, branches, or commit history here.
- Real entrypoint is `index.html` -> `main.js`; `Arrow.js` is the only extracted game class.

## Commands
- `npm run dev` starts Vite on `localhost:3000` and auto-opens a browser (`vite.config.js`).
- `npm test` runs Vitest. Focused examples: `npm test -- tests/arrow.test.js`, `npm test -- tests/static-main.test.js`.
- `npm run build` writes production output to `dist/`.
- `npm run preview` previews the built app.

## Runtime Architecture
- `main.js` owns canvas setup, game loop, image upload, image-to-mask conversion, random level generation, undo, JSON export, Web Audio, particles, and DOM event wiring.
- `Arrow.js` owns path geometry, movement, hit testing, approximate collision/escape checks, and cutout arrowhead rendering.
- Canvas backing size is hardcoded to `600x600`; CSS scales it responsively. Pointer math in `main.js` maps screen coords back to canvas coords.

## Generator Facts
- `Grid Max` is image mask/detail resolution, not a pure difficulty setting. Higher values preserve more shape detail but increase cell count, generation cost, and fallback/failure odds.
- Implemented generators are `1cell`, `squiggly`, and `mixed`. `mixed`/`squiggly` can fall back to `1cell`; complete image-mask failure can fall back to the default heart mask.
- Reverse Injection is documented only as a future option in `IMAGE_TO_PUZZLE_PLAN.md`; it is not implemented. Do not claim guaranteed solvability for arbitrary images.
- Escape validation must reject rays through already filled cells and through the current candidate `path`; `tests/static-main.test.js` guards against self-overlap/uroboros regressions.

## Tests And Coverage
- `tests/arrow.test.js` covers `Arrow` geometry/collision basics.
- `tests/static-main.test.js` is intentional: it guards cleanup/doc/runtime invariants by reading source files as text.
- There are no DOM/canvas integration tests. For UI or generator changes, run `npm test`, `npm run build`, then browser-smoke `npm run dev` with Playwright or manual checks.

## Docs Status
- `SPEC.md`, `IMAGE_TO_PUZZLE_PLAN.md`, and `REPORT.md` are useful but should be reconciled with code when they disagree.
- `MASTER_PLAN.md` is mostly roadmap/history; verify against `main.js`/`Arrow.js` before treating an item as missing.
