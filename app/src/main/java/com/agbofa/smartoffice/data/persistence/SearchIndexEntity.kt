package com.agbofa.smartoffice.data.persistence

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "search_index",
    indices = [Index(value = ["type", "entityId"], unique = true)],
)
data class SearchIndexEntity(
    @PrimaryKey val id: String,
    val type: String,
    val entityId: String,
    val content: String,
    val createdAt: Long,
)
