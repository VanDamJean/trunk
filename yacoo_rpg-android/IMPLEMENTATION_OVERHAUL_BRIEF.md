# Yacoo RPG — Antigravity/Gemini Implementation Brief (Casual Idle RPG UI)

## Purpose

This document is the coding-side handoff for Antigravity IDE / Gemini. The goal is to implement the visible art-direction overhaul described in `ART_DIRECTION_OVERHAUL_BRIEF.md` in the existing Kotlin + Jetpack Compose Android app.

The app must shift from a "blocky screen-by-screen prototype" to a **highly polished, seamless "floating HUD" casual Idle RPG**.

---

## Current Project Context

Project root: `/Users/a1/Desktop/manus/yacoo_rpg-android`
Build command: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug`

---

## Required Implementation Strategy (The Casual Overhaul)

### Phase 1 — Transparent & Floating Layout Infrastructure
Currently, the app relies heavily on `Scaffold` with solid background colors, rigid `Column`/`Row` grids, and opaque components.
1. Implement a **Global Full-Screen Background Canvas** (e.g., Beach/Sky illustration) that stays persistent beneath the UI.
2. Remove solid backgrounds from `Shell.kt` and individual screens.
3. Replace rigid `Column` blocks with `Box` layouts and `Modifier.offset` to allow UI elements to float and overlap naturally.

### Phase 2 — Top HUD & Resource Badges
1. Replace the standard `TopAppBar` or flat header with floating "Pill" badges.
2. The Top HUD should consist of heavily rounded, dark-transparent badges showing Energy, Gems, and Gold.
3. The User Profile/Level badge should overlap the edge of its container to create a sticker effect.
4. Implement `Modifier.cartoonBorder()` and `Modifier.cartoonShadow()` on these floating badges.

### Phase 3 — The Home / Action Stage (Merged Feel)
Target file: `app/src/main/java/com/yacoo/rpg/ui/screens/HomeScreen.kt`
1. The Hero must be positioned dynamically on the lower-third of the screen, standing on the "ground" of the background.
2. Add a massive, glowing Yellow/Orange `Start/Adventure` button hovering just above the bottom navigation bar.
3. Implement floating left/right side-menu icons for Quests, Shop, Events (placeholders).
4. Remove any giant blocky text panels; replace them with concise floating text with heavy stroke/shadow.

### Phase 4 — Bottom Navigation Redesign
Target file: `app/src/main/java/com/yacoo/rpg/ui/components/Shell.kt`
1. The bottom navigation bar must NOT be a flat Material rectangle.
2. It should be a rounded, distinct dark panel (e.g., dark navy `#1E1B2E`) with a thick border.
3. The active navigation icon should be significantly larger and "pop out" upward from the bar.
4. Integrate the new, perfectly background-removed 3D icons.

### Phase 5 — Gear / Inventory Pop-over Panel
Target file: `app/src/main/java/com/yacoo/rpg/ui/screens/EquipmentScreen.kt`
1. Instead of a new solid screen, the Gear screen should feel like a large frosted-glass or cream-colored rounded panel `DarkOverlayPanel` layered over the world background.
2. The hero paperdoll should be cleanly rendered without any white boxes (using perfectly clipped transparent PNGs).
3. Equipment slots (`PurpleItemCard`) should have a highly polished look with inset shadows, thick borders, and level badges overlapping the corner of the card.

### Phase 6 — Combat Screen Refinement
Target file: `app/src/main/java/com/yacoo/rpg/ui/screens/CombatScreen.kt`
1. Ensure the background seamlessly matches the Home screen (or is completely transparent to show the global background).
2. The Dice Panel must be restyled from a massive blocky console into a sleek, rounded, floating control board.
3. Health bars must be thick, pill-shaped, with black outlines, floating directly under the combatants.
4. Damage numbers should be huge, bouncy, and use a vibrant critical-hit color with a heavy stroke.

### Phase 7 — Gacha & Upgrade Redesign
1. **Upgrade**: Floating cards with glowing arrows. Use `UpgradeSuccessPanel` with a sunburst effect.
2. **Gacha**: Center the glowing Treasure Chest. Make the "Draw" buttons massive and bouncy.

---

## Compose Implementation Notes

### True Outline & Shadows
Since Compose lacks a true text stroke, heavily rely on:
- Layered text for outlines.
- `Modifier.border(3.dp, Color(0xFF1E1B2E), RoundedCornerShape(percent = 50))` for all UI elements.
- `.cartoonShadow()` custom modifier for depth.

### Seamless Transparency
- Make sure all loaded `GameIcon` PNGs have true alpha transparency (Alpha=0). Use `Modifier.clip()` only for cropping standard rectangular cards, NOT for fixing broken PNG backgrounds.

### Layout Z-Indexing
Use `Box` extensively:
```kotlin
Box(modifier = Modifier.fillMaxSize()) {
    // 1. Global Background
    AdventureBackground()
    
    // 2. Game Stage (Characters)
    Box(modifier = Modifier.align(Alignment.Center)) { ... }
    
    // 3. Floating HUDs
    TopResourceHud(modifier = Modifier.align(Alignment.TopCenter))
    
    // 4. Massive Action Button
    ChunkyActionButton(modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-80).dp))
}
```

---

## Verification Requirements

1. Verify that `rembg` (or similar script) has perfectly stripped all backgrounds from PNG assets in `res/drawable/`.
2. Build debug APK successfully.
3. The app layout must look dynamic and floating, not constrained to standard rows and columns.
4. The visual density and color saturation must match the Reference 3 image.
