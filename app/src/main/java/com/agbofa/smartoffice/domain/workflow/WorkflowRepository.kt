package com.agbofa.smartoffice.domain.workflow

import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.operations.OperationalRecordId

interface WorkflowRepository {
    fun save(workflow: Workflow): DomainResult<Workflow>

    /**
     * Persist a workflow and its required steps atomically.
     * Implementations must not leave a workflow without its steps.
     */
    fun saveWithSteps(workflow: Workflow, steps: List<WorkflowStep>): DomainResult<Workflow>

    fun findById(id: WorkflowId): Workflow?
    fun findByOperationalRecordId(operationalRecordId: OperationalRecordId): Workflow?
    fun listAll(): List<Workflow>
}

interface WorkflowStepRepository {
    fun save(step: WorkflowStep): DomainResult<WorkflowStep>
    fun findById(id: WorkflowStepId): WorkflowStep?
    fun listByWorkflowId(workflowId: WorkflowId): List<WorkflowStep>
}

interface WorkflowStepTransitionRepository {
    fun save(transition: WorkflowStepTransition): DomainResult<WorkflowStepTransition>
    fun findById(id: WorkflowStepTransitionId): WorkflowStepTransition?
    fun listByStepId(workflowStepId: WorkflowStepId): List<WorkflowStepTransition>
    fun listByWorkflowStepIds(stepIds: List<WorkflowStepId>): List<WorkflowStepTransition>
}
