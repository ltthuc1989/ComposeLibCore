package com.ltthuc.database.impl.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ltthuc.database.impl.entities.SampleEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface SampleDao {

    @Query("SELECT * FROM sample_items")
    fun observeAll(): Flow<List<SampleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SampleEntity>)

    @Query("DELETE FROM sample_items")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(items: List<SampleEntity>) {
        deleteAll()
        insertAll(items)
    }
}
