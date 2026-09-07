package com.agbofa.smartoffice.data.operations

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.journal.JournalEntryId
import com.agbofa.smartoffice.domain.operations.OperationalRecord
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalRecordRepository

class InMemoryOperationalRecordRepository : OperationalRecordRepository {
    private val byId = LinkedHashMap<String, OperationalRecord>()
    private val byJournal = LinkedHashMap<String, OperationalRecord>()

    override fun save(record: OperationalRecord): DomainResult<OperationalRecord> {
        if (byJournal.containsKey(record.journalEntryId.value)) {
            return DomainResult.Failure(
                DomainError.InvalidState("Operational record already exists for journal entry"),
            )
        }
        byId[record.id.value] = record
        byJournal[record.journalEntryId.value] = record
        return DomainResult.Success(record)
    }

    override fun findById(id: OperationalRecordId): OperationalRecord? = byId[id.value]

    override fun findByJournalEntryId(journalEntryId: JournalEntryId): OperationalRecord? =
        byJournal[journalEntryId.value]

    override fun listAll(): List<OperationalRecord> = byId.values.toList()
}
