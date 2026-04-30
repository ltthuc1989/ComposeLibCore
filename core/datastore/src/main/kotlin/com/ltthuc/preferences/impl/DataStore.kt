package com.ltthuc.preferences.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ltthuc.utils.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
internal class DataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "compose_template_datastore")

    private val accessToken = stringPreferencesKey("access_token")
    private val refreshToken = stringPreferencesKey("refresh_token")
    private val themeMode = stringPreferencesKey("theme_mode")

    private suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { it[key] = value }
    }

    private fun <T> observe(key: Preferences.Key<T>): Flow<T?> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }
            .map { preferences -> preferences[key] }

    private suspend fun <T> get(key: Preferences.Key<T>): T? = observe(key).first()

    fun themeMode(): Flow<ThemeMode> =
        observe(themeMode).map { stored ->
            stored?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.System
        }

    suspend fun setThemeMode(mode: ThemeMode) {
        save(themeMode, mode.name)
    }

    fun authorized(): Flow<Boolean> = observe(accessToken).map { !it.isNullOrEmpty() }

    suspend fun isAuthorized(): Boolean = !get(accessToken).isNullOrBlank()

    suspend fun getAccessToken(): String? = get(accessToken)

    suspend fun saveAccessToken(token: String) {
        save(accessToken, token)
    }

    suspend fun deleteAccessToken() {
        context.dataStore.edit { it.remove(accessToken) }
    }

    suspend fun getRefreshToken(): String? = get(refreshToken)

    suspend fun saveRefreshToken(token: String) {
        save(refreshToken, token)
    }
}
