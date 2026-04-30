package com.ltthuc.ui.extensions

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.then(
    Modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick,
    ),
)

fun Modifier.debounceClickable(
    intervalMs: Long = 500,
    onClick: () -> Unit,
): Modifier = composed {
    val flow = remember { MutableSharedFlow<Unit>(extraBufferCapacity = 1) }
    val source = remember { MutableInteractionSource() }
    val indication = LocalIndication.current
    LaunchedEffect(Unit) {
        var lastEmittedAt = 0L
        flow.collectLatest {
            val now = System.currentTimeMillis()
            if (now - lastEmittedAt >= intervalMs) {
                lastEmittedAt = now
                onClick()
            }
        }
    }
    this.then(
        Modifier.clickable(
            interactionSource = source,
            indication = indication,
            onClick = { flow.tryEmit(Unit) },
        ),
    )
}
