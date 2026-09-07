package com.agbofa.smartoffice.application.operations

import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.operations.OperationalStateProjection
import com.agbofa.smartoffice.domain.operations.OperationalStateRepository

class GetOperationalRecordStateUseCase(
    private val states: OperationalStateRepository,
) {
    fun execute(operationalRecordId: OperationalRecordId): OperationalState =
        OperationalStateProjection.current(
            states.listByOperationalRecordId(operationalRecordId),
        )
}
