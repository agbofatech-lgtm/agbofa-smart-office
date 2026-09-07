package com.agbofa.smartoffice.domain.workflow

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

@JvmInline
value class WorkflowId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<WorkflowId> {
            val trimmed = value.trim()
            return if (trimmed.isEmpty()) {
                DomainResult.Failure(
                    DomainError.ValidationError("WorkflowId must not be blank", "WorkflowId"),
                )
            } else {
                DomainResult.Success(WorkflowId(trimmed))
            }
        }
    }
}
