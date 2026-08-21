package com.ltthuc.appupdate.impl

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

private val Context.appUpdateDataStore by preferencesDataStore("app_update_prefs")

/**
 * Remembers which version we have already offered and when, so a declined update stays declined
 * across process deaths. Keyed by the available versionCode: a NEWER build resets the count, because
 * "no thanks to 1.4" is not "no thanks forever".
 */
@Singleton
internal class AppUpdateStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val versionKey = intPreferencesKey("prompted_version")
    private val atKey = longPreferencesKey("prompted_at")
    private val countKey = intPreferencesKey("prompt_count")

    suspend fun record(versionCode: Int, nowMillis: Long) {
        context.appUpdateDataStore.edit { prefs ->
            val same = prefs[versionKey] == versionCode
            prefs[versionKey] = versionCode
            prefs[atKey] = nowMillis
            prefs[countKey] = if (same) (prefs[countKey] ?: 0) + 1 else 1
        }
    }

    suspend fun history(versionCode: Int): PromptHistory {
        val prefs = context.appUpdateDataStore.data.first()
        if (prefs[versionKey] != versionCode) return PromptHistory(count = 0, atMillis = 0L)
        return PromptHistory(count = prefs[countKey] ?: 0, atMillis = prefs[atKey] ?: 0L)
    }
}

internal data class PromptHistory(val count: Int, val atMillis: Long)
