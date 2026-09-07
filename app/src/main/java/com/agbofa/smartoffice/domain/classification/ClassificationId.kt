package com.agbofa.smartoffice.domain.classification

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

/**
 * Identity of a classification revision.
 * Caller supplies the value. This type does not generate IDs.
 */
@JvmInline
value class ClassificationId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<ClassificationId> {
            val trimmed = value.trim()
            return if (trimmed.isEmpty()) {
                DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "ClassificationId must not be blank",
                        path = "ClassificationId",
                    ),
                )
            } else {
                DomainResult.Success(ClassificationId(trimmed))
            }
        }
    }
}
