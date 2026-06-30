package com.ltthuc.settings_common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltthuc.settings_common.theme.palette

/**
 * Reusable visual primitives for an iOS-style grouped settings screen. All
 * colors read from [palette] (CompositionLocal), so wrapping the subtree in
 * `SettingsCommonTheme(colors = ...)` is how consumers customise.
 */
@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    horizontalPadding: androidx.compose.ui.unit.Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    val p = palette()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .clip(RoundedCornerShape(10.dp))
            .background(p.cardSurface),
    ) {
        content()
    }
}

@Composable
fun SettingsRow(
    label: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    labelColor: Color? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: @Composable () -> Unit = {},
) {
    val p = palette()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) {
            leading()
            Spacer(Modifier.width(12.dp))
        }
        Text(label, color = labelColor ?: p.labelPrimary, fontSize = 16.sp)
        Box(modifier = Modifier.weight(1f))
        trailing()
    }
}

@Composable
fun SettingsRowDivider(modifier: Modifier = Modifier) {
    val p = palette()
    HorizontalDivider(
        color = p.separator,
        modifier = modifier.padding(start = 16.dp),
    )
}

@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    val p = palette()
    Text(
        text = text,
        color = p.labelSecondary,
        fontSize = 13.sp,
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 6.dp),
    )
}

@Composable
fun SectionFooter(text: String, modifier: Modifier = Modifier) {
    val p = palette()
    Text(
        text = text,
        color = p.labelSecondary,
        fontSize = 13.sp,
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 6.dp),
    )
}
