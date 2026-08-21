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
        updatePriority: Int,
        history: PromptHistory,
        nowMillis: Long,
        config: AppUpdateConfig,
    ): Boolean {
        // Play reports null for about the first day after it starts serving a release, so null
        // counts as 0 — which means a non-zero minStalenessDays hides a fresh release for exactly
        // that long. A high-priority release skips the wait outright.
        val urgent = updatePriority >= config.priorityBypassesStaleness
        if (!urgent && (stalenessDays ?: 0) < config.minStalenessDays) return false
        if (history.count == 0) return true
        if (history.count >= config.maxPromptsPerVersion) return false
        return nowMillis - history.atMillis >= config.remindAfterDays * MILLIS_PER_DAY
    }
}
