package com.agbofa.smartoffice.data.operations

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.operations.OperationalDependency
import com.agbofa.smartoffice.domain.operations.OperationalDependencyId
import com.agbofa.smartoffice.domain.operations.OperationalDependencyRepository
import com.agbofa.smartoffice.domain.operations.OperationalRecordId

class InMemoryOperationalDependencyRepository : OperationalDependencyRepository {
    private val byId = LinkedHashMap<String, OperationalDependency>()

    override fun save(dependency: OperationalDependency): DomainResult<OperationalDependency> {
        if (byId.containsKey(dependency.id.value)) {
            return DomainResult.Failure(DomainError.InvalidState("Dependency id already exists"))
        }
        if (findByPair(dependency.dependentOperationalRecordId, dependency.prerequisiteOperationalRecordId) != null) {
            return DomainResult.Failure(DomainError.InvalidState("Dependency already exists"))
        }
        byId[dependency.id.value] = dependency
        return DomainResult.Success(dependency)
    }

    override fun findById(id: OperationalDependencyId): OperationalDependency? = byId[id.value]

    override fun findByPair(
        dependentOperationalRecordId: OperationalRecordId,
        prerequisiteOperationalRecordId: OperationalRecordId,
    ): OperationalDependency? = byId.values.firstOrNull {
        it.dependentOperationalRecordId == dependentOperationalRecordId &&
            it.prerequisiteOperationalRecordId == prerequisiteOperationalRecordId
    }

    override fun listAll(): List<OperationalDependency> =
        byId.values.sortedWith(
            compareBy<OperationalDependency> { it.dependentOperationalRecordId.value }
                .thenBy { it.prerequisiteOperationalRecordId.value }
                .thenBy { it.id.value },
        )

    override fun listByDependent(dependentOperationalRecordId: OperationalRecordId): List<OperationalDependency> =
        listAll().filter { it.dependentOperationalRecordId == dependentOperationalRecordId }

    override fun listByPrerequisite(prerequisiteOperationalRecordId: OperationalRecordId): List<OperationalDependency> =
        listAll().filter { it.prerequisiteOperationalRecordId == prerequisiteOperationalRecordId }
}
