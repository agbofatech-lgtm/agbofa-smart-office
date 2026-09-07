package com.agbofa.smartoffice.data.persistence

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "human_decisions",
    indices = [
        Index(value = ["createdAt"]),
        Index(value = ["subjectKind", "subjectTargetId"]),
    ],
)
data class DecisionEntity(
    @PrimaryKey val id: String,
    val subjectKind: String,
    val subjectTargetId: String,
    val actionType: String,
    val rationale: String,
    val createdAt: String,
    val basis: String,
)
