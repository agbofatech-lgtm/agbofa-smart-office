package com.agbofa.smartoffice.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OperationalStateTransitionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: OperationalStateTransitionEntity)

    @Query("SELECT * FROM operational_state_transitions WHERE id = :id LIMIT 1")
    fun findById(id: String): OperationalStateTransitionEntity?

    @Query("SELECT * FROM operational_state_transitions WHERE operationalRecordId = :operationalRecordId")
    fun listByOperationalRecordId(operationalRecordId: String): List<OperationalStateTransitionEntity>
}
