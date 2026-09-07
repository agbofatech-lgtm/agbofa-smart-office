package com.agbofa.smartoffice.data.persistence

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalStateRepository
import com.agbofa.smartoffice.domain.operations.OperationalStateTransition
import com.agbofa.smartoffice.domain.operations.OperationalStateTransitionId

class RoomOperationalStateRepository(
    private val dao: OperationalStateTransitionDao,
) : OperationalStateRepository {
    override fun save(transition: OperationalStateTransition): DomainResult<OperationalStateTransition> {
        return try {
            dao.insert(transition.toEntity())
            DomainResult.Success(transition)
        } catch (error: Exception) {
            DomainResult.Failure(
                DomainError.PersistenceFailure(error.message ?: "State transition persist failed"),
            )
        }
    }

    override fun findById(id: OperationalStateTransitionId): OperationalStateTransition? =
        dao.findById(id.value)?.toDomain()

    override fun listByOperationalRecordId(
        operationalRecordId: OperationalRecordId,
    ): List<OperationalStateTransition> =
        dao.listByOperationalRecordId(operationalRecordId.value).mapNotNull { it.toDomain() }
}
