package com.ltthuc.ui.themes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Dark-mode-aware iOS HIG system colors used by every `com.ltthuc.ui.components.ios.*`
 * widget. Pairs with [LocalIosColorScheme] + [IosColorSchemeProvider].
 *
 * The default value of the [LocalIosColorScheme] is [Light] — values match the legacy
 * static [IosColors] object hex-for-hex, so consumers that never install a provider keep
 * the pre-existing light-only behavior (full backward compat with v1.0.0 callers).
 *
 * Apps that want dark-mode follow-through install the provider near the root, typically
 * wiring it to `isSystemInDarkTheme()`:
 *
 * ```
 * IosColorSchemeProvider(useDarkMode = isSystemInDarkTheme()) {
 *     // every IosCard / IosTextField / etc. below here now palette-tracks
 * }
 * ```
 *
 * `onAccent` is intentionally white in both modes — iOS HIG keeps "control-on-tint"
 * foregrounds white regardless of dark mode (selected segmented pill, primary button
 * label). Don't bind it to [labelPrimary], which flips with dark.
 */
data class IosColorScheme(
    val systemGroupedBackground: Color,
    val secondarySystemGroupedBackground: Color,
    val tertiarySystemGroupedBackground: Color,
    val systemGray5: Color,
    val systemGray6: Color,
    val labelPrimary: Color,
    val labelSecondary: Color,
    val labelTertiary: Color,
    val separator: Color,
    val onAccent: Color,
) {
    companion object {
        val Light = IosColorScheme(
            systemGroupedBackground = Color(0xFFF2F2F7),
            secondarySystemGroupedBackground = Color(0xFFFFFFFF),
            tertiarySystemGroupedBackground = Color(0xFFF2F2F7),
            systemGray5 = Color(0xFFE5E5EA),
            systemGray6 = Color(0xFFF2F2F7),
            labelPrimary = Color(0xFF000000),
            labelSecondary = Color(0x993C3C43),
            labelTertiary = Color(0x4D3C3C43),
            separator = Color(0x4D3C3C43),
            onAccent = Color.White,
        )

        val Dark = IosColorScheme(
            systemGroupedBackground = Color(0xFF000000),
            secondarySystemGroupedBackground = Color(0xFF1C1C1E),
            tertiarySystemGroupedBackground = Color(0xFF2C2C2E),
            systemGray5 = Color(0xFF2C2C2E),
            systemGray6 = Color(0xFF1C1C1E),
            labelPrimary = Color(0xFFFFFFFF),
            labelSecondary = Color(0x99EBEBF5),
            labelTertiary = Color(0x4DEBEBF5),
            separator = Color(0x8C545458),
            onAccent = Color.White,
        )
    }
}

/**
 * CompositionLocal exposing the active [IosColorScheme]. Static because the palette only
 * changes on a system day/night flip, which triggers Activity recreation — no per-recomposition
 * tracking needed.
 */
val LocalIosColorScheme = staticCompositionLocalOf { IosColorScheme.Light }

/**
 * Installs the right [IosColorScheme] based on [useDarkMode]. Wire this near the app root,
 * typically with `useDarkMode = isSystemInDarkTheme()`.
 */
@Composable
fun IosColorSchemeProvider(useDarkMode: Boolean, content: @Composable () -> Unit) {
    val scheme = if (useDarkMode) IosColorScheme.Dark else IosColorScheme.Light
    CompositionLocalProvider(LocalIosColorScheme provides scheme, content = content)
}

/** Terse accessor — `iosColors().labelPrimary` reads better than `LocalIosColorScheme.current.labelPrimary`. */
@Composable
@ReadOnlyComposable
fun iosColors(): IosColorScheme = LocalIosColorScheme.current
