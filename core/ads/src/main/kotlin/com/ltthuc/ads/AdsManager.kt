package com.ltthuc.ads

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.ltthuc.utils.secrets.ISecretAdsKey
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import javax.inject.Inject
import kotlinx.coroutines.CompletableDeferred

class AdsManager @Inject constructor(
    private val application: Application,
    private val adsKey: ISecretAdsKey,
) {
    private val tag = AdsManager::class.java.simpleName
    private var adView: AdView? = null
    private var adLoadDeferred: CompletableDeferred<AdView?>? = null
    private var retryCount = 0
    private val maxRetries = 2

    init {
        if (!AdsSettings.disableAd) {
            application.registerComponentCallbacks(object : ComponentCallbacks2 {
                override fun onTrimMemory(level: Int) {
                    if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) destroyAd()
                }

                override fun onConfigurationChanged(newConfig: Configuration) = Unit

                override fun onLowMemory() = destroyAd()
            })
        }
    }

    suspend fun getAd(container: View? = null): AdView? {
        if (AdsSettings.disableAd) return null
        adView?.let { return it }

        if (adLoadDeferred == null) {
            retryCount = 0
            loadAd(container)
        }
        return try {
            adLoadDeferred?.await()?.also { adView = it }
        } catch (e: Exception) {
            Log.e(tag, "Ad loading failed: ${e.message}")
            null
        }
    }

    fun loadAd(view: View? = null) {
        if (adView != null || adLoadDeferred != null) return
        adLoadDeferred = CompletableDeferred()

        AdView(application).apply {
            adUnitId = adsKey.getBannerAdsID()
            setAdSize(if (view != null) adSize(context, view) else AdSize.BANNER)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    adLoadDeferred?.complete(this@apply)
                    retryCount = 0
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    if (retryCount < maxRetries) {
                        retryCount++
                        cleanupLoadingState()
                        loadAd(view)
                    } else {
                        adLoadDeferred?.complete(null)
                        cleanupLoadingState()
                        retryCount = 0
                    }
                }
            }
            loadAd(AdRequest.Builder().build())
        }
    }

    fun removeAd(container: ViewGroup? = null) {
        when {
            container != null -> adView?.let { container.removeView(it) }
            adView?.parent != null -> (adView?.parent as ViewGroup).removeView(adView)
        }
    }

    suspend fun showAds(container: ViewGroup, onShow: () -> Unit = {}) {
        adView = getAd(container)
        (adView?.parent as? ViewGroup)?.removeView(adView)
        adView?.let { ad ->
            ad.visibility = View.VISIBLE
            container.addView(ad)
            container.visibility = View.VISIBLE
            onShow()
        }
    }

    fun destroyAd() {
        adView?.destroy()
        adView = null
        adLoadDeferred?.cancel()
        cleanupLoadingState()
        retryCount = 0
    }

    private fun cleanupLoadingState() {
        adLoadDeferred = null
    }
}
