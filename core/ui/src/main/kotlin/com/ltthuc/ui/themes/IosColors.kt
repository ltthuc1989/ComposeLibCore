package com.ltthuc.ui.themes

import androidx.compose.ui.graphics.Color

/**
 * iOS HIG system colors used as background hierarchy. Values are taken from Apple's
 * reference (light mode) — `Color(uiColor: .systemGroupedBackground)` etc. Hardcoded
 * because the iOS-styled components rely on these fixed shades; no automatic dark-mode
 * follow-through.
 */
object IosColors {
    /** `.systemGroupedBackground` — page background behind cards (light gray). */
    val systemGroupedBackground = Color(0xFFF2F2F7)

    /** `.secondarySystemGroupedBackground` — card surface (white). */
    val secondarySystemGroupedBackground = Color(0xFFFFFFFF)

    /** `.tertiarySystemGroupedBackground` — used inside cards for divider-like fills. */
    val tertiarySystemGroupedBackground = Color(0xFFF2F2F7)

    /** `.systemGray6` light = same hex as `systemGroupedBackground` per iOS HIG. */
    val systemGray6 = Color(0xFFF2F2F7)

    /** `.systemGray5` — slightly darker; visible over `systemGroupedBackground`. Use for segmented picker track. */
    val systemGray5 = Color(0xFFE5E5EA)

    /** `.systemGray4` — darker still, used for active fills / placeholders. */
    val systemGray4 = Color(0xFFD1D1D6)

    /** `.label` — primary foreground. */
    val labelPrimary = Color(0xFF000000)

    /** `.secondaryLabel` — gray subtitles. */
    val labelSecondary = Color(0x993C3C43)

    /** `.tertiaryLabel` — chevrons, faint icons. */
    val labelTertiary = Color(0x4D3C3C43)

    /** `.separator` — hairline divider. */
    val separator = Color(0x4D3C3C43)

    // SwiftUI semantic accent colors — iOS HIG defaults (light mode).
    val systemBlue = Color(0xFF007AFF)
    val systemCyan = Color(0xFF32ADE6)
    val systemOrange = Color(0xFFFF9500)
    val systemYellow = Color(0xFFFFCC00)
    val systemPurple = Color(0xFFAF52DE)
    val systemPink = Color(0xFFFF2D55)
    val systemGreen = Color(0xFF34C759)
    val systemMint = Color(0xFF00C7BE)
    val systemRed = Color(0xFFFF3B30)
}
