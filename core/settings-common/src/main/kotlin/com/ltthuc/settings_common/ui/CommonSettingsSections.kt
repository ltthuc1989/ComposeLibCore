package com.ltthuc.settings_common.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ltthuc.settings_common.R
import com.ltthuc.settings_common.billing.LocalRemoveAdsController
import com.ltthuc.settings_common.billing.RemoveAdsController
import com.ltthuc.settings_common.internal.findActivity
import com.ltthuc.settings_common.internal.resolveAppName
import com.ltthuc.settings_common.theme.palette

data class MoreAppEntry(
    val displayName: String,
    val packageName: String,
    @DrawableRes val iconRes: Int? = null,
)

/**
 * Drop-in Support section: Contact & Feedback | Share | Rate. Bundled labels
 * in 11 locales — pass nothing for 99% of apps.
 *
 * Override paths: per-call params (runtime) OR consumer's own
 * `res/values/strings.xml` `csc_*` keys (Android resource merger wins
 * consumer over lib, no code changes).
 */
@Composable
fun CommonSupportSection(
    supportEmail: String,
    versionName: String,
    modifier: Modifier = Modifier,
    sectionTitle: String? = null,
    contactLabel: String? = null,
    shareLabel: String? = null,
    rateLabel: String? = null,
    feedbackSubject: String? = null,
    shareMessage: String? = null,
    versionLabel: String? = null,
) {
    val context = LocalContext.current
    val appName = remember(context) { context.resolveAppName() }
    val packageName = context.packageName

    Column(modifier) {
        SectionHeader(sectionTitle ?: stringResource(R.string.csc_support))
        SettingsCard {
            ContactFeedbackRow(
                label = contactLabel ?: stringResource(R.string.csc_contact_feedback),
                toEmail = supportEmail,
                subject = feedbackSubject
                    ?: stringResource(R.string.csc_feedback_subject, appName),
                appVersionLabel = versionLabel ?: stringResource(R.string.csc_version_label),
                versionName = versionName,
            )
            SettingsRowDivider()
            ShareAppRow(
                label = shareLabel ?: stringResource(R.string.csc_share_app),
                shareMessage = shareMessage
                    ?: stringResource(R.string.csc_share_message, appName, packageName),
            )
            SettingsRowDivider()
            RateAppRow(label = rateLabel ?: stringResource(R.string.csc_rate_app))
        }
    }
}

/**
 * Drop-in More Apps section. Hides itself entirely when [apps] is empty so
 * consumers can pass `emptyList()` without leaving a layout hole.
 */
@Composable
fun CommonMoreAppsSection(
    apps: List<MoreAppEntry>,
    modifier: Modifier = Modifier,
    sectionTitle: String? = null,
) {
    if (apps.isEmpty()) return
    Column(modifier) {
        SectionHeader(sectionTitle ?: stringResource(R.string.csc_more_apps))
        SettingsCard {
            apps.forEachIndexed { index, entry ->
                MoreAppRow(
                    displayName = entry.displayName,
                    packageName = entry.packageName,
                    iconRes = entry.iconRes,
                )
                if (index < apps.lastIndex) SettingsRowDivider()
            }
        }
    }
}

/**
 * Drop-in Premium / Remove Ads section. Reads state from
 * [LocalRemoveAdsController] (provided by `RemoveAdsControllerProvider` at the
 * app root). Renders the rich pre-purchase upsell card OR the green-tick
 * post-purchase row, plus a Restore Purchases row.
 *
 * Auto-hides when productLoadAttempted && productInfo == null && !hasRemovedAds
 * (Play billing returned nothing — better than infinite "Loading…").
 */
@Composable
fun CommonPremiumSection(
    modifier: Modifier = Modifier,
    controller: RemoveAdsController = LocalRemoveAdsController.current,
    sectionTitle: String? = null,
    purchaseHeadline: String? = null,
    purchaseSubtitle: String? = null,
    purchaseButtonLabel: String? = null,
    purchasedLabel: String? = null,
    purchasedSubtitle: String? = null,
    restoreLabel: String? = null,
    benefits: List<String>? = null,
    loadingLabel: String? = null,
) {
    val p = palette()
    val hasRemovedAds by controller.hasRemovedAds.collectAsStateWithLifecycle()
    val productInfo by controller.productInfo.collectAsStateWithLifecycle()
    val productLoadAttempted by controller.productLoadAttempted.collectAsStateWithLifecycle()
    val activity = findActivity()

    val productUnavailable = productLoadAttempted && productInfo == null && !hasRemovedAds
    if (productUnavailable) return

    Column(modifier) {
        if (hasRemovedAds) SectionHeader(sectionTitle ?: stringResource(R.string.csc_premium))
        SettingsCard {
            if (hasRemovedAds) {
                PurchasedRow(
                    label = purchasedLabel ?: stringResource(R.string.csc_ads_removed),
                    subtitle = purchasedSubtitle,
                )
            } else {
                RemoveAdsUpsellInline(
                    headline = purchaseHeadline ?: stringResource(R.string.csc_remove_ads),
                    subtitle = purchaseSubtitle,
                    buttonLabel = purchaseButtonLabel,
                    benefits = benefits,
                    loadingLabel = loadingLabel,
                    priceLabel = productInfo?.formattedPrice,
                    onPurchase = { activity?.let { controller.launchPurchase(it) } },
                )
            }
            SettingsRowDivider()
            SettingsRow(
                label = restoreLabel ?: stringResource(R.string.csc_restore_purchases),
                onClick = { controller.restorePurchases() },
                leading = {
                    Icon(Icons.Filled.Refresh, contentDescription = null, tint = p.labelSecondary)
                },
            )
        }
    }
}

/**
 * Purchased-state row: green check + label, with an optional secondary line.
 * Mirrors [SettingsRow]'s padding/leading spacing but supports two lines.
 */
@Composable
private fun PurchasedRow(label: String, subtitle: String?) {
    val p = palette()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = p.systemGreen)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, color = p.labelPrimary, fontSize = 16.sp)
            if (subtitle != null) {
                Text(subtitle, color = p.labelSecondary, fontSize = 14.sp)
            }
        }
    }
}

// Default benefit-bullet icons, paired by index with the rendered benefit list.
private val benefitIcons = listOf(Icons.Filled.Cancel, Icons.Filled.Favorite, Icons.Filled.Verified)

@Composable
private fun RemoveAdsUpsellInline(
    headline: String,
    priceLabel: String?,
    onPurchase: () -> Unit,
    subtitle: String? = null,
    buttonLabel: String? = null,
    benefits: List<String>? = null,
    loadingLabel: String? = null,
) {
    val p = palette()
    // null → the library's three default bullets; emptyList() → none; else custom.
    val benefitItems = benefits ?: listOf(
        stringResource(R.string.csc_benefit_no_interruptions),
        stringResource(R.string.csc_benefit_support_development),
        stringResource(R.string.csc_benefit_one_time_payment),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = p.accentOrange,
                modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = headline,
                color = p.labelPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        if (subtitle != null) {
            Text(text = subtitle, color = p.labelSecondary, fontSize = 15.sp)
        }
        if (benefitItems.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                benefitItems.forEachIndexed { index, text ->
                    BenefitRow(benefitIcons[index.coerceAtMost(benefitIcons.lastIndex)], text)
                }
            }
        }
        Spacer(Modifier.size(4.dp))
        if (priceLabel == null) {
            // Centered progress + secondary "Loading…" text. iOS parity: no
            // disabled-looking button during product load.
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = p.accentOrange,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = loadingLabel ?: stringResource(R.string.csc_purchase_loading),
                    color = p.labelSecondary,
                    fontSize = 14.sp,
                )
            }
        } else {
            // iOS button layout: button label on the left, formatted price on the right.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(p.accentOrange)
                    .clickable(onClick = onPurchase)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = buttonLabel ?: headline,
                    color = p.onAccent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = priceLabel,
                    color = p.onAccent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun BenefitRow(icon: ImageVector, text: String) {
    val p = palette()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = p.accentInteractive,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(text = text, color = p.labelSecondary, fontSize = 14.sp)
    }
}
