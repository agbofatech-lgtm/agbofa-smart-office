package com.agbofa.smartoffice.data.persistence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workflows",
    foreignKeys = [
        ForeignKey(
            entity = OperationalRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["operationalRecordId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index(value = ["operationalRecordId"], unique = true)],
)
data class WorkflowEntity(
    @PrimaryKey val id: String,
    val operationalRecordId: String,
    val createdAt: String,
    val basis: String,
    val ruleVersion: String?,
)
