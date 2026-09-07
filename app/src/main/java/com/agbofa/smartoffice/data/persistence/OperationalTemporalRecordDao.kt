package com.agbofa.smartoffice.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OperationalTemporalRecordDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: OperationalTemporalRecordEntity)

    @Query("SELECT * FROM operational_temporal_records WHERE id = :id LIMIT 1")
    fun findById(id: String): OperationalTemporalRecordEntity?

    @Query("SELECT * FROM operational_temporal_records WHERE operationalRecordId = :operationalRecordId")
    fun listByOperationalRecordId(operationalRecordId: String): List<OperationalTemporalRecordEntity>
}
