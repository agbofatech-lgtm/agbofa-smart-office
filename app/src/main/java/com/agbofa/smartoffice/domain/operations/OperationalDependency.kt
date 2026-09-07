package com.agbofa.smartoffice.domain.operations

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.OperationalDependencyCreationInstant

/**
 * Immutable edge: [dependentOperationalRecordId] REQUIRES [prerequisiteOperationalRecordId].
 *
 * Does not mutate either record. Does not execute workflow.
 */
data class OperationalDependency private constructor(
    val id: OperationalDependencyId,
    val dependentOperationalRecordId: OperationalRecordId,
    val prerequisiteOperationalRecordId: OperationalRecordId,
    val type: OperationalDependencyType,
    val createdAt: OperationalDependencyCreationInstant,
    val basis: OperationalDependencyBasis,
    val ruleVersion: String?,
) {
    companion object {
        fun of(
            id: OperationalDependencyId,
            dependentOperationalRecordId: OperationalRecordId,
            prerequisiteOperationalRecordId: OperationalRecordId,
            type: OperationalDependencyType = OperationalDependencyType.REQUIRES,
            createdAt: OperationalDependencyCreationInstant,
            basis: OperationalDependencyBasis,
            ruleVersion: String? = null,
        ): DomainResult<OperationalDependency> {
            if (dependentOperationalRecordId == prerequisiteOperationalRecordId) {
                return DomainResult.Failure(
                    DomainError.InvalidState("Operational record cannot depend on itself"),
                )
            }
            if (basis == OperationalDependencyBasis.RULE && ruleVersion.isNullOrBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "RULE dependency requires ruleVersion",
                        path = "ruleVersion",
                    ),
                )
            }
            if (basis == OperationalDependencyBasis.MANUAL && !ruleVersion.isNullOrBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "MANUAL dependency must not carry ruleVersion",
                        path = "ruleVersion",
                    ),
                )
            }
            return DomainResult.Success(
                OperationalDependency(
                    id = id,
                    dependentOperationalRecordId = dependentOperationalRecordId,
                    prerequisiteOperationalRecordId = prerequisiteOperationalRecordId,
                    type = type,
                    createdAt = createdAt,
                    basis = basis,
                    ruleVersion = ruleVersion,
                ),
            )
        }
    }
}
