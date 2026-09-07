package com.agbofa.smartoffice.domain.foundation.identity

import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.error.DomainError

/**
 * Foundational operation identity.
 *
 * Not a task, journal, or finance identifier. Those belong to later phases.
 */
@JvmInline
value class OperationId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<OperationId> {
            val trimmed = value.trim()
            return if (trimmed.isEmpty()) {
                DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "OperationId must not be blank",
                        path = "OperationId",
                    ),
                )
            } else {
                DomainResult.Success(OperationId(trimmed))
            }
        }
    }
}
