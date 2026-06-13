# Draft: Android UI Safe Area, HUD, and Gacha Prep

## Requirements (confirmed)
- User reports current UI does not handle notch / punch-hole areas correctly; screenshot shows content and top HUD overlapping the device cutout/status area.
- Main UI should prepare for a top player information area with player info, gold, gems, and eventually energy.
- Main UI should prepare for weapon and armor gacha entry points.
- A gacha page should be planned as part of the future UI direction.
- User is testing through Android Studio emulator / preview workflow, not necessarily physical phone installation.
- User selected scope: include safe-area fixes, main HUD preparation, weapon/armor gacha entry points, and gacha page planning.
- User selected energy handling: reserve space/structure only for later, do not fully implement active energy UI now.
- User selected gacha structure: one gacha page with weapon/armor tabs.
- User selected HUD data source: connect to existing `MockData` / player mock data if available.

## Technical Decisions
- Pending: exact safe-area strategy to plan after codebase exploration reports current Compose layout/insets setup.
- Scope decision: plan full UI foundation work through gacha page support, not safe-area-only.
- Energy decision: reserve slot/data structure for future energy, but avoid active energy mechanics in this pass.
- Gacha navigation decision: plan a single gacha destination/page with tabs for weapon and armor draws.
- HUD data decision: prefer binding top HUD to existing mock/player data instead of hardcoded duplicate values.

## Research Findings
- SDD framework detection: no `openspec/` or `.specify/` directory detected from repository root.
- Explore agents launched:
  - UI structure / window insets / navigation / screen files.
  - Test infrastructure / Compose UI test support.

## Open Questions
- Pending codebase research: exact files/functions for HUD insertion, safe-area handling, navigation route addition, and testing/build commands.

## Scope Boundaries
- INCLUDE: Android Compose safe-area/cutout handling, top player/currency HUD preparation, weapon/armor gacha UI preparation, gacha page planning.
- EXCLUDE: active energy mechanics, real backend economy, real payment/shop logic, real gacha probability systems, and production asset replacement unless explicitly requested.
