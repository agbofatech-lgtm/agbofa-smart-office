package com.agbofa.smartoffice.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WorkflowDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: WorkflowEntity)

    @Query("SELECT * FROM workflows WHERE id = :id LIMIT 1")
    fun findById(id: String): WorkflowEntity?

    @Query("SELECT * FROM workflows WHERE operationalRecordId = :operationalRecordId LIMIT 1")
    fun findByOperationalRecordId(operationalRecordId: String): WorkflowEntity?
}

@Dao
interface WorkflowStepDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: WorkflowStepEntity)

    @Query("SELECT * FROM workflow_steps WHERE id = :id LIMIT 1")
    fun findById(id: String): WorkflowStepEntity?

    @Query("SELECT * FROM workflow_steps WHERE workflowId = :workflowId")
    fun listByWorkflowId(workflowId: String): List<WorkflowStepEntity>
}

@Dao
interface WorkflowStepTransitionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: WorkflowStepTransitionEntity)

    @Query("SELECT * FROM workflow_step_transitions WHERE id = :id LIMIT 1")
    fun findById(id: String): WorkflowStepTransitionEntity?

    @Query("SELECT * FROM workflow_step_transitions WHERE workflowStepId = :workflowStepId")
    fun listByStepId(workflowStepId: String): List<WorkflowStepTransitionEntity>

    @Query("SELECT * FROM workflow_step_transitions WHERE workflowStepId IN (:workflowStepIds)")
    fun listByStepIds(workflowStepIds: List<String>): List<WorkflowStepTransitionEntity>
}
