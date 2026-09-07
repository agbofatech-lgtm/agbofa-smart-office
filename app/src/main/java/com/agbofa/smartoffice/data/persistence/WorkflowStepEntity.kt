package com.agbofa.smartoffice.data.persistence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workflow_steps",
    foreignKeys = [
        ForeignKey(
            entity = WorkflowEntity::class,
            parentColumns = ["id"],
            childColumns = ["workflowId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["workflowId", "ordinal"], unique = true),
        Index(value = ["workflowId", "key"], unique = true),
        Index(value = ["workflowId"]),
    ],
)
data class WorkflowStepEntity(
    @PrimaryKey val id: String,
    val workflowId: String,
    val ordinal: Int,
    val key: String,
    val label: String,
)
