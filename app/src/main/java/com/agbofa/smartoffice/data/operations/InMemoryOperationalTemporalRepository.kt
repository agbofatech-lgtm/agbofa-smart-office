package com.agbofa.smartoffice.data.operations

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalTemporalId
import com.agbofa.smartoffice.domain.operations.OperationalTemporalProjection
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRecord
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRepository

class InMemoryOperationalTemporalRepository : OperationalTemporalRepository {
    private val byId = LinkedHashMap<String, OperationalTemporalRecord>()

    override fun save(record: OperationalTemporalRecord): DomainResult<OperationalTemporalRecord> {
        if (byId.containsKey(record.id.value)) {
            return DomainResult.Failure(DomainError.InvalidState("Temporal record id already exists"))
        }
        byId[record.id.value] = record
        return DomainResult.Success(record)
    }

    override fun findById(id: OperationalTemporalId): OperationalTemporalRecord? = byId[id.value]

    override fun listByOperationalRecordId(operationalRecordId: OperationalRecordId): List<OperationalTemporalRecord> =
        OperationalTemporalProjection.ordered(
            byId.values.filter { it.operationalRecordId == operationalRecordId },
        )
}
