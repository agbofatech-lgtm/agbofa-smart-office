package com.agbofa.smartoffice.data.persistence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "journal_entries",
    foreignKeys = [
        ForeignKey(
            entity = CaptureEntity::class,
            parentColumns = ["id"],
            childColumns = ["captureId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["captureId"], unique = true),
        Index(value = ["admittedAt"]),
    ],
)
data class JournalEntryEntity(
    @PrimaryKey val id: String,
    val captureId: String,
    val admittedAt: String,
)
