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
import com.agbofa.smartoffice.domain.decision.ActionExecutionOutcome
import com.agbofa.smartoffice.domain.decision.AuthorizedActionType
import com.agbofa.smartoffice.domain.decision.DecisionSubjectKind
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.ActionRequestInstant
import com.agbofa.smartoffice.domain.foundation.time.DecisionCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.DecisionTransitionInstant
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
        decisions,
        history,
        requests,
        executions,
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

    @Test
    fun requestRejectedUntilApproved() {
        assertTrue(
            create.execute(
                decisionId = "dec-1",
                subjectKind = DecisionSubjectKind.OPERATIONAL_RECORD,
                subjectTargetId = "op-missing",
                actionType = AuthorizedActionType.TRANSITION_OPERATIONAL_STATE,
                rationale = "Activate follow-up",
                createdAt = DecisionCreationInstant(instant("2026-09-07T12:00:00Z")),
            ) is DomainResult.Success,
        )
        val before = requestAction.execute(
            requestId = "act-1",
            decisionId = "dec-1",
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
            decisionId = "dec-1",
            requestedAt = ActionRequestInstant(instant("2026-09-07T12:15:00Z")),
            toStateName = OperationalState.ACTIVE.name,
        )
        assertTrue(after is DomainResult.Success)
    }

    @Test
    fun executeDoesNotWriteStateWhenOwnerRejectsMissingRecord() {
        create.execute(
            decisionId = "dec-2",
            subjectKind = DecisionSubjectKind.OPERATIONAL_RECORD,
            subjectTargetId = "op-missing",
            actionType = AuthorizedActionType.TRANSITION_OPERATIONAL_STATE,
            rationale = "Activate missing record",
            createdAt = DecisionCreationInstant(instant("2026-09-07T12:00:00Z")),
        )
        approve.execute("tr-2", "dec-2", DecisionTransitionInstant(instant("2026-09-07T12:10:00Z")))
        requestAction.execute(
            requestId = "act-2",
            decisionId = "dec-2",
            requestedAt = ActionRequestInstant(instant("2026-09-07T12:15:00Z")),
            toStateName = OperationalState.ACTIVE.name,
        )
        val executed = execute.execute(
            executionId = "exec-2",
            requestId = "act-2",
            executedAt = ActionRequestInstant(instant("2026-09-07T12:20:00Z")),
            operationalStateTransitionId = "st-owner-1",
        )
        assertTrue(executed is DomainResult.Success)
        assertEquals(
            ActionExecutionOutcome.REJECTED,
            (executed as DomainResult.Success).value.outcome,
        )
        assertTrue(
            states.listByOperationalRecordId(
                (OperationalRecordId.of("op-missing") as DomainResult.Success).value,
            ).isEmpty(),
        )
        val second = execute.execute(
            executionId = "exec-3",
            requestId = "act-2",
            executedAt = ActionRequestInstant(instant("2026-09-07T12:21:00Z")),
            operationalStateTransitionId = "st-owner-2",
        )
        assertTrue(second is DomainResult.Failure)
    }

    @Test
    fun rejectIsTerminal() {
        create.execute(
            decisionId = "dec-3",
            subjectKind = DecisionSubjectKind.OPERATIONAL_RECORD,
            subjectTargetId = "op-1",
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
