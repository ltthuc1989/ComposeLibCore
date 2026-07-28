package com.ltthuc.ui.components.ios

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * iOS `Slider(value:in:step:)` analogue.
 *
 * Material 3's [Slider] cannot be recoloured into the iOS look, because two of its defaults are
 * structural rather than cosmetic:
 *  1. passing `steps = N` (to honour an iOS `step:`) makes M3 draw **tick marks** along the
 *     track — iOS draws none; and
 *  2. the M3 1.3.x default thumb is a 4dp x 44dp vertical **handle bar**
 *     (`SliderTokens.HandleWidth`/`HandleHeight`), not iOS's round knob.
 *
 * `SliderDefaults.colors(...)` only recolours, so both are fixed here via the `thumb`/`track`
 * slot overloads: the slider stays continuous (`steps = 0`, hence tickless) and [step] is applied
 * by snapping the value in [onValueChange] instead.
 *
 * @param step iOS `step:`. Values snap to the nearest multiple measured from `valueRange.start`.
 *   Pass 0f for a fully continuous slider.
 * @param tint active-track colour — iOS `.tint(...)`.
 * @param inactiveTrackColor the remaining track; defaults to a neutral gray.
 * @param thumbColor iOS's knob is white in both appearances; override only if the design says so.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun IosSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    step: Float = 0f,
    tint: Color = Color.Unspecified,
    inactiveTrackColor: Color = Color.Unspecified,
    thumbColor: Color = Color.White,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
) {
    val activeColor = tint.takeOrElse { MaterialTheme.colorScheme.primary }
    val inactiveColor = inactiveTrackColor.takeOrElse {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.20f)
    }
    val thumbRadiusPx = with(LocalDensity.current) { THUMB_DIAMETER.toPx() } / 2f
    val span = (valueRange.endInclusive - valueRange.start).takeIf { it > 0f } ?: 1f
    val fraction = ((value - valueRange.start) / span).coerceIn(0f, 1f)

    Slider(
        value = value,
        onValueChange = { raw -> onValueChange(snapToStep(raw, valueRange, step)) },
        onValueChangeFinished = onValueChangeFinished,
        valueRange = valueRange,
        // MUST stay 0: any positive value makes M3 render tick marks, which iOS never shows.
        // Stepping is handled by `snapToStep` above.
        steps = 0,
        enabled = enabled,
        modifier = modifier.height(TOUCH_HEIGHT),
        colors = SliderDefaults.colors(
            activeTrackColor = activeColor,
            inactiveTrackColor = inactiveColor,
            thumbColor = thumbColor,
        ),
        thumb = {
            Box(
                Modifier
                    .size(THUMB_DIAMETER)
                    .shadow(elevation = 3.dp, shape = CircleShape)
                    .background(thumbColor, CircleShape),
            )
        },
        track = {
            // `SliderDefaults.Track` exposes no thickness parameter and defaults to a 16dp
            // token, so the thin iOS track is drawn by hand. The fill fraction is derived from
            // [value] rather than `SliderState.coercedValueAsFraction`, which is library-internal.
            Canvas(
                Modifier
                    .fillMaxWidth()
                    .height(TRACK_THICKNESS),
            ) {
                val corner = CornerRadius(size.height / 2f, size.height / 2f)
                drawRoundRect(color = inactiveColor, cornerRadius = corner)
                // Inset the active fill by the thumb radius so its end lands on the thumb's
                // centre instead of overshooting past it.
                val usable = (size.width - thumbRadiusPx * 2f).coerceAtLeast(0f)
                val activeWidth = (thumbRadiusPx + usable * fraction).coerceAtMost(size.width)
                if (activeWidth > 0f) {
                    drawRoundRect(
                        color = activeColor,
                        topLeft = Offset.Zero,
                        size = Size(activeWidth, size.height),
                        cornerRadius = corner,
                    )
                }
            }
        },
    )
}

/** iOS knob diameter. */
private val THUMB_DIAMETER: Dp = 20.dp

/** iOS draws a thin rounded line, not M3's 16dp bar. */
private val TRACK_THICKNESS: Dp = 6.dp

/**
 * The raw slider reserves a ~48dp touch band; iOS's row is tighter. Constrained here so callers
 * don't each have to remember it.
 */
private val TOUCH_HEIGHT: Dp = 28.dp

/**
 * Snaps [raw] to the nearest multiple of [step] measured from `range.start`, then clamps into
 * [range]. Returns [raw] clamped when [step] is non-positive (continuous slider).
 */
internal fun snapToStep(
    raw: Float,
    range: ClosedFloatingPointRange<Float>,
    step: Float,
): Float {
    if (step <= 0f) return raw.coerceIn(range.start, range.endInclusive)
    val steps = ((raw - range.start) / step).roundToInt()
    return (range.start + steps * step).coerceIn(range.start, range.endInclusive)
}
