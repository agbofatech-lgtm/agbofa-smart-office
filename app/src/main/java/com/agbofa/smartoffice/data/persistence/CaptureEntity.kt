package com.agbofa.smartoffice.data.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "captures")
data class CaptureEntity(
    @PrimaryKey val id: String,
    val originalExpression: String,
    val capturedAt: String,
    val source: String,
)
