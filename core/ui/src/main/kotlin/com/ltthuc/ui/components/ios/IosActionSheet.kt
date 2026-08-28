package com.ltthuc.ui.components.ios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltthuc.ui.themes.iosColors

/**
 * The body of a SwiftUI `confirmationDialog` / UIKit `UIAlertController(.actionSheet)`: an inset
 * card of centred, tinted action rows, optionally headed by a grey title, with the cancel row
 * separated below as its own card.
 *
 * This renders the CONTENT only — it does not present itself. Host it in whatever the app already
 * uses for modals (`ModalBottomSheet`, a dialog); that keeps the presentation, scrim and dismiss
 * gesture in the app's hands and this component purely visual.
 *
 * ### Why the rows are a builder lambda, not a composable slot
 * An action sheet has to draw a hairline *between* rows and never above the first or below the
 * last, which a `ColumnScope` slot cannot express (the same constraint that made
 * [IosInsetGroupedSection] index-based). A sheet's rows are also routinely conditional — "Print"
 * only where the device can print — so an index-based API would push the caller into maintaining a
 * parallel list. Collecting the rows through [IosActionSheetScope] gives both: `if (…) action(…)`
 * reads naturally and the separators stay this component's problem.
 *
 * ### Colour defaults
 * [tint] defaults to `labelPrimary` rather than iOS's systemBlue, because [IosColorScheme] carries
 * no blue and a hard-coded one would be wrong in dark mode. An app mirroring iOS's default accent
 * passes its own palette's blue explicitly.
 *
 * ### Usage
 *
 * ```
 * ModalBottomSheet(onDismissRequest = ::dismiss) {
 *     IosActionSheet(
 *         title = stringResource(R.string.share_results),
 *         cancelLabel = stringResource(R.string.cancel),
 *         onCancel = ::dismiss,
 *         tint = palette.systemBlue,
 *     ) {
 *         action(stringResource(R.string.share_as_image), onClick = ::shareImage)
 *         if (canPrint) action(stringResource(R.string.print), onClick = ::print)
 *     }
 * }
 * ```
 *
 * @param title grey heading above the first row, as `titleVisibility: .visible` renders it.
 * @param cancelLabel when non-null, a detached bold row below the card. iOS always ships one;
 *   an Android host that dismisses by swipe or back may leave it out.
 */
@Composable
fun IosActionSheet(
    modifier: Modifier = Modifier,
    title: String? = null,
    cancelLabel: String? = null,
    onCancel: () -> Unit = {},
    tint: Color = Color.Unspecified,
    background: Color = Color.Unspecified,
    separatorColor: Color = Color.Unspecified,
    cornerRadius: Dp = ACTION_SHEET_CORNER_RADIUS,
    content: IosActionSheetScope.() -> Unit,
) {
    val colors = iosColors()
    val surface = background.takeOrElse { colors.secondarySystemGroupedBackground }
    val hairline = separatorColor.takeOrElse { colors.separator }
    val label = tint.takeOrElse { colors.labelPrimary }
    val actions = IosActionSheetScope().apply(content).actions

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ACTION_SHEET_CARD_GAP),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(cornerRadius))
                .background(surface),
        ) {
            if (title != null) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    color = colors.labelSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 15.dp),
                )
                HorizontalDivider(thickness = Dp.Hairline, color = hairline)
            }
            actions.forEachIndexed { index, action ->
                if (index > 0) HorizontalDivider(thickness = Dp.Hairline, color = hairline)
                ActionRow(
                    label = action.label,
                    tint = action.tint.takeOrElse { label },
                    onClick = action.onClick,
                )
            }
        }

        if (cancelLabel != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(surface),
            ) {
                ActionRow(
                    label = cancelLabel,
                    tint = label,
                    fontWeight = FontWeight.SemiBold,
                    onClick = onCancel,
                )
            }
        }
    }
}

/** Collects the rows declared in an [IosActionSheet] body. */
class IosActionSheetScope internal constructor() {
    internal val actions = mutableListOf<IosActionSheetAction>()

    /**
     * Adds one row. [tint] overrides the sheet's tint for this row alone — iOS's
     * `role: .destructive` is a red row, so pass the palette's red rather than a literal.
     */
    fun action(label: String, tint: Color = Color.Unspecified, onClick: () -> Unit) {
        actions += IosActionSheetAction(label = label, tint = tint, onClick = onClick)
    }
}

internal data class IosActionSheetAction(
    val label: String,
    val tint: Color,
    val onClick: () -> Unit,
)

@Composable
private fun ActionRow(
    label: String,
    tint: Color,
    onClick: () -> Unit,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = ACTION_SHEET_ROW_HEIGHT)
            .iosClickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 20.sp,
            fontWeight = fontWeight,
            color = tint,
            textAlign = TextAlign.Center,
        )
    }
}

/** iOS's action sheet is rounder than an inset-grouped list's 10dp card. */
private val ACTION_SHEET_CORNER_RADIUS = 14.dp

/** The gap iOS leaves between the action card and the detached cancel card. */
private val ACTION_SHEET_CARD_GAP = 8.dp

/** `heightIn(min = …)`, not a fixed height: a translated label may need two lines. */
private val ACTION_SHEET_ROW_HEIGHT = 57.dp
