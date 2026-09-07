package com.agbofa.smartoffice.application.capture

import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant

/**
 * Application command to capture an expression.
 *
 * Identity and time are supplied by the caller. The use case does not
 * read a clock or generate identifiers.
 */
data class CaptureExpressionCommand(
    val id: String,
    val expression: String,
    val capturedAt: CaptureInstant,
)
