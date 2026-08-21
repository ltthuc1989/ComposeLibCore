package com.ltthuc.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.ltthuc.utils.secrets.ISecretAdsKey
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardedAdsManager(
    private val context: Context,
    private val secret: ISecretAdsKey,
) {
    init { loadRewardedAd() }

    private val tag = "RewardedAdsManager"
    private var rewardedAd: RewardedAd? = null

    /**
     * True when a rewarded ad is loaded and showable right now. Callers that gate a feature behind
     * a reward MUST check this before offering the user an opt-in prompt, so the app never promises
     * an ad it cannot serve (AdMob "Rewards implementation - User choice").
     */
    val isRewardedAdReady: Boolean
        get() = rewardedAd != null && !AdsSettings.disableAd && !AdsSettings.isRewardAdsShowing

    private fun loadRewardedAd() {
        if (AdsSettings.disableAd) return
        RewardedAd.load(
            context,
            secret.getRewardAdsID(),
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d(tag, "Failed: ${error.message}")
                    rewardedAd = null
                }
            },
        )
    }

    fun showRewardedAdIfAvailable(
        activity: Activity,
        onCloseAd: () -> Unit = {},
        onUserEarnedReward: (RewardItem) -> Unit = {},
    ) {
        if (!activity.applicationContext.isConnected) {
            onCloseAd()
            return
        }
        if (AdsSettings.disableAd) {
            onCloseAd()
            return
        }
        if (AdsSettings.isRewardAdsShowing) return

        val ad = rewardedAd
        if (ad == null) {
            onCloseAd()
            loadRewardedAd()
            return
        }

        ad.show(activity) { reward -> onUserEarnedReward(reward) }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                AdsSettings.isRewardAdsShowing = true
            }

            override fun onAdDismissedFullScreenContent() {
                AdsSettings.isRewardAdsShowing = false
                rewardedAd = null
                onCloseAd()
                loadRewardedAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d(tag, "Failed to show: ${adError.message}")
                onCloseAd()
            }
        }
    }
}
