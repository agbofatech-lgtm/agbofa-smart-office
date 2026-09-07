package com.agbofa.smartoffice.application.decision

import com.agbofa.smartoffice.domain.decision.AuthorizedActionType
import com.agbofa.smartoffice.domain.decision.Decision
import com.agbofa.smartoffice.domain.decision.DecisionBasis
import com.agbofa.smartoffice.domain.decision.DecisionId
import com.agbofa.smartoffice.domain.decision.DecisionPolicy
import com.agbofa.smartoffice.domain.decision.DecisionProjection
import com.agbofa.smartoffice.domain.decision.DecisionRepository
import com.agbofa.smartoffice.domain.decision.DecisionStatus
import com.agbofa.smartoffice.domain.decision.DecisionSubject
import com.agbofa.smartoffice.domain.decision.DecisionTransition
import com.agbofa.smartoffice.domain.decision.DecisionTransitionId
import com.agbofa.smartoffice.domain.decision.DecisionTransitionRepository
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.DecisionCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.DecisionTransitionInstant

class CreateDecisionUseCase(
    private val decisions: DecisionRepository,
) {
    fun execute(
        decisionId: String,
        subject: DecisionSubject,
        actionType: AuthorizedActionType,
        rationale: String,
        createdAt: DecisionCreationInstant,
    ): DomainResult<Decision> {
        val id = when (val result = DecisionId.of(decisionId)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        val decision = when (val result = Decision.of(id, subject, actionType, rationale, createdAt)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        return decisions.save(decision)
    }
}

class GetDecisionUseCase(
    private val decisions: DecisionRepository,
) {
    fun execute(decisionId: String): DomainResult<Decision> {
        val id = when (val result = DecisionId.of(decisionId)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        val decision = decisions.findById(id)
            ?: return DomainResult.Failure(DomainError.InvalidState("Decision does not exist"))
        return DomainResult.Success(decision)
    }
}

class GetDecisionHistoryUseCase(
    private val transitions: DecisionTransitionRepository,
) {
    fun execute(decisionId: String): DomainResult<List<DecisionTransition>> {
        val id = when (val result = DecisionId.of(decisionId)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        return DomainResult.Success(transitions.listByDecisionId(id))
    }
}

class GetDecisionProjectionUseCase(
    private val decisions: DecisionRepository,
    private val transitions: DecisionTransitionRepository,
) {
    fun execute(decisionId: String): DomainResult<DecisionStatus> {
        val id = when (val result = DecisionId.of(decisionId)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        if (decisions.findById(id) == null) {
            return DomainResult.Failure(DomainError.InvalidState("Decision does not exist"))
        }
        return DomainResult.Success(DecisionProjection.current(transitions.listByDecisionId(id)))
    }
}

class TransitionDecisionUseCase(
    private val decisions: DecisionRepository,
    private val transitions: DecisionTransitionRepository,
) {
    fun execute(
        transitionId: String,
        decisionId: String,
        toStatus: DecisionStatus,
        transitionedAt: DecisionTransitionInstant,
        basis: DecisionBasis = DecisionBasis.MANUAL,
    ): DomainResult<DecisionTransition> {
        val id = when (val result = DecisionId.of(decisionId)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        if (decisions.findById(id) == null) {
            return DomainResult.Failure(DomainError.InvalidState("Decision does not exist"))
        }
        val current = DecisionProjection.current(transitions.listByDecisionId(id))
        if (toStatus == current) {
            return DomainResult.Failure(DomainError.InvalidState("Decision already $current"))
        }
        if (!DecisionPolicy.allows(current, toStatus)) {
            return DomainResult.Failure(DomainError.InvalidState("Illegal decision transition $current → $toStatus"))
        }
        val tid = when (val result = DecisionTransitionId.of(transitionId)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        val transition = when (
            val result = DecisionTransition.of(tid, id, current, toStatus, transitionedAt, basis)
        ) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        return transitions.save(transition)
    }
}

class ApproveDecisionUseCase(
    private val transition: TransitionDecisionUseCase,
) {
    fun execute(transitionId: String, decisionId: String, at: DecisionTransitionInstant) =
        transition.execute(transitionId, decisionId, DecisionStatus.APPROVED, at)
}

class RejectDecisionUseCase(
    private val transition: TransitionDecisionUseCase,
) {
    fun execute(transitionId: String, decisionId: String, at: DecisionTransitionInstant) =
        transition.execute(transitionId, decisionId, DecisionStatus.REJECTED, at)
}

class WithdrawDecisionUseCase(
    private val transition: TransitionDecisionUseCase,
) {
    fun execute(transitionId: String, decisionId: String, at: DecisionTransitionInstant) =
        transition.execute(transitionId, decisionId, DecisionStatus.WITHDRAWN, at)
}
