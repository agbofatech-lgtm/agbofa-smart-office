package com.agbofa.smartoffice.data.persistence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "operational_temporal_records",
    foreignKeys = [
        ForeignKey(
            entity = OperationalRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["operationalRecordId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["operationalRecordId"]),
        Index(value = ["assignedAt"]),
    ],
)
data class OperationalTemporalRecordEntity(
    @PrimaryKey val id: String,
    val operationalRecordId: String,
    val resolution: String,
    val referenceExpression: String?,
    val dueInstant: String?,
    val civilDateTime: String?,
    val civilZone: String?,
    val basis: String,
    val ruleVersion: String?,
    val assignedAt: String,
)
