package com.ltthuc.ads

import android.app.Activity
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Locks [RewardedGate] against AdMob's "Rewards implementation - User choice" policy: the prompt is
 * only offered when an ad can actually be served, declining costs the user nothing, and an ad we
 * failed to serve never blocks the gated feature.
 */
class RewardedGateTest {

    private val activity = mockk<Activity>(relaxed = true)

    /** Records the callbacks instead of invoking them, so a test resolves them when it wants. */
    private class FakeShower(override var isReady: Boolean = true) : RewardedAdShower {
        var showCount = 0
        var onClose: (() -> Unit)? = null
        var onEarned: (() -> Unit)? = null

        override fun show(activity: Activity, onClose: () -> Unit, onEarned: () -> Unit) {
            showCount++
            this.onClose = onClose
            this.onEarned = onEarned
        }
    }

    /** The inline no-ad path: the manager calls back before `show` returns. */
    private class InlineCloseShower : RewardedAdShower {
        override val isReady = false
        override fun show(activity: Activity, onClose: () -> Unit, onEarned: () -> Unit) = onClose()
    }

    @Before fun setUp() { AdsSettings.disableAd = false }
    @After fun tearDown() { AdsSettings.disableAd = false }

    @Test
    fun shouldPrompt_trueOnlyWhenAdsOnAndAdLoaded() {
        assertTrue(RewardedGate(FakeShower(isReady = true)).shouldPrompt)
        assertFalse(
            "no ad loaded must skip the prompt so the feature is never blocked",
            RewardedGate(FakeShower(isReady = false)).shouldPrompt,
        )
    }

    @Test
    fun shouldPrompt_falseForPremium() {
        AdsSettings.disableAd = true
        assertFalse(RewardedGate(FakeShower(isReady = true)).shouldPrompt)
    }

    @Test
    fun premium_grantsDirectly_withoutShowingAd() {
        AdsSettings.disableAd = true
        val shower = FakeShower()
        var granted = false

        RewardedGate(shower).showRewarded(activity) { granted = true }

        assertTrue(granted)
        assertFalse("premium must not request an ad", shower.showCount > 0)
    }

    @Test
    fun rewardEarned_grants() {
        val shower = FakeShower()
        var granted = false

        RewardedGate(shower).showRewarded(activity) { granted = true }
        shower.onEarned!!()
        shower.onClose!!() // the manager always closes after the reward

        assertTrue(granted)
    }

    @Test
    fun dismissedWithoutReward_doesNotGrant() {
        val shower = FakeShower()
        var granted = false

        RewardedGate(shower).showRewarded(activity) { granted = true }
        shower.onClose!!() // presented, then closed early — resolves after `show` returned

        assertFalse("skipping the ad must not grant the reward", granted)
    }

    @Test
    fun adNeverPresented_grantsAnyway() {
        // Opted-in user, but the manager had nothing to show: never punish them for our failure to
        // serve — blocking here is the behaviour AdMob flags.
        var granted = false

        RewardedGate(InlineCloseShower()).showRewarded(activity) { granted = true }

        assertTrue(granted)
    }
}
