package com.ltthuc.ui.components.ios

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Drop-in iOS `.navigationBarTitleDisplayMode(.large)` screen. Prefer this over the low-level
 * [IosLargeTitleScaffold] for every large-title screen.
 *
 * It owns the scroll container, places the `34sp` large title as the **first child** of the scroll
 * body (so it slides away naturally), and drives the pinned inline-title cross-fade from the same
 * [scrollState] via [rememberCollapseProgress]. That closes the trap that makes the collapse silently
 * fail: hand-rolling a scaffold with a static `Text` title as a *sibling* of a separate scroll
 * container. Here the caller never touches the scroll/title wiring, so it can't be forgotten.
 *
 * Put your screen body in [content] — a [ColumnScope] inside the scroll container, below the title.
 * Own your inner padding / bottom inset there (e.g. horizontal 16dp, and a bottom spacer clearing a
 * floating tab bar).
 *
 * Surface family (see CLAUDE.md §6.3.2): pass `systemBackground` for non-grouped screens
 * (`NavigationStack { VStack }`), or the grouped page bg for `Form`/`List` screens. [onBack] adds a
 * leading back arrow (inline mode); [actions] fills the pinned bar's trailing slot.
 */
@Composable
fun IosLargeTitleScreen(
    title: String,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    background: Color = Color.Unspecified,
    titleColor: Color = Color.Unspecified,
    separatorColor: Color = Color.Unspecified,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    val pageColor = background.takeOrElse { MaterialTheme.colorScheme.surface }
    val largeTitleColor = titleColor.takeOrElse { MaterialTheme.colorScheme.onSurface }
    IosLargeTitleScaffold(
        title = title,
        collapseProgress = rememberCollapseProgress(scrollState),
        modifier = modifier,
        background = background,
        titleColor = titleColor,
        separatorColor = separatorColor,
        onBack = onBack,
        actions = actions,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(pageColor)
                .verticalScroll(scrollState),
        ) {
            // The large title MUST be the first child of the scroll body so it physically scrolls
            // away while the pinned inline title cross-fades in.
            Text(
                text = title,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        // Clear the pinned 44dp bar when a back button occupies it.
                        top = if (onBack != null) IosLargeTitleBarHeight else 8.dp,
                        bottom = 4.dp,
                    ),
                color = largeTitleColor,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
            )
            content()
        }
    }
}
