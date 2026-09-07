package com.agbofa.smartoffice.data.persistence

import com.agbofa.smartoffice.domain.capture.CaptureId
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.journal.JournalEntry
import com.agbofa.smartoffice.domain.journal.JournalEntryId
import com.agbofa.smartoffice.domain.journal.JournalRepository

class RoomJournalRepository(
    private val dao: JournalEntryDao,
) : JournalRepository {
    override fun save(entry: JournalEntry): DomainResult<JournalEntry> {
        return try {
            dao.insert(entry.toEntity())
            DomainResult.Success(entry)
        } catch (error: Exception) {
            DomainResult.Failure(
                DomainError.PersistenceFailure(error.message ?: "Journal persist failed"),
            )
        }
    }

    override fun findById(id: JournalEntryId): JournalEntry? = dao.findById(id.value)?.toDomain()

    override fun findByCaptureId(captureId: CaptureId): JournalEntry? =
        dao.findByCaptureId(captureId.value)?.toDomain()

    override fun listAll(): List<JournalEntry> = dao.list().mapNotNull { it.toDomain() }
}
