package com.agbofa.smartoffice.application.operations

import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalTemporalProjection
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRecord
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRepository

class GetOperationalTemporalUseCase(
    private val temporals: OperationalTemporalRepository,
) {
    fun execute(operationalRecordId: OperationalRecordId): OperationalTemporalRecord? = current(operationalRecordId)

    fun current(operationalRecordId: OperationalRecordId): OperationalTemporalRecord? =
        OperationalTemporalProjection.current(temporals.listByOperationalRecordId(operationalRecordId))

    fun history(operationalRecordId: OperationalRecordId): List<OperationalTemporalRecord> =
        OperationalTemporalProjection.ordered(temporals.listByOperationalRecordId(operationalRecordId))
}
