package com.agbofa.smartoffice.data.persistence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "human_decision_transitions",
    foreignKeys = [
        ForeignKey(
            entity = DecisionEntity::class,
            parentColumns = ["id"],
            childColumns = ["decisionId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["decisionId"]),
        Index(value = ["transitionedAt"]),
    ],
)
data class DecisionTransitionEntity(
    @PrimaryKey val id: String,
    val decisionId: String,
    val fromStatus: String,
    val toStatus: String,
    val transitionedAt: String,
    val basis: String,
)
