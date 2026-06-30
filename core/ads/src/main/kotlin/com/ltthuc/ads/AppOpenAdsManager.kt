package com.ltthuc.ads

import android.app.Activity
import android.app.Application
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
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                isAdShowing = true
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
        currentActivity?.let { showAdIfAvailable(it) }
    }

    private fun isMainActivity(activity: Activity): Boolean =
        activity::class.java.simpleName == "MainActivity"
}
