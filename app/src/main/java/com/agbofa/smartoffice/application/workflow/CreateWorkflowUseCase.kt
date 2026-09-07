package com.agbofa.smartoffice.application.workflow

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.WorkflowCreationInstant
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalRecordRepository
import com.agbofa.smartoffice.domain.workflow.Workflow
import com.agbofa.smartoffice.domain.workflow.WorkflowBasis
import com.agbofa.smartoffice.domain.workflow.WorkflowId
import com.agbofa.smartoffice.domain.workflow.WorkflowRepository
import com.agbofa.smartoffice.domain.workflow.WorkflowStep
import com.agbofa.smartoffice.domain.workflow.WorkflowStepId
import com.agbofa.smartoffice.domain.workflow.WorkflowStepRepository

class CreateWorkflowUseCase(
    private val records: OperationalRecordRepository,
    private val workflows: WorkflowRepository,
    private val steps: WorkflowStepRepository,
) {
    fun execute(
        workflowId: String,
        operationalRecordIdValue: String,
        createdAt: WorkflowCreationInstant,
        stepSpecs: List<WorkflowStepDefinition>,
        basis: WorkflowBasis = WorkflowBasis.MANUAL,
        ruleVersion: String? = null,
    ): DomainResult<Workflow> {
        val recordId = when (val result = OperationalRecordId.of(operationalRecordIdValue)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        if (records.findById(recordId) == null) {
            return DomainResult.Failure(DomainError.InvalidState("Operational record does not exist"))
        }
        if (workflows.findByOperationalRecordId(recordId) != null) {
            return DomainResult.Failure(DomainError.InvalidState("Workflow already exists for operational record"))
        }
        if (stepSpecs.isEmpty()) {
            return DomainResult.Failure(DomainError.ValidationError("Workflow requires at least one step", "steps"))
        }
        if (stepSpecs.map { it.ordinal }.toSet().size != stepSpecs.size) {
            return DomainResult.Failure(DomainError.InvalidState("Workflow step ordinals must be unique"))
        }
        if (stepSpecs.map { it.key.trim() }.toSet().size != stepSpecs.size) {
            return DomainResult.Failure(DomainError.InvalidState("Workflow step keys must be unique"))
        }
        val id = when (val result = WorkflowId.of(workflowId)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        val workflow = when (
            val result = Workflow.of(id, recordId, createdAt, basis, ruleVersion)
        ) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        val prepared = mutableListOf<WorkflowStep>()
        for (spec in stepSpecs.sortedBy { it.ordinal }) {
            val stepId = when (val result = WorkflowStepId.of(spec.stepId)) {
                is DomainResult.Failure -> return result
                is DomainResult.Success -> result.value
            }
            val step = when (val result = WorkflowStep.of(stepId, workflow.id, spec.ordinal, spec.key, spec.label)) {
                is DomainResult.Failure -> return result
                is DomainResult.Success -> result.value
            }
            prepared += step
        }
        return workflows.saveWithSteps(workflow, prepared)
    }
}
