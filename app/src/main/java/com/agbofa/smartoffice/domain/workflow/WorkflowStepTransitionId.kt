package com.agbofa.smartoffice.domain.workflow

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

@JvmInline
value class WorkflowStepTransitionId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<WorkflowStepTransitionId> {
            val trimmed = value.trim()
            return if (trimmed.isEmpty()) {
                DomainResult.Failure(
                    DomainError.ValidationError(
                        "WorkflowStepTransitionId must not be blank",
                        "WorkflowStepTransitionId",
                    ),
                )
            } else {
                DomainResult.Success(WorkflowStepTransitionId(trimmed))
            }
        }
    }
}
