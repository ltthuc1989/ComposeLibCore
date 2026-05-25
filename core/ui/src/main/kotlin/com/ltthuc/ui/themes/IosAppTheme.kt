package com.ltthuc.ui.themes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Five user-selectable accent themes — direct port of the Swift `AppTheme` enum.
 * SwiftUI semantic colors (`.blue/.cyan/.orange/.yellow/.purple/.pink/.green/.mint/.red`)
 * are mapped to their iOS HIG hex values via [IosColors].
 *
 * Renamed from `AppTheme` → `IosAppTheme` to make intent explicit (these are iOS-styled
 * theme variants, distinct from the lib's Material theme `ComposeTemplateTheme`).
 */
enum class IosAppTheme(
    val primary: Color,
    val accent: Color,
    val gradient: List<Color>,
) {
    Blue(IosColors.systemBlue, IosColors.systemCyan, listOf(IosColors.systemBlue, IosColors.systemCyan)),
    Orange(IosColors.systemOrange, IosColors.systemYellow, listOf(IosColors.systemOrange, IosColors.systemYellow)),
    Purple(IosColors.systemPurple, IosColors.systemPink, listOf(IosColors.systemPurple, IosColors.systemPink)),
    Green(IosColors.systemGreen, IosColors.systemMint, listOf(IosColors.systemGreen, IosColors.systemMint)),
    Pink(IosColors.systemPink, IosColors.systemRed, listOf(IosColors.systemPink, IosColors.systemRed));

    companion object {
        val Default = Blue
    }
}

/**
 * CompositionLocal that propagates the currently selected [IosAppTheme] down the composable
 * tree. Composables consuming the theme read `LocalIosAppTheme.current` instead of taking the
 * theme as a parameter — matches the iOS `@EnvironmentObject AppSettings.selectedTheme` pattern.
 */
val LocalIosAppTheme = compositionLocalOf { IosAppTheme.Default }

/** Convenience accessor: `currentIosAppTheme()` instead of `LocalIosAppTheme.current`. */
@Composable
@ReadOnlyComposable
fun currentIosAppTheme(): IosAppTheme = LocalIosAppTheme.current
