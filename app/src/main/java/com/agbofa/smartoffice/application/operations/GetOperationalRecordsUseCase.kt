package com.agbofa.smartoffice.application.operations

import com.agbofa.smartoffice.domain.operations.OperationalRecord
import com.agbofa.smartoffice.domain.operations.OperationalRecordRepository

class GetOperationalRecordsUseCase(
    private val records: OperationalRecordRepository,
) {
    fun execute(): List<OperationalRecord> = records.listAll()
}
