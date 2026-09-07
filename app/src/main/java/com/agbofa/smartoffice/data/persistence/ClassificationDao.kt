package com.agbofa.smartoffice.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ClassificationDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: ClassificationEntity)

    @Query("SELECT * FROM classifications WHERE id = :id LIMIT 1")
    fun findById(id: String): ClassificationEntity?

    @Query(
        "SELECT * FROM classifications WHERE journalEntryId = :journalEntryId " +
            "ORDER BY revision DESC LIMIT 1",
    )
    fun findActiveByJournalEntryId(journalEntryId: String): ClassificationEntity?

    @Query(
        "SELECT * FROM classifications WHERE journalEntryId = :journalEntryId " +
            "ORDER BY revision ASC",
    )
    fun listByJournalEntryId(journalEntryId: String): List<ClassificationEntity>

    @Query(
        "SELECT c.* FROM classifications c " +
            "INNER JOIN (" +
            "  SELECT journalEntryId, MAX(revision) AS revision " +
            "  FROM classifications GROUP BY journalEntryId" +
            ") latest ON c.journalEntryId = latest.journalEntryId " +
            "AND c.revision = latest.revision",
    )
    fun listActive(): List<ClassificationEntity>
}
