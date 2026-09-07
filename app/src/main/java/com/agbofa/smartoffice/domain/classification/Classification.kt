package com.agbofa.smartoffice.domain.classification

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.ClassificationInstant
import com.agbofa.smartoffice.domain.journal.JournalEntryId

/**
 * Immutable operational classification of a JournalEntry.
 *
 * Does not own Capture text. Does not execute the classified operation.
 */
data class Classification private constructor(
    val id: ClassificationId,
    val journalEntryId: JournalEntryId,
    val type: ClassificationType,
    val basis: ClassificationBasis,
    val classifiedAt: ClassificationInstant,
    val revision: Int,
    val ruleVersion: String?,
    val supersedesId: ClassificationId?,
) {
    companion object {
        fun of(
            id: ClassificationId,
            journalEntryId: JournalEntryId,
            type: ClassificationType,
            basis: ClassificationBasis,
            classifiedAt: ClassificationInstant,
            revision: Int,
            ruleVersion: String? = null,
            supersedesId: ClassificationId? = null,
        ): DomainResult<Classification> {
            if (type == ClassificationType.UNCLASSIFIED) {
                return DomainResult.Failure(
                    DomainError.InvalidState(
                        "UNCLASSIFIED is the absence of a classification row",
                    ),
                )
            }
            if (revision < 1) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "Classification revision must be >= 1",
                        path = "revision",
                    ),
                )
            }
            if (revision > 1 && supersedesId == null) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "Reclassification must reference the superseded classification",
                        path = "supersedesId",
                    ),
                )
            }
            if (revision == 1 && supersedesId != null) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "First classification cannot supersede another",
                        path = "supersedesId",
                    ),
                )
            }
            if (basis == ClassificationBasis.RULE && ruleVersion.isNullOrBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "RULE classification requires ruleVersion",
                        path = "ruleVersion",
                    ),
                )
            }
            if (basis == ClassificationBasis.MANUAL && !ruleVersion.isNullOrBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "MANUAL classification must not carry ruleVersion",
                        path = "ruleVersion",
                    ),
                )
            }
            return DomainResult.Success(
                Classification(
                    id = id,
                    journalEntryId = journalEntryId,
                    type = type,
                    basis = basis,
                    classifiedAt = classifiedAt,
                    revision = revision,
                    ruleVersion = ruleVersion,
                    supersedesId = supersedesId,
                ),
            )
        }

        fun from(
            id: String,
            journalEntryId: JournalEntryId,
            type: ClassificationType,
            basis: ClassificationBasis,
            classifiedAt: ClassificationInstant,
            revision: Int,
            ruleVersion: String? = null,
            supersedesId: ClassificationId? = null,
        ): DomainResult<Classification> {
            val classificationId = when (val result = ClassificationId.of(id)) {
                is DomainResult.Failure -> return result
                is DomainResult.Success -> result.value
            }
            return of(
                id = classificationId,
                journalEntryId = journalEntryId,
                type = type,
                basis = basis,
                classifiedAt = classifiedAt,
                revision = revision,
                ruleVersion = ruleVersion,
                supersedesId = supersedesId,
            )
        }
    }
}
