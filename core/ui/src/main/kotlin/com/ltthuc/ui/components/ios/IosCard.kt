package com.ltthuc.ui.components.ios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ltthuc.ui.themes.iosColors

/**
 * Direct port of iOS card pattern:
 *
 *     RoundedRectangle(cornerRadius: 16)
 *         .fill(Color(uiColor: .secondarySystemGroupedBackground))
 *         .shadow(color: Color.black.opacity(0.05), radius: 8, x: 0, y: 4)
 *
 * Use [iosCardModifier] on any container that should look like an iOS card. The default
 * [background] tracks the active [com.ltthuc.ui.themes.IosColorScheme] so dark-mode is
 * automatic once an [com.ltthuc.ui.themes.IosColorSchemeProvider] is installed at the root.
 */
@Composable
@ReadOnlyComposable
fun Modifier.iosCardModifier(
    cornerRadius: Dp = 16.dp,
    shadowElevation: Dp = 8.dp,
    fillMaxWidth: Boolean = true,
    background: Color = iosColors().secondarySystemGroupedBackground,
): Modifier = this
    .let { if (fillMaxWidth) it.fillMaxWidth() else it }
    .shadow(
        elevation = shadowElevation,
        shape = RoundedCornerShape(cornerRadius),
        ambientColor = Color.Black.copy(alpha = 0.05f),
        spotColor = Color.Black.copy(alpha = 0.05f),
    )
    .background(background, RoundedCornerShape(cornerRadius))
