package com.agbofa.smartoffice.application.operations

import com.agbofa.smartoffice.domain.classification.ClassificationRepository
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.OperationalCreationInstant
import com.agbofa.smartoffice.domain.journal.JournalEntryId
import com.agbofa.smartoffice.domain.journal.JournalRepository
import com.agbofa.smartoffice.domain.operations.OperationalCreationBasis
import com.agbofa.smartoffice.domain.operations.OperationalRecord
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalRecordRepository
import com.agbofa.smartoffice.domain.operations.OperationalRecordType

class CreateOperationalRecordUseCase(
    private val journal: JournalRepository,
    private val classifications: ClassificationRepository,
    private val operations: OperationalRecordRepository,
) {
    fun execute(
        operationalRecordId: String,
        journalEntryIdValue: String,
        createdAt: OperationalCreationInstant,
        creationBasis: OperationalCreationBasis = OperationalCreationBasis.MANUAL,
        ruleVersion: String? = null,
    ): DomainResult<OperationalRecord> {
        val journalEntryId = when (val result = JournalEntryId.of(journalEntryIdValue)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        if (journal.findById(journalEntryId) == null) {
            return DomainResult.Failure(
                DomainError.InvalidState("Journal entry does not exist"),
            )
        }
        val active = classifications.findActiveByJournalEntryId(journalEntryId)
            ?: return DomainResult.Failure(
                DomainError.InvalidState("Journal entry is unclassified"),
            )
        val type = OperationalRecordType.from(active.type)
            ?: return DomainResult.Failure(
                DomainError.InvalidState("Active classification cannot be operationalized"),
            )
        if (operations.findByJournalEntryId(journalEntryId) != null) {
            return DomainResult.Failure(
                DomainError.InvalidState("Operational record already exists for journal entry"),
            )
        }
        val id = when (val result = OperationalRecordId.of(operationalRecordId)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        val record = when (
            val result = OperationalRecord.of(
                id = id,
                journalEntryId = journalEntryId,
                classificationId = active.id,
                type = type,
                createdAt = createdAt,
                creationBasis = creationBasis,
                ruleVersion = ruleVersion,
            )
        ) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        return operations.save(record)
    }
}
