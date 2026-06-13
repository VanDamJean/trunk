package com.yacoo.rpg.ui.theme

import androidx.compose.ui.graphics.Color

// ── Overhauled Yacoo RPG Color Palette (Cartoon Casual Style V2) ────────

// -- Charcoal Border & Core Text --
val ColorInk         = Color(0xFF1C1A1F)
val ColorInkStrong   = Color(0xFF000000)
val ColorInkSoft     = Color(0xFF3C3A42)
val ColorMuted       = Color(0xFF6B6973)

// -- Panel Brown (Section Headers, Dividers) --
val ColorPanelBrown       = Color(0xFF8B5E4C)
val ColorPanelBrownDark   = Color(0xFF6F4738)
val ColorPanelBrownLight  = Color(0xFFB9846F)

// -- Content Container Backings (Light Comic Paper) --
val ColorCard        = Color(0xFFFFFDF9)
val ColorCardSolid   = Color(0xFFFFFDF9)
val ColorCream       = Color(0xFFFDF9F0)
val ColorCreamWarm   = Color(0xFFFFF3D1)
val ColorCreamSoft   = Color(0xFFFFEAA8)
val ColorDisabled    = Color(0xFFBDBAB3)

// -- Parchment Cream (Extended) --
val ColorParchment       = Color(0xFFFFE8B8)
val ColorParchmentLight  = Color(0xFFFFF3D3)
val ColorParchmentDark   = Color(0xFFF4D8A4)

// -- Primary (Green Theme / Healing / Confirm) --
val ColorPrimaryTop    = Color(0xFF8DE855)
val ColorPrimaryBottom = Color(0xFF46B92C)
val ColorPrimaryShadow = Color(0xFF2B9337)

// -- Secondary (Gold / Action / Chapter Start) --
val ColorSecondaryTop    = Color(0xFFFFD96F)
val ColorSecondaryBottom = Color(0xFFFFAE00)
val ColorSecondaryShadow = Color(0xFFCC8A00)

// -- Warning / Danger (Red/Pink) --
val ColorDangerTop    = Color(0xFFFF4E7A)
val ColorDangerBottom = Color(0xFFD92C55)
val ColorDangerShadow = Color(0xFFA62140)

// -- HP Bar --
val ColorHpFillStart = Color(0xFF52D669)
val ColorHpFillEnd   = Color(0xFF8DE855)
val ColorHpTrack     = Color(0xFF3C3A42)

// -- Dice Held --
val ColorHeld        = Color(0xFFE4FBC4)
val ColorHeldBorder  = Color(0xFF46B92C)

val ColorRedDot      = Color(0xFFFF3B3B)

// ── Background / Surface Tokens ──────────────────────────────────────
val ColorScreenBg       = Color(0xFF0F0A1A)   // Deep Dark Purple (Capybara Go ref)
val ColorScreenBgAlt    = Color(0xFF0E0D14)   // Lowest background level
val ColorChrome         = Color(0xFF1A1425)   // Panel chrome (purple tint)
val ColorSurfaceCard    = Color(0xFFFFFDF9)   // Clean cream white card background
val ColorSurfaceElevated = Color(0xFFFFFFFF)
val ColorSurfacePanel   = Color(0xFFF5F1E9)   // Inner panel light gray-beige
val ColorOverlayDim     = Color(0xCC0E0D14)   // Dark overlay (high opacity)

// ── Resource-specific Colors ──────────────────────────────────────
val ColorGemPurple      = Color(0xFFE261FF)
val ColorStaminaCyan    = Color(0xFF5CE5FF)

// ── Outline / Border Tokens ──────────────────────────────────────────
val ColorOutline        = Color(0xFF1C1A1F)   // Fixed thick outline
val ColorOutlineSubtle  = Color(0xFFE6E2D8)
val ColorOutlineStrong  = Color(0xFF000000)

// ── Semantic Text Tokens ─────────────────────────────────────────────
val ColorTextPrimary    = Color(0xFF1C1A1F)
val ColorTextSecondary  = Color(0xFF56545C)
val ColorTextTertiary   = Color(0xFF8A8890)
val ColorTextOnPrimary  = Color(0xFFFFFDF9)
val ColorTextOnDark     = Color(0xFFFFFDF9)
val ColorTextDanger     = Color(0xFFD92C55)

// ── Rarity Color Tokens (High Saturation Neons) ─────────────────────
val RarityCommon       = Color(0xFF9E9EA8)     // Gray
val RarityUncommon     = Color(0xFF4CA52E)     // Green
val RarityRare         = Color(0xFF2196F3)     // Blue (brighter, Capybara Go ref)
val RarityEpic         = Color(0xFF9A42E8)     // Purple
val RarityLegendary    = Color(0xFFFF9E22)     // Orange Gold

// Rarity background tints (slightly softer neon tints for cards)
val RarityCommonBg     = Color(0xFFECECEF)
val RarityUncommonBg   = Color(0xFFE4FCD8)
val RarityRareBg       = Color(0xFFDCF0FF)
val RarityEpicBg       = Color(0xFFF4E6FF)
val RarityLegendaryBg  = Color(0xFFFFF0DC)

// Mythic rarity (Capybara Go high-tier)
val RarityMythic       = Color(0xFFFF1744)     // Red
val RarityMythicBg     = Color(0xFFFFE0E6)

// Item Purple (For Item Cards)
val ColorItemPurple       = Color(0xFF9A42E8)
val ColorItemPurpleDark   = Color(0xFF7E2CCD)
val ColorItemPurpleLight  = Color(0xFFB75CFF)

// ── HUD / Navigation Tokens ──────────────────────────────────────────
val ColorHudBg         = Color(0x800F0A1A)     // Semi-transparent dark HUD
val ColorNavBg         = Color(0xCC1A1425)     // Semi-transparent bottom nav
val ColorNavIndicator  = Color(0xFFFFAE00)     // Golden active accent
val ColorNavActive     = Color(0xFFFFAE00)
val ColorNavInactive   = Color(0xFF8A8890)

// ── Card Tokens ──────────────────────────────────────────────────────
val ColorCardBorder    = ColorOutline
val ColorCardShadow    = Color(0xFF1C1A1F)
val ColorCardHighlight = Color(0xFFFFF3D1)

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
val ColorSlotEmpty     = Color(0xFFECECEF)
val ColorSlotEquipped  = ColorPrimaryBottom
val ColorSlotLocked    = Color(0x669E9EA8)

// ── Reward / Banner Tokens ───────────────────────────────────────────
val ColorRewardCommon   = Color(0xFF9E9EA8)
val ColorRewardRare     = Color(0xFF1A78B8)
val ColorRewardEpic     = Color(0xFF9A42E8)
val ColorVictoryBanner  = ColorPrimaryBottom
val ColorDefeatBanner   = ColorDangerBottom

// ── Art colors (preserved or adjusted for cartoon contrast) ──────────
val ArtHeroLight  = Color(0xFFFFD96F)
val ArtHeroDeep   = Color(0xFFFFAE00)
val ArtLeafLight  = Color(0xFF8DE855)
val ArtLeafDeep   = Color(0xFF46B92C)
val ArtMossLight  = Color(0xFF4BD35B)
val ArtMossDeep   = Color(0xFF2B9337)
val ArtBark       = Color(0xFF8B5E4C)
val ArtCream      = Color(0xFFFFFDF9)
val ArtSky        = Color(0xFF8BE4FF)
val ArtHillBack   = Color(0xFF46B92C)
val ArtHillFront  = Color(0xFF2B9337)
val ArtGround     = Color(0xFFDCA74E)
val ArtPath       = Color(0xFFFFF3D3)
val ArtSun        = Color(0xFFFFEA6C)
val ArtOutline    = Color(0xFF1C1A1F)
val ArtLine       = Color(0xFF1C1A1F)
