package com.ltthuc.appupdate.api

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
 * One-line install for the flexible update check, the same shape as `RateAutoInit.install(this)`.
 * Call from `Application.onCreate()` AFTER `super.onCreate()` — the Hilt graph must be ready:
 *
 * ```
 * override fun onCreate() {
 *     super.onCreate()
 *     AppUpdateAutoInit.install(this)
 * }
 * ```
 *
 * Then render [AppUpdateController.state] somewhere persistent — see `UpdateReadyBar`.
 *
 * **Checks per process foreground, not per Activity creation**, for the same reason the rating
 * cadence does: a Back press can background the task while leaving the Activity alive, so
 * reopening from the launcher is a hot start that never runs `onCreate`. That reopen is exactly
 * when a background download has had time to finish and needs surfacing.
 *
 * [suppress] exists for the case that leaves the process — an ad click that lands in Play and back.
 * Ad-aware consumers pass
 * `suppress = { AdsSettings.isInterstitialShowing || AdsSettings.isRewardAdsShowing }`; this module
 * deliberately does not depend on template-ads to read those itself.
 */
object AppUpdateAutoInit {

    @JvmStatic
    @JvmOverloads
    fun install(
        app: Application,
        config: AppUpdateConfig = AppUpdateConfig(),
        isHost: (Activity) -> Boolean = { it.javaClass.simpleName == "MainActivity" },
        suppress: () -> Boolean = { false },
    ) {
        val controller = EntryPointAccessors
            .fromApplication(app, AppUpdateEntryPoint::class.java)
            .appUpdateController()
        val tracker = ForegroundChecker(controller, config, isHost, suppress)
        app.registerActivityLifecycleCallbacks(tracker)
        ProcessLifecycleOwner.get().lifecycle.addObserver(tracker)
    }
}

private class ForegroundChecker(
    private val controller: AppUpdateController,
    private val config: AppUpdateConfig,
    private val isHost: (Activity) -> Boolean,
    private val suppress: () -> Boolean,
) : Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var host: Activity? = null

    override fun onStart(owner: LifecycleOwner) {
        if (suppress()) return
        val activity = host ?: return
        scope.launch { controller.refresh(activity, config, System.currentTimeMillis()) }
    }

    override fun onStop(owner: LifecycleOwner) = controller.stopListening()

    // Bound at CREATE, not START: ProcessLifecycleOwner registers during process init, so its
    // ON_START runs before ours for the same event. Binding a step earlier is what guarantees
    // [host] is set by the time [onStart] reads it on a cold launch.
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
