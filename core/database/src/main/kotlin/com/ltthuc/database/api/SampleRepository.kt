package com.ltthuc.database.api

import com.ltthuc.database.api.models.SampleItem
import kotlinx.coroutines.flow.Flow

interface SampleRepository {
    fun observeAll(): Flow<List<SampleItem>>
    suspend fun replaceAll(items: List<SampleItem>)
    suspend fun clear()
}
