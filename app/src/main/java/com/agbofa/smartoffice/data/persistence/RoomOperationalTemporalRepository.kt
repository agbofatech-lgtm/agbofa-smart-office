package com.agbofa.smartoffice.data.persistence

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalTemporalId
import com.agbofa.smartoffice.domain.operations.OperationalTemporalProjection
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRecord
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRepository

class RoomOperationalTemporalRepository(
    private val dao: OperationalTemporalRecordDao,
) : OperationalTemporalRepository {
    override fun save(record: OperationalTemporalRecord): DomainResult<OperationalTemporalRecord> {
        if (dao.findById(record.id.value) != null) {
            return DomainResult.Failure(DomainError.InvalidState("Temporal record id already exists"))
        }
        return try {
            dao.insert(record.toEntity())
            DomainResult.Success(record)
        } catch (error: Exception) {
            DomainResult.Failure(
                DomainError.PersistenceFailure(error.message ?: "Failed to persist temporal record"),
            )
        }
    }

    override fun findById(id: OperationalTemporalId): OperationalTemporalRecord? =
        dao.findById(id.value)?.toDomain()

    override fun listByOperationalRecordId(
        operationalRecordId: OperationalRecordId,
    ): List<OperationalTemporalRecord> =
        OperationalTemporalProjection.ordered(
            dao.listByOperationalRecordId(operationalRecordId.value).mapNotNull { it.toDomain() },
        )
}
