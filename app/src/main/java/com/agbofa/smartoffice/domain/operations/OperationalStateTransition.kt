package com.agbofa.smartoffice.domain.operations

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.OperationalTransitionInstant

/**
 * Immutable lifecycle transition of an OperationalRecord.
 *
 * Does not mutate Capture, Journal, Classification, or OperationalRecord
 * provenance. Does not own due dates or workflow steps.
 */
data class OperationalStateTransition private constructor(
    val id: OperationalStateTransitionId,
    val operationalRecordId: OperationalRecordId,
    val fromState: OperationalState,
    val toState: OperationalState,
    val transitionedAt: OperationalTransitionInstant,
    val basis: OperationalTransitionBasis,
    val ruleVersion: String?,
) {
    companion object {
        fun of(
            id: OperationalStateTransitionId,
            operationalRecordId: OperationalRecordId,
            fromState: OperationalState,
            toState: OperationalState,
            transitionedAt: OperationalTransitionInstant,
            basis: OperationalTransitionBasis,
            ruleVersion: String? = null,
        ): DomainResult<OperationalStateTransition> {
            if (!OperationalStatePolicy.permitted(fromState, toState)) {
                return DomainResult.Failure(
                    DomainError.InvalidState(
                        "Transition $fromState → $toState is not permitted",
                    ),
                )
            }
            if (basis == OperationalTransitionBasis.RULE && ruleVersion.isNullOrBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "RULE transition requires ruleVersion",
                        path = "ruleVersion",
                    ),
                )
            }
            if (basis == OperationalTransitionBasis.MANUAL && !ruleVersion.isNullOrBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "MANUAL transition must not carry ruleVersion",
                        path = "ruleVersion",
                    ),
                )
            }
            return DomainResult.Success(
                OperationalStateTransition(
                    id = id,
                    operationalRecordId = operationalRecordId,
                    fromState = fromState,
                    toState = toState,
                    transitionedAt = transitionedAt,
                    basis = basis,
                    ruleVersion = ruleVersion,
                ),
            )
        }
    }
}
