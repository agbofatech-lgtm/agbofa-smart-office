package com.agbofa.smartoffice.domain.classification

import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.journal.JournalEntryId

/**
 * Persistence port for classification revisions.
 *
 * Implementations must not mutate Capture or Journal rows.
 */
interface ClassificationRepository {
    fun save(classification: Classification): DomainResult<Classification>
    fun findById(id: ClassificationId): Classification?
    fun findActiveByJournalEntryId(journalEntryId: JournalEntryId): Classification?
    fun listByJournalEntryId(journalEntryId: JournalEntryId): List<Classification>
    fun listActive(): List<Classification>
}
