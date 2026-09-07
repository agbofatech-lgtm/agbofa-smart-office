package com.agbofa.smartoffice.data.persistence

import com.agbofa.smartoffice.domain.classification.Classification
import com.agbofa.smartoffice.domain.classification.ClassificationId
import com.agbofa.smartoffice.domain.classification.ClassificationRepository
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.journal.JournalEntryId

class RoomClassificationRepository(
    private val dao: ClassificationDao,
) : ClassificationRepository {
    override fun save(classification: Classification): DomainResult<Classification> {
        return try {
            dao.insert(classification.toEntity())
            DomainResult.Success(classification)
        } catch (error: Exception) {
            DomainResult.Failure(
                DomainError.PersistenceFailure(
                    error.message ?: "Failed to persist classification",
                ),
            )
        }
    }

    override fun findById(id: ClassificationId): Classification? =
        dao.findById(id.value)?.toDomain()

    override fun findActiveByJournalEntryId(journalEntryId: JournalEntryId): Classification? =
        dao.findActiveByJournalEntryId(journalEntryId.value)?.toDomain()

    override fun listByJournalEntryId(journalEntryId: JournalEntryId): List<Classification> =
        dao.listByJournalEntryId(journalEntryId.value).mapNotNull { it.toDomain() }

    override fun listActive(): List<Classification> =
        dao.listActive().mapNotNull { it.toDomain() }
}
