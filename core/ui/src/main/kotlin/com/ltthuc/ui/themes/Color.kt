package com.ltthuc.ui.themes

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val Primary = Color(0xFFFFAB40)
private val PrimaryDark = Color(0xFFFFC372)
private val Secondary = Color(0xFF665EFF)
private val SecondaryDark = Color(0xFF9C95FF)

internal val LightColorSchemeM3 = lightColorScheme(
    primary = Primary,
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFFCC8A33),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Secondary,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF4D49CC),
    onSecondaryContainer = Color(0xFFFFFFFF),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1B1B1B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1B1B),
    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF),
)

internal val DarkColorSchemeM3 = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFFCC8A33),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = SecondaryDark,
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF7A73FF),
    onSecondaryContainer = Color(0xFF000000),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E6E6),
    surface = Color(0xFF1F1F1F),
    onSurface = Color(0xFFE6E6E6),
    error = Color(0xFFCF6679),
    onError = Color(0xFF000000),
)

enum class DecorColors(val color: Color) {
    BLUE(Color(0xFF1A95BB)),
    YELLOW(Color(0xFFC29515)),
    ORANGE(Color(0xFFCE5D18)),
    GREEN(Color(0xFF3A8F7D)),
    DARK_BLUE(Color(0xFF28669C)),
}
