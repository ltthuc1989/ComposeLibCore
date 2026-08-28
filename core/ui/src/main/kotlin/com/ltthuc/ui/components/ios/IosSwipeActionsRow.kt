package com.ltthuc.ui.components.ios

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/**
 * One revealed action in an [IosSwipeActionsRow], mirroring a SwiftUI
 * `Button { … } label: { Label(title, systemImage:) }` inside `.swipeActions`.
 */
data class IosSwipeAction(
    val label: String,
    val icon: ImageVector,
    /** The action's fill colour — SwiftUI's `.tint(…)`, or `.destructive` red. */
    val tint: Color,
    val onClick: () -> Unit,
)

/**
 * A list row whose horizontal drag reveals actions on either edge, the Compose equivalent of
 * SwiftUI's `.swipeActions(edge: .leading / .trailing)`.
 *
 * Material 3 has no equivalent. `SwipeToDismissBox` looks close but is a different interaction: it
 * *dismisses* the row past a threshold rather than revealing buttons that stay put until one is
 * tapped, and it carries a single background rather than N discrete actions. Modelling two leading
 * actions plus a trailing one on top of it means fighting its dismissal semantics.
 *
 * Behaviour, matching iOS:
 *  - drag from the leading edge reveals [leadingActions], from the trailing edge [trailingActions];
 *  - releasing past half the revealed width settles open, otherwise it springs shut;
 *  - tapping any action closes the row first, then invokes it;
 *  - only one row in a list stays open: the host holds the open row's [id] in [openId], and every
 *    row whose [id] differs shuts itself.
 *
 * The row does NOT consume vertical drags, so it nests inside a `LazyColumn` without stealing its
 * scroll.
 *
 * @param id identifies this row; compared against [openId].
 * @param openId the row currently open, or null. A row closes when this is neither null nor its own
 * [id]. Comparing identities rather than reacting to a bare "something changed" signal is the whole
 * point: a signal derived from the open row necessarily changes at the moment a row opens, so the
 * row would immediately shut itself again.
 * @param onOpened invoked with [id] when the row settles open, so the host can record it. Do NOT
 * try to detect this from the caller with a `pointerInput` on the row content: a nested
 * `detectHorizontalDragGestures` consumes the drag before this component's `draggable` sees it,
 * and the row then never opens at all.
 * @param actionWidth width of each revealed action button.
 */
@Composable
fun IosSwipeActionsRow(
    modifier: Modifier = Modifier,
    leadingActions: List<IosSwipeAction> = emptyList(),
    trailingActions: List<IosSwipeAction> = emptyList(),
    id: Any,
    openId: Any? = null,
    actionWidth: Dp = 76.dp,
    onOpened: (Any) -> Unit = {},
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

    val leadingWidthPx = with(density) { (actionWidth * leadingActions.size).toPx() }
    val trailingWidthPx = with(density) { (actionWidth * trailingActions.size).toPx() }

    LaunchedEffect(openId) { if (openId != id) offsetX.animateTo(0f) }
    // A row whose actions disappeared (e.g. duplicate became unavailable) must not stay stuck open
    // showing nothing.
    LaunchedEffect(leadingActions.size, trailingActions.size) {
        if (offsetX.value > 0f && leadingActions.isEmpty()) offsetX.animateTo(0f)
        if (offsetX.value < 0f && trailingActions.isEmpty()) offsetX.animateTo(0f)
    }

    fun close() = scope.launch { offsetX.animateTo(0f) }

    Box(modifier = modifier.fillMaxWidth()) {
        // Revealed actions sit BEHIND the content and are only visible where it has slid away.
        if (leadingActions.isNotEmpty()) {
            SwipeActionStrip(
                actions = leadingActions,
                actionWidth = actionWidth,
                arrangement = Arrangement.Start,
                onInvoke = { close(); it.onClick() },
                modifier = Modifier.matchParentSize(),
            )
        }
        if (trailingActions.isNotEmpty()) {
            SwipeActionStrip(
                actions = trailingActions,
                actionWidth = actionWidth,
                arrangement = Arrangement.End,
                onInvoke = { close(); it.onClick() },
                modifier = Modifier.matchParentSize(),
            )
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .fillMaxWidth()
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        scope.launch {
                            // Clamped to what each side can actually reveal, so an edge with no
                            // actions cannot be dragged open at all.
                            val next = (offsetX.value + delta)
                                .coerceIn(-trailingWidthPx, leadingWidthPx)
                            offsetX.snapTo(next)
                        }
                    },
                    onDragStopped = {
                        val open = if (offsetX.value > 0f) leadingWidthPx else trailingWidthPx
                        val settled = when {
                            open == 0f -> 0f
                            abs(offsetX.value) < open * SETTLE_FRACTION -> 0f
                            offsetX.value > 0f -> leadingWidthPx
                            else -> -trailingWidthPx
                        }
                        offsetX.animateTo(settled)
                        if (settled != 0f) onOpened(id)
                    },
                ),
        ) {
            content()
        }
    }
}

@Composable
private fun SwipeActionStrip(
    actions: List<IosSwipeAction>,
    actionWidth: Dp,
    arrangement: Arrangement.Horizontal,
    onInvoke: (IosSwipeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    // matchParentSize() from the caller spans the whole row; the arrangement is what pins the
    // coloured buttons to the correct edge.
    Row(modifier = modifier, horizontalArrangement = arrangement) {
        actions.forEach { action ->
            Column(
                modifier = Modifier
                    .width(actionWidth)
                    .fillMaxHeight()
                    .background(action.tint)
                    .iosClickable { onInvoke(action) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.label,
                    tint = Color.White,
                    modifier = Modifier.size(ICON_SIZE),
                )
                Text(
                    text = action.label,
                    color = Color.White,
                    fontSize = LABEL_SIZE,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

/** Past this fraction of the revealed width, releasing settles open rather than shut. */
private const val SETTLE_FRACTION = 0.5f
private val ICON_SIZE = 20.dp
private val LABEL_SIZE = 11.sp
