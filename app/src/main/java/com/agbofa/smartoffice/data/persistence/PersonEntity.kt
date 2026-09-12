package com.agbofa.smartoffice.data.persistence

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "people",
    indices = [
        Index(value = ["displayName"]),
        Index(value = ["category"]),
        Index(value = ["role"]),
        Index(value = ["archivedAt"]),
    ],
)
data class PersonEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val category: String?,
    val role: String?,
    val phone: String?,
    val email: String?,
    val location: String?,
    val createdAt: String,
    val updatedAt: String,
    val archivedAt: String?,
)
