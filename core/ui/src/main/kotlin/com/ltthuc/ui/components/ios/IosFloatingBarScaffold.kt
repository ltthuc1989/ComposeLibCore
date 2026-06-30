package com.ltthuc.ui.components.ios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * App-shell scaffold with a **floating bottom bar that content scrolls behind** — the iOS-26 style
 * where the tab bar / ad sit over edge-to-edge content rather than docked below it.
 *
 * Encapsulates the three pieces every clone otherwise hand-rolls in its `*App.kt`:
 * 1. an edge-to-edge [Box] painted with [background],
 * 2. the bottom bar overlaid at [Alignment.BottomCenter], whose height is measured and handed back to
 *    [content] as `bottomInset` (the measured bar height **plus** [contentBottomGap]) so each screen can
 *    pad the bottom of its scroll container — the last item rests a visible gap above the floating bar
 *    AND above the scrim fade, instead of touching the bar top and dissolving into the gradient,
 * 3. a vertical **gradient scrim** (transparent → [background]) of height `bottomInset + scrimFadeHeight`
 *    so scrolling content dissolves into the page color just above the bar (Compose has no backdrop
 *    blur; this is the lightweight iOS-tab-bar-fade approximation).
 *
 * The app supplies only the app-specific slots: [bottomBar] (the floating pill + ad/spacer) and
 * [content] (its `NavHost`, wiring `bottomInset` into each screen's bottom padding).
 *
 * @param background page color; also the gradient scrim's end color. Defaults to
 * `MaterialTheme.colorScheme.background`.
 * @param scrimFadeHeight extra fade lead-in above the measured bar height. Set `0.dp` to disable the
 * scrim entirely.
 * @param contentBottomGap breathing room folded into the `bottomInset` handed to [content], on top of
 * the measured bar height, so the last scroll item clears the bar and the scrim fade. Consumers should
 * apply the handed-back `bottomInset` directly and NOT add their own magic clearance constant.
 * @param bottomBar the floating bottom content, laid out in a bottom-aligned [Column]
 * (`horizontalAlignment = CenterHorizontally`, so a wrap-content pill centers and a full-width ad
 * spans). Overlaid on top of the scrim and content. The scaffold already applies
 * `navigationBarsPadding()` to this column — do NOT add it again inside the slot or its children, or
 * the system-nav inset doubles up and the bar floats far above the bottom edge.
 * @param content the main content; receives the measured `bottomInset` to apply as bottom padding
 * inside its scroll containers.
 */
@Composable
fun IosFloatingBarScaffold(
    modifier: Modifier = Modifier,
    background: Color = Color.Unspecified,
    scrimFadeHeight: Dp = 56.dp,
    contentBottomGap: Dp = 40.dp,
    bottomBar: @Composable ColumnScope.() -> Unit,
    content: @Composable (bottomInset: Dp) -> Unit,
) {
    val bg = background.takeOrElse { MaterialTheme.colorScheme.background }
    val density = LocalDensity.current
    var barHeight by remember { mutableStateOf(0.dp) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bg),
    ) {
        // Hand content the measured bar height plus the breathing gap, so its last item rests clear of
        // both the bar and the scrim fade (the scrim itself stays anchored to the raw bar height).
        content(barHeight + contentBottomGap)

        if (scrimFadeHeight > 0.dp) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(barHeight + scrimFadeHeight)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, bg))),
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                // Apply the system-nav inset ONCE here so consumer [bottomBar] slots never have to
                // (and can't double it up by adding navigationBarsPadding() per child — a real bug
                // that floated the bar far above the bottom edge). Placed before onSizeChanged so the
                // measured `bottomInset` includes the inset, keeping content's bottom padding correct.
                .navigationBarsPadding()
                .onSizeChanged { barHeight = with(density) { it.height.toDp() } },
            horizontalAlignment = Alignment.CenterHorizontally,
            content = bottomBar,
        )
    }
}
