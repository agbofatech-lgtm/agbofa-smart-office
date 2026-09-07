package com.agbofa.smartoffice.domain.capture

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

/**
 * Identity of a capture record.
 *
 * The caller supplies the raw value. This type does not generate IDs.
 */
@JvmInline
value class CaptureId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<CaptureId> {
            val trimmed = value.trim()
            return if (trimmed.isEmpty()) {
                DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "CaptureId must not be blank",
                        path = "CaptureId",
                    ),
                )
            } else {
                DomainResult.Success(CaptureId(trimmed))
            }
        }
    }
}
