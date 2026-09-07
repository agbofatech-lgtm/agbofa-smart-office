package com.agbofa.smartoffice.domain.workflow

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.WorkflowTransitionInstant

data class WorkflowStepTransition private constructor(
    val id: WorkflowStepTransitionId,
    val workflowStepId: WorkflowStepId,
    val fromStatus: WorkflowStepStatus,
    val toStatus: WorkflowStepStatus,
    val transitionedAt: WorkflowTransitionInstant,
    val basis: WorkflowBasis,
    val ruleVersion: String?,
) {
    companion object {
        fun of(
            id: WorkflowStepTransitionId,
            workflowStepId: WorkflowStepId,
            fromStatus: WorkflowStepStatus,
            toStatus: WorkflowStepStatus,
            transitionedAt: WorkflowTransitionInstant,
            basis: WorkflowBasis,
            ruleVersion: String? = null,
        ): DomainResult<WorkflowStepTransition> {
            if (!WorkflowStepPolicy.permitted(fromStatus, toStatus)) {
                return DomainResult.Failure(
                    DomainError.InvalidState("Transition $fromStatus → $toStatus is not permitted"),
                )
            }
            if (basis == WorkflowBasis.RULE && ruleVersion.isNullOrBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError("RULE workflow transition requires ruleVersion", "ruleVersion"),
                )
            }
            if (basis == WorkflowBasis.MANUAL && !ruleVersion.isNullOrBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        "MANUAL workflow transition must not carry ruleVersion",
                        "ruleVersion",
                    ),
                )
            }
            return DomainResult.Success(
                WorkflowStepTransition(
                    id, workflowStepId, fromStatus, toStatus, transitionedAt, basis, ruleVersion,
                ),
            )
        }
    }
}
