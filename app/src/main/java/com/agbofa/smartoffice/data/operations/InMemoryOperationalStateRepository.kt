package com.agbofa.smartoffice.data.operations

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalStateRepository
import com.agbofa.smartoffice.domain.operations.OperationalStateTransition
import com.agbofa.smartoffice.domain.operations.OperationalStateTransitionId

class InMemoryOperationalStateRepository : OperationalStateRepository {
    private val byId = LinkedHashMap<String, OperationalStateTransition>()

    override fun save(transition: OperationalStateTransition): DomainResult<OperationalStateTransition> {
        if (byId.containsKey(transition.id.value)) {
            return DomainResult.Failure(
                DomainError.InvalidState("Transition id already exists"),
            )
        }
        byId[transition.id.value] = transition
        return DomainResult.Success(transition)
    }

    override fun findById(id: OperationalStateTransitionId): OperationalStateTransition? =
        byId[id.value]

    override fun listByOperationalRecordId(
        operationalRecordId: OperationalRecordId,
    ): List<OperationalStateTransition> =
        byId.values.filter { it.operationalRecordId == operationalRecordId }
}
