package com.agbofa.smartoffice.data.persistence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "operational_state_transitions",
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
        Index(value = ["transitionedAt"]),
    ],
)
data class OperationalStateTransitionEntity(
    @PrimaryKey val id: String,
    val operationalRecordId: String,
    val fromState: String,
    val toState: String,
    val transitionedAt: String,
    val basis: String,
    val ruleVersion: String?,
)
