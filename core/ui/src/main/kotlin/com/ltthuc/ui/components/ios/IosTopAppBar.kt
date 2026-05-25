package com.ltthuc.ui.components.ios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltthuc.ui.base.ToolbarAction
import com.ltthuc.ui.themes.iosColors

/**
 * iOS `UINavigationBar` — flat 44dp nav bar with hairline bottom border.
 *
 * Mirrors the slot pattern of [com.ltthuc.ui.base.AppToolbarSmall] but with iOS-flat
 * styling: no Material elevation / tonal surface, palette-aware background + content
 * color, 17sp SemiBold title, 22dp icons in 44dp hit targets, opacity-flash tap feedback
 * via [iosClickable] (no ripple).
 *
 * ### Usage
 *
 * ```
 * IosTopAppBar(
 *     title = "Mulch Calculator",
 *     onBack = { nav.popBackStack() },               // shows ArrowBack
 *     actions = listOf(
 *         ToolbarAction(Icons.Filled.Settings, "Settings", onClick = { nav.navigate("settings") }),
 *     ),
 *     centerTitle = true,                            // iOS calculator-style title
 * )
 * ```
 *
 * @param onBack when non-null, [navigationIcon] is shown at the leading edge and tapped to
 * invoke this callback. When null, no nav icon renders.
 * @param actions trailing icons (0..N). Use [com.ltthuc.ui.base.ToolbarAction] to bundle
 * icon + contentDescription + onClick.
 * @param centerTitle iOS settings sheets use a leading-aligned title (`false`); iOS
 * calculator-style screens use a centered title (`true`).
 */
@Composable
fun IosTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    navigationIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    actions: List<ToolbarAction> = emptyList(),
    centerTitle: Boolean = true,
    backgroundColor: Color = iosColors().secondarySystemGroupedBackground,
    contentColor: Color = iosColors().labelPrimary,
    separatorColor: Color = iosColors().separator,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .windowInsetsPadding(WindowInsets.statusBars)
            .drawBehind {
                // Hairline bottom border — iOS UINavigationBar separator.
                val strokePx = 0.5.dp.toPx()
                drawLine(
                    color = separatorColor,
                    start = Offset(0f, size.height - strokePx / 2f),
                    end = Offset(size.width, size.height - strokePx / 2f),
                    strokeWidth = strokePx,
                )
            },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(44.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Leading slot — back icon or 16dp inset.
            if (onBack != null) {
                IosIconButton(
                    icon = navigationIcon,
                    contentDescription = "Back",
                    tint = contentColor,
                    onClick = onBack,
                )
            } else {
                Spacer(Modifier.width(16.dp))
            }

            if (centerTitle) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    modifier = Modifier.padding(start = if (onBack == null) 0.dp else 4.dp),
                )
                Spacer(Modifier.weight(1f))
            }

            // Trailing slot — zero or more actions, plus a 16dp inset when empty.
            if (actions.isEmpty()) {
                Spacer(Modifier.width(16.dp))
            } else {
                actions.forEach { action ->
                    IosIconButton(
                        icon = action.icon,
                        contentDescription = action.contentDescription,
                        tint = action.tint ?: contentColor,
                        enabled = action.enabled,
                        onClick = action.onClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun IosIconButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .iosClickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
    }
}
