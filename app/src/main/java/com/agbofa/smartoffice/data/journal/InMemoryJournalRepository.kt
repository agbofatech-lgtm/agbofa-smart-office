package com.agbofa.smartoffice.data.journal

import com.agbofa.smartoffice.domain.capture.CaptureId
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.journal.JournalEntry
import com.agbofa.smartoffice.domain.journal.JournalEntryId
import com.agbofa.smartoffice.domain.journal.JournalRepository

/**
 * Process-local journal store used by unit tests.
 * Production wiring uses RoomJournalRepository.
 *
 * Shared maps allow a restart simulation without Room.
 */
class InMemoryJournalRepository(
    private val byId: MutableMap<String, JournalEntry> = LinkedHashMap(),
    private val byCapture: MutableMap<String, JournalEntry> = LinkedHashMap(),
) : JournalRepository {

    override fun save(entry: JournalEntry): DomainResult<JournalEntry> {
        if (byCapture.containsKey(entry.captureId.value)) {
            return DomainResult.Failure(
                DomainError.InvalidState("Capture already admitted"),
            )
        }
        byId[entry.id.value] = entry
        byCapture[entry.captureId.value] = entry
        return DomainResult.Success(entry)
    }

    override fun findById(id: JournalEntryId): JournalEntry? = byId[id.value]

    override fun findByCaptureId(captureId: CaptureId): JournalEntry? =
        byCapture[captureId.value]

    override fun listAll(): List<JournalEntry> = byId.values.toList()
}
