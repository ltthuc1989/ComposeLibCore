package com.dvm.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

@Composable
fun Image(
    data: Any?,
    modifier: Modifier = Modifier,
    loading: @Composable () -> Unit = { Loading() },
    error: @Composable () -> Unit = { ErrorImage() }
) {
    val painter = rememberAsyncImagePainter(data)

    Box(modifier.fillMaxSize()) {
        androidx.compose.foundation.Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        when (painter.state) {
            is AsyncImagePainter.State.Loading -> loading()
            is AsyncImagePainter.State.Error -> error()
            else -> { /* ignore */ }
        }
    }
}

@Composable
fun Loading() {
    BoxWithConstraints {
        val infiniteTransition = rememberInfiniteTransition()
        val progress = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500),
                repeatMode = RepeatMode.Restart
            )
        ).value

        val width = constraints.maxWidth.toFloat() + 600
        val height = constraints.maxHeight.toFloat() + 400
        val startOffset = Offset(
            x = width * progress - 600,
            y = height * progress - 400
        )
        val endOffset = Offset(
            x = width * progress,
            y = height * progress
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.1f),
                            Color.White,
                            Color.Black.copy(alpha = 0.1f),
                        ),
                        start = startOffset,
                        end = endOffset
                    )
                )
        )
    }
}