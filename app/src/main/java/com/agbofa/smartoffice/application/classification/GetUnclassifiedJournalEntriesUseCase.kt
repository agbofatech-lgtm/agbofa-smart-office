package com.agbofa.smartoffice.application.classification

import com.agbofa.smartoffice.domain.classification.ClassificationRepository
import com.agbofa.smartoffice.domain.journal.JournalEntry
import com.agbofa.smartoffice.domain.journal.JournalRepository

class GetUnclassifiedJournalEntriesUseCase(
    private val journal: JournalRepository,
    private val classifications: ClassificationRepository,
) {
    fun execute(): List<JournalEntry> {
        return journal.listAll().filter { entry ->
            val active = classifications.findActiveByJournalEntryId(entry.id)
            active == null
        }
    }
}
