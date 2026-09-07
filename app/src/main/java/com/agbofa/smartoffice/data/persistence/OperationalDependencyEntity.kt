package com.agbofa.smartoffice.data.persistence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "operational_dependencies",
    foreignKeys = [
        ForeignKey(
            entity = OperationalRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["dependentOperationalRecordId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = OperationalRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["prerequisiteOperationalRecordId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(
            value = ["dependentOperationalRecordId", "prerequisiteOperationalRecordId"],
            unique = true,
        ),
        Index(value = ["dependentOperationalRecordId"]),
        Index(value = ["prerequisiteOperationalRecordId"]),
    ],
)
data class OperationalDependencyEntity(
    @PrimaryKey val id: String,
    val dependentOperationalRecordId: String,
    val prerequisiteOperationalRecordId: String,
    val type: String,
    val createdAt: String,
    val basis: String,
    val ruleVersion: String?,
)
