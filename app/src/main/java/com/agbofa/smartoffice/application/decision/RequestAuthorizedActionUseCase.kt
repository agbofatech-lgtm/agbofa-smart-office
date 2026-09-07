package com.agbofa.smartoffice.application.decision

import com.agbofa.smartoffice.domain.decision.AuthorizedActionRequest
import com.agbofa.smartoffice.domain.decision.AuthorizedActionRequestId
import com.agbofa.smartoffice.domain.decision.AuthorizedActionRequestRepository
import com.agbofa.smartoffice.domain.decision.AuthorizedActionType
import com.agbofa.smartoffice.domain.decision.DecisionId
import com.agbofa.smartoffice.domain.decision.DecisionProjection
import com.agbofa.smartoffice.domain.decision.DecisionRepository
import com.agbofa.smartoffice.domain.decision.DecisionStatus
import com.agbofa.smartoffice.domain.decision.DecisionTransitionRepository
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.ActionRequestInstant

class RequestAuthorizedActionUseCase(
    private val decisions: DecisionRepository,
    private val history: DecisionTransitionRepository,
    private val requests: AuthorizedActionRequestRepository,
) {
    fun execute(
        requestId: String,
        decisionIdValue: String,
        requestedAt: ActionRequestInstant,
        toStateName: String? = null,
        completeTransitionId: String? = null,
        activateTransitionId: String? = null,
    ): DomainResult<AuthorizedActionRequest> {
        val decisionId = when (val result = DecisionId.of(decisionIdValue)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        val decision = decisions.findById(decisionId)
            ?: return DomainResult.Failure(DomainError.InvalidState("Decision does not exist"))
        val status = DecisionProjection.current(history.listByDecisionId(decisionId))
        if (status != DecisionStatus.APPROVED) {
            return DomainResult.Failure(
                DomainError.InvalidState("Authorized action requires an APPROVED decision"),
            )
        }
        val id = when (val result = AuthorizedActionRequestId.of(requestId)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        val request = when (
            val result = AuthorizedActionRequest.of(
                id = id,
                decisionId = decisionId,
                actionType = decision.actionType,
                targetId = decision.subject.targetId,
                requestedAt = requestedAt,
                toStateName = toStateName,
                completeTransitionId = completeTransitionId,
                activateTransitionId = activateTransitionId,
            )
        ) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        if (request.actionType == AuthorizedActionType.TRANSITION_OPERATIONAL_STATE &&
            request.targetId != decision.subject.targetId
        ) {
            return DomainResult.Failure(DomainError.InvalidState("Action target does not match decision subject"))
        }
        return requests.save(request)
    }
}
