package com.ltthuc.appupdate.api

/**
 * When to offer a flexible in-app update. Defaults are deliberately unpushy: Play's own guidance is
 * that a flexible update is a suggestion, and an app that re-asks on every launch trains users to
 * dismiss it.
 *
 * @param minStalenessDays days the update must have been available on Play before we mention it.
 *   `clientVersionStalenessDays` is null until Play has served the new version to this device for a
 *   day, so 0 means "ask the moment it appears" and is treated as no gate at all.
 * @param remindAfterDays how long to stay quiet after offering. Recorded when the sheet is SHOWN,
 *   not when it is dismissed — accepting moves the state to Downloading, so the snooze only ever
 *   affects a user who said no.
 * @param maxPromptsPerVersion stop offering the same version after this many attempts, whatever the
 *   calendar says.
 */
data class AppUpdateConfig(
    val minStalenessDays: Int = 3,
    val remindAfterDays: Int = 7,
    val maxPromptsPerVersion: Int = 3,
)

/** What the update pipeline is doing right now. Drive UI off this; it never blocks the app. */
sealed interface AppUpdateState {

    /** Nothing to offer, or the user has been asked recently enough. */
    data object Idle : AppUpdateState

    /** Play is fetching the APK in the background; the app stays fully usable. */
    data class Downloading(val bytesDownloaded: Long, val totalBytes: Long) : AppUpdateState {
        /** 0f..1f, or null while Play has not reported a total yet. */
        val fraction: Float? get() = totalBytes.takeIf { it > 0 }?.let { bytesDownloaded.toFloat() / it }
    }

    /**
     * Downloaded and waiting. The install only happens on restart, so the app MUST surface this —
     * a flexible update that is never completed is a download the user paid for and never got.
     */
    data object ReadyToInstall : AppUpdateState
}
