package com.agbofa.smartoffice.application.decision

import com.agbofa.smartoffice.application.operations.TransitionOperationalRecordStateUseCase
import com.agbofa.smartoffice.application.workflow.AdvanceWorkflowUseCase
import com.agbofa.smartoffice.application.workflow.TransitionWorkflowStepUseCase
import com.agbofa.smartoffice.data.decision.InMemoryAuthorizedActionExecutionRepository
import com.agbofa.smartoffice.data.decision.InMemoryAuthorizedActionRequestRepository
import com.agbofa.smartoffice.data.decision.InMemoryDecisionRepository
import com.agbofa.smartoffice.data.decision.InMemoryDecisionTransitionRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalRecordRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalStateRepository
import com.agbofa.smartoffice.data.workflow.InMemoryWorkflowRepository
import com.agbofa.smartoffice.data.workflow.InMemoryWorkflowStepRepository
import com.agbofa.smartoffice.data.workflow.InMemoryWorkflowStepTransitionRepository
import com.agbofa.smartoffice.domain.decision.AuthorizedActionExecutionResult
import com.agbofa.smartoffice.domain.decision.AuthorizedActionType
import com.agbofa.smartoffice.domain.decision.DecisionSubject
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.ActionRequestInstant
import com.agbofa.smartoffice.domain.foundation.time.DecisionCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.DecisionTransitionInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalTransitionInstant
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class DecisionActionEngineTest {
    private val decisions = InMemoryDecisionRepository()
    private val history = InMemoryDecisionTransitionRepository()
    private val requests = InMemoryAuthorizedActionRequestRepository()
    private val executions = InMemoryAuthorizedActionExecutionRepository()
    private val records = InMemoryOperationalRecordRepository()
    private val states = InMemoryOperationalStateRepository()
    private val create = CreateDecisionUseCase(decisions)
    private val approve = ApproveDecisionUseCase(TransitionDecisionUseCase(decisions, history))
    private val reject = RejectDecisionUseCase(TransitionDecisionUseCase(decisions, history))
    private val requestAction = RequestAuthorizedActionUseCase(decisions, history, requests)
    private val execute = ExecuteAuthorizedActionUseCase(
        requests,
        executions,
        history,
        TransitionOperationalRecordStateUseCase(records, states),
        AdvanceWorkflowUseCase(
            InMemoryWorkflowRepository(),
            InMemoryWorkflowStepRepository(),
            TransitionWorkflowStepUseCase(
                InMemoryWorkflowStepRepository(),
                InMemoryWorkflowStepTransitionRepository(),
            ),
            InMemoryWorkflowStepTransitionRepository(),
        ),
    )

    private fun instant(value: String) = Instant.parse(value)
    private fun recordSubject(value: String) =
        DecisionSubject.operationalRecord((OperationalRecordId.of(value) as DomainResult.Success).value)

    @Test
    fun requestRejectedUntilApproved() {
        assertTrue(
            create.execute(
                decisionId = "dec-1",
                subject = recordSubject("op-missing"),
                actionType = AuthorizedActionType.TRANSITION_OPERATIONAL_STATE,
                rationale = "Activate follow-up",
                createdAt = DecisionCreationInstant(instant("2026-09-07T12:00:00Z")),
            ) is DomainResult.Success,
        )
        val before = requestAction.execute(
            requestId = "act-1",
            decisionIdValue = "dec-1",
            requestedAt = ActionRequestInstant(instant("2026-09-07T12:05:00Z")),
            toStateName = OperationalState.ACTIVE.name,
        )
        assertTrue(before is DomainResult.Failure)
        assertTrue(
            approve.execute("tr-1", "dec-1", DecisionTransitionInstant(instant("2026-09-07T12:10:00Z")))
                is DomainResult.Success,
        )
        val after = requestAction.execute(
            requestId = "act-1",
            decisionIdValue = "dec-1",
            requestedAt = ActionRequestInstant(instant("2026-09-07T12:15:00Z")),
            toStateName = OperationalState.ACTIVE.name,
        )
        assertTrue(after is DomainResult.Success)
    }

    @Test
    fun executeDoesNotWriteStateWhenOwnerRejectsMissingRecord() {
        create.execute(
            decisionId = "dec-2",
            subject = recordSubject("op-missing"),
            actionType = AuthorizedActionType.TRANSITION_OPERATIONAL_STATE,
            rationale = "Activate missing record",
            createdAt = DecisionCreationInstant(instant("2026-09-07T12:00:00Z")),
        )
        approve.execute("tr-2", "dec-2", DecisionTransitionInstant(instant("2026-09-07T12:10:00Z")))
        requestAction.execute(
            requestId = "act-2",
            decisionIdValue = "dec-2",
            requestedAt = ActionRequestInstant(instant("2026-09-07T12:15:00Z")),
            toStateName = OperationalState.ACTIVE.name,
        )
        val executed = execute.execute(
            requestIdValue = "act-2",
            executionIdValue = "exec-2",
            executedAt = ActionRequestInstant(instant("2026-09-07T12:20:00Z")),
            ownerStateTransitionId = "st-owner-1",
            ownerStateTransitionedAt = OperationalTransitionInstant(instant("2026-09-07T12:20:00Z")),
        )
        assertTrue(executed is DomainResult.Success)
        assertEquals(
            AuthorizedActionExecutionResult.REJECTED_BY_OWNER,
            (executed as DomainResult.Success).value.result,
        )
        assertTrue(
            states.listByOperationalRecordId(
                (OperationalRecordId.of("op-missing") as DomainResult.Success).value,
            ).isEmpty(),
        )
        val second = execute.execute(
            requestIdValue = "act-2",
            executionIdValue = "exec-3",
            executedAt = ActionRequestInstant(instant("2026-09-07T12:21:00Z")),
            ownerStateTransitionId = "st-owner-2",
            ownerStateTransitionedAt = OperationalTransitionInstant(instant("2026-09-07T12:21:00Z")),
        )
        assertTrue(second is DomainResult.Failure)
    }

    @Test
    fun rejectIsTerminal() {
        create.execute(
            decisionId = "dec-3",
            subject = recordSubject("op-1"),
            actionType = AuthorizedActionType.TRANSITION_OPERATIONAL_STATE,
            rationale = "Reject path",
            createdAt = DecisionCreationInstant(instant("2026-09-07T12:00:00Z")),
        )
        assertTrue(
            reject.execute("tr-3", "dec-3", DecisionTransitionInstant(instant("2026-09-07T12:10:00Z")))
                is DomainResult.Success,
        )
        assertTrue(
            approve.execute("tr-4", "dec-3", DecisionTransitionInstant(instant("2026-09-07T12:11:00Z")))
                is DomainResult.Failure,
        )
    }
}
