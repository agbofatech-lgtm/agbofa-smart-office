package com.agbofa.smartoffice.application.operations

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.OperationalTransitionInstant
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalRecordRepository
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.operations.OperationalStateProjection
import com.agbofa.smartoffice.domain.operations.OperationalStateRepository
import com.agbofa.smartoffice.domain.operations.OperationalStateTransition
import com.agbofa.smartoffice.domain.operations.OperationalStateTransitionId
import com.agbofa.smartoffice.domain.operations.OperationalTransitionBasis

class TransitionOperationalRecordStateUseCase(
    private val records: OperationalRecordRepository,
    private val states: OperationalStateRepository,
) {
    fun execute(
        transitionId: String,
        operationalRecordIdValue: String,
        toState: OperationalState,
        transitionedAt: OperationalTransitionInstant,
        basis: OperationalTransitionBasis = OperationalTransitionBasis.MANUAL,
        ruleVersion: String? = null,
    ): DomainResult<OperationalStateTransition> {
        val recordId = when (val result = OperationalRecordId.of(operationalRecordIdValue)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        if (records.findById(recordId) == null) {
            return DomainResult.Failure(
                DomainError.InvalidState("Operational record does not exist"),
            )
        }
        val current = OperationalStateProjection.current(
            states.listByOperationalRecordId(recordId),
        )
        val id = when (val result = OperationalStateTransitionId.of(transitionId)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        val transition = when (
            val result = OperationalStateTransition.of(
                id = id,
                operationalRecordId = recordId,
                fromState = current,
                toState = toState,
                transitionedAt = transitionedAt,
                basis = basis,
                ruleVersion = ruleVersion,
            )
        ) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        return states.save(transition)
    }
}
