package com.agbofa.smartoffice.domain.capture

import com.agbofa.smartoffice.domain.foundation.result.DomainResult

/**
 * Persistence port for Capture records.
 *
 * Implementations live outside domain. Phase 3 authorizes an in-memory
 * store only. This port does not interpret captured text.
 */
interface CaptureRepository {
    fun save(capture: Capture): DomainResult<Capture>
    fun findById(id: CaptureId): Capture?
}
