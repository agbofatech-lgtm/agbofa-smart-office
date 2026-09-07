package com.agbofa.smartoffice.data.persistence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workflow_step_transitions",
    foreignKeys = [
        ForeignKey(
            entity = WorkflowStepEntity::class,
            parentColumns = ["id"],
            childColumns = ["workflowStepId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["workflowStepId"]),
        Index(value = ["transitionedAt"]),
    ],
)
data class WorkflowStepTransitionEntity(
    @PrimaryKey val id: String,
    val workflowStepId: String,
    val fromStatus: String,
    val toStatus: String,
    val transitionedAt: String,
    val basis: String,
    val ruleVersion: String?,
)
