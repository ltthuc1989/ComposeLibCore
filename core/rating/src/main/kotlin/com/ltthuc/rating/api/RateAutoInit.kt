package com.ltthuc.rating.api

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * One-line install for the "ask every Nth app open" rating cadence. Call from
 * `Application.onCreate()` AFTER `super.onCreate()` (the Hilt graph must be ready), the same shape
 * as `AppOpenAutoInit.install(this)`:
 *
 * ```
 * override fun onCreate() {
 *     super.onCreate()
 *     RateAutoInit.install(this, RateConfig(key = "app_open", numberOfTimes = 4))
 * }
 * ```
 *
 * **Counts process foregrounds, not Activity creations.** A Back press can send the task to the
 * background while leaving the Activity alive (measured on Android 16: same ActivityRecord id
 * before and after, process untouched), so reopening from the launcher is a HOT start that never
 * runs `onCreate`. Anything hooked to `Activity.onCreate` misses those reopens entirely and the
 * counter can sit still forever.
 *
 * AdMob's interstitial / rewarded / app-open screens are Activities inside the app's own process,
 * so one covering the host never drops ProcessLifecycleOwner below STARTED and cannot fake an open.
 * [suppress] is for the case that does escape the process — an ad click that lands the user in Play
 * and back. Ad-aware consumers pass
 * `suppress = { AdsSettings.isInterstitialShowing || AdsSettings.isRewardAdsShowing }`; this module
 * deliberately does not depend on template-ads to read those itself.
 */
object RateAutoInit {

    @JvmStatic
    @JvmOverloads
    fun install(
        app: Application,
        config: RateConfig = RateConfig(),
        delayMs: Long = RateHelper.DEFAULT_PRESENT_DELAY_MS,
        isHost: (Activity) -> Boolean = { it.javaClass.simpleName == "MainActivity" },
        suppress: () -> Boolean = { false },
    ) {
        val helper = EntryPointAccessors
            .fromApplication(app, RateEntryPoint::class.java)
            .rateHelper()
        val tracker = ForegroundTracker(helper, config, delayMs, isHost, suppress)
        app.registerActivityLifecycleCallbacks(tracker)
        ProcessLifecycleOwner.get().lifecycle.addObserver(tracker)
    }
}

private class ForegroundTracker(
    private val helper: RateHelper,
    private val config: RateConfig,
    private val delayMs: Long,
    private val isHost: (Activity) -> Boolean,
    private val suppress: () -> Boolean,
) : Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var host: Activity? = null

    override fun onStart(owner: LifecycleOwner) {
        if (suppress()) return
        val activity = host ?: return
        scope.launch { helper.recordAndRequest(activity, config, delayMs) }
    }

    // Bound at CREATE, not START: ProcessLifecycleOwner registers its own callbacks during process
    // init, so its ON_START runs before ours for the same event. Binding one lifecycle step earlier
    // is what guarantees [host] is set by the time [onStart] reads it on a cold launch.
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (isHost(activity)) host = activity
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (host === activity) host = null
    }

    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityResumed(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
}
