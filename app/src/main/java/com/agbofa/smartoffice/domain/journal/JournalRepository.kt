package com.agbofa.smartoffice.domain.journal

import com.agbofa.smartoffice.domain.capture.CaptureId
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

/**
 * Persistence port for journal admissions.
 *
 * Implementations must not rewrite Capture evidence.
 * Duplicate admission of the same CaptureId must fail.
 */
interface JournalRepository {
    fun save(entry: JournalEntry): DomainResult<JournalEntry>
    fun findById(id: JournalEntryId): JournalEntry?
    fun findByCaptureId(captureId: CaptureId): JournalEntry?
    fun listAll(): List<JournalEntry>
}
