package com.agbofa.smartoffice.domain.operations

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

/**
 * Identity of one immutable state transition.
 *
 * The caller supplies the raw value. This type does not generate IDs.
 */
@JvmInline
value class OperationalStateTransitionId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<OperationalStateTransitionId> {
            val trimmed = value.trim()
            return if (trimmed.isEmpty()) {
                DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "OperationalStateTransitionId must not be blank",
                        path = "OperationalStateTransitionId",
                    ),
                )
            } else {
                DomainResult.Success(OperationalStateTransitionId(trimmed))
            }
        }
    }
}
