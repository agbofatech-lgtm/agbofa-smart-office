package com.agbofa.smartoffice.data.persistence

import com.agbofa.smartoffice.domain.capture.Capture
import com.agbofa.smartoffice.domain.capture.CaptureId
import com.agbofa.smartoffice.domain.capture.CaptureSource
import com.agbofa.smartoffice.domain.capture.OriginalExpression
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
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
