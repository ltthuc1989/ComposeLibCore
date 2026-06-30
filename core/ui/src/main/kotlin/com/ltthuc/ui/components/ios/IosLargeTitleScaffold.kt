package com.ltthuc.ui.components.ios

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Height of the pinned inline nav bar row (below the status bar) — iOS `UINavigationBar`. */
val IosLargeTitleBarHeight: Dp = 44.dp

/**
 * iOS `.navigationBarTitleDisplayMode(.large)` — a large left-aligned title that lives at the top
 * of the scroll content and slides away as the user scrolls, while a small **centered** inline
 * title and a hairline bottom separator **fade in** on a pinned top bar.
 *
 * Unlike [com.ltthuc.ui.base.AppToolbarLarge] (which keeps the title left-aligned and merely shrinks
 * it), this matches the iOS cross-fade: the inline title is centered and its alpha — together with the
 * pinned bar background and separator — is driven by [collapseProgress].
 *
 * The widget does **not** own scrolling. The caller renders the large `34sp` title as the first child
 * of its own scroll container (so it scrolls naturally) and computes [collapseProgress] from its scroll
 * state via [rememberCollapseProgress]. Give that large title a leading `Modifier.statusBarsPadding()`
 * so at rest it sits below the status bar and cross-fades in place with the inline title.
 *
 * Colors default to `Color.Unspecified` and resolve to `MaterialTheme.colorScheme.*`, so each consumer
 * passes its own iOS palette and dark-mode follows through (see CLAUDE.md §6.3.1).
 *
 * @param collapseProgress 0f = fully expanded (bar transparent, no inline title), 1f = fully collapsed
 * (opaque bar, centered inline title + separator). The caller computes this from its scroll state.
 * @param background drawn on the pinned bar; should equal the page background so the large title
 * disappears cleanly behind it.
 * @param onBack when non-null, a leading back arrow is shown in the pinned bar.
 * @param actions trailing slot in the pinned bar (e.g. an Edit / clear action).
 * @param content the scrollable area, **including** the large `34sp` title as its first child.
 */
@Composable
fun IosLargeTitleScaffold(
    title: String,
    collapseProgress: Float,
    modifier: Modifier = Modifier,
    background: Color = Color.Unspecified,
    titleColor: Color = Color.Unspecified,
    separatorColor: Color = Color.Unspecified,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    val barColor = background.takeOrElse { MaterialTheme.colorScheme.surface }
    val inlineColor = titleColor.takeOrElse { MaterialTheme.colorScheme.onSurface }
    val lineColor = separatorColor.takeOrElse { MaterialTheme.colorScheme.outlineVariant }
    val progress = collapseProgress.coerceIn(0f, 1f)

    Box(modifier = modifier.fillMaxSize()) {
        content()

        // Pinned top bar overlay — fades from transparent to opaque as the title collapses.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(barColor.copy(alpha = progress))
                .statusBarsPadding()
                .drawBehind {
                    val strokePx = 0.5.dp.toPx()
                    drawLine(
                        color = lineColor.copy(alpha = lineColor.alpha * progress),
                        start = Offset(0f, size.height - strokePx / 2f),
                        end = Offset(size.width, size.height - strokePx / 2f),
                        strokeWidth = strokePx,
                    )
                },
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(IosLargeTitleBarHeight),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Leading slot — back arrow or inset. Min 44dp keeps the title centered.
                Box(modifier = Modifier.widthIn(min = 44.dp), contentAlignment = Alignment.CenterStart) {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = inlineColor,
                            )
                        }
                    } else {
                        Spacer(Modifier.width(16.dp))
                    }
                }

                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = inlineColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).alpha(progress),
                )

                // Trailing slot — actions or inset. Min 44dp balances the leading slot.
                Row(
                    modifier = Modifier.widthIn(min = 44.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions,
                )
            }
        }
    }
}

/**
 * Collapse progress (0f..1f) for a [ScrollState]-backed screen (`Column.verticalScroll`).
 * Ramps from 0 to 1 over the first [thresholdDp] of scroll — roughly the large title's height.
 */
@Composable
fun rememberCollapseProgress(scrollState: ScrollState, thresholdDp: Dp = 44.dp): Float {
    val thresholdPx = with(LocalDensity.current) { thresholdDp.toPx() }
    return if (thresholdPx <= 0f) 1f else (scrollState.value / thresholdPx).coerceIn(0f, 1f)
}

/**
 * Collapse progress (0f..1f) for a [LazyListState]-backed screen (`LazyColumn`). Returns 1f once any
 * item beyond the first has scrolled into view; otherwise ramps over the first item's [thresholdDp].
 */
@Composable
fun rememberCollapseProgress(listState: LazyListState, thresholdDp: Dp = 44.dp): Float {
    val thresholdPx = with(LocalDensity.current) { thresholdDp.toPx() }
    val progress by remember(listState, thresholdPx) {
        derivedStateOf {
            if (listState.firstVisibleItemIndex > 0 || thresholdPx <= 0f) {
                1f
            } else {
                (listState.firstVisibleItemScrollOffset / thresholdPx).coerceIn(0f, 1f)
            }
        }
    }
    return progress
}
