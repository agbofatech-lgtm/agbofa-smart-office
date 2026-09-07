package com.agbofa.smartoffice.application.decision

import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.application.classification.ClassifyJournalEntryUseCase
import com.agbofa.smartoffice.application.journal.AdmitCaptureToJournalUseCase
import com.agbofa.smartoffice.application.operations.CreateOperationalRecordUseCase
import com.agbofa.smartoffice.application.operations.GetOperationalRecordStateUseCase
import com.agbofa.smartoffice.application.operations.TransitionOperationalRecordStateUseCase
import com.agbofa.smartoffice.application.workflow.AdvanceWorkflowUseCase
import com.agbofa.smartoffice.application.workflow.TransitionWorkflowStepUseCase
import com.agbofa.smartoffice.data.capture.InMemoryCaptureRepository
import com.agbofa.smartoffice.data.classification.InMemoryClassificationRepository
import com.agbofa.smartoffice.data.decision.InMemoryAuthorizedActionExecutionRepository
import com.agbofa.smartoffice.data.decision.InMemoryAuthorizedActionRequestRepository
import com.agbofa.smartoffice.data.decision.InMemoryDecisionRepository
import com.agbofa.smartoffice.data.decision.InMemoryDecisionTransitionRepository
import com.agbofa.smartoffice.data.journal.InMemoryJournalRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalRecordRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalStateRepository
import com.agbofa.smartoffice.data.workflow.InMemoryWorkflowRepository
import com.agbofa.smartoffice.data.workflow.InMemoryWorkflowStepRepository
import com.agbofa.smartoffice.data.workflow.InMemoryWorkflowStepTransitionRepository
import com.agbofa.smartoffice.domain.classification.ClassificationBasis
import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.decision.AuthorizedActionType
import com.agbofa.smartoffice.domain.decision.DecisionStatus
import com.agbofa.smartoffice.domain.decision.DecisionSubject
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.ActionRequestInstant
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.domain.foundation.time.ClassificationInstant
import com.agbofa.smartoffice.domain.foundation.time.DecisionCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.DecisionTransitionInstant
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalTransitionInstant
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class AuthorizedActionEngineTest {
    private val t0 = Instant.parse("2026-09-07T14:00:00Z")
    private val captures = InMemoryCaptureRepository()
    private val journal = InMemoryJournalRepository()
    private val classifications = InMemoryClassificationRepository()
    private val records = InMemoryOperationalRecordRepository()
    private val states = InMemoryOperationalStateRepository()
    private val decisions = InMemoryDecisionRepository()
    private val decisionHistory = InMemoryDecisionTransitionRepository()
    private val requests = InMemoryAuthorizedActionRequestRepository()
    private val executions = InMemoryAuthorizedActionExecutionRepository()
    private val workflows = InMemoryWorkflowRepository()
    private val steps = InMemoryWorkflowStepRepository()
    private val workflowTransitions = InMemoryWorkflowStepTransitionRepository()
    private val createDecision = CreateDecisionUseCase(decisions)
    private val approve = ApproveDecisionUseCase(TransitionDecisionUseCase(decisions, decisionHistory))
    private val project = GetDecisionProjectionUseCase(decisions, decisionHistory)
    private val requestAction = RequestAuthorizedActionUseCase(decisions, decisionHistory, requests)
    private val executeAction = ExecuteAuthorizedActionUseCase(
        requests,
        executions,
        decisionHistory,
        TransitionOperationalRecordStateUseCase(records, states),
        AdvanceWorkflowUseCase(
            workflows,
            steps,
            TransitionWorkflowStepUseCase(steps, workflowTransitions),
            workflowTransitions,
        ),
    )

    private fun seedRecord(): OperationalRecordId {
        assertTrue(CaptureExpressionUseCase(captures).execute("c-1", "Pay fees", CaptureInstant(t0)) is DomainResult.Success)
        assertTrue(AdmitCaptureToJournalUseCase(captures, journal).execute("j-1", "c-1", JournalAdmissionInstant(t0)) is DomainResult.Success)
        assertTrue(
            ClassifyJournalEntryUseCase(journal, classifications).execute(
                "cl-1", "j-1", ClassificationType.FOLLOW_UP, ClassificationBasis.MANUAL, ClassificationInstant(t0),
            ) is DomainResult.Success,
        )
        assertTrue(
            CreateOperationalRecordUseCase(journal, classifications, records)
                .execute("op-1", "j-1", OperationalCreationInstant(t0)) is DomainResult.Success,
        )
        return (OperationalRecordId.of("op-1") as DomainResult.Success).value
    }

    @Test
    fun proposedCannotExecuteAndApproveDispatchesOwnerUseCase() {
        val recordId = seedRecord()
        assertTrue(
            createDecision.execute(
                "d-1",
                DecisionSubject.operationalRecord(recordId),
                AuthorizedActionType.TRANSITION_OPERATIONAL_STATE,
                "Activate",
                DecisionCreationInstant(t0),
            ) is DomainResult.Success,
        )
        assertEquals(DecisionStatus.PROPOSED, (project.execute("d-1") as DomainResult.Success).value)
        val blocked = requestAction.execute(
            requestId = "ar-1",
            decisionIdValue = "d-1",
            requestedAt = ActionRequestInstant(t0),
            toStateName = OperationalState.ACTIVE.name,
        )
        assertTrue(blocked is DomainResult.Failure)
        assertTrue(approve.execute("dt-1", "d-1", DecisionTransitionInstant(t0)) is DomainResult.Success)
        assertEquals(DecisionStatus.APPROVED, (project.execute("d-1") as DomainResult.Success).value)
        assertTrue(
            requestAction.execute(
                requestId = "ar-1",
                decisionIdValue = "d-1",
                requestedAt = ActionRequestInstant(t0),
                toStateName = OperationalState.ACTIVE.name,
            ) is DomainResult.Success,
        )
        assertTrue(
            executeAction.execute(
                requestIdValue = "ar-1",
                executionIdValue = "ex-1",
                executedAt = ActionRequestInstant(t0),
                ownerStateTransitionId = "st-1",
                ownerStateTransitionedAt = OperationalTransitionInstant(t0),
            ) is DomainResult.Success,
        )
        assertEquals(OperationalState.ACTIVE, GetOperationalRecordStateUseCase(states).execute(recordId))
        assertTrue(
            executeAction.execute(
                requestIdValue = "ar-1",
                executionIdValue = "ex-2",
                executedAt = ActionRequestInstant(t0),
                ownerStateTransitionId = "st-2",
                ownerStateTransitionedAt = OperationalTransitionInstant(t0),
            ) is DomainResult.Failure,
        )
    }

    @Test
    fun decisionDoesNotWriteStateWithoutExecution() {
        val recordId = seedRecord()
        createDecision.execute(
            "d-1",
            DecisionSubject.operationalRecord(recordId),
            AuthorizedActionType.TRANSITION_OPERATIONAL_STATE,
            "Activate",
            DecisionCreationInstant(t0),
        )
        approve.execute("dt-1", "d-1", DecisionTransitionInstant(t0))
        assertEquals(OperationalState.OPEN, GetOperationalRecordStateUseCase(states).execute(recordId))
    }
}
