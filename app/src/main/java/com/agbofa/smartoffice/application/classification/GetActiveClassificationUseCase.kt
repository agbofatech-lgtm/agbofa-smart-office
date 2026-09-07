package com.agbofa.smartoffice.application.classification

import com.agbofa.smartoffice.domain.classification.Classification
import com.agbofa.smartoffice.domain.classification.ClassificationRepository
import com.agbofa.smartoffice.domain.journal.JournalEntryId

class GetActiveClassificationUseCase(
    private val classifications: ClassificationRepository,
) {
    fun execute(journalEntryId: JournalEntryId): Classification? =
        classifications.findActiveByJournalEntryId(journalEntryId)
}
