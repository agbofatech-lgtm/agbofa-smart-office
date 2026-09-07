package com.agbofa.smartoffice.domain.operations

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

/**
 * Identity of one operational record.
 *
 * The caller supplies the raw value. This type does not generate IDs.
 */
@JvmInline
value class OperationalRecordId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<OperationalRecordId> {
            val trimmed = value.trim()
            return if (trimmed.isEmpty()) {
                DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "OperationalRecordId must not be blank",
                        path = "OperationalRecordId",
                    ),
                )
            } else {
                DomainResult.Success(OperationalRecordId(trimmed))
            }
        }
    }
}
