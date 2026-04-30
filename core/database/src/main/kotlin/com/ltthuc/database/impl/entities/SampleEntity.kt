package com.ltthuc.database.impl.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sample_items")
internal data class SampleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
)
