package com.ltthuc.ads

import android.app.Activity

/**
 * Testable seam over [RewardedAdsManager]. [onClose] fires on every non-reward outcome (no ad,
 * dismissed, failed to show); [onEarned] only when the reward is granted.
 */
interface RewardedAdShower {
    val isReady: Boolean
    fun show(activity: Activity, onClose: () -> Unit, onEarned: () -> Unit)
}

/**
 * Gates a feature behind a rewarded ad under AdMob's **"Rewards implementation - User choice"**
 * policy. The policy is not about the ad — it is about the tap that starts it: a rewarded ad may
 * only begin after the user reads what the reward is and explicitly accepts, and declining must
 * cost them nothing.
 *
 * Drive it in two steps, never one:
 *
 * ```kotlin
 * if (!gate.shouldPrompt) {
 *     unlock()                                  // premium, or no ad to serve — never block
 * } else {
 *     showPrompt {                              // RewardedPrompt; its onWatch does:
 *         gate.showRewarded(activity, ::unlock)
 *     }
 * }
 * ```
 *
 * Calling [showRewarded] straight from the feature's own button is the violation this class
 * exists to prevent.
 */
class RewardedGate(private val shower: RewardedAdShower) {

    /**
     * True only when there is a real ad to serve, so the prompt never promises one that cannot
     * play. Premium users and an empty ad cache both skip the prompt and get the feature for free:
     * gating is a monetisation choice, and a core feature must not depend on ad availability.
     */
    val shouldPrompt: Boolean
        get() = !AdsSettings.disableAd && shower.isReady

    /**
     * Runs after an explicit opt-in. Reward earned → [onGranted]. Ad never started despite the
     * readiness check → [onGranted] too: the user agreed, so a failure to serve is ours, not
     * theirs. Ad shown and dismissed early → nothing happens and the feature stays available.
     */
    fun showRewarded(activity: Activity, onGranted: () -> Unit) {
        if (AdsSettings.disableAd) {
            onGranted()
            return
        }
        var earned = false
        // RewardedAdsManager calls back inline only when it never presented an ad (none loaded, no
        // network, ads disabled); a real presentation always resolves asynchronously.
        var neverPresented = true
        shower.show(
            activity,
            onClose = { if (!earned && neverPresented) onGranted() },
            onEarned = { earned = true; onGranted() },
        )
        neverPresented = false
    }

    companion object {
        fun from(manager: RewardedAdsManager): RewardedGate = RewardedGate(
            object : RewardedAdShower {
                override val isReady: Boolean get() = manager.isRewardedAdReady

                override fun show(activity: Activity, onClose: () -> Unit, onEarned: () -> Unit) =
                    manager.showRewardedAdIfAvailable(
                        activity = activity,
                        onCloseAd = onClose,
                        onUserEarnedReward = { onEarned() },
                    )
            },
        )
    }
}
