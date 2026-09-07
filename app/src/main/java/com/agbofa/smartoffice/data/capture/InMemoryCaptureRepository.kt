package com.agbofa.smartoffice.data.capture

import com.agbofa.smartoffice.domain.capture.Capture
import com.agbofa.smartoffice.domain.capture.CaptureId
import com.agbofa.smartoffice.domain.capture.CaptureRepository
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

/**
 * Process-local capture store.
 *
 * Used by unit tests. Production wiring uses Room.
 * An injected map lets tests recreate the repository and still
 * observe previously saved records (restart simulation).
 */
class InMemoryCaptureRepository(
    private val records: MutableMap<String, Capture> = LinkedHashMap(),
) : CaptureRepository {
    override fun save(capture: Capture): DomainResult<Capture> {
        records[capture.id.value] = capture
        return DomainResult.Success(capture)
    }

    override fun findById(id: CaptureId): Capture? = records[id.value]
}
