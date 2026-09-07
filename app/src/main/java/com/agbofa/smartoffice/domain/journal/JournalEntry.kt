package com.agbofa.smartoffice.domain.journal

import com.agbofa.smartoffice.domain.capture.CaptureId
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant

/**
 * Append-only operational history record.
 *
 * References Capture evidence. Does not own or copy the original
 * expression. Does not classify, price, or schedule that evidence.
 */
data class JournalEntry private constructor(
    val id: JournalEntryId,
    val captureId: CaptureId,
    val admittedAt: JournalAdmissionInstant,
) {
    companion object {
        fun of(
            id: JournalEntryId,
            captureId: CaptureId,
            admittedAt: JournalAdmissionInstant,
        ): DomainResult<JournalEntry> {
            return DomainResult.Success(
                JournalEntry(
                    id = id,
                    captureId = captureId,
                    admittedAt = admittedAt,
                ),
            )
        }

        fun from(
            id: String,
            captureId: CaptureId,
            admittedAt: JournalAdmissionInstant,
        ): DomainResult<JournalEntry> {
            val journalId = when (val result = JournalEntryId.of(id)) {
                is DomainResult.Failure -> return result
                is DomainResult.Success -> result.value
            }
            return of(journalId, captureId, admittedAt)
        }
    }
}
