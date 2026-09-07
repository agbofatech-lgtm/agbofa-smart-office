package com.agbofa.smartoffice.application.operations

import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalStateProjection
import com.agbofa.smartoffice.domain.operations.OperationalStateRepository
import com.agbofa.smartoffice.domain.operations.OperationalStateTransition

class GetOperationalStateHistoryUseCase(
    private val states: OperationalStateRepository,
) {
    fun execute(operationalRecordId: OperationalRecordId): List<OperationalStateTransition> =
        OperationalStateProjection.ordered(states.listByOperationalRecordId(operationalRecordId))
}
