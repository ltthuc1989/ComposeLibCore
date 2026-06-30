package com.ltthuc.settings_common.data

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.ltthuc.ads.AdsSettings

/**
 * Common user-initiated external Intents — mail, share sheet, Play Store.
 *
 * Each entry point sets [AdsSettings.isOtherAppShowing] = true BEFORE launching
 * the Intent so the App Open ad observer skips the next foreground-return. The
 * flag auto-resets after one suppression (see template-ads AppOpenAdsManager).
 */
object ExternalLinks {

    fun sendFeedback(
        context: Context,
        toEmail: String,
        subject: String,
        appVersionLabel: String,
        versionName: String,
    ) {
        val device = "${Build.MANUFACTURER} ${Build.MODEL}"
        val osVersion = Build.VERSION.RELEASE
        val body = "$appVersionLabel $versionName\nDevice: $device / Android $osVersion\n\n"
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            // Recipient goes in the URI so ACTION_SENDTO resolves cleanly against
            // email-app intent filters. EXTRA_EMAIL alone is silently ignored by
            // most mail apps' SENDTO filters and was the root cause of the
            // "Contact & Feedback does nothing" bug on Android 11+.
            data = Uri.parse("mailto:" + Uri.encode(toEmail))
            putExtra(Intent.EXTRA_EMAIL, arrayOf(toEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        launchWithAppOpenSuppressed(context, intent)
    }

    fun shareApp(context: Context, message: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        launchWithAppOpenSuppressed(context, Intent.createChooser(intent, null))
    }

    fun openPlayStorePage(context: Context, packageName: String) {
        val market = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        val web = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$packageName"),
        )
        AdsSettings.isOtherAppShowing = true
        runCatching { context.startActivity(market) }.recoverCatching { context.startActivity(web) }
    }

    fun openDeveloperPage(context: Context, developerId: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/developer?id=$developerId"),
        )
        launchWithAppOpenSuppressed(context, intent)
    }

    fun openUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        launchWithAppOpenSuppressed(context, intent)
    }

    fun rateApp(activity: Activity) {
        openPlayStorePage(activity, activity.packageName)
    }

    fun launchWithAppOpenSuppressed(context: Context, intent: Intent) {
        AdsSettings.isOtherAppShowing = true
        runCatching { context.startActivity(intent) }
    }
}
