package com.ltthuc.ui.components.ios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.ltthuc.ui.themes.LocalIosAppTheme
import com.ltthuc.ui.themes.iosColors

/**
 * iOS `UIMenu`-style popover picker. Generic over the option type `T`.
 *
 * The trigger row renders `label` in the [accentColor] with a small expand chevron;
 * on tap, a popup floats over the trigger showing each option as a 44dp row. The
 * current selection gets a trailing checkmark. Tapping a row commits + dismisses;
 * tapping outside dismisses without committing.
 *
 * Visual spec (matches iOS UIMenu):
 * - Trigger: accent-colored label + 16dp `UnfoldMore` chevron, right-aligned by default.
 * - Popup surface: palette `secondarySystemGroupedBackground`, 13dp corner radius,
 *   12dp shadow elevation with 15% black ambient/spot color.
 * - Row: 44dp min-height, 16dp horizontal padding, label leading + check trailing.
 * - Divider between rows: 0.5dp `palette.separator`, inset 16dp from leading edge.
 *
 * ### Usage
 *
 * ```
 * IosMenuPicker(
 *     label = currentCurrency.symbol,
 *     options = Currency.entries,
 *     selected = currentCurrency,
 *     onSelected = viewModel::setCurrency,
 *     optionLabel = { "${it.name} (${it.symbol})" },
 * )
 * ```
 */
@Composable
fun <T> IosMenuPicker(
    label: String,
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    optionLabel: @Composable (T) -> String,
    modifier: Modifier = Modifier,
    accentColor: Color = LocalIosAppTheme.current.primary,
) {
    val palette = iosColors()
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        // Trigger row — accent-colored label + chevron, tap toggles popup.
        Row(
            modifier = Modifier
                .iosClickable { expanded = !expanded }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                fontSize = 17.sp,
                color = accentColor,
            )
            Icon(
                imageVector = Icons.Filled.UnfoldMore,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp),
            )
        }

        if (expanded) {
            Popup(
                alignment = Alignment.TopEnd,
                offset = IntOffset(0, with(androidx.compose.ui.platform.LocalDensity.current) { 32.dp.roundToPx() }),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true),
            ) {
                IosMenuPopupSurface(palette = palette) {
                    options.forEachIndexed { index, option ->
                        if (index > 0) {
                            HorizontalDivider(
                                color = palette.separator,
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(start = 16.dp),
                            )
                        }
                        IosMenuPickerRow(
                            label = optionLabel(option),
                            isSelected = option == selected,
                            accentColor = accentColor,
                            labelColor = palette.labelPrimary,
                            onClick = {
                                onSelected(option)
                                expanded = false
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IosMenuPopupSurface(
    palette: com.ltthuc.ui.themes.IosColorScheme,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(13.dp)
    Column(
        modifier = Modifier
            .widthIn(min = 200.dp, max = 320.dp)
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = Color.Black.copy(alpha = 0.15f),
            )
            .background(palette.secondarySystemGroupedBackground, shape),
    ) {
        content()
    }
}

@Composable
private fun IosMenuPickerRow(
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    labelColor: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .iosClickable(onClick = onClick)
            .defaultMinSize(minHeight = 44.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = labelColor,
            modifier = Modifier.weight(1f),
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
