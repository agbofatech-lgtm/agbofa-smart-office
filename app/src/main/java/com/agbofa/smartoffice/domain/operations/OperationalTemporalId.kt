package com.agbofa.smartoffice.domain.operations

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

@JvmInline
value class OperationalTemporalId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<OperationalTemporalId> {
            val trimmed = value.trim()
            return if (trimmed.isEmpty()) {
                DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "OperationalTemporalId must not be blank",
                        path = "OperationalTemporalId",
                    ),
                )
            } else {
                DomainResult.Success(OperationalTemporalId(trimmed))
            }
        }
    }
}
