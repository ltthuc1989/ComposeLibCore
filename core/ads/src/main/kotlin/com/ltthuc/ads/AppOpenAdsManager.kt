package com.ltthuc.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.ltthuc.utils.secrets.ISecretAdsKey
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.Date

class AppOpenAdsManager(
    private val app: Application,
    private val secret: ISecretAdsKey,
) : Application.ActivityLifecycleCallbacks, LifecycleEventObserver {

    private val tag = "AppOpenAdsManager"
    private var appOpenAd: AppOpenAd? = null
    private var appOpen: AppOpen? = null
    private var isAdLoading = false
    private var isAdShowing = false
    private var loadTime: Long = 0
    private var currentActivity: Activity? = null
    private var registered = false

    /**
     * The foreground count on which the FIRST App Open ad may show. **Default 1** — no grace period.
     *
     * The ad shows once `openCount >= showFromOpenCount`, so 1 means the very first open and 3 means
     * the third. Expressed as the count it fires ON rather than a number of opens to skip, because
     * the skip-count form reads as an off-by-one at every call site. Set via
     * [AppOpenAutoInit.install].
     *
     * The default is 1 so the library imposes no product policy of its own: an app that configures
     * nothing behaves the way the AdMob SDK does on its own. Holding the first ad back is a real
     * decision about a real audience — it belongs to the app that has that audience, stated
     * explicitly at its own call site, not smuggled in as a library default that nobody reads.
     *
     * ### This is a ONE-TIME gate, not a recurring cadence
     * The counter only ever increments — nothing resets it. Once it passes the threshold every
     * later foreground is eligible, subject to the usual guards (fill, the 4-hour cache,
     * `disableAd`, `isOtherAppShowing`). It is an opening grace period, NOT "one ad every N opens".
     *
     * Do not read it as `RateConfig.numberOfTimes`, which looks similar and behaves differently:
     * `RateHelper` calls `resetCount` after each prompt, so that one IS a repeating cadence. There
     * is deliberately no "every Nth open" mode here — App Open ads are already rate-limited by fill
     * and by the 4-hour expiry, and a second counter on top would silently suppress most of them.
     */
    var showFromOpenCount: Int = DEFAULT_SHOW_FROM_OPEN_COUNT

    /**
     * Show at most one App Open ad every N foregrounds, counted from the last one actually shown.
     *
     * `null` (the default) keeps the original behaviour: once [showFromOpenCount] is reached, every
     * later foreground is eligible. Set it to space them out — with `showFromOpenCount = 3` and
     * `repeatEveryOpens = 3` the ad lands on opens 3, 6, 9, …
     *
     * Note this multiplies with the limits AdMob already imposes: a cached ad expires 4 hours after
     * it loads, and a request can simply return no fill. Both mean an eligible open may still show
     * nothing, so the user sees FEWER ads than the number here suggests, never more.
     */
    var repeatEveryOpens: Int? = null

    private val openPrefs by lazy {
        app.getSharedPreferences(OPEN_PREFS, Context.MODE_PRIVATE)
    }

    /** Counts this foreground and returns the running total. Persisted across process death. */
    private fun recordOpen(): Int {
        val next = openPrefs.getInt(KEY_OPEN_COUNT, 0) + 1
        openPrefs.edit().putInt(KEY_OPEN_COUNT, next).apply()
        return next
    }

    private fun openCount(): Int = openPrefs.getInt(KEY_OPEN_COUNT, 0)

    /** The open number the last App Open ad was shown on; 0 when none ever has been. */
    private fun lastShownOpen(): Int = openPrefs.getInt(KEY_LAST_SHOWN_OPEN, 0)

    private fun recordShown() {
        openPrefs.edit().putInt(KEY_LAST_SHOWN_OPEN, openCount()).apply()
    }

    /**
     * Whether this particular foreground is allowed to show an ad.
     *
     * Two independent rules: [showFromOpenCount] holds the FIRST ad back, and [repeatEveryOpens]
     * — when set — spaces out the ones after it. Counting the gap from the open the last ad was
     * SHOWN on, not from a counter that resets, means an open where the ad was unavailable (no
     * fill, expired cache) does not consume the user's quota: the ad simply appears on the next
     * eligible open instead of being skipped for another full cycle.
     */
    private fun mayShowOnThisOpen(): Boolean {
        val count = openCount()
        if (count < showFromOpenCount) return false
        val every = repeatEveryOpens ?: return true
        val last = lastShownOpen()
        if (last == 0) return true
        return count - last >= every
    }

    fun setAppOpen(appOpen: AppOpen) {
        if (AdsSettings.disableAd || AdsSettings.disableOpenAds) return
        if (registered) {
            // Allow callback override on subsequent calls (e.g. base Activity replacing
            // a NoOp default installed earlier from Application).
            this.appOpen = appOpen
            return
        }
        registered = true
        app.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        this.appOpen = appOpen
    }

    private fun loadAd() {
        if (isAdLoading) return
        isAdLoading = true
        AppOpenAd.load(
            app.applicationContext,
            secret.getAppOpenAdsID(),
            AdRequest.Builder().build(),
            object : AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    loadTime = Date().time
                    isAdLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isAdLoading = false
                    appOpenAd = null
                    Log.d(tag, "Failed to load: $error")
                }
            },
        )
    }

    private fun showAdIfAvailable(activity: Activity) {
        if (AdsSettings.disableAd || AdsSettings.disableOpenAds) return
        if (isAdShowing) return
        if (!isAdAvailable()) {
            loadAd()
            return
        }
        if (AdsSettings.isSplashScreen || AdsSettings.isOtherAppShowing) {
            AdsSettings.isOtherAppShowing = false
            return
        }
        // Placed AFTER the load and flag handling above on purpose: during the opening grace period
        // the ad still preloads and `isOtherAppShowing` is still consumed, so the only thing the
        // gate changes is whether this particular foreground may SHOW.
        if (!mayShowOnThisOpen()) return
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                isAdShowing = true
                // Recorded on SHOWN, not on the attempt: an open that produced no ad must not
                // start the next interval.
                recordShown()
                appOpen?.closeAds()
            }

            override fun onAdDismissedFullScreenContent() {
                isAdShowing = false
                appOpenAd = null
                appOpen?.restoreAds()
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(tag, adError.message)
                isAdShowing = false
                appOpenAd = null
                loadAd()
            }
        }
        Handler(activity.mainLooper).postDelayed({ appOpenAd?.show(activity) }, 200)
    }

    private fun isAdAvailable(): Boolean = appOpenAd != null && wasLoadTimeWithin(4)

    private fun wasLoadTimeWithin(hours: Int): Boolean {
        val diff = Date().time - loadTime
        return diff < 3_600_000L * hours
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) {
        if (isMainActivity(activity)) currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        if (isMainActivity(activity)) currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        if (isMainActivity(activity)) currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity) {
        if (isMainActivity(activity)) currentActivity = activity
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        if (isMainActivity(activity)) {
            app.unregisterActivityLifecycleCallbacks(this)
            currentActivity = null
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event != Lifecycle.Event.ON_RESUME) return
        if (AdsSettings.isInterstitialShowing || AdsSettings.isRewardAdsShowing) return
        // Counted before the show is attempted, and only for foregrounds that are genuine app
        // opens — a return from this app's own interstitial bailed out above and must not count.
        recordOpen()
        currentActivity?.let { showAdIfAvailable(it) }
    }

    private fun isMainActivity(activity: Activity): Boolean =
        activity::class.java.simpleName == "MainActivity"

    companion object {
        /** No grace period: the ad may show from the very first foreground. */
        const val DEFAULT_SHOW_FROM_OPEN_COUNT = 1

        private const val OPEN_PREFS = "ads_app_open_prefs"
        private const val KEY_OPEN_COUNT = "app_open_count"
        private const val KEY_LAST_SHOWN_OPEN = "app_open_last_shown"
    }
}
