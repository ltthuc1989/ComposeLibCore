package com.ltthuc.settings_common.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Color tokens consumed by every composable in this library. Defaults track iOS
 * HIG light-mode (see [iosLightColors]). Apps that need dark mode (or a custom
 * brand palette) supply their own [SettingsCommonColors] and wrap the relevant
 * content in [SettingsCommonTheme].
 *
 * `staticCompositionLocalOf` chosen because palette only changes on day/night
 * config change (full activity recreation), not at composition granularity.
 */
data class SettingsCommonColors(
    val pageBackground: Color,
    val cardSurface: Color,
    val labelPrimary: Color,
    val labelSecondary: Color,
    val labelTertiary: Color,
    val separator: Color,

    // Accent for "Contact & Feedback" row label + interactive iOS-blue accents.
    val accentInteractive: Color,

    // Always white (or near-white) — text drawn ON TOP of the accent fill.
    // iOS HIG keeps this constant regardless of dark mode.
    val onAccent: Color,

    // Brand-vivid orange for "Go Ad-Free!" CTA + AutoAwesome sparkle icon.
    // Hard-coded the same in light/dark to preserve brand pop on both modes.
    val accentOrange: Color,

    val systemRed: Color,
    val systemGreen: Color,
)

val iosLightColors = SettingsCommonColors(
    pageBackground = Color(0xFFF2F2F7),
    cardSurface = Color(0xFFFFFFFF),
    labelPrimary = Color(0xFF000000),
    labelSecondary = Color(0x993C3C43),
    labelTertiary = Color(0x4D3C3C43),
    separator = Color(0x493C3C43),
    accentInteractive = Color(0xFF007AFF),
    onAccent = Color(0xFFFFFFFF),
    accentOrange = Color(0xFFFF9500),
    systemRed = Color(0xFFFF3B30),
    systemGreen = Color(0xFF34C759),
)

val LocalSettingsCommonColors = staticCompositionLocalOf { iosLightColors }

@Composable
fun palette(): SettingsCommonColors = LocalSettingsCommonColors.current

/**
 * Wrap any subtree that uses settings-common composables. Default param is the
 * current provider value, so nesting is safe.
 */
@Composable
fun SettingsCommonTheme(
    colors: SettingsCommonColors = LocalSettingsCommonColors.current,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalSettingsCommonColors provides colors, content = content)
}
