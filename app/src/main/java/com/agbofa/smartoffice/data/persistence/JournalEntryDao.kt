package com.agbofa.smartoffice.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface JournalEntryDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: JournalEntryEntity)

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    fun findById(id: String): JournalEntryEntity?

    @Query("SELECT * FROM journal_entries WHERE captureId = :captureId")
    fun findByCaptureId(captureId: String): JournalEntryEntity?

    @Query("SELECT * FROM journal_entries")
    fun list(): List<JournalEntryEntity>
}
