package com.ltthuc.ui.components.ios

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltthuc.ui.themes.currentIosAppTheme
import com.ltthuc.ui.themes.iosColors

/**
 * iOS `.textFieldStyle(.roundedBorder)` equivalent:
 * - hairline gray border (1dp), `RoundedCornerShape(6.dp)`
 * - compact height (auto by font size + 6dp vertical padding)
 * - right-aligned text by default
 * - 17sp `.body` font
 * - placeholder shown when empty (tertiary label color)
 *
 * Built on `BasicTextField` (NOT `OutlinedTextField`) to avoid Material 3's heavy
 * floating-label / 56dp min-height styling.
 *
 * Palette-aware via [com.ltthuc.ui.themes.IosColorScheme]. Install
 * [com.ltthuc.ui.themes.IosColorSchemeProvider] near the app root for dark-mode follow-through.
 */
@Composable
fun IosTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "0",
    keyboardType: KeyboardType = KeyboardType.Decimal,
    textAlign: TextAlign = TextAlign.End,
) {
    val theme = currentIosAppTheme()
    val palette = iosColors()
    val textStyle = TextStyle(
        fontSize = 17.sp,
        textAlign = textAlign,
        color = palette.labelPrimary,
    )
    Box(
        modifier = modifier
            .background(palette.secondarySystemGroupedBackground, RoundedCornerShape(6.dp))
            .border(1.dp, palette.separator, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = textStyle,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            cursorBrush = SolidColor(theme.primary),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = textStyle.copy(color = palette.labelTertiary),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    inner()
                }
            },
        )
    }
}
