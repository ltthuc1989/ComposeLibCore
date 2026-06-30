package com.ltthuc.settings_common.internal

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * Returns the app's user-visible label (e.g. "Cubic Yards Calculator") from
 * the package manager. Used internally to fill the {appName} placeholder in
 * bundled feedback subjects + share messages so consumer code passes nothing.
 */
internal fun Context.resolveAppName(): String {
    val info = applicationInfo
    val label = info.loadLabel(packageManager).toString()
    return label.ifBlank { packageName.substringAfterLast('.') }
}

/**
 * Walks the [ContextWrapper] chain to find the hosting [Activity]. Needed for
 * launching IAP via `BillingApi.launchPurchase(activity, ...)` from a composable
 * tree that only has `LocalContext.current`.
 */
internal fun Context.findActivity(): Activity? {
    var c: Context? = this
    while (c is ContextWrapper) {
        if (c is Activity) return c
        c = c.baseContext
    }
    return null
}
