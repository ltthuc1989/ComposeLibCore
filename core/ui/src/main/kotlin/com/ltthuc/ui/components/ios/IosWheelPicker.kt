package com.ltthuc.ui.components.ios

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * iOS `Picker(selection:) { … }.pickerStyle(.wheel)` analogue.
 *
 * Neither Compose Foundation nor Material 3 ships a wheel/drum picker, so this builds one from a
 * snapping [LazyColumn]: exactly [visibleItems] rows are shown, the centre row is the selection,
 * and rows fade with distance from the centre to suggest the iOS drum curvature.
 *
 * Selection is reported only once the list has **settled** — dragging across values does not emit
 * an event per row, matching iOS, which commits on rest.
 *
 * @param items the full option list, in display order.
 * @param selected the currently selected item. Changing it from outside animates the wheel to
 *   that row, so the picker stays a controlled component.
 * @param label rendered text for an item.
 * @param visibleItems odd number of rows shown at once; iOS's compact wheel shows 3.
 */
@Composable
fun <T> IosWheelPicker(
    items: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 34.dp,
    visibleItems: Int = 3,
    textColor: Color = Color.Unspecified,
    selectionBackground: Color = Color.Unspecified,
) {
    if (items.isEmpty()) return

    val resolvedText = textColor.takeOrElse { MaterialTheme.colorScheme.onSurface }
    val resolvedBand = selectionBackground.takeOrElse {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
    }

    // One blank row above and below, so item 0 can sit in the centre slot.
    val edgeRows = visibleItems / 2
    val selectedIndex = items.indexOf(selected).coerceAtLeast(0)
    // Keyed on [items]: when the option list is REPLACED (e.g. a unit switch swaps kilograms for
    // pounds) the old scroll offset is meaningless. Without the key the state survives, the wheel
    // stays parked on the previous row index, and the settle-commit below writes that stale row
    // back over the caller's value — silently changing the selection.
    val listState = remember(items) { LazyListState(firstVisibleItemIndex = selectedIndex) }
    val itemHeightPx = with(LocalDensity.current) { itemHeight.toPx() }

    // derivedStateOf so this recomposes only when the CENTRED ROW changes, not on every scroll
    // pixel — reading `firstVisibleItemScrollOffset` directly would recompose ~60-120x/s during
    // a fling (CLAUDE.md 6.16.8).
    val centeredIndex by remember(items.size, itemHeightPx) {
        derivedStateOf {
            val base = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            val index = if (offset > itemHeightPx / 2f) base + 1 else base
            index.coerceIn(0, items.lastIndex)
        }
    }

    // Commit only once scrolling stops (iOS commits on rest, not mid-drag), and only after a real
    // user drag: `isScrollInProgress` is already false on first composition, so committing on
    // every falling edge would emit a selection the user never made.
    LaunchedEffect(listState, items) {
        var userScrolled = false
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { inProgress ->
                if (inProgress) {
                    userScrolled = true
                } else if (userScrolled) {
                    items.getOrNull(centeredIndex)
                        ?.let { item -> if (item != selected) onSelected(item) }
                }
            }
    }

    // Follow external changes (e.g. the unit system switched and reset the value).
    LaunchedEffect(selected, items) {
        val target = items.indexOf(selected)
        if (target >= 0 && target != centeredIndex && !listState.isScrollInProgress) {
            listState.animateScrollToItem(target)
        }
    }

    Box(
        modifier = modifier.height(itemHeight * visibleItems),
        contentAlignment = Alignment.Center,
    ) {
        // iOS's selection band sits behind the centre row.
        Box(
            Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(resolvedBand, RoundedCornerShape(8.dp)),
        )
        LazyColumn(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
            contentPadding = PaddingValues(vertical = itemHeight * edgeRows),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(items.size) { index ->
                val distance = abs(index - centeredIndex)
                val isCentre = distance == 0
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        // Rows fade with distance from the centre, standing in for the drum's
                        // perspective falloff.
                        .alpha(if (isCentre) 1f else (1f - distance * 0.32f).coerceAtLeast(0.25f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label(items[index]),
                        color = resolvedText,
                        fontSize = if (isCentre) 20.sp else 17.sp,
                        fontWeight = if (isCentre) FontWeight.SemiBold else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
