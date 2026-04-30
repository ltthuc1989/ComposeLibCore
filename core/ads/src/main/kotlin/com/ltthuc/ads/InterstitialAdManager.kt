package com.ltthuc.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.ltthuc.utils.secrets.ISecretAdsKey
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager(
    private val context: Context,
    private val secret: ISecretAdsKey,
) {
    init { loadInterstitialAd() }

    private val tag = "InterstitialAdManager"
    private var interstitialAd: InterstitialAd? = null

    private fun loadInterstitialAd() {
        if (AdsSettings.disableAd) return
        InterstitialAd.load(
            context,
            secret.getFullScreenAdsID(),
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d(tag, "Failed: ${error.message}")
                    interstitialAd = null
                }
            },
        )
    }

    fun showInterstitialAdIfAvailable(activity: Activity, onCloseAd: () -> Unit = {}) {
        if (!activity.applicationContext.isConnected) {
            onCloseAd()
            return
        }
        if (AdsSettings.disableAd) {
            onCloseAd()
            return
        }
        if (AdsSettings.isInterstitialShowing) return
        val ad = interstitialAd
        if (ad == null) {
            loadInterstitialAd()
            onCloseAd()
            return
        }

        ad.show(activity)
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                AdsSettings.isInterstitialShowing = true
            }

            override fun onAdDismissedFullScreenContent() {
                AdsSettings.isInterstitialShowing = false
                interstitialAd = null
                onCloseAd()
                loadInterstitialAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d(tag, "Failed to show: ${adError.message}")
                onCloseAd()
            }
        }
    }
}
