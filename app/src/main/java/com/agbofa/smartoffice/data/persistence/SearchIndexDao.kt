package com.agbofa.smartoffice.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchIndexDao {
    @Query("DELETE FROM search_index")
    fun clear()

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(entries: List<SearchIndexEntity>)

    @Query("SELECT * FROM search_index")
    fun list(): List<SearchIndexEntity>
}
