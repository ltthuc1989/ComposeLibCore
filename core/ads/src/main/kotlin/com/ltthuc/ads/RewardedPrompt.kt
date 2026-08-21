package com.ltthuc.ads

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Colours for [RewardedPrompt]. Passed in rather than read from `IosColors`, which is light-only —
 * a consumer with a dark palette must hand its own values through (see the dark-mode rule in the
 * project rules).
 */
@Immutable
data class RewardedPromptColors(
    val surface: Color,
    val accent: Color,
    val onAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val link: Color,
    val shadow: Color = Color(0x66000000),
)

/**
 * The opt-in AdMob's **"Rewards implementation - User choice"** policy requires in front of every
 * rewarded ad. Pair it with [RewardedGate]: show this only when `gate.shouldPrompt` is true, and
 * call `gate.showRewarded(...)` from [onWatch] — nowhere else may start a rewarded ad.
 *
 * [title] and [message] have no defaults on purpose. The policy requires the prompt to state the
 * reward the user actually receives, which only the consumer knows ("save this calculation to your
 * projects"), and a generic default here would ship as a vague one. The button labels do have
 * defaults, bundled in every locale this library carries.
 *
 * [onDismiss] — the decline row, system back, and tap-outside — must return the user to exactly
 * where they were. Routing a decline to a paywall, or dropping the work they were saving, is the
 * violation this component exists to prevent.
 */
@Composable
fun RewardedPrompt(
    title: String,
    message: String,
    colors: RewardedPromptColors,
    onWatch: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    watchLabel: String = stringResource(R.string.ads_rewarded_watch),
    dismissLabel: String = stringResource(R.string.ads_rewarded_not_now),
    removeAdsLabel: String = stringResource(R.string.ads_rewarded_remove_ads),
    onRemoveAds: (() -> Unit)? = null,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(16.dp), ambientColor = colors.shadow)
                .background(colors.surface, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PlayBadge(accent = colors.accent)
            Spacer(Modifier.size(16.dp))
            Text(
                text = title,
                color = colors.textPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = message,
                color = colors.textSecondary,
                fontSize = 15.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.size(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(colors.accent, RoundedCornerShape(12.dp))
                    .clickable(onClick = onWatch),
                contentAlignment = Alignment.Center,
            ) {
                Text(watchLabel, color = colors.onAccent, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.size(4.dp))
            // Declining must be as reachable as accepting: full width, same type size, never dimmed.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Text(dismissLabel, color = colors.textSecondary, fontSize = 16.sp)
            }
            onRemoveAds?.let { removeAds ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .clickable(onClick = removeAds),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(removeAdsLabel, color = colors.link, fontSize = 14.sp)
                }
            }
        }
    }
}

/** Play glyph in a tinted disc — drawn rather than an icon so this module needs no icon artifact. */
@Composable
private fun PlayBadge(accent: Color) {
    Box(
        modifier = Modifier.size(56.dp).background(accent.copy(alpha = 0.15f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(22.dp)) {
            val path = Path().apply {
                moveTo(size.width * 0.12f, 0f)
                lineTo(size.width * 0.12f, size.height)
                lineTo(size.width * 0.94f, size.height / 2f)
                close()
            }
            drawPath(path, accent)
        }
    }
}
