package com.ltthuc.appupdate.api

/**
 * When to offer a flexible in-app update. Defaults are deliberately unpushy: Play's own guidance is
 * that a flexible update is a suggestion, and an app that re-asks on every launch trains users to
 * dismiss it.
 *
 * @param minStalenessDays days the update must have been available on Play before we mention it.
 *   **Defaults to 0 — offer as soon as Play reports one.** `clientVersionStalenessDays` is null for
 *   roughly the first day after Play serves a release, and null counts as 0 here, so any positive
 *   value silences the feature entirely for that whole period. Raise it only if you genuinely want
 *   users sitting on a stale build, and expect a freshly published version to stay invisible for
 *   that many days.
 * @param priorityBypassesStaleness ignore [minStalenessDays] when Play reports an `updatePriority`
 *   at least this high. Priority is set per release in the Publishing API, so a critical fix ships
 *   at once even under a conservative staleness gate. Play's scale is 0–5.
 * @param remindAfterDays how long to stay quiet after offering. Recorded when the sheet is SHOWN,
 *   not when it is dismissed — accepting moves the state to Downloading, so the snooze only ever
 *   affects a user who said no.
 * @param maxPromptsPerVersion stop offering the same version after this many attempts, whatever the
 *   calendar says.
 */
data class AppUpdateConfig(
    val minStalenessDays: Int = 0,
    val priorityBypassesStaleness: Int = 4,
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
