package com.agbofa.smartoffice.data.persistence

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.workflow.Workflow
import com.agbofa.smartoffice.domain.workflow.WorkflowId
import com.agbofa.smartoffice.domain.workflow.WorkflowProgression
import com.agbofa.smartoffice.domain.workflow.WorkflowRepository
import com.agbofa.smartoffice.domain.workflow.WorkflowStep
import com.agbofa.smartoffice.domain.workflow.WorkflowStepId
import com.agbofa.smartoffice.domain.workflow.WorkflowStepProjection
import com.agbofa.smartoffice.domain.workflow.WorkflowStepRepository
import com.agbofa.smartoffice.domain.workflow.WorkflowStepTransition
import com.agbofa.smartoffice.domain.workflow.WorkflowStepTransitionId
import com.agbofa.smartoffice.domain.workflow.WorkflowStepTransitionRepository

class RoomWorkflowRepository(
    private val dao: WorkflowDao,
) : WorkflowRepository {
    override fun save(workflow: Workflow): DomainResult<Workflow> {
        if (dao.findById(workflow.id.value) != null) {
            return DomainResult.Failure(DomainError.InvalidState("Workflow id already exists"))
        }
        if (dao.findByOperationalRecordId(workflow.operationalRecordId.value) != null) {
            return DomainResult.Failure(DomainError.InvalidState("Workflow already exists for operational record"))
        }
        return try {
            dao.insert(workflow.toEntity())
            DomainResult.Success(workflow)
        } catch (error: Exception) {
            DomainResult.Failure(DomainError.PersistenceFailure(error.message ?: "Failed to persist workflow"))
        }
    }

    override fun findById(id: WorkflowId): Workflow? = dao.findById(id.value)?.toDomain()

    override fun findByOperationalRecordId(operationalRecordId: OperationalRecordId): Workflow? =
        dao.findByOperationalRecordId(operationalRecordId.value)?.toDomain()

    override fun listAll(): List<Workflow> =
        dao.list().mapNotNull { it.toDomain() }.sortedBy { it.id.value }
}

class RoomWorkflowStepRepository(
    private val dao: WorkflowStepDao,
) : WorkflowStepRepository {
    override fun save(step: WorkflowStep): DomainResult<WorkflowStep> {
        if (dao.findById(step.id.value) != null) {
            return DomainResult.Failure(DomainError.InvalidState("Workflow step id already exists"))
        }
        return try {
            dao.insert(step.toEntity())
            DomainResult.Success(step)
        } catch (error: Exception) {
            DomainResult.Failure(DomainError.PersistenceFailure(error.message ?: "Failed to persist workflow step"))
        }
    }

    override fun findById(id: WorkflowStepId): WorkflowStep? = dao.findById(id.value)?.toDomain()

    override fun listByWorkflowId(workflowId: WorkflowId): List<WorkflowStep> =
        WorkflowProgression.orderedSteps(dao.listByWorkflowId(workflowId.value).mapNotNull { it.toDomain() })
}

class RoomWorkflowStepTransitionRepository(
    private val dao: WorkflowStepTransitionDao,
) : WorkflowStepTransitionRepository {
    override fun save(transition: WorkflowStepTransition): DomainResult<WorkflowStepTransition> {
        if (dao.findById(transition.id.value) != null) {
            return DomainResult.Failure(DomainError.InvalidState("Workflow step transition id already exists"))
        }
        return try {
            dao.insert(transition.toEntity())
            DomainResult.Success(transition)
        } catch (error: Exception) {
            DomainResult.Failure(
                DomainError.PersistenceFailure(error.message ?: "Failed to persist workflow step transition"),
            )
        }
    }

    override fun findById(id: WorkflowStepTransitionId): WorkflowStepTransition? =
        dao.findById(id.value)?.toDomain()

    override fun listByStepId(workflowStepId: WorkflowStepId): List<WorkflowStepTransition> =
        WorkflowStepProjection.ordered(dao.listByStepId(workflowStepId.value).mapNotNull { it.toDomain() })

    override fun listByWorkflowStepIds(stepIds: List<WorkflowStepId>): List<WorkflowStepTransition> =
        WorkflowStepProjection.ordered(
            stepIds.flatMap { listByStepId(it) },
        )
}
