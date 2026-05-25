package com.ltthuc.ui.components.ios

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltthuc.ui.themes.currentIosAppTheme
import com.ltthuc.ui.themes.iosColors

/**
 * iOS UISearchBar / `.searchable` style — capsule track with magnifier icon, rounded.
 *
 * Visual spec (iOS-style elevated search bar):
 * - Capsule shape: `RoundedCornerShape(percent = 50)` for fully rounded ends
 * - White / dark-gray background (`secondarySystemGroupedBackground`) — raised over the gray page
 * - Subtle shadow elevation 4dp with 0.06 black alpha — gives the 3D / floating look
 * - Magnifier icon left, `iosColors().labelSecondary` tint
 * - Inline text field (no border, transparent), 16sp body
 * - Clear button right when text non-empty (small "X" circle)
 * - Padding: 12dp horizontal × 10dp vertical inside the capsule
 *
 * Palette-aware via [com.ltthuc.ui.themes.IosColorScheme]. Material 3's `SearchBar` is too
 * heavyweight (collapsible, full-screen overlay); this lightweight composable matches the
 * iOS inline appearance for in-page search input.
 */
@Composable
fun IosSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val theme = currentIosAppTheme()
    val palette = iosColors()
    val capsule = RoundedCornerShape(percent = 50)
    Row(
        modifier = modifier
            .fillMaxWidth()
            // Elevated 3D look — shadow first (rendered outside the shape), then fill on top.
            .shadow(
                elevation = 4.dp,
                shape = capsule,
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.06f),
            )
            .background(palette.secondarySystemGroupedBackground, capsule)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = palette.labelSecondary,
            modifier = Modifier.size(18.dp),
        )
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.labelSecondary,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = palette.labelPrimary,
                ),
                cursorBrush = SolidColor(theme.primary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (value.isNotEmpty()) {
            Icon(
                imageVector = Icons.Filled.Cancel,
                contentDescription = "Clear",
                tint = palette.labelSecondary,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onValueChange("") },
            )
        }
    }
}
