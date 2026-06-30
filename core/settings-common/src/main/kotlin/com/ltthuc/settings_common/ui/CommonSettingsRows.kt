package com.ltthuc.settings_common.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.annotation.DrawableRes
import com.ltthuc.settings_common.data.ExternalLinks
import com.ltthuc.settings_common.theme.palette

/**
 * Pre-built individual settings rows. Most consumers will prefer the higher-level
 * `CommonSupportSection` / `CommonMoreAppsSection` / `CommonPremiumUpsellCard`
 * composables in [CommonSettingsSections]. These low-level row functions stay
 * exposed for apps that need to mix rows manually.
 */

@Composable
fun ContactFeedbackRow(
    label: String,
    toEmail: String,
    subject: String,
    appVersionLabel: String,
    versionName: String,
    modifier: Modifier = Modifier,
) {
    val p = palette()
    val context = LocalContext.current
    SettingsRow(
        label = label,
        modifier = modifier,
        labelColor = p.accentInteractive,
        onClick = {
            ExternalLinks.sendFeedback(context, toEmail, subject, appVersionLabel, versionName)
        },
        leading = { Icon(Icons.Filled.Email, contentDescription = null, tint = p.accentInteractive) },
    )
}

@Composable
fun ShareAppRow(
    label: String,
    shareMessage: String,
    modifier: Modifier = Modifier,
) {
    val p = palette()
    val context = LocalContext.current
    SettingsRow(
        label = label,
        modifier = modifier,
        labelColor = p.accentInteractive,
        onClick = { ExternalLinks.shareApp(context, shareMessage) },
        leading = { Icon(Icons.Filled.Share, contentDescription = null, tint = p.accentInteractive) },
    )
}

@Composable
fun RateAppRow(
    label: String,
    modifier: Modifier = Modifier,
) {
    val p = palette()
    val context = LocalContext.current
    SettingsRow(
        label = label,
        modifier = modifier,
        labelColor = p.accentInteractive,
        onClick = { ExternalLinks.openPlayStorePage(context, context.packageName) },
        leading = { Icon(Icons.Filled.Star, contentDescription = null, tint = p.accentInteractive) },
    )
}

@Composable
fun MoreAppRow(
    displayName: String,
    packageName: String,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int? = null,
) {
    val p = palette()
    val context = LocalContext.current
    SettingsRow(
        label = displayName,
        modifier = modifier,
        onClick = { ExternalLinks.openPlayStorePage(context, packageName) },
        leading = iconRes?.let {
            {
                Image(
                    painter = painterResource(it),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(10.dp)),
                )
            }
        },
        trailing = {
            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                tint = p.labelTertiary,
                modifier = Modifier.size(18.dp),
            )
        },
    )
}

@Composable
fun findActivity(): Activity? {
    val context = LocalContext.current
    return remember(context) {
        var c: Context? = context
        while (c is ContextWrapper) {
            if (c is Activity) return@remember c
            c = c.baseContext
        }
        null
    }
}
