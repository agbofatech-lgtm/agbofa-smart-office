package com.agbofa.smartoffice.data.persistence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "authorized_action_requests",
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
        Index(value = ["requestedAt"]),
    ],
)
data class AuthorizedActionRequestEntity(
    @PrimaryKey val id: String,
    val decisionId: String,
    val actionType: String,
    val targetId: String,
    val requestedAt: String,
    val toStateName: String?,
    val completeTransitionId: String?,
    val activateTransitionId: String?,
)
