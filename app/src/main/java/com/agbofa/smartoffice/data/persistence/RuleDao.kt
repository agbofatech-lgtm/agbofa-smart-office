package com.agbofa.smartoffice.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RuleDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: RuleEntity)

    @Query("SELECT * FROM rules WHERE id = :id AND version = :version LIMIT 1")
    fun find(id: String, version: String): RuleEntity?

    @Query("SELECT * FROM rules WHERE id = :id")
    fun listById(id: String): List<RuleEntity>

    @Query("SELECT * FROM rules")
    fun list(): List<RuleEntity>
}
