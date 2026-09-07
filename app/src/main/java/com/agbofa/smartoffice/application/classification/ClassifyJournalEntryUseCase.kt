package com.agbofa.smartoffice.application.classification

import com.agbofa.smartoffice.domain.classification.Classification
import com.agbofa.smartoffice.domain.classification.ClassificationBasis
import com.agbofa.smartoffice.domain.classification.ClassificationId
import com.agbofa.smartoffice.domain.classification.ClassificationRepository
import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.ClassificationInstant
import com.agbofa.smartoffice.domain.journal.JournalEntryId
import com.agbofa.smartoffice.domain.journal.JournalRepository

class ClassifyJournalEntryUseCase(
    private val journal: JournalRepository,
    private val classifications: ClassificationRepository,
) {
    fun execute(
        classificationId: String,
        journalEntryIdValue: String,
        type: ClassificationType,
        basis: ClassificationBasis,
        classifiedAt: ClassificationInstant,
        ruleVersion: String? = null,
    ): DomainResult<Classification> {
        val journalEntryId = when (val result = JournalEntryId.of(journalEntryIdValue)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        if (journal.findById(journalEntryId) == null) {
            return DomainResult.Failure(
                DomainError.InvalidState("Journal entry does not exist"),
            )
        }
        val id = when (val result = ClassificationId.of(classificationId)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        val current = classifications.findActiveByJournalEntryId(journalEntryId)
        val revision = if (current == null) 1 else current.revision + 1
        val classification = when (
            val result = Classification.of(
                id = id,
                journalEntryId = journalEntryId,
                type = type,
                basis = basis,
                classifiedAt = classifiedAt,
                revision = revision,
                ruleVersion = ruleVersion,
                supersedesId = current?.id,
            )
        ) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        return classifications.save(classification)
    }
}
