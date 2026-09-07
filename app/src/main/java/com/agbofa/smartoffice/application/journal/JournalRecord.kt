package com.agbofa.smartoffice.application.journal

import com.agbofa.smartoffice.domain.capture.CaptureId
import com.agbofa.smartoffice.domain.capture.OriginalExpression
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import com.agbofa.smartoffice.domain.journal.JournalEntryId

/**
 * Read projection. Not a second owner of Capture evidence.
 */
data class JournalRecord(
    val entryId: JournalEntryId,
    val captureId: CaptureId,
    val originalExpression: OriginalExpression,
    val capturedAt: CaptureInstant,
    val admittedAt: JournalAdmissionInstant,
)
