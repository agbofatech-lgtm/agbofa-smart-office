package com.agbofa.smartoffice.domain.capture

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant

/**
 * Immutable operational capture.
 *
 * This is evidence of what was entered. It is not a journal entry,
 * classification, task, or financial record.
 */
data class Capture private constructor(
    val id: CaptureId,
    val originalExpression: OriginalExpression,
    val capturedAt: CaptureInstant,
    val source: CaptureSource,
) {
    companion object {
        fun of(
            id: CaptureId,
            originalExpression: OriginalExpression,
            capturedAt: CaptureInstant,
            source: CaptureSource = CaptureSource.TEXT,
        ): DomainResult<Capture> {
            if (source != CaptureSource.TEXT) {
                return DomainResult.Failure(
                    DomainError.InvalidState(
                        "Phase 3 implements TEXT capture only",
                    ),
                )
            }
            return DomainResult.Success(
                Capture(
                    id = id,
                    originalExpression = originalExpression,
                    capturedAt = capturedAt,
                    source = source,
                ),
            )
        }

        fun from(
            id: String,
            expression: String,
            capturedAt: CaptureInstant,
            source: CaptureSource = CaptureSource.TEXT,
        ): DomainResult<Capture> {
            val captureId = when (val result = CaptureId.of(id)) {
                is DomainResult.Failure -> return result
                is DomainResult.Success -> result.value
            }
            val original = when (val result = OriginalExpression.of(expression)) {
                is DomainResult.Failure -> return result
                is DomainResult.Success -> result.value
            }
            return of(captureId, original, capturedAt, source)
        }
    }
}
