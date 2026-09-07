package com.agbofa.smartoffice.application.operations

import com.agbofa.smartoffice.domain.journal.JournalEntryId
import com.agbofa.smartoffice.domain.operations.OperationalRecord
import com.agbofa.smartoffice.domain.operations.OperationalRecordRepository

class GetOperationalRecordForJournalEntryUseCase(
    private val operations: OperationalRecordRepository,
) {
    fun execute(journalEntryId: JournalEntryId): OperationalRecord? =
        operations.findByJournalEntryId(journalEntryId)
}
