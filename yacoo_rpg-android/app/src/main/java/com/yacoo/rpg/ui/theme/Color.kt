package com.yacoo.rpg.ui.theme

import androidx.compose.ui.graphics.Color

// ── Overhauled Yacoo RPG Color Palette (Dark Pixel-Fantasy Style) ────────

// -- Charcoal Border & Core Text --
val ColorInk         = Color(0xFF090612)
val ColorInkStrong   = Color(0xFF000000)
val ColorInkSoft     = Color(0xFF1C1635)
val ColorMuted       = Color(0xFF8E8A9F)

// -- Panel Colors (Replaces Panel Brown) --
val ColorPanelBrown       = Color(0xFF2D214A)
val ColorPanelBrownDark   = Color(0xFF211A3A)
val ColorPanelBrownLight  = Color(0xFF3A2A60)

// -- Content Container Backings (Dark Themes) --
val ColorCard        = Color(0xFF211A3A)
val ColorCardSolid   = Color(0xFF211A3A)
val ColorCream       = Color(0xFF2D214A)
val ColorCreamWarm   = Color(0xFF3A2A60)
val ColorCreamSoft   = Color(0xFF4D3A78)
val ColorDisabled    = Color(0xFF4D3A78)

// -- Parchment (Dark Purple Theme) --
val ColorParchment       = Color(0xFF3A2A60)
val ColorParchmentLight  = Color(0xFF4D3A78)
val ColorParchmentDark   = Color(0xFF211A3A)

// -- Primary (CTA Yellow/Orange for Highlights & Buttons) --
val ColorPrimaryTop    = Color(0xFFFFCC4D)
val ColorPrimaryBottom = Color(0xFFF6B43B)
val ColorPrimaryShadow = Color(0xFFCC8A00)

// -- Secondary (Accent Purple Theme) --
val ColorSecondaryTop    = Color(0xFFB75CFF)
val ColorSecondaryBottom = Color(0xFF8E44FF)
val ColorSecondaryShadow = Color(0xFF4D3A78)

// -- Warning / Danger (Red/Pink/Ribbon Red) --
val ColorDangerTop    = Color(0xFFFF6533)
val ColorDangerBottom = Color(0xFFD7392F)
val ColorDangerShadow = Color(0xFF8A211B)

// -- HP Bar --
val ColorHpFillStart = Color(0xFFFF6533)
val ColorHpFillEnd   = Color(0xFFD7392F)
val ColorHpTrack     = Color(0xFF090612)

// -- Dice Held --
val ColorHeld        = Color(0xFF4D3A78)
val ColorHeldBorder  = Color(0xFFB75CFF)

val ColorRedDot      = Color(0xFFFF3B3B)

// ── Background / Surface Tokens ──────────────────────────────────────
val ColorScreenBg       = Color(0xFF0B0B18)   // Deep dark navy
val ColorScreenBgAlt    = Color(0xFF090612)   // Black/lowest level
val ColorChrome         = Color(0xFF121025)   // Semi-dark panel
val ColorSurfaceCard    = Color(0xFF1C1635)   // Slate purple card background
val ColorSurfaceElevated = Color(0xFF2D214A)
val ColorSurfacePanel   = Color(0xFF211A3A)   // Standard dark panel
val ColorOverlayDim     = Color(0xDD090612)   // Dark overlay

// ── Resource-specific Colors ──────────────────────────────────────
val ColorGemPurple      = Color(0xFFB75CFF)
val ColorStaminaCyan    = Color(0xFF45E8FF)

// ── Outline / Border Tokens ──────────────────────────────────────────
val ColorOutline        = Color(0xFF090612)   // Dark outline
val ColorOutlineSubtle  = Color(0xFF4D3A78)
val ColorOutlineStrong  = Color(0xFF000000)

// ── Semantic Text Tokens ─────────────────────────────────────────────
val ColorTextPrimary    = Color(0xFFFFFDF9)
val ColorTextSecondary  = Color(0xFFB8B5C0)
val ColorTextTertiary   = Color(0xFF8E8A9F)
val ColorTextOnPrimary  = Color(0xFF090612)   // Dark text on bright yellow buttons
val ColorTextOnDark     = Color(0xFFFFFDF9)
val ColorTextDanger     = Color(0xFFFF6533)

// ── Rarity Color Tokens (High Saturation Neons) ─────────────────────
val RarityCommon       = Color(0xFF8E8A9F)     // Gray
val RarityUncommon     = Color(0xFF4CA52E)     // Green
val RarityRare         = Color(0xFF2196F3)     // Blue
val RarityEpic         = Color(0xFFB75CFF)     // Purple
val RarityLegendary    = Color(0xFFFF9E22)     // Orange Gold

// Rarity background tints (slightly softer neon tints for cards)
val RarityCommonBg     = Color(0xFF2D214A)
val RarityUncommonBg   = Color(0xFF1F3C18)
val RarityRareBg       = Color(0xFF152A3C)
val RarityEpicBg       = Color(0xFF2F1D3C)
val RarityLegendaryBg  = Color(0xFF3C2C15)

// Mythic rarity
val RarityMythic       = Color(0xFFFF1744)     // Red
val RarityMythicBg     = Color(0xFF3C181E)

// Item Purple
val ColorItemPurple       = Color(0xFFB75CFF)
val ColorItemPurpleDark   = Color(0xFF8E44FF)
val ColorItemPurpleLight  = Color(0xFFD08DFF)

// ── HUD / Navigation Tokens ──────────────────────────────────────────
val ColorHudBg         = Color(0xAA0B0B18)
val ColorNavBg         = Color(0xFF0F0A1A)
val ColorNavIndicator  = Color(0xFFB75CFF)
val ColorNavActive     = Color(0xFFFFFDF9)
val ColorNavInactive   = Color(0xFF8E8A9F)

// ── Card Tokens ──────────────────────────────────────────────────────
val ColorCardBorder    = ColorOutline
val ColorCardShadow    = Color(0xFF090612)
val ColorCardHighlight = Color(0xFF3A2A60)

// ── Button Gradient Tokens ───────────────────────────────────────────
val ColorBtnPrimaryStart    = ColorPrimaryTop
val ColorBtnPrimaryEnd      = ColorPrimaryBottom
val ColorBtnPrimaryShadow   = ColorPrimaryShadow
val ColorBtnSecondaryStart  = ColorSecondaryTop
val ColorBtnSecondaryEnd    = ColorSecondaryBottom
val ColorBtnSecondaryShadow = ColorSecondaryShadow
val ColorBtnDangerStart     = ColorDangerTop
val ColorBtnDangerEnd       = ColorDangerBottom

// ── Slot / Equipment Tokens ──────────────────────────────────────────
val ColorSlotEmpty     = Color(0xFF1C1635)
val ColorSlotEquipped  = ColorSecondaryBottom
val ColorSlotLocked    = Color(0x668E8A9F)

// ── Reward / Banner Tokens ───────────────────────────────────────────
val ColorRewardCommon   = Color(0xFF8E8A9F)
val ColorRewardRare     = Color(0xFF2196F3)
val ColorRewardEpic     = Color(0xFFB75CFF)
val ColorVictoryBanner  = ColorSecondaryBottom
val ColorDefeatBanner   = ColorDangerBottom

val ArtHeroLight  = Color(0xFFFFCC4D)
val ArtHeroDeep   = Color(0xFFF6B43B)
val ArtLeafLight  = Color(0xFF4CA52E)
val ArtLeafDeep   = Color(0xFF1F3C18)
val ArtMossLight  = Color(0xFF152A3C)
val ArtMossDeep   = Color(0xFF090612)
val ArtBark       = Color(0xFF2D214A)
val ArtCream      = Color(0xFFFFFDF9)
val ArtSky        = Color(0xFF121025)
val ArtHillBack   = Color(0xFF1C1635)
val ArtHillFront  = Color(0xFF121025)
val ArtGround     = Color(0xFF0B0B18)
val ArtPath       = Color(0xFF211A3A)
val ArtSun        = Color(0xFFB75CFF)
val ArtOutline    = Color(0xFF090612)
val ArtLine       = Color(0xFF090612)
