package com.ltthuc.rating.impl

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.ratingDataStore by preferencesDataStore("rating_prefs")
private const val DEFAULT_COUNT_KEY = "default_count"

@Singleton
internal class RatingDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val ratedKey = booleanPreferencesKey("is_rated")
    private fun countKey(key: String?) = intPreferencesKey("count_${key ?: DEFAULT_COUNT_KEY}")

    suspend fun increaseCount(key: String?, maxCount: Int) {
        context.ratingDataStore.edit { prefs ->
            val k = countKey(key)
            val current = prefs[k] ?: 0
            if (current < maxCount) prefs[k] = current + 1
        }
    }

    suspend fun getCount(key: String?): Int =
        context.ratingDataStore.data.first()[countKey(key)] ?: 0

    suspend fun resetCount(key: String?) {
        context.ratingDataStore.edit { it[countKey(key)] = 0 }
    }

    suspend fun setRated(value: Boolean) {
        context.ratingDataStore.edit { it[ratedKey] = value }
    }

    suspend fun isRated(): Boolean =
        context.ratingDataStore.data.first()[ratedKey] ?: false
}
