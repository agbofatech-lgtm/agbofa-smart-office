package com.agbofa.smartoffice.domain.operations

import com.agbofa.smartoffice.domain.classification.ClassificationId
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.OperationalCreationInstant
import com.agbofa.smartoffice.domain.journal.JournalEntryId

/**
 * Canonical structured continuation of a classified JournalEntry.
 *
 * References Journal and the Classification revision used at creation.
 * Does not own Capture text, money, due dates, or lifecycle state.
 */
data class OperationalRecord private constructor(
    val id: OperationalRecordId,
    val journalEntryId: JournalEntryId,
    val classificationId: ClassificationId,
    val type: OperationalRecordType,
    val createdAt: OperationalCreationInstant,
    val creationBasis: OperationalCreationBasis,
    val ruleVersion: String?,
) {
    companion object {
        fun of(
            id: OperationalRecordId,
            journalEntryId: JournalEntryId,
            classificationId: ClassificationId,
            type: OperationalRecordType,
            createdAt: OperationalCreationInstant,
            creationBasis: OperationalCreationBasis,
            ruleVersion: String? = null,
        ): DomainResult<OperationalRecord> {
            if (creationBasis == OperationalCreationBasis.RULE && ruleVersion.isNullOrBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "RULE operationalization requires ruleVersion",
                        path = "ruleVersion",
                    ),
                )
            }
            if (creationBasis == OperationalCreationBasis.MANUAL && !ruleVersion.isNullOrBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "MANUAL operationalization must not carry ruleVersion",
                        path = "ruleVersion",
                    ),
                )
            }
            return DomainResult.Success(
                OperationalRecord(
                    id = id,
                    journalEntryId = journalEntryId,
                    classificationId = classificationId,
                    type = type,
                    createdAt = createdAt,
                    creationBasis = creationBasis,
                    ruleVersion = ruleVersion,
                ),
            )
        }
    }
}
