package com.agbofa.smartoffice.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CaptureDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: CaptureEntity)

    @Query("SELECT * FROM captures WHERE id = :id")
    fun findById(id: String): CaptureEntity?
}
