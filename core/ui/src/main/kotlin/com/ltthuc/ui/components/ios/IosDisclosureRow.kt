package com.ltthuc.ui.components.ios

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltthuc.ui.themes.iosColors

/**
 * An iOS list disclosure row: optional [leading] slot, a wrapping title, and a trailing chevron.
 *
 * This is the `NavigationLink`-style row every iOS `List` / `Form` is built from:
 *
 *     HStack(spacing: 14) {
 *         <leading>
 *         Text(title).font(.body).foregroundColor(.primary)
 *         Spacer()
 *         Image(systemName: "chevron.right")
 *             .font(.system(size: 13, weight: .semibold))
 *             .foregroundColor(Color(UIColor.tertiaryLabel))
 *     }
 *
 * Pair it with [IosInsetGroupedSection], which supplies the card background and the inset hairline
 * separators an iOS `.insetGrouped` list draws between rows.
 *
 * Tap feedback is [iosClickable]'s press-flash, never a Material ripple — a ripple is the single
 * most recognisable tell that an "iOS-styled" screen is actually Material.
 *
 * The title is deliberately allowed to wrap onto multiple lines rather than ellipsising. iOS list
 * rows grow to fit their label, and several real titles are long enough to need it; truncating
 * would silently hide the end of a row's name in the longer translations.
 *
 * Colours resolve from [com.ltthuc.ui.themes.IosColorScheme], so dark mode follows automatically
 * once an `IosColorSchemeProvider` is installed at the root.
 *
 * ### Usage
 *
 * ```
 * IosDisclosureRow(
 *     title = stringResource(R.string.calc_angle_title),
 *     onClick = { onSelect(CalculatorId.ANGLE) },
 *     leading = { DiagramBadge(icon = HomeIcon.ANGLE, color = badgeColor) },
 * )
 * ```
 *
 * @param leading optional content shown before the title — an icon, badge or thumbnail.
 * @param showChevron set false for a row that performs an action in place rather than navigating.
 */
@Composable
fun IosDisclosureRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showChevron: Boolean = true,
    titleColor: Color = Color.Unspecified,
    chevronColor: Color = Color.Unspecified,
    contentSpacing: Dp = 14.dp,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 12.dp,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    val colors = iosColors()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .iosClickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(contentSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leading?.invoke()
        Text(
            text = title,
            // .font(.body) is 17pt on iOS; the Material `bodyLarge` default is 16sp and would
            // quietly shrink every list row, so the size is stated rather than inherited.
            fontSize = 17.sp,
            lineHeight = 22.sp,
            color = titleColor.takeOrElse { colors.labelPrimary },
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f),
        )
        trailing?.invoke(this)
        if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = chevronColor.takeOrElse { colors.labelTertiary },
                // AutoMirrored so the chevron points left in RTL locales, where iOS flips it too.
                modifier = Modifier.size(CHEVRON_SIZE),
            )
        }
    }
}

/**
 * iOS renders the disclosure chevron at 13pt semibold. Material's `KeyboardArrowRight` glyph has
 * more internal padding than SF Symbols' `chevron.right`, so the box is sized up to 20dp to land
 * on the same optical weight rather than matching the point size literally.
 */
private val CHEVRON_SIZE: Dp = 20.dp
