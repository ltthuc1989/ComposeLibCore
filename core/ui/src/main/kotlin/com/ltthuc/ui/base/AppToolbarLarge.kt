package com.ltthuc.ui.base

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import com.ltthuc.ui.themes.Dimens

@Composable
fun AppToolbarLarge(
    title: String,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: List<ToolbarAction> = emptyList(),
    expandedHeight: Dp = Dimens.toolbarLargeExpandedHeight,
    collapsedHeight: Dp = Dimens.toolbarHeight,
) {
    val density = LocalDensity.current
    val expandedPx = with(density) { (expandedHeight - collapsedHeight).toPx() }
    val collapseFraction = if (expandedPx <= 0f) {
        1f
    } else {
        (scrollState.value / expandedPx).coerceIn(0f, 1f)
    }
    val currentHeight = lerp(expandedHeight, collapsedHeight, collapseFraction)
    val titleSizeSp = lerp(
        MaterialTheme.typography.headlineSmall.fontSize.value,
        MaterialTheme.typography.titleMedium.fontSize.value,
        collapseFraction,
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(currentHeight)
            .windowInsetsPadding(WindowInsets.statusBars),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shadowElevation = lerp(0.dp, 4.dp, collapseFraction),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(collapsedHeight),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                } else {
                    Spacer(Modifier.padding(start = Dimens.spacingS))
                }
                Spacer(Modifier.weight(1f))
                actions.forEach { action ->
                    IconButton(onClick = action.onClick, enabled = action.enabled) {
                        Icon(
                            action.icon,
                            contentDescription = action.contentDescription,
                            tint = action.tint ?: LocalContentColor.current,
                        )
                    }
                }
            }
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = TextUnit(titleSizeSp, MaterialTheme.typography.headlineSmall.fontSize.type),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = Dimens.spacingM, bottom = Dimens.spacingM),
            )
        }
    }
}
