package com.agbofa.smartoffice.application.journal

import com.agbofa.smartoffice.domain.capture.CaptureId
import com.agbofa.smartoffice.domain.capture.CaptureRepository
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import com.agbofa.smartoffice.domain.journal.JournalEntry
import com.agbofa.smartoffice.domain.journal.JournalRepository

/**
 * Admit an existing Capture into the Journal once.
 */
class AdmitCaptureToJournalUseCase(
    private val captures: CaptureRepository,
    private val journal: JournalRepository,
) {
    fun execute(
        journalEntryId: String,
        captureIdValue: String,
        admittedAt: JournalAdmissionInstant,
    ): DomainResult<JournalEntry> {
        val captureId = when (val result = CaptureId.of(captureIdValue)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        val capture = captures.findById(captureId)
            ?: return DomainResult.Failure(
                DomainError.InvalidState("Capture does not exist"),
            )
        if (journal.findByCaptureId(capture.id) != null) {
            return DomainResult.Failure(
                DomainError.InvalidState("Capture already admitted to the journal"),
            )
        }
        val entry = when (val result = JournalEntry.from(journalEntryId, capture.id, admittedAt)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        return journal.save(entry)
    }
}
