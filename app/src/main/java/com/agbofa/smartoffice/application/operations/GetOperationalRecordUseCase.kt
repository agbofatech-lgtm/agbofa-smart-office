package com.agbofa.smartoffice.application.operations

import com.agbofa.smartoffice.domain.operations.OperationalRecord
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalRecordRepository

class GetOperationalRecordUseCase(
    private val operations: OperationalRecordRepository,
) {
    fun execute(id: OperationalRecordId): OperationalRecord? = operations.findById(id)
}
