package com.agbofa.smartoffice.domain.operations

import com.agbofa.smartoffice.domain.foundation.result.DomainResult

interface OperationalTemporalRepository {
    fun save(record: OperationalTemporalRecord): DomainResult<OperationalTemporalRecord>
    fun findById(id: OperationalTemporalId): OperationalTemporalRecord?
    fun listByOperationalRecordId(operationalRecordId: OperationalRecordId): List<OperationalTemporalRecord>
}
