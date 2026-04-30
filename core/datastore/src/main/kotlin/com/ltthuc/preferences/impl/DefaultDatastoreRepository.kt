package com.ltthuc.preferences.impl

import com.ltthuc.preferences.api.DatastoreRepository
import com.ltthuc.utils.ThemeMode
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
internal class DefaultDatastoreRepository @Inject constructor(
    private val dataStore: DataStore,
) : DatastoreRepository {

    override fun themeMode(): Flow<ThemeMode> = dataStore.themeMode()

    override suspend fun setThemeMode(mode: ThemeMode) = dataStore.setThemeMode(mode)

    override fun authorized(): Flow<Boolean> = dataStore.authorized()

    override suspend fun isAuthorized(): Boolean = dataStore.isAuthorized()

    override suspend fun getAccessToken(): String? = dataStore.getAccessToken()

    override suspend fun saveAccessToken(token: String) = dataStore.saveAccessToken(token)

    override suspend fun deleteAccessToken() = dataStore.deleteAccessToken()

    override suspend fun getRefreshToken(): String? = dataStore.getRefreshToken()

    override suspend fun saveRefreshToken(token: String) = dataStore.saveRefreshToken(token)
}
