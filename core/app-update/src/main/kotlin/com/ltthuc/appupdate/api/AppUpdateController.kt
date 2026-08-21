package com.ltthuc.appupdate.api

import android.app.Activity
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.requestAppUpdateInfo
import com.ltthuc.appupdate.impl.AppUpdatePolicy
import com.ltthuc.appupdate.impl.AppUpdateStore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "AppUpdate"

/**
 * Flexible in-app update: Play downloads the new APK in the background while the app stays fully
 * usable, and the install happens on the next restart. Nothing here ever blocks the UI.
 *
 * Install it once with [com.ltthuc.appupdate.api.AppUpdateAutoInit] and render [state]; the only
 * call a consumer makes by hand is [completeUpdate], from the "Restart" action of whatever bar it
 * shows for [AppUpdateState.ReadyToInstall].
 *
 * **A downloaded update that is never completed is the failure mode of this API** — the user has
 * paid for the bytes and gets nothing. That is why [state] surfaces `ReadyToInstall` instead of
 * quietly waiting for a restart that may never come.
 */
@Singleton
class AppUpdateController @Inject internal constructor(
    private val manager: AppUpdateManager,
    private val store: AppUpdateStore,
) {
    private val _state = MutableStateFlow<AppUpdateState>(AppUpdateState.Idle)
    val state: StateFlow<AppUpdateState> = _state.asStateFlow()

    private val listener = InstallStateUpdatedListener { install ->
        _state.value = when (install.installStatus()) {
            InstallStatus.DOWNLOADING ->
                AppUpdateState.Downloading(install.bytesDownloaded(), install.totalBytesToDownload())
            InstallStatus.DOWNLOADED -> AppUpdateState.ReadyToInstall
            else -> AppUpdateState.Idle
        }
    }

    private var listening = false

    /**
     * Run on every process foreground. Picks up a download that finished while the app was in the
     * background, then offers an update if [AppUpdatePolicy] says the user has not been asked too
     * recently. Silent on failure: no Play Store, no network and a sideloaded build all land here,
     * and none of them is worth an error in front of the user.
     */
    suspend fun refresh(activity: Activity, config: AppUpdateConfig, nowMillis: Long) {
        if (!listening) {
            manager.registerListener(listener)
            listening = true
        }
        val info = runCatching { manager.requestAppUpdateInfo() }
            .getOrElse {
                Log.d(TAG, "update check unavailable: ${it.message}")
                return
            }

        // A download can complete while the app is backgrounded, and the listener is not running
        // then — so the resumed state has to be read, not waited for.
        if (info.installStatus() == InstallStatus.DOWNLOADED) {
            _state.value = AppUpdateState.ReadyToInstall
            return
        }
        if (info.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE) return
        if (!info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) return

        val version = info.availableVersionCode()
        val offer = AppUpdatePolicy.shouldOffer(
            stalenessDays = info.clientVersionStalenessDays(),
            history = store.history(version),
            nowMillis = nowMillis,
            config = config,
        )
        if (!offer) return

        // Recorded when the sheet is SHOWN, not when it is answered: accepting moves us to
        // Downloading, so this only ever silences a user who declined — and it survives the
        // process death that a decline-then-quit would otherwise erase.
        store.record(version, nowMillis)
        runCatching {
            manager.startUpdateFlowForResult(
                info,
                activity,
                AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
                REQUEST_CODE,
            )
        }.onFailure { Log.d(TAG, "could not start update flow: ${it.message}") }
    }

    /** Restarts the app and installs the downloaded update. Wire to the "Restart" action. */
    fun completeUpdate() {
        manager.completeUpdate()
    }

    internal fun stopListening() {
        if (listening) {
            manager.unregisterListener(listener)
            listening = false
        }
    }

    companion object {
        /** Arrives at the host Activity's `onActivityResult`; ignoring it is fine for flexible. */
        const val REQUEST_CODE = 8_531
    }
}
