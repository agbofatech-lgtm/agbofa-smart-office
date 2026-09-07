package com.agbofa.smartoffice.domain.operations

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

@JvmInline
value class OperationalDependencyId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<OperationalDependencyId> {
            val trimmed = value.trim()
            return if (trimmed.isEmpty()) {
                DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "OperationalDependencyId must not be blank",
                        path = "OperationalDependencyId",
                    ),
                )
            } else {
                DomainResult.Success(OperationalDependencyId(trimmed))
            }
        }
    }
}
