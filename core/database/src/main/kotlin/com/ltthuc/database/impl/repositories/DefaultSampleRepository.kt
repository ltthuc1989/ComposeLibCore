package com.ltthuc.database.impl.repositories

import com.ltthuc.database.api.SampleRepository
import com.ltthuc.database.api.models.SampleItem
import com.ltthuc.database.impl.dao.SampleDao
import com.ltthuc.database.impl.entities.SampleEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DefaultSampleRepository @Inject constructor(
    private val dao: SampleDao,
) : SampleRepository {

    override fun observeAll(): Flow<List<SampleItem>> =
        dao.observeAll().map { entities -> entities.map { it.toModel() } }

    override suspend fun replaceAll(items: List<SampleItem>) {
        dao.replaceAll(items.map { it.toEntity() })
    }

    override suspend fun clear() {
        dao.deleteAll()
    }
}

private fun SampleEntity.toModel() = SampleItem(id = id, title = title, subtitle = subtitle)
private fun SampleItem.toEntity() = SampleEntity(id = id, title = title, subtitle = subtitle)
