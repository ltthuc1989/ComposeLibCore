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
 */
object AppOpenAutoInit {
    @JvmStatic
    @JvmOverloads
    fun install(app: Application, callback: AppOpen = AppOpen.NoOp) {
        val manager = EntryPointAccessors
            .fromApplication(app, AdsEntryPoint::class.java)
            .appOpenAdsManager()
        manager.setAppOpen(callback)
    }
}
