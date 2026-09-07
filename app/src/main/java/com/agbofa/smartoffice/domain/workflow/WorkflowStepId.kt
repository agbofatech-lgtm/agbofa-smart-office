package com.agbofa.smartoffice.domain.workflow

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

@JvmInline
value class WorkflowStepId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<WorkflowStepId> {
            val trimmed = value.trim()
            return if (trimmed.isEmpty()) {
                DomainResult.Failure(
                    DomainError.ValidationError("WorkflowStepId must not be blank", "WorkflowStepId"),
                )
            } else {
                DomainResult.Success(WorkflowStepId(trimmed))
            }
        }
    }
}
