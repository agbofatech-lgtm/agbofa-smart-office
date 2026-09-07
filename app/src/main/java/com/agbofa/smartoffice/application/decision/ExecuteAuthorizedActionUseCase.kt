package com.agbofa.smartoffice.application.decision

import com.agbofa.smartoffice.application.operations.TransitionOperationalRecordStateUseCase
import com.agbofa.smartoffice.application.workflow.AdvanceWorkflowUseCase
import com.agbofa.smartoffice.domain.decision.AuthorizedActionExecution
import com.agbofa.smartoffice.domain.decision.AuthorizedActionExecutionId
import com.agbofa.smartoffice.domain.decision.AuthorizedActionExecutionRepository
import com.agbofa.smartoffice.domain.decision.AuthorizedActionExecutionResult
import com.agbofa.smartoffice.domain.decision.AuthorizedActionRequestId
import com.agbofa.smartoffice.domain.decision.AuthorizedActionRequestRepository
import com.agbofa.smartoffice.domain.decision.AuthorizedActionType
import com.agbofa.smartoffice.domain.decision.DecisionProjection
import com.agbofa.smartoffice.domain.decision.DecisionStatus
import com.agbofa.smartoffice.domain.decision.DecisionTransitionRepository
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.ActionRequestInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalTransitionInstant
import com.agbofa.smartoffice.domain.foundation.time.WorkflowTransitionInstant
import com.agbofa.smartoffice.domain.operations.OperationalState

class ExecuteAuthorizedActionUseCase(
    private val requests: AuthorizedActionRequestRepository,
    private val executions: AuthorizedActionExecutionRepository,
    private val history: DecisionTransitionRepository,
    private val transitionState: TransitionOperationalRecordStateUseCase,
    private val advanceWorkflow: AdvanceWorkflowUseCase,
) {
    fun execute(
        requestIdValue: String,
        executionIdValue: String,
        executedAt: ActionRequestInstant,
        ownerStateTransitionId: String? = null,
        ownerStateTransitionedAt: OperationalTransitionInstant? = null,
        ownerWorkflowTransitionedAt: WorkflowTransitionInstant? = null,
    ): DomainResult<AuthorizedActionExecution> {
        val requestId = when (val result = AuthorizedActionRequestId.of(requestIdValue)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        val request = requests.findById(requestId)
            ?: return DomainResult.Failure(DomainError.InvalidState("Authorized action request does not exist"))
        if (executions.findByRequestId(requestId) != null) {
            return DomainResult.Failure(DomainError.InvalidState("Action request already executed"))
        }
        val status = DecisionProjection.current(history.listByDecisionId(request.decisionId))
        if (status != DecisionStatus.APPROVED) {
            return record(
                executionIdValue, requestId, request.decisionId, executedAt,
                AuthorizedActionExecutionResult.REJECTED_BY_AUTHORIZATION,
                "Decision is not APPROVED",
            )
        }
        val ownerResult: DomainResult<*> = when (request.actionType) {
            AuthorizedActionType.TRANSITION_OPERATIONAL_STATE -> {
                val toState = request.toStateName?.let { runCatching { OperationalState.valueOf(it) }.getOrNull() }
                    ?: return DomainResult.Failure(DomainError.InvalidState("Action is missing toState"))
                val transitionId = ownerStateTransitionId
                    ?: return DomainResult.Failure(DomainError.ValidationError("Owner state transition id required", "ownerStateTransitionId"))
                val at = ownerStateTransitionedAt
                    ?: return DomainResult.Failure(DomainError.ValidationError("Owner state transition instant required", "ownerStateTransitionedAt"))
                transitionState.execute(
                    transitionId = transitionId,
                    operationalRecordIdValue = request.targetId,
                    toState = toState,
                    transitionedAt = at,
                )
            }
            AuthorizedActionType.ADVANCE_WORKFLOW -> {
                val at = ownerWorkflowTransitionedAt
                    ?: return DomainResult.Failure(DomainError.ValidationError("Owner workflow transition instant required", "ownerWorkflowTransitionedAt"))
                advanceWorkflow.execute(
                    workflowIdValue = request.targetId,
                    completeTransitionId = request.completeTransitionId.orEmpty(),
                    activateTransitionId = request.activateTransitionId.orEmpty(),
                    transitionedAt = at,
                )
            }
        }
        val outcome = when (ownerResult) {
            is DomainResult.Success -> AuthorizedActionExecutionResult.DISPATCHED to "Owner use case accepted"
            is DomainResult.Failure -> AuthorizedActionExecutionResult.REJECTED_BY_OWNER to ownerResult.error.message
        }
        return record(
            executionIdValue, requestId, request.decisionId, executedAt, outcome.first, outcome.second,
        )
    }

    private fun record(
        executionIdValue: String,
        requestId: AuthorizedActionRequestId,
        decisionId: com.agbofa.smartoffice.domain.decision.DecisionId,
        executedAt: ActionRequestInstant,
        result: AuthorizedActionExecutionResult,
        detail: String,
    ): DomainResult<AuthorizedActionExecution> {
        val id = when (val parsed = AuthorizedActionExecutionId.of(executionIdValue)) {
            is DomainResult.Failure -> return parsed
            is DomainResult.Success -> parsed.value
        }
        val execution = when (
            val parsed = AuthorizedActionExecution.of(id, requestId, decisionId, executedAt, result, detail)
        ) {
            is DomainResult.Failure -> return parsed
            is DomainResult.Success -> parsed.value
        }
        return executions.save(execution)
    }
}
