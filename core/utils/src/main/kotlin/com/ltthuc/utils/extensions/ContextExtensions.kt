package com.ltthuc.utils.extensions

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build

fun Context.findActivity(): Activity? {
    var current: Context? = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

fun Context.openUrl(url: String): Boolean = try {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
    true
} catch (e: ActivityNotFoundException) {
    false
}

fun Context.shareText(text: String, title: String? = null): Boolean = try {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooser = Intent.createChooser(intent, title).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(chooser)
    true
} catch (e: ActivityNotFoundException) {
    false
}

fun Activity.openAppOnStore(packageName: String) {
    val market = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
    try {
        startActivity(market)
    } catch (_: ActivityNotFoundException) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
    }
}

fun Activity.suggestFeedback(email: String) {
    @Suppress("DEPRECATION")
    val versionName = packageManager.getPackageInfo(packageName, 0).versionName ?: ""
    val appLabel = applicationInfo.loadLabel(packageManager).toString()
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, "Feedback on $appLabel version $versionName")
        putExtra(
            Intent.EXTRA_TEXT,
            "\n\n---\nDevice: ${Build.MANUFACTURER} ${Build.MODEL}\nAndroid: ${Build.VERSION.RELEASE}\n",
        )
    }
    startActivity(Intent.createChooser(intent, "Send feedback"))
}
