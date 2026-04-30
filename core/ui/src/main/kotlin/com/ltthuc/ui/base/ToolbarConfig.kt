package com.ltthuc.ui.base

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class ToolbarAction(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit,
    val tint: Color? = null,
    val enabled: Boolean = true,
)

@Immutable
data class TabItem(
    val title: String,
    val icon: ImageVector? = null,
)
