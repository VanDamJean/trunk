# Yacoo RPG — Full Art Direction Overhaul Brief (Legend of Slime / Mushroom Brave Style)

## Purpose

This document defines the target visual direction for a full UI/art overhaul of the Android Jetpack Compose RPG app. 

The provided screenshot (Reference 3) shows the exact desired direction: a **highly casual, continuous-screen Idle RPG style** (similar to Legend of Slime or Mushroom Brave). 
Instead of rigid boxes and "menu screens", the game should feel like one large living canvas where UI elements float above a dynamic background.

**Important:** Use the screenshot only as style reference. Do **not** copy exact characters, icons, layouts, logos, UI frames, fonts, item designs, or proprietary assets from the reference game.

---

## Target Style Summary

The app should move away from blocky, boxed-in layouts and transition into a **seamless, vibrant, floating-HUD Idle RPG**.

Key visual keywords:
- Full-screen continuous world (Lobby and Combat visually merged)
- Floating bubble UI (No hard rectangular boxes)
- Highly saturated, pastel-leaning vibrant colors (Cyan, Yellow, Soft Pink)
- Thick, soft, rounded black/dark-blue outlines
- Cute, extremely simplified mascot characters
- Top/Bottom floating HUDs instead of solid action bars
- Overlapping elements (e.g., icons breaking out of their borders)
- "Sticker" aesthetic for all icons and buttons

---

## Core Visual Rules

### 1. Seamless World Layout
- The background is **not** a solid color or a gradient. It must be a full-screen, vibrant 2D illustration (e.g., sky, ocean, sand).
- Characters (Hero, Pets, Monsters) stand directly on the ground of this background illustration.
- There are no "Combat" vs "Home" split backgrounds; the world should feel continuous.

### 2. Outline Language
All important UI elements need soft but bold outlines.
- Floating panels: 3-4dp dark blue/black outline.
- Icons: Thick sticker-style outlines.
- Characters: Very clean, thick cartoon outlines.

### 3. Floating UI (The HUD)
Do not use `Scaffold` with solid TopAppBar or BottomAppBar colors.
- **Top Bar**: Floating pill-shaped resource counters (Energy, Gems, Gold) that hover over the sky background. Profile pictures should stick out of the main ranking frame.
- **Side Menus**: Small, stacked floating bubble icons on the left/right edges for Events, Quests, Mail, etc.
- **Bottom Nav**: A distinct, rounded bottom area. The center "Battle/Start" button should be enormous, pill-shaped, and glowing, hovering just above the bottom menu.
- **Navigation Tabs**: Instead of standard Material tabs, use chunky, squircle/curved slots with vibrant icon stickers.

### 4. Color Palette
Shift from dark/muddy RPG colors to bright, energetic casual colors.
- **App Chrome (Outlines & Text Shadows)**: `#1E1B2E` (Very dark navy, softer than pure black).
- **Primary Buttons (Start/Action)**: Brilliant Yellow `#FFD13B` with Orange gradients `#FFA122`.
- **Background Tones**: Light Cyan sky `#99E5FF`, Sandy beige ground `#EED9B3`.
- **Resource Accents**: Magenta/Pink for Gems `#FF3B9E`, Bright Cyan for Energy `#1EE3FF`.
- **Panels**: Cream/White or semi-transparent frosted glass over the background.

### 5. Typography
- Highly legible, heavily stylized game font.
- Use `FontWeight.Black`.
- Every major text label MUST have a dark outline (stroke) or heavy drop shadow for readability against the illustrated background.

### 6. Character/Creature Art
- **Hero**: Extremely simple, cute mascot (e.g., a tiny cloud, a blob, or a chibi animal).
- **Monsters**: Cute but expressive.
- **Animations**: Lots of bouncing, squash-and-stretch, and floating.

---

## Screen-by-Screen Target

### Home / Adventure (Merged) Screen
The primary screen of the game.
- **Background**: Full screen beach/grassland illustration.
- **Center**: The hero (and pets) walking or standing.
- **Top HUD**: Character Name, Level, Power (combat power), Energy, Gems, Gold as floating pills.
- **Left/Right Edges**: Stack of event icons (gift boxes, shop, daily quests).
- **Lower Middle**: Massive Yellow "START" button, overlapping the scene.
- **Bottom**: The navigation bar.

### Gear / Inventory Screen
Instead of completely hiding the game world, the inventory should be a large panel that slides up or pops over the background.
- **Panel**: Frosted or cream-colored rounded rectangle floating in the center.
- **Layout**: Hero paperdoll at the top, equipped slots arranged in a circle or left/right.
- **Grid**: Inventory grid below.
- Icons should be highly polished and chunky.

### Shop / Gacha Screen
- Full screen illustration changes to a "treasure cave" or "magic shop".
- Massive glowing treasure chest in the center.
- Chunky "Draw 1x" and "Draw 10x" buttons below it.

### Combat Screen (If separated)
- If combat is a separate screen, it should look identical to the Home screen but with monsters spawning on the right side.
- **Dice UI**: The dice panel should NOT be a massive blocky grey rectangle. It should be a stylized, rounded floating board. The dice themselves should look like physical, shiny 3D objects with thick outlines.

---

## Acceptance Criteria

The art overhaul is successful only if:
1. The app no longer looks like it uses rigid Material Design columns/rows.
2. The UI looks like it is **floating over a beautiful, living background**.
3. The color palette is overwhelmingly vibrant, casual, and energetic.
4. Heavy use of sticker-outlines and drop shadows on all text and icons.
5. The "Lobby" and "Action" areas feel perfectly integrated.
