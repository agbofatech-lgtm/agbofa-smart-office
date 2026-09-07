package com.agbofa.smartoffice.data.classification

import com.agbofa.smartoffice.domain.classification.Classification
import com.agbofa.smartoffice.domain.classification.ClassificationId
import com.agbofa.smartoffice.domain.classification.ClassificationRepository
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.journal.JournalEntryId

/**
 * Process-local classification store for unit tests.
 * Production uses RoomClassificationRepository.
 */
class InMemoryClassificationRepository : ClassificationRepository {
    private val byId = LinkedHashMap<String, Classification>()

    override fun save(classification: Classification): DomainResult<Classification> {
        val existing = byId.values.filter {
            it.journalEntryId == classification.journalEntryId &&
                it.revision == classification.revision
        }
        if (existing.isNotEmpty()) {
            return DomainResult.Failure(
                DomainError.InvalidState("Classification revision already exists"),
            )
        }
        byId[classification.id.value] = classification
        return DomainResult.Success(classification)
    }

    override fun findById(id: ClassificationId): Classification? = byId[id.value]

    override fun findActiveByJournalEntryId(journalEntryId: JournalEntryId): Classification? =
        byId.values
            .filter { it.journalEntryId == journalEntryId }
            .maxByOrNull { it.revision }

    override fun listByJournalEntryId(journalEntryId: JournalEntryId): List<Classification> =
        byId.values
            .filter { it.journalEntryId == journalEntryId }
            .sortedBy { it.revision }

    override fun listActive(): List<Classification> =
        byId.values
            .groupBy { it.journalEntryId.value }
            .mapNotNull { (_, rows) -> rows.maxByOrNull { it.revision } }
}
