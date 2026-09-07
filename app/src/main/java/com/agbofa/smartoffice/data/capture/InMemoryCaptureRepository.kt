package com.agbofa.smartoffice.data.capture

import com.agbofa.smartoffice.domain.capture.Capture
import com.agbofa.smartoffice.domain.capture.CaptureId
import com.agbofa.smartoffice.domain.capture.CaptureRepository
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

/**
 * Process-local capture store.
 *
 * Phase 3 does not introduce Room. This implementation is offline and
 * has no network dependency. It does not rewrite stored expressions.
 */
class InMemoryCaptureRepository : CaptureRepository {
    private val records = LinkedHashMap<String, Capture>()

    override fun save(capture: Capture): DomainResult<Capture> {
        records[capture.id.value] = capture
        return DomainResult.Success(capture)
    }

    override fun findById(id: CaptureId): Capture? = records[id.value]
}
