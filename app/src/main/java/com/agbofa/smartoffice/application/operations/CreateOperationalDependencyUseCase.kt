package com.agbofa.smartoffice.application.operations

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.OperationalDependencyCreationInstant
import com.agbofa.smartoffice.domain.operations.OperationalDependency
import com.agbofa.smartoffice.domain.operations.OperationalDependencyBasis
import com.agbofa.smartoffice.domain.operations.OperationalDependencyCycleDetector
import com.agbofa.smartoffice.domain.operations.OperationalDependencyId
import com.agbofa.smartoffice.domain.operations.OperationalDependencyRepository
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalRecordRepository

class CreateOperationalDependencyUseCase(
    private val records: OperationalRecordRepository,
    private val dependencies: OperationalDependencyRepository,
) {
    fun execute(
        dependencyId: String,
        dependentIdValue: String,
        prerequisiteIdValue: String,
        createdAt: OperationalDependencyCreationInstant,
        basis: OperationalDependencyBasis = OperationalDependencyBasis.MANUAL,
        ruleVersion: String? = null,
    ): DomainResult<OperationalDependency> {
        val dependentId = when (val result = OperationalRecordId.of(dependentIdValue)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        val prerequisiteId = when (val result = OperationalRecordId.of(prerequisiteIdValue)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        if (records.findById(dependentId) == null) {
            return DomainResult.Failure(DomainError.InvalidState("Dependent operational record does not exist"))
        }
        if (records.findById(prerequisiteId) == null) {
            return DomainResult.Failure(DomainError.InvalidState("Prerequisite operational record does not exist"))
        }
        if (dependencies.findByPair(dependentId, prerequisiteId) != null) {
            return DomainResult.Failure(DomainError.InvalidState("Dependency already exists"))
        }
        if (OperationalDependencyCycleDetector.wouldCreateCycle(dependencies.listAll(), dependentId, prerequisiteId)) {
            return DomainResult.Failure(DomainError.InvalidState("Dependency would create a cycle"))
        }
        val id = when (val result = OperationalDependencyId.of(dependencyId)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        val dependency = when (
            val result = OperationalDependency.of(
                id = id,
                dependentOperationalRecordId = dependentId,
                prerequisiteOperationalRecordId = prerequisiteId,
                createdAt = createdAt,
                basis = basis,
                ruleVersion = ruleVersion,
            )
        ) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        return dependencies.save(dependency)
    }
}
