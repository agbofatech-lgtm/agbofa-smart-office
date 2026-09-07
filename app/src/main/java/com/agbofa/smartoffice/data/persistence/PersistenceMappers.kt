package com.agbofa.smartoffice.data.persistence

import com.agbofa.smartoffice.domain.capture.Capture
import com.agbofa.smartoffice.domain.operations.OperationalCreationBasis
import com.agbofa.smartoffice.domain.operations.OperationalRecord
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalRecordType
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.operations.OperationalStateTransition
import com.agbofa.smartoffice.domain.operations.OperationalStateTransitionId
import com.agbofa.smartoffice.domain.operations.OperationalTransitionBasis
import com.agbofa.smartoffice.domain.foundation.time.OperationalTransitionInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalCreationInstant
import com.agbofa.smartoffice.domain.capture.CaptureId
import com.agbofa.smartoffice.domain.capture.CaptureSource
import com.agbofa.smartoffice.domain.capture.OriginalExpression
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.classification.Classification
import com.agbofa.smartoffice.domain.classification.ClassificationBasis
import com.agbofa.smartoffice.domain.classification.ClassificationId
import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.domain.foundation.time.ClassificationInstant
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import com.agbofa.smartoffice.domain.journal.JournalEntry
import com.agbofa.smartoffice.domain.journal.JournalEntryId
import java.time.Instant

internal fun Capture.toEntity(): CaptureEntity = CaptureEntity(
    id = id.value,
    originalExpression = originalExpression.value,
    capturedAt = capturedAt.value.toString(),
    source = source.name,
)

internal fun CaptureEntity.toDomain(): Capture? {
    val id = (CaptureId.of(id) as? DomainResult.Success)?.value ?: return null
    val expression = (OriginalExpression.of(originalExpression) as? DomainResult.Success)?.value
        ?: return null
    val instant = CaptureInstant(Instant.parse(capturedAt))
    val source = runCatching { CaptureSource.valueOf(source) }.getOrNull() ?: return null
    return (Capture.of(id, expression, instant, source) as? DomainResult.Success)?.value
}

internal fun JournalEntry.toEntity(): JournalEntryEntity = JournalEntryEntity(
    id = id.value,
    captureId = captureId.value,
    admittedAt = admittedAt.value.toString(),
)

internal fun JournalEntryEntity.toDomain(): JournalEntry? {
    val id = (JournalEntryId.of(id) as? DomainResult.Success)?.value ?: return null
    val captureId = (CaptureId.of(captureId) as? DomainResult.Success)?.value ?: return null
    val admittedAt = JournalAdmissionInstant(Instant.parse(admittedAt))
    return (JournalEntry.of(id, captureId, admittedAt) as? DomainResult.Success)?.value
}

internal fun Classification.toEntity(): ClassificationEntity = ClassificationEntity(
    id = id.value,
    journalEntryId = journalEntryId.value,
    type = type.name,
    basis = basis.name,
    classifiedAt = classifiedAt.value.toString(),
    revision = revision,
    ruleVersion = ruleVersion,
    supersedesId = supersedesId?.value,
)

internal fun ClassificationEntity.toDomain(): Classification? {
    val id = (ClassificationId.of(id) as? DomainResult.Success)?.value ?: return null
    val journalEntryId = (JournalEntryId.of(journalEntryId) as? DomainResult.Success)?.value
        ?: return null
    val type = runCatching { ClassificationType.valueOf(type) }.getOrNull() ?: return null
    val basis = runCatching { ClassificationBasis.valueOf(basis) }.getOrNull() ?: return null
    val classifiedAt = ClassificationInstant(Instant.parse(classifiedAt))
    val supersedes = supersedesId?.let {
        (ClassificationId.of(it) as? DomainResult.Success)?.value
    }
    return (
        Classification.of(
            id = id,
            journalEntryId = journalEntryId,
            type = type,
            basis = basis,
            classifiedAt = classifiedAt,
            revision = revision,
            ruleVersion = ruleVersion,
            supersedesId = supersedes,
        ) as? DomainResult.Success
        )?.value
}

internal fun OperationalRecord.toEntity(): OperationalRecordEntity = OperationalRecordEntity(
    id = id.value,
    journalEntryId = journalEntryId.value,
    classificationId = classificationId.value,
    type = type.name,
    createdAt = createdAt.value.toString(),
    creationBasis = creationBasis.name,
    ruleVersion = ruleVersion,
)

internal fun OperationalRecordEntity.toDomain(): OperationalRecord? {
    val id = (OperationalRecordId.of(id) as? DomainResult.Success)?.value ?: return null
    val journalEntryId = (JournalEntryId.of(journalEntryId) as? DomainResult.Success)?.value
        ?: return null
    val classificationId = (ClassificationId.of(classificationId) as? DomainResult.Success)?.value
        ?: return null
    val type = runCatching { OperationalRecordType.valueOf(type) }.getOrNull() ?: return null
    val basis = runCatching { OperationalCreationBasis.valueOf(creationBasis) }.getOrNull()
        ?: return null
    return (OperationalRecord.of(
        id = id,
        journalEntryId = journalEntryId,
        classificationId = classificationId,
        type = type,
        createdAt = OperationalCreationInstant(Instant.parse(createdAt)),
        creationBasis = basis,
        ruleVersion = ruleVersion,
    ) as? DomainResult.Success)?.value
}

internal fun OperationalStateTransition.toEntity(): OperationalStateTransitionEntity =
    OperationalStateTransitionEntity(
        id = id.value,
        operationalRecordId = operationalRecordId.value,
        fromState = fromState.name,
        toState = toState.name,
        transitionedAt = transitionedAt.value.toString(),
        basis = basis.name,
        ruleVersion = ruleVersion,
    )

internal fun OperationalStateTransitionEntity.toDomain(): OperationalStateTransition? {
    val id = (OperationalStateTransitionId.of(id) as? DomainResult.Success)?.value ?: return null
    val recordId = (OperationalRecordId.of(operationalRecordId) as? DomainResult.Success)?.value
        ?: return null
    val from = runCatching { OperationalState.valueOf(fromState) }.getOrNull() ?: return null
    val to = runCatching { OperationalState.valueOf(toState) }.getOrNull() ?: return null
    val basis = runCatching { OperationalTransitionBasis.valueOf(basis) }.getOrNull() ?: return null
    return (OperationalStateTransition.of(
        id = id,
        operationalRecordId = recordId,
        fromState = from,
        toState = to,
        transitionedAt = OperationalTransitionInstant(Instant.parse(transitionedAt)),
        basis = basis,
        ruleVersion = ruleVersion,
    ) as? DomainResult.Success)?.value
}

