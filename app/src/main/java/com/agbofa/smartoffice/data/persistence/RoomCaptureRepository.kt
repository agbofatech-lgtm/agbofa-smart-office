package com.agbofa.smartoffice.data.persistence

import com.agbofa.smartoffice.domain.capture.Capture
import com.agbofa.smartoffice.domain.capture.CaptureId
import com.agbofa.smartoffice.domain.capture.CaptureRepository
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

class RoomCaptureRepository(
    private val dao: CaptureDao,
) : CaptureRepository {
    override fun save(capture: Capture): DomainResult<Capture> {
        return try {
            dao.insert(capture.toEntity())
            DomainResult.Success(capture)
        } catch (error: Exception) {
            DomainResult.Failure(
                DomainError.PersistenceFailure(error.message ?: "Capture persist failed"),
            )
        }
    }

    override fun findById(id: CaptureId): Capture? = dao.findById(id.value)?.toDomain()
}
