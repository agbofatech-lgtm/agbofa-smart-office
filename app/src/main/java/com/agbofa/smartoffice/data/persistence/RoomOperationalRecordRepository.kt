package com.agbofa.smartoffice.data.persistence

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.journal.JournalEntryId
import com.agbofa.smartoffice.domain.operations.OperationalRecord
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalRecordRepository

class RoomOperationalRecordRepository(
    private val dao: OperationalRecordDao,
) : OperationalRecordRepository {
    override fun save(record: OperationalRecord): DomainResult<OperationalRecord> {
        return try {
            dao.insert(record.toEntity())
            DomainResult.Success(record)
        } catch (error: Exception) {
            DomainResult.Failure(
                DomainError.PersistenceFailure(error.message ?: "Operational record persist failed"),
            )
        }
    }

    override fun findById(id: OperationalRecordId): OperationalRecord? =
        dao.findById(id.value)?.toDomain()

    override fun findByJournalEntryId(journalEntryId: JournalEntryId): OperationalRecord? =
        dao.findByJournalEntryId(journalEntryId.value)?.toDomain()

    override fun listAll(): List<OperationalRecord> = dao.listAll().mapNotNull { it.toDomain() }
}
