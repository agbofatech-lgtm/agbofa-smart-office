package com.agbofa.smartoffice.domain.decision

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.DecisionTransitionInstant

data class DecisionTransition private constructor(
    val id: DecisionTransitionId,
    val decisionId: DecisionId,
    val fromStatus: DecisionStatus,
    val toStatus: DecisionStatus,
    val transitionedAt: DecisionTransitionInstant,
    val basis: DecisionBasis,
) {
    companion object {
        fun of(
            id: DecisionTransitionId,
            decisionId: DecisionId,
            fromStatus: DecisionStatus,
            toStatus: DecisionStatus,
            transitionedAt: DecisionTransitionInstant,
            basis: DecisionBasis = DecisionBasis.MANUAL,
        ): DomainResult<DecisionTransition> {
            if (!DecisionPolicy.allows(fromStatus, toStatus)) {
                return DomainResult.Failure(
                    DomainError.InvalidState("Illegal decision transition $fromStatus → $toStatus"),
                )
            }
            return DomainResult.Success(
                DecisionTransition(id, decisionId, fromStatus, toStatus, transitionedAt, basis),
            )
        }
    }
}
