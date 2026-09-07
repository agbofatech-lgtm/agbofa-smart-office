package com.agbofa.smartoffice.data.workflow

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

class InMemoryWorkflowRepository : WorkflowRepository {
    private val byId = LinkedHashMap<String, Workflow>()

    override fun save(workflow: Workflow): DomainResult<Workflow> {
        if (byId.containsKey(workflow.id.value)) {
            return DomainResult.Failure(DomainError.InvalidState("Workflow id already exists"))
        }
        if (byId.values.any { it.operationalRecordId == workflow.operationalRecordId }) {
            return DomainResult.Failure(DomainError.InvalidState("Workflow already exists for operational record"))
        }
        byId[workflow.id.value] = workflow
        return DomainResult.Success(workflow)
    }

    override fun saveWithSteps(workflow: Workflow, steps: List<WorkflowStep>): DomainResult<Workflow> {
        if (steps.isEmpty()) {
            return DomainResult.Failure(DomainError.ValidationError("Workflow requires at least one step", "steps"))
        }
        val saved = save(workflow)
        if (saved is DomainResult.Failure) return saved
        val stepStore = companionSteps ?: return DomainResult.Failure(
            DomainError.PersistenceFailure("In-memory workflow step store is not attached"),
        )
        val persisted = mutableListOf<WorkflowStep>()
        for (step in steps) {
            when (val result = stepStore.save(step)) {
                is DomainResult.Failure -> {
                    persisted.forEach { stepStore.remove(it.id) }
                    byId.remove(workflow.id.value)
                    return result
                }
                is DomainResult.Success -> persisted += result.value
            }
        }
        return DomainResult.Success(workflow)
    }

    internal var companionSteps: InMemoryWorkflowStepRepository? = null

    override fun findById(id: WorkflowId): Workflow? = byId[id.value]

    override fun findByOperationalRecordId(operationalRecordId: OperationalRecordId): Workflow? =
        byId.values.firstOrNull { it.operationalRecordId == operationalRecordId }

    override fun listAll(): List<Workflow> =
        byId.values.sortedBy { it.id.value }
}

class InMemoryWorkflowStepRepository : WorkflowStepRepository {
    private val byId = LinkedHashMap<String, WorkflowStep>()

    override fun save(step: WorkflowStep): DomainResult<WorkflowStep> {
        if (byId.containsKey(step.id.value)) {
            return DomainResult.Failure(DomainError.InvalidState("Workflow step id already exists"))
        }
        if (byId.values.any { it.workflowId == step.workflowId && it.ordinal == step.ordinal }) {
            return DomainResult.Failure(DomainError.InvalidState("Workflow step ordinal already exists"))
        }
        if (byId.values.any { it.workflowId == step.workflowId && it.key == step.key }) {
            return DomainResult.Failure(DomainError.InvalidState("Workflow step key already exists"))
        }
        byId[step.id.value] = step
        return DomainResult.Success(step)
    }

    internal fun remove(id: WorkflowStepId) {
        byId.remove(id.value)
    }

    override fun findById(id: WorkflowStepId): WorkflowStep? = byId[id.value]

    override fun listByWorkflowId(workflowId: WorkflowId): List<WorkflowStep> =
        WorkflowProgression.orderedSteps(byId.values.filter { it.workflowId == workflowId })
}

class InMemoryWorkflowStepTransitionRepository : WorkflowStepTransitionRepository {
    private val byId = LinkedHashMap<String, WorkflowStepTransition>()

    override fun save(transition: WorkflowStepTransition): DomainResult<WorkflowStepTransition> {
        if (byId.containsKey(transition.id.value)) {
            return DomainResult.Failure(DomainError.InvalidState("Workflow step transition id already exists"))
        }
        byId[transition.id.value] = transition
        return DomainResult.Success(transition)
    }

    override fun findById(id: WorkflowStepTransitionId): WorkflowStepTransition? = byId[id.value]

    override fun listByStepId(workflowStepId: WorkflowStepId): List<WorkflowStepTransition> =
        WorkflowStepProjection.ordered(byId.values.filter { it.workflowStepId == workflowStepId })

    override fun listByWorkflowStepIds(stepIds: List<WorkflowStepId>): List<WorkflowStepTransition> {
        val wanted = stepIds.map { it.value }.toSet()
        return WorkflowStepProjection.ordered(byId.values.filter { it.workflowStepId.value in wanted })
    }
}
