package com.agbofa.smartoffice.application.workflow

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.WorkflowTransitionInstant
import com.agbofa.smartoffice.domain.workflow.WorkflowBasis
import com.agbofa.smartoffice.domain.workflow.WorkflowStepId
import com.agbofa.smartoffice.domain.workflow.WorkflowStepProjection
import com.agbofa.smartoffice.domain.workflow.WorkflowStepRepository
import com.agbofa.smartoffice.domain.workflow.WorkflowStepStatus
import com.agbofa.smartoffice.domain.workflow.WorkflowStepTransition
import com.agbofa.smartoffice.domain.workflow.WorkflowStepTransitionId
import com.agbofa.smartoffice.domain.workflow.WorkflowStepTransitionRepository

class TransitionWorkflowStepUseCase(
    private val steps: WorkflowStepRepository,
    private val transitions: WorkflowStepTransitionRepository,
) {
    fun execute(
        transitionId: String,
        workflowStepIdValue: String,
        toStatus: WorkflowStepStatus,
        transitionedAt: WorkflowTransitionInstant,
        basis: WorkflowBasis = WorkflowBasis.MANUAL,
        ruleVersion: String? = null,
    ): DomainResult<WorkflowStepTransition> {
        val stepId = when (val result = WorkflowStepId.of(workflowStepIdValue)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        if (steps.findById(stepId) == null) {
            return DomainResult.Failure(DomainError.InvalidState("Workflow step does not exist"))
        }
        val current = WorkflowStepProjection.current(transitions.listByStepId(stepId))
        val id = when (val result = WorkflowStepTransitionId.of(transitionId)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        val transition = when (
            val result = WorkflowStepTransition.of(
                id = id,
                workflowStepId = stepId,
                fromStatus = current,
                toStatus = toStatus,
                transitionedAt = transitionedAt,
                basis = basis,
                ruleVersion = ruleVersion,
            )
        ) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        return transitions.save(transition)
    }
}
