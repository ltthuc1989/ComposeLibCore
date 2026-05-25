package com.ltthuc.ui.components.ios

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * iOS-style "press flash" tap feedback — brief opacity dip, no Material ripple.
 *
 * Drop-in replacement for `Modifier.clickable { … }` at any iOS-styled call site (button,
 * row, chip, card). On press, the composable's alpha animates from 1.0 → 0.6 over 120ms;
 * on release it animates back. This mirrors the iOS `UIControl` press behavior closely
 * (no expanding ripple, no haptic — apps that want haptics should wire them separately).
 *
 * ### Usage
 *
 * ```
 * Box(
 *     modifier = Modifier
 *         .iosClickable { viewModel.onTap() }
 *         .padding(12.dp)
 * ) { Text("Tap me") }
 * ```
 *
 * @param enabled when false, clicks are ignored and no alpha animation runs.
 * @param onClick invoked on release (standard `clickable` semantics).
 */
@Composable
fun Modifier.iosClickable(
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val alpha by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.6f else 1f,
        animationSpec = tween(durationMillis = 120, easing = LinearOutSlowInEasing),
        label = "iosClickablePressAlpha",
    )
    this
        .graphicsLayer { this.alpha = alpha }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick,
        )
}
