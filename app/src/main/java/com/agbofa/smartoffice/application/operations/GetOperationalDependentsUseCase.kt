package com.agbofa.smartoffice.application.operations

import com.agbofa.smartoffice.domain.operations.OperationalDependency
import com.agbofa.smartoffice.domain.operations.OperationalDependencyRepository
import com.agbofa.smartoffice.domain.operations.OperationalRecordId

class GetOperationalDependentsUseCase(
    private val dependencies: OperationalDependencyRepository,
) {
    fun execute(prerequisiteId: OperationalRecordId): List<OperationalDependency> =
        dependencies.listByPrerequisite(prerequisiteId)
}
