package com.agbofa.smartoffice.domain.journal

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

/**
 * Identity of a journal admission record.
 *
 * The caller supplies the raw value. This type does not generate IDs.
 */
@JvmInline
value class JournalEntryId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<JournalEntryId> {
            val trimmed = value.trim()
            return if (trimmed.isEmpty()) {
                DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "JournalEntryId must not be blank",
                        path = "JournalEntryId",
                    ),
                )
            } else {
                DomainResult.Success(JournalEntryId(trimmed))
            }
        }
    }
}
