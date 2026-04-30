package com.ltthuc.preferences.api

import com.ltthuc.utils.ThemeMode
import kotlinx.coroutines.flow.Flow

interface DatastoreRepository {
    fun themeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)

    fun authorized(): Flow<Boolean>
    suspend fun isAuthorized(): Boolean
    suspend fun getAccessToken(): String?
    suspend fun saveAccessToken(token: String)
    suspend fun deleteAccessToken()
    suspend fun getRefreshToken(): String?
    suspend fun saveRefreshToken(token: String)
}
