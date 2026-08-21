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
    private val never = PromptHistory(count = 0, atMillis = 0L)
    private val now = 1_000L * 60 * 60 * 24 * 100 // day 100, so "days ago" arithmetic stays positive

    private fun daysAgo(days: Int) = now - days * 24L * 60 * 60 * 1000

    @Test
    fun freshUpdate_notOfferedUntilItHasAged() {
        assertFalse(AppUpdatePolicy.shouldOffer(stalenessDays = 2, history = never, nowMillis = now, config = config))
        assertTrue(AppUpdatePolicy.shouldOffer(stalenessDays = 3, history = never, nowMillis = now, config = config))
    }

    @Test
    fun nullStaleness_treatedAsZero_notAsUnknownGoAhead() {
        // Play reports null until the update has been served to this device for a day. Reading that
        // as "no information, offer anyway" would defeat minStalenessDays on every fresh release.
        assertFalse(AppUpdatePolicy.shouldOffer(stalenessDays = null, history = never, nowMillis = now, config = config))
    }

    @Test
    fun nullStaleness_offeredWhenNoStalenessGateIsSet() {
        val eager = config.copy(minStalenessDays = 0)
        assertTrue(AppUpdatePolicy.shouldOffer(stalenessDays = null, history = never, nowMillis = now, config = eager))
    }

    @Test
    fun recentlyDeclined_staysQuietUntilTheReminderWindowPasses() {
        val declinedYesterday = PromptHistory(count = 1, atMillis = daysAgo(1))
        assertFalse(AppUpdatePolicy.shouldOffer(stalenessDays = 30, history = declinedYesterday, nowMillis = now, config = config))

        val declinedLastWeek = PromptHistory(count = 1, atMillis = daysAgo(7))
        assertTrue(AppUpdatePolicy.shouldOffer(stalenessDays = 30, history = declinedLastWeek, nowMillis = now, config = config))
    }

    @Test
    fun askedTooManyTimes_stopsRegardlessOfTheCalendar() {
        val exhausted = PromptHistory(count = 3, atMillis = daysAgo(365))
        assertFalse(AppUpdatePolicy.shouldOffer(stalenessDays = 30, history = exhausted, nowMillis = now, config = config))
    }
}
