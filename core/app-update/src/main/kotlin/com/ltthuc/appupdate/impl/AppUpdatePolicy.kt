package com.ltthuc.appupdate.impl

import com.ltthuc.appupdate.api.AppUpdateConfig

private const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000

/**
 * The whole "should we bother the user" decision, pulled out of the Play plumbing so it can be
 * tested without a device. Everything above it is I/O.
 */
internal object AppUpdatePolicy {

    fun shouldOffer(
        stalenessDays: Int?,
        history: PromptHistory,
        nowMillis: Long,
        config: AppUpdateConfig,
    ): Boolean {
        // Play reports null until the update has been served to this device for a day. Treating
        // that as 0 would offer instantly and defeat minStalenessDays.
        if ((stalenessDays ?: 0) < config.minStalenessDays) return false
        if (history.count == 0) return true
        if (history.count >= config.maxPromptsPerVersion) return false
        return nowMillis - history.atMillis >= config.remindAfterDays * MILLIS_PER_DAY
    }
}
