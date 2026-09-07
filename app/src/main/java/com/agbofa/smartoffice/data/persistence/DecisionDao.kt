package com.agbofa.smartoffice.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DecisionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: DecisionEntity)

    @Query("SELECT * FROM human_decisions WHERE id = :id LIMIT 1")
    fun findById(id: String): DecisionEntity?

    @Query("SELECT * FROM human_decisions")
    fun list(): List<DecisionEntity>
}

@Dao
interface DecisionTransitionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: DecisionTransitionEntity)

    @Query("SELECT * FROM human_decision_transitions WHERE id = :id LIMIT 1")
    fun findById(id: String): DecisionTransitionEntity?

    @Query("SELECT * FROM human_decision_transitions WHERE decisionId = :decisionId")
    fun listByDecisionId(decisionId: String): List<DecisionTransitionEntity>
}

@Dao
interface AuthorizedActionRequestDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: AuthorizedActionRequestEntity)

    @Query("SELECT * FROM authorized_action_requests WHERE id = :id LIMIT 1")
    fun findById(id: String): AuthorizedActionRequestEntity?

    @Query("SELECT * FROM authorized_action_requests WHERE decisionId = :decisionId")
    fun listByDecisionId(decisionId: String): List<AuthorizedActionRequestEntity>
}

@Dao
interface AuthorizedActionExecutionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: AuthorizedActionExecutionEntity)

    @Query("SELECT * FROM authorized_action_executions WHERE requestId = :requestId LIMIT 1")
    fun findByRequestId(requestId: String): AuthorizedActionExecutionEntity?

    @Query("SELECT * FROM authorized_action_executions WHERE decisionId = :decisionId")
    fun listByDecisionId(decisionId: String): List<AuthorizedActionExecutionEntity>
}

