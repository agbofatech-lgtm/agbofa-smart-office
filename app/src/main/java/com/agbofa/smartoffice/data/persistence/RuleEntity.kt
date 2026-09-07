package com.agbofa.smartoffice.data.persistence

import androidx.room.Entity

@Entity(
    tableName = "rules",
    primaryKeys = ["id", "version"],
)
data class RuleEntity(
    val id: String,
    val version: String,
    val key: String,
    val condition: String,
    val decision: String,
    val createdAt: String,
    val basis: String,
)
