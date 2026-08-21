package com.ltthuc.appupdate

import com.ltthuc.appupdate.api.AppUpdateConfig
import com.ltthuc.appupdate.impl.AppUpdatePolicy
import com.ltthuc.appupdate.impl.PromptHistory
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Locks the "should we bother the user" rules — the only branching this module owns. */
class AppUpdatePolicyTest {

    private val config = AppUpdateConfig(minStalenessDays = 3, remindAfterDays = 7, maxPromptsPerVersion = 3)
    private val routine = 0 // updatePriority Play reports for an ordinary release
    private val never = PromptHistory(count = 0, atMillis = 0L)
    private val now = 1_000L * 60 * 60 * 24 * 100 // day 100, so "days ago" arithmetic stays positive

    private fun daysAgo(days: Int) = now - days * 24L * 60 * 60 * 1000

    @Test
    fun freshUpdate_notOfferedUntilItHasAged() {
        assertFalse(AppUpdatePolicy.shouldOffer(stalenessDays = 2, updatePriority = routine, history = never, nowMillis = now, config = config))
        assertTrue(AppUpdatePolicy.shouldOffer(stalenessDays = 3, updatePriority = routine, history = never, nowMillis = now, config = config))
    }

    @Test
    fun nullStaleness_treatedAsZero_notAsUnknownGoAhead() {
        // Play reports null until the update has been served to this device for a day. Reading that
        // as "no information, offer anyway" would defeat minStalenessDays on every fresh release.
        assertFalse(AppUpdatePolicy.shouldOffer(stalenessDays = null, updatePriority = routine, history = never, nowMillis = now, config = config))
    }

    @Test
    fun nullStaleness_offeredWhenNoStalenessGateIsSet() {
        val eager = config.copy(minStalenessDays = 0)
        assertTrue(AppUpdatePolicy.shouldOffer(stalenessDays = null, updatePriority = routine, history = never, nowMillis = now, config = eager))
    }

    @Test
    fun recentlyDeclined_staysQuietUntilTheReminderWindowPasses() {
        val declinedYesterday = PromptHistory(count = 1, atMillis = daysAgo(1))
        assertFalse(AppUpdatePolicy.shouldOffer(stalenessDays = 30, updatePriority = routine, history = declinedYesterday, nowMillis = now, config = config))

        val declinedLastWeek = PromptHistory(count = 1, atMillis = daysAgo(7))
        assertTrue(AppUpdatePolicy.shouldOffer(stalenessDays = 30, updatePriority = routine, history = declinedLastWeek, nowMillis = now, config = config))
    }

    @Test
    fun askedTooManyTimes_stopsRegardlessOfTheCalendar() {
        val exhausted = PromptHistory(count = 3, atMillis = daysAgo(365))
        assertFalse(AppUpdatePolicy.shouldOffer(stalenessDays = 30, updatePriority = routine, history = exhausted, nowMillis = now, config = config))
    }

    @Test
    fun defaultConfig_offersImmediately_soAFreshReleaseIsNotInvisible() {
        // A non-zero minStalenessDays hides every new release for that many days, which reads as
        // "in-app update is broken" the first time anyone tests it. The default must not do that.
        assertTrue(
            AppUpdatePolicy.shouldOffer(
                stalenessDays = null, updatePriority = routine, history = never,
                nowMillis = now, config = AppUpdateConfig(),
            ),
        )
    }

    @Test
    fun highPriorityRelease_skipsTheStalenessWait() {
        val critical = config.priorityBypassesStaleness
        assertTrue(
            AppUpdatePolicy.shouldOffer(
                stalenessDays = null, updatePriority = critical, history = never,
                nowMillis = now, config = config,
            ),
        )
        // The bypass covers staleness only — a user asked yesterday still gets their quiet week.
        assertFalse(
            AppUpdatePolicy.shouldOffer(
                stalenessDays = null, updatePriority = critical,
                history = PromptHistory(count = 1, atMillis = daysAgo(1)),
                nowMillis = now, config = config,
            ),
        )
    }
}
