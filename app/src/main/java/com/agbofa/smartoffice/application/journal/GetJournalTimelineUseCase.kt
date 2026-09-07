package com.agbofa.smartoffice.application.journal

import com.agbofa.smartoffice.domain.capture.CaptureRepository
import com.agbofa.smartoffice.domain.classification.ClassificationRepository
import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.journal.JournalEntry
import com.agbofa.smartoffice.domain.journal.JournalRepository

class GetJournalTimelineUseCase(
    private val captures: CaptureRepository,
    private val journal: JournalRepository,
    private val classifications: ClassificationRepository? = null,
) {
    fun execute(): List<JournalRecord> {
        return journal.listAll()
            .sortedWith(CHRONOLOGY)
            .mapNotNull { entry -> toRecord(entry) }
    }

    private fun toRecord(entry: JournalEntry): JournalRecord? {
        val capture = captures.findById(entry.captureId) ?: return null
        val type = classifications
            ?.findActiveByJournalEntryId(entry.id)
            ?.type
            ?: ClassificationType.UNCLASSIFIED
        return JournalRecord(
            entryId = entry.id,
            captureId = entry.captureId,
            originalExpression = capture.originalExpression,
            capturedAt = capture.capturedAt,
            admittedAt = entry.admittedAt,
            classificationType = type,
        )
    }

    companion object {
        val CHRONOLOGY = compareBy<JournalEntry> { it.admittedAt.value }
            .thenBy { it.id.value }
    }
}
