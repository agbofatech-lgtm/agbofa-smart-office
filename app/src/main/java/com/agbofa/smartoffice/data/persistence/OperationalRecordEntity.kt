package com.agbofa.smartoffice.data.persistence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "operational_records",
    foreignKeys = [
        ForeignKey(
            entity = JournalEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["journalEntryId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = ClassificationEntity::class,
            parentColumns = ["id"],
            childColumns = ["classificationId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["journalEntryId"], unique = true),
        Index(value = ["classificationId"]),
    ],
)
data class OperationalRecordEntity(
    @PrimaryKey val id: String,
    val journalEntryId: String,
    val classificationId: String,
    val type: String,
    val createdAt: String,
    val creationBasis: String,
    val ruleVersion: String?,
)
