package com.agbofa.smartoffice.domain.operations

import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.journal.JournalEntryId

/**
 * Persistence port for operational records.
 *
 * Implementations must not rewrite Capture, Journal, or Classification.
 */
interface OperationalRecordRepository {
    fun save(record: OperationalRecord): DomainResult<OperationalRecord>
    fun findById(id: OperationalRecordId): OperationalRecord?
    fun findByJournalEntryId(journalEntryId: JournalEntryId): OperationalRecord?
    fun listAll(): List<OperationalRecord>
}
