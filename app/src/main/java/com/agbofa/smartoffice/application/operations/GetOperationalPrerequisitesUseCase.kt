package com.agbofa.smartoffice.application.operations

import com.agbofa.smartoffice.domain.operations.OperationalDependency
import com.agbofa.smartoffice.domain.operations.OperationalDependencyRepository
import com.agbofa.smartoffice.domain.operations.OperationalRecordId

class GetOperationalPrerequisitesUseCase(
    private val dependencies: OperationalDependencyRepository,
) {
    fun execute(dependentId: OperationalRecordId): List<OperationalDependency> =
        dependencies.listByDependent(dependentId)
}
