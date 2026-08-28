package com.ltthuc.ads

import android.app.Application
import dagger.hilt.android.EntryPointAccessors

/**
 * One-line install API for App Open Ads. Call from `Application.onCreate()` AFTER
 * `super.onCreate()` (Hilt graph must be ready). Handles manager resolution +
 * lifecycle observer registration so consumers don't need to inject anything.
 *
 * Usage:
 * ```
 * override fun onCreate() {
 *     super.onCreate()
 *     MobileAds.initialize(applicationContext)
 *     AppOpenAutoInit.install(this)
 * }
 * ```
 *
 * For consumers that need to react to ad show/dismiss (e.g. hide banners while the
 * full-screen App Open is up), pass a custom [AppOpen] callback:
 * `AppOpenAutoInit.install(this, myAppOpenImpl)`.
 *
 * ### The opening grace period
 * [showFromOpenCount] is the foreground count the first App Open ad may show on — **default 1**, so
 * a consumer that passes nothing gets the SDK's own behaviour and the ad may appear on the very
 * first open.
 *
 * Most apps should raise it. A full-screen ad on first launch is the worst possible first
 * impression, and it lands on exactly the install-day user most likely to leave a rating; iOS ports
 * frequently already have this gate (`AppDelegate`'s `numberOfOpenSettings > 2` → pass `3`). It is
 * left at 1 by default because that choice belongs to the app, made visibly at its own call site,
 * rather than to a library default nobody reads.
 *
 * On its own it gates only the FIRST ad — from the third open onward every foreground is eligible.
 * To space the later ones out too, pass [repeatEveryOpens]:
 *
 * ```
 * AppOpenAutoInit.install(this, showFromOpenCount = 3, repeatEveryOpens = 3)  // opens 3, 6, 9, …
 * ```
 *
 * Left `null` (the default) nothing changes. See [AppOpenAdsManager.repeatEveryOpens] for why the
 * interval is measured from the last ad actually SHOWN rather than from a resetting counter.
 *
 * The count is persisted, so it survives process death and counts real opens rather than launches.
 */
object AppOpenAutoInit {
    @JvmStatic
    @JvmOverloads
    fun install(
        app: Application,
        callback: AppOpen = AppOpen.NoOp,
        showFromOpenCount: Int = AppOpenAdsManager.DEFAULT_SHOW_FROM_OPEN_COUNT,
        repeatEveryOpens: Int? = null,
    ) {
        val manager = EntryPointAccessors
            .fromApplication(app, AdsEntryPoint::class.java)
            .appOpenAdsManager()
        manager.showFromOpenCount = showFromOpenCount
        manager.repeatEveryOpens = repeatEveryOpens
        manager.setAppOpen(callback)
    }
}
