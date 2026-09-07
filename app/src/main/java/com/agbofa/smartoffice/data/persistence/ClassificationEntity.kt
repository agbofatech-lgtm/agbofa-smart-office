package com.agbofa.smartoffice.data.persistence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Persistence row for a classification revision. Not a domain type.
 */
@Entity(
    tableName = "classifications",
    foreignKeys = [
        ForeignKey(
            entity = JournalEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["journalEntryId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["journalEntryId", "revision"], unique = true),
        Index(value = ["journalEntryId"]),
    ],
)
data class ClassificationEntity(
    @PrimaryKey val id: String,
    val journalEntryId: String,
    val type: String,
    val basis: String,
    val classifiedAt: String,
    val revision: Int,
    val ruleVersion: String?,
    val supersedesId: String?,
)
