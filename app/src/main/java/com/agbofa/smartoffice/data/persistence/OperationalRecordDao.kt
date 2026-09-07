package com.agbofa.smartoffice.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OperationalRecordDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: OperationalRecordEntity)

    @Query("SELECT * FROM operational_records WHERE id = :id LIMIT 1")
    fun findById(id: String): OperationalRecordEntity?

    @Query("SELECT * FROM operational_records WHERE journalEntryId = :journalEntryId LIMIT 1")
    fun findByJournalEntryId(journalEntryId: String): OperationalRecordEntity?

    @Query("SELECT * FROM operational_records")
    fun listAll(): List<OperationalRecordEntity>
}
