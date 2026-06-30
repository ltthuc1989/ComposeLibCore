package com.ltthuc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * iOS Stepper analogue: two joined ± buttons in a pill, with a divider in the
 * middle. Compose has no built-in `Stepper`; mirror iOS SwiftUI's `Stepper`
 * shape and bounded-stepping behaviour.
 *
 * - [value]: current integer value
 * - [range]: inclusive bounds. The − or + button is disabled when at the
 *   matching boundary (matches iOS, which dims the half instead of clamping
 *   silently).
 * - [step]: amount added/removed per tap; defaults to 1.
 * - [onValueChange]: invoked with the new value when a button is tapped.
 *
 * Colors are parameter-injected so consumers in dark mode pass their own palette
 * tokens (see compose-template CLAUDE.md §6.3.1 — template-ui defaults are
 * light-only). When [tint] is unspecified the component falls back to
 * `MaterialTheme.colorScheme.surfaceVariant` / `onSurface`.
 */
@Composable
fun IosStepper(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    step: Int = 1,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    disabledContentColor: Color = Color.Unspecified,
    dividerColor: Color = Color.Unspecified,
) {
    val resolvedBackground = if (backgroundColor == Color.Unspecified) {
        MaterialTheme.colorScheme.surfaceVariant
    } else backgroundColor
    val resolvedContent = if (contentColor == Color.Unspecified) {
        MaterialTheme.colorScheme.onSurface
    } else contentColor
    val resolvedDisabled = if (disabledContentColor == Color.Unspecified) {
        resolvedContent.copy(alpha = 0.30f)
    } else disabledContentColor
    val resolvedDivider = if (dividerColor == Color.Unspecified) {
        resolvedContent.copy(alpha = 0.15f)
    } else dividerColor

    val canDecrement = value - step >= range.first
    val canIncrement = value + step <= range.last

    Row(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(resolvedBackground),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepperHalf(
            icon = Icons.Filled.Remove,
            enabled = canDecrement,
            contentColor = resolvedContent,
            disabledColor = resolvedDisabled,
            onClick = { if (canDecrement) onValueChange(value - step) },
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(resolvedDivider),
        )
        StepperHalf(
            icon = Icons.Filled.Add,
            enabled = canIncrement,
            contentColor = resolvedContent,
            disabledColor = resolvedDisabled,
            onClick = { if (canIncrement) onValueChange(value + step) },
        )
    }
}

@Composable
private fun StepperHalf(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    contentColor: Color,
    disabledColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 32.dp)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (enabled) contentColor else disabledColor,
            modifier = Modifier.size(20.dp),
        )
    }
}
