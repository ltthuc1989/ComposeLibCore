package com.ltthuc.ui.components.ios

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltthuc.ui.themes.iosColors

/**
 * iOS `.pickerStyle(.segmented)` equivalent — capsule track with white selected pill.
 *
 * Visual spec (iOS 13+ native):
 * - Track: `tertiarySystemFill` (12% gray-blue), CAPSULE shape (`RoundedCornerShape(percent = 50)`),
 *   2dp inner padding so the selected pill is inset from the track edge
 * - Selected: white pill, capsule shape, ~1dp shadow, 13sp SemiBold black
 * - Unselected: transparent (gray track shows through), 13sp Regular gray
 *
 * IMPLEMENTATION NOTES:
 * - Use `Box + Modifier.shadow + .background + .clickable` for the selected pill — NOT
 *   `Surface(onClick = ...)`, which enforces `minimumInteractiveComponentSize = 48.dp` and
 *   makes the pill far taller than iOS native (visible as huge top/bottom gray gap).
 * - Both branches use the same `Box` + `contentAlignment = Center` so text centers identically;
 *   asymmetric structure (Surface vs Box) breaks vertical alignment of unselected text.
 *
 * Palette-aware: text colors track [com.ltthuc.ui.themes.IosColorScheme]; the gray track
 * (rgba(118,118,128,0.12)) composites correctly over both light and dark backgrounds so it
 * stays as a literal value.
 */
@Composable
fun <T> IosSegmented(
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
) {
    val palette = iosColors()
    val capsule = RoundedCornerShape(percent = 50)
    Row(
        modifier = modifier
            .fillMaxWidth()
            // iOS `tertiarySystemFill`: rgba(118, 118, 128, 0.12) — composites correctly over light/dark.
            .background(Color(0x1F767680), capsule)
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            val baseModifier = Modifier.weight(1f)
            val pillModifier = if (isSelected) {
                baseModifier
                    .shadow(elevation = 1.dp, shape = capsule)
                    .background(palette.secondarySystemGroupedBackground, capsule)
            } else {
                baseModifier
            }
            Box(
                modifier = pillModifier.clickable { onSelected(option) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label(option),
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) palette.labelPrimary else palette.labelSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 12.dp),
                )
            }
        }
    }
}
