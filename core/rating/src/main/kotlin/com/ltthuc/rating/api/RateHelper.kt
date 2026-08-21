package com.ltthuc.rating.api

import android.app.Activity
import com.ltthuc.rating.impl.RatingDataStore
import com.ltthuc.rating.impl.ReviewLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RateHelper @Inject internal constructor(
    private val ds: RatingDataStore,
    private val reviewLauncher: ReviewLauncher,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    private var config = RateConfig()
    private var isShowing = false
    private var pendingComplete: (() -> Unit)? = null

    private val _showCustomDialog = MutableStateFlow(false)
    val showCustomDialog: StateFlow<Boolean> = _showCustomDialog.asStateFlow()

    fun setConfig(config: RateConfig) {
        this.config = config
    }

    suspend fun increaseCount() {
        ds.increaseCount(config.key, config.numberOfTimes)
    }

    suspend fun requestRate(activity: Activity, onComplete: () -> Unit = {}) {
        if (config.autoIncreaseCount) ds.increaseCount(config.key, config.numberOfTimes)
        if (ds.isRated()) {
            launchReviewIfThreshold(activity, onComplete)
        } else {
            showCustomDialogIfThreshold(onComplete)
        }
    }

    /**
     * Records one event on [config] and then, if the threshold is met, shows the prompt — as one
     * atomic step.
     *
     * Apps with more than one trigger (app-open cadence, first-save, post-purchase) each need their
     * own [RateConfig.key], but this class holds a SINGLE mutable [config]. Two triggers overlapping
     * would otherwise interleave `setConfig` / `increaseCount` / `requestRate` and count against
     * each other's key. Routing every trigger through here serialises them.
     *
     * The count is written before [delayMs] elapses, mirroring how a launch counter must survive the
     * user quitting during the delay; only the presentation waits.
     */
    suspend fun recordAndRequest(
        activity: Activity,
        config: RateConfig,
        delayMs: Long = DEFAULT_PRESENT_DELAY_MS,
        onComplete: () -> Unit = {},
    ) {
        mutex.withLock {
            // autoIncreaseCount would double-count on top of the explicit increaseCount below.
            setConfig(config.copy(autoIncreaseCount = false))
            increaseCount()
            if (delayMs > 0) delay(delayMs)
            requestRate(activity, onComplete)
        }
    }

    fun showInAppReviewOnlyOnce(activity: Activity, onComplete: () -> Unit = {}) {
        scope.launch {
            val success = reviewLauncher.launch(activity, onComplete)
            if (success) ds.setRated(true)
        }
    }

    fun onRateNow(activity: Activity) {
        if (!_showCustomDialog.value) return
        _showCustomDialog.value = false
        isShowing = false
        val cb = pendingComplete
        pendingComplete = null
        scope.launch {
            val success = reviewLauncher.launch(activity, cb ?: {})
            if (success) ds.setRated(true)
        }
    }

    fun onLater() {
        if (!_showCustomDialog.value) return
        _showCustomDialog.value = false
        isShowing = false
        pendingComplete?.invoke()
        pendingComplete = null
    }

    private suspend fun launchReviewIfThreshold(activity: Activity, onComplete: () -> Unit) {
        if (isShowing) return
        if (ds.getCount(config.key) >= config.numberOfTimes) {
            isShowing = true
            ds.resetCount(config.key)
            scope.launch {
                reviewLauncher.launch(activity) {
                    isShowing = false
                    onComplete()
                }
            }
        } else {
            onComplete()
        }
    }

    private suspend fun showCustomDialogIfThreshold(onComplete: () -> Unit) {
        if (isShowing) return
        if (ds.getCount(config.key) >= config.numberOfTimes) {
            isShowing = true
            ds.resetCount(config.key)
            pendingComplete = onComplete
            _showCustomDialog.value = true
        } else {
            onComplete()
        }
    }

    companion object {
        /** Lets the window settle before the prompt appears; Play's review flow needs a resumed
         *  Activity, which a cold launch does not yet have when the trigger fires. */
        const val DEFAULT_PRESENT_DELAY_MS = 1_000L
    }
}
