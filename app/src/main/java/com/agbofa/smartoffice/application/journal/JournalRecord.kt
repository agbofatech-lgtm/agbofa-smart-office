package com.agbofa.smartoffice.application.journal

import com.agbofa.smartoffice.domain.capture.CaptureId
import com.agbofa.smartoffice.domain.capture.OriginalExpression
import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import com.agbofa.smartoffice.domain.journal.JournalEntryId

/**
 * Read projection. Classification and operational existence are attached,
 * not owned as Capture or Journal truth.
 */
data class JournalRecord(
    val entryId: JournalEntryId,
    val captureId: CaptureId,
    val originalExpression: OriginalExpression,
    val capturedAt: CaptureInstant,
    val admittedAt: JournalAdmissionInstant,
    val classificationType: ClassificationType = ClassificationType.UNCLASSIFIED,
    val operationalRecordExists: Boolean = false,
)
