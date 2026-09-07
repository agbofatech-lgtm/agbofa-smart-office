package com.agbofa.smartoffice.domain.operations

import com.agbofa.smartoffice.domain.foundation.result.DomainResult

interface OperationalDependencyRepository {
    fun save(dependency: OperationalDependency): DomainResult<OperationalDependency>
    fun findById(id: OperationalDependencyId): OperationalDependency?
    fun findByPair(
        dependentOperationalRecordId: OperationalRecordId,
        prerequisiteOperationalRecordId: OperationalRecordId,
    ): OperationalDependency?
    fun listAll(): List<OperationalDependency>
    fun listByDependent(dependentOperationalRecordId: OperationalRecordId): List<OperationalDependency>
    fun listByPrerequisite(prerequisiteOperationalRecordId: OperationalRecordId): List<OperationalDependency>
}
