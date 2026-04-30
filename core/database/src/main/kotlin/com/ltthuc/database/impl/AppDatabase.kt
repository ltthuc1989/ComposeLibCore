package com.ltthuc.database.impl

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ltthuc.database.impl.dao.SampleDao
import com.ltthuc.database.impl.entities.SampleEntity

@Database(
    entities = [SampleEntity::class],
    version = 1,
    exportSchema = false,
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun sampleDao(): SampleDao
}

internal fun buildDatabase(context: Context): AppDatabase =
    Room.databaseBuilder(context, AppDatabase::class.java, "ComposeTemplateDatabase")
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
