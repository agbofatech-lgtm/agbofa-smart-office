package com.agbofa.smartoffice.domain.workflow

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.WorkflowCreationInstant
import com.agbofa.smartoffice.domain.operations.OperationalRecordId

/**
 * Deterministic process attached to an OperationalRecord.
 *
 * Does not own OperationalState, temporal data, or dependencies.
 */
data class Workflow private constructor(
    val id: WorkflowId,
    val operationalRecordId: OperationalRecordId,
    val createdAt: WorkflowCreationInstant,
    val basis: WorkflowBasis,
    val ruleVersion: String?,
) {
    companion object {
        fun of(
            id: WorkflowId,
            operationalRecordId: OperationalRecordId,
            createdAt: WorkflowCreationInstant,
            basis: WorkflowBasis,
            ruleVersion: String? = null,
        ): DomainResult<Workflow> {
            if (basis == WorkflowBasis.RULE && ruleVersion.isNullOrBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError("RULE workflow requires ruleVersion", "ruleVersion"),
                )
            }
            if (basis == WorkflowBasis.MANUAL && !ruleVersion.isNullOrBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        "MANUAL workflow must not carry ruleVersion",
                        "ruleVersion",
                    ),
                )
            }
            return DomainResult.Success(
                Workflow(id, operationalRecordId, createdAt, basis, ruleVersion),
            )
        }
    }
}
