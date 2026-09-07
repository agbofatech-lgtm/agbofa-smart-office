package com.agbofa.smartoffice.domain.decision

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.ActionRequestInstant

/**
 * Explicit human request to invoke an owning-domain use case.
 * Does not mutate that domain itself.
 */
data class AuthorizedActionRequest private constructor(
    val id: AuthorizedActionRequestId,
    val decisionId: DecisionId,
    val actionType: AuthorizedActionType,
    val targetId: String,
    val requestedAt: ActionRequestInstant,
    val toStateName: String?,
    val completeTransitionId: String?,
    val activateTransitionId: String?,
) {
    companion object {
        fun transitionState(
            id: AuthorizedActionRequestId,
            decisionId: DecisionId,
            parameters: TransitionStateParameters,
            requestedAt: ActionRequestInstant,
        ): DomainResult<AuthorizedActionRequest> =
            of(
                id = id,
                decisionId = decisionId,
                actionType = AuthorizedActionType.TRANSITION_OPERATIONAL_STATE,
                targetId = parameters.operationalRecordId.value,
                requestedAt = requestedAt,
                toStateName = parameters.toState.name,
                completeTransitionId = null,
                activateTransitionId = null,
            )

        fun advanceWorkflow(
            id: AuthorizedActionRequestId,
            decisionId: DecisionId,
            parameters: AdvanceWorkflowParameters,
            requestedAt: ActionRequestInstant,
        ): DomainResult<AuthorizedActionRequest> =
            of(
                id = id,
                decisionId = decisionId,
                actionType = AuthorizedActionType.ADVANCE_WORKFLOW,
                targetId = parameters.workflowId.value,
                requestedAt = requestedAt,
                toStateName = null,
                completeTransitionId = parameters.completeTransitionId,
                activateTransitionId = parameters.activateTransitionId,
            )

        fun of(
            id: AuthorizedActionRequestId,
            decisionId: DecisionId,
            actionType: AuthorizedActionType,
            targetId: String,
            requestedAt: ActionRequestInstant,
            toStateName: String? = null,
            completeTransitionId: String? = null,
            activateTransitionId: String? = null,
        ): DomainResult<AuthorizedActionRequest> {
            if (targetId.isBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError("Action target must not be blank", "targetId"),
                )
            }
            if (actionType == AuthorizedActionType.TRANSITION_OPERATIONAL_STATE && toStateName.isNullOrBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError("State transition action requires toState", "toState"),
                )
            }
            if (actionType == AuthorizedActionType.ADVANCE_WORKFLOW &&
                (completeTransitionId.isNullOrBlank() || activateTransitionId.isNullOrBlank())
            ) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        "Advance workflow action requires both transition identities",
                        "transitions",
                    ),
                )
            }
            return DomainResult.Success(
                AuthorizedActionRequest(
                    id, decisionId, actionType, targetId.trim(), requestedAt,
                    toStateName, completeTransitionId, activateTransitionId,
                ),
            )
        }
    }
}
