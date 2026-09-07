package com.agbofa.smartoffice.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OperationalDependencyDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: OperationalDependencyEntity)

    @Query("SELECT * FROM operational_dependencies WHERE id = :id LIMIT 1")
    fun findById(id: String): OperationalDependencyEntity?

    @Query(
        "SELECT * FROM operational_dependencies " +
            "WHERE dependentOperationalRecordId = :dependentId " +
            "AND prerequisiteOperationalRecordId = :prerequisiteId LIMIT 1",
    )
    fun findByPair(dependentId: String, prerequisiteId: String): OperationalDependencyEntity?

    @Query("SELECT * FROM operational_dependencies")
    fun list(): List<OperationalDependencyEntity>

    @Query("SELECT * FROM operational_dependencies WHERE dependentOperationalRecordId = :dependentId")
    fun listByDependent(dependentId: String): List<OperationalDependencyEntity>

    @Query("SELECT * FROM operational_dependencies WHERE prerequisiteOperationalRecordId = :prerequisiteId")
    fun listByPrerequisite(prerequisiteId: String): List<OperationalDependencyEntity>
}
