package com.ltthuc.ui.components.ios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltthuc.ui.themes.LocalIosAppTheme
import com.ltthuc.ui.themes.iosColors

/**
 * Custom iOS "Modern-style" pill row — generic over option type.
 *
 * Differs from [IosSegmented] (which renders the iOS-Classic capsule with a white selected
 * pill, matching native `Picker(.segmented)`). Use [IosPillRow] when the iOS source uses
 * the custom modern-style pill pattern: text-only labels in a rectangular-rounded row,
 * accent-fill on the selected pill, white text on the selected pill.
 *
 * Visual spec:
 * - HStack of equal-width text pills, 2dp inner spacing.
 * - Track: 9dp corner radius, `iosColors().systemGray6` background, 3dp inner padding.
 * - Pill: 7dp corner radius, `accentColor.copy(alpha = 0.95f)` background when selected,
 *   transparent when unselected.
 * - Text: 14sp, SemiBold when selected else Medium; `iosColors().onAccent` (white) when
 *   selected, `iosColors().labelPrimary` when unselected.
 * - 9dp vertical padding inside each pill, fillMaxWidth.
 * - Tap feedback via [iosClickable] — no Material ripple.
 *
 * Palette-aware via [com.ltthuc.ui.themes.IosColorScheme]; install
 * [com.ltthuc.ui.themes.IosColorSchemeProvider] near the root for dark-mode follow-through.
 *
 * ### Usage
 *
 * ```
 * IosPillRow(
 *     options = ShapeType.entries,
 *     selected = currentShape,
 *     onSelected = viewModel::setShape,
 *     label = { stringResource(it.labelRes) },
 * )
 * ```
 */
@Composable
fun <T> IosPillRow(
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    label: @Composable (T) -> String,
    modifier: Modifier = Modifier,
    accentColor: Color = LocalIosAppTheme.current.primary,
) {
    val palette = iosColors()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.systemGray6, RoundedCornerShape(9.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = if (isSelected) accentColor.copy(alpha = 0.95f) else Color.Transparent,
                        shape = RoundedCornerShape(7.dp),
                    )
                    .iosClickable { onSelected(option) }
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label(option),
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isSelected) palette.onAccent else palette.labelPrimary,
                )
            }
        }
    }
}
