package com.ltthuc.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ltthuc.ui.themes.Dimens

enum class AppCardVariant { Filled, Outlined, Elevated }

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    variant: AppCardVariant = AppCardVariant.Elevated,
    contentPadding: Dp = Dimens.spacingM,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(Dimens.radiusM)
    val containerColor = MaterialTheme.colorScheme.surface
    val elevation: Dp
    val border: BorderStroke?
    when (variant) {
        AppCardVariant.Filled -> {
            elevation = 0.dp
            border = null
        }
        AppCardVariant.Outlined -> {
            elevation = 0.dp
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
        }
        AppCardVariant.Elevated -> {
            elevation = 4.dp
            border = null
        }
    }

    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = border,
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
