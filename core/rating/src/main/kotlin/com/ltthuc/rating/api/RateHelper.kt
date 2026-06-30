package com.ltthuc.rating.api

import android.app.Activity
import com.ltthuc.rating.impl.RatingDataStore
import com.ltthuc.rating.impl.ReviewLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RateHelper @Inject internal constructor(
    private val ds: RatingDataStore,
    private val reviewLauncher: ReviewLauncher,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
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
}
