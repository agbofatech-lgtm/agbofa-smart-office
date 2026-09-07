package com.agbofa.smartoffice.application.journal

import com.agbofa.smartoffice.domain.capture.CaptureRepository
import com.agbofa.smartoffice.domain.journal.JournalEntry
import com.agbofa.smartoffice.domain.journal.JournalRepository

/**
 * Deterministic journal chronology.
 *
 * Primary order: JournalAdmissionInstant
 * Tie-break: JournalEntryId value
 *
 * Does not use insertion order. Does not own Capture text.
 */
class GetJournalTimelineUseCase(
    private val captures: CaptureRepository,
    private val journal: JournalRepository,
) {
    fun execute(): List<JournalRecord> {
        return journal.listAll()
            .sortedWith(CHRONOLOGY)
            .mapNotNull { entry -> toRecord(entry) }
    }

    private fun toRecord(entry: JournalEntry): JournalRecord? {
        val capture = captures.findById(entry.captureId) ?: return null
        return JournalRecord(
            entryId = entry.id,
            captureId = entry.captureId,
            originalExpression = capture.originalExpression,
            capturedAt = capture.capturedAt,
            admittedAt = entry.admittedAt,
        )
    }

    companion object {
        val CHRONOLOGY = compareBy<JournalEntry> { it.admittedAt.value }
            .thenBy { it.id.value }
    }
}
