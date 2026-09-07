package com.agbofa.smartoffice.application.capture

import com.agbofa.smartoffice.domain.capture.Capture
import com.agbofa.smartoffice.domain.capture.CaptureRepository
import com.agbofa.smartoffice.domain.capture.CaptureSource
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant

/**
 * Capture an expression as an immutable record.
 *
 * Presentation calls this. It does not classify, schedule, or parse money.
 */
class CaptureExpressionUseCase(
    private val repository: CaptureRepository,
) {
    fun execute(command: CaptureExpressionCommand): DomainResult<Capture> {
        return execute(
            idValue = command.id,
            expression = command.expression,
            capturedAt = command.capturedAt,
        )
    }

    fun execute(
        idValue: String,
        expression: String,
        capturedAt: CaptureInstant,
        source: CaptureSource = CaptureSource.TEXT,
    ): DomainResult<Capture> {
        val capture = when (val result = Capture.from(idValue, expression, capturedAt, source)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        return repository.save(capture)
    }
}

typealias CaptureExpression = CaptureExpressionUseCase
