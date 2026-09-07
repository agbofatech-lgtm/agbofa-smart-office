package com.agbofa.smartoffice.data.persistence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "authorized_action_executions",
    foreignKeys = [
        ForeignKey(
            entity = AuthorizedActionRequestEntity::class,
            parentColumns = ["id"],
            childColumns = ["requestId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["requestId"], unique = true),
        Index(value = ["decisionId"]),
    ],
)
data class AuthorizedActionExecutionEntity(
    @PrimaryKey val id: String,
    val requestId: String,
    val decisionId: String,
    val executedAt: String,
    val outcome: String,
    val detail: String,
)
