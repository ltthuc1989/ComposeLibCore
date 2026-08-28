package com.ltthuc.ui.components.ios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltthuc.ui.themes.iosColors

/**
 * A section of an iOS `List(.insetGrouped)` / `Form`: an inset rounded card holding [count] rows,
 * with a hairline separator drawn between them — never above the first or below the last.
 *
 * ### Why this exists rather than a plain Column
 * The separator inset is the detail that gives an iOS list away when it is wrong. iOS starts the
 * hairline at the row's *content* origin, not at the card edge, so it clears the leading icon and
 * lines up under the text. [separatorStartInset] models that, and defaults to the card's own
 * horizontal padding for rows with no leading content.
 *
 * ### Why an index-based API
 * The row content is taken as `content: @Composable (index: Int) -> Unit` rather than a
 * `ColumnScope` block, because the section has to interleave separators and therefore needs to know
 * where each row begins and ends. A trailing-lambda Column cannot express "between children".
 *
 * The corner radius defaults to 10dp — iOS's `.insetGrouped` value. Note this is NOT the 16dp of
 * [iosCardModifier], which models the larger free-standing card iOS uses outside lists, and the
 * section is deliberately flat: `.insetGrouped` rows draw no shadow.
 *
 * ### Usage
 *
 * ```
 * IosInsetGroupedSection(count = rows.size, separatorStartInset = 74.dp) { i ->
 *     IosDisclosureRow(title = rows[i].title, onClick = { onSelect(rows[i]) }) { … }
 * }
 * ```
 *
 * @param header optional uppercase section header rendered above the card, as `Section { }` does.
 * @param separatorStartInset distance from the card's leading edge to the start of the hairline.
 */
@Composable
fun IosInsetGroupedSection(
    count: Int,
    modifier: Modifier = Modifier,
    header: String? = null,
    cornerRadius: Dp = 10.dp,
    background: Color = Color.Unspecified,
    separatorColor: Color = Color.Unspecified,
    separatorStartInset: Dp = 16.dp,
    content: @Composable (index: Int) -> Unit,
) {
    val colors = iosColors()
    Column(modifier = modifier.fillMaxWidth()) {
        if (header != null) {
            Text(
                text = header,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = colors.labelSecondary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 6.dp),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(cornerRadius))
                .background(background.takeOrElse { colors.secondarySystemGroupedBackground }),
        ) {
            repeat(count) { index ->
                content(index)
                // Between rows only — a trailing hairline would draw on the card's bottom edge,
                // which iOS never does.
                if (index < count - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = separatorStartInset),
                        thickness = Dp.Hairline,
                        color = separatorColor.takeOrElse { colors.separator },
                    )
                }
            }
        }
    }
}

/**
 * Overload for a section whose rows are not index-addressable — a fixed handful of heterogeneous
 * rows written inline. Separators are the caller's responsibility here, since the section cannot
 * see where one child ends and the next begins.
 */
@Composable
fun IosInsetGroupedCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp,
    background: Color = Color.Unspecified,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = iosColors()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(background.takeOrElse { colors.secondarySystemGroupedBackground }),
        content = content,
    )
}
