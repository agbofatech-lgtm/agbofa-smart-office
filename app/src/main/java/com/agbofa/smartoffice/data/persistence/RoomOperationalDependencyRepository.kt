package com.agbofa.smartoffice.data.persistence

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.operations.OperationalDependency
import com.agbofa.smartoffice.domain.operations.OperationalDependencyId
import com.agbofa.smartoffice.domain.operations.OperationalDependencyRepository
import com.agbofa.smartoffice.domain.operations.OperationalRecordId

class RoomOperationalDependencyRepository(
    private val dao: OperationalDependencyDao,
) : OperationalDependencyRepository {
    override fun save(dependency: OperationalDependency): DomainResult<OperationalDependency> {
        if (dao.findById(dependency.id.value) != null) {
            return DomainResult.Failure(DomainError.InvalidState("Dependency id already exists"))
        }
        if (dao.findByPair(
                dependency.dependentOperationalRecordId.value,
                dependency.prerequisiteOperationalRecordId.value,
            ) != null
        ) {
            return DomainResult.Failure(DomainError.InvalidState("Dependency already exists"))
        }
        return try {
            dao.insert(dependency.toEntity())
            DomainResult.Success(dependency)
        } catch (error: Exception) {
            DomainResult.Failure(
                DomainError.PersistenceFailure(error.message ?: "Failed to persist dependency"),
            )
        }
    }

    override fun findById(id: OperationalDependencyId): OperationalDependency? =
        dao.findById(id.value)?.toDomain()

    override fun findByPair(
        dependentOperationalRecordId: OperationalRecordId,
        prerequisiteOperationalRecordId: OperationalRecordId,
    ): OperationalDependency? =
        dao.findByPair(
            dependentOperationalRecordId.value,
            prerequisiteOperationalRecordId.value,
        )?.toDomain()

    override fun listAll(): List<OperationalDependency> =
        dao.list().mapNotNull { it.toDomain() }.sortedWith(
            compareBy<OperationalDependency> { it.createdAt.value }.thenBy { it.id.value },
        )

    override fun listByDependent(dependentOperationalRecordId: OperationalRecordId): List<OperationalDependency> =
        listAll().filter { it.dependentOperationalRecordId == dependentOperationalRecordId }

    override fun listByPrerequisite(prerequisiteOperationalRecordId: OperationalRecordId): List<OperationalDependency> =
        listAll().filter { it.prerequisiteOperationalRecordId == prerequisiteOperationalRecordId }
}
