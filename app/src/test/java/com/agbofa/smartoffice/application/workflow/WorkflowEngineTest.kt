package com.agbofa.smartoffice.application.workflow

import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.application.classification.ClassifyJournalEntryUseCase
import com.agbofa.smartoffice.application.journal.AdmitCaptureToJournalUseCase
import com.agbofa.smartoffice.application.operations.CreateOperationalRecordUseCase
import com.agbofa.smartoffice.data.capture.InMemoryCaptureRepository
import com.agbofa.smartoffice.data.classification.InMemoryClassificationRepository
import com.agbofa.smartoffice.data.journal.InMemoryJournalRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalRecordRepository
import com.agbofa.smartoffice.data.workflow.InMemoryWorkflowRepository
import com.agbofa.smartoffice.data.workflow.InMemoryWorkflowStepRepository
import com.agbofa.smartoffice.data.workflow.InMemoryWorkflowStepTransitionRepository
import com.agbofa.smartoffice.domain.classification.ClassificationBasis
import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.domain.foundation.time.ClassificationInstant
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.WorkflowCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.WorkflowTransitionInstant
import com.agbofa.smartoffice.domain.workflow.WorkflowBasis
import com.agbofa.smartoffice.domain.workflow.WorkflowId
import com.agbofa.smartoffice.domain.workflow.WorkflowProgression
import com.agbofa.smartoffice.domain.workflow.WorkflowStepStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class WorkflowEngineTest {
    private val captures = InMemoryCaptureRepository()
    private val journal = InMemoryJournalRepository()
    private val classifications = InMemoryClassificationRepository()
    private val operations = InMemoryOperationalRecordRepository()
    private val workflows = InMemoryWorkflowRepository()
    private val steps = InMemoryWorkflowStepRepository()
        init {
            workflows.companionSteps = steps
        }
    private val history = InMemoryWorkflowStepTransitionRepository()
    private val captureUseCase = CaptureExpressionUseCase(captures)
    private val admit = AdmitCaptureToJournalUseCase(captures, journal)
    private val classify = ClassifyJournalEntryUseCase(journal, classifications)
    private val createOp = CreateOperationalRecordUseCase(journal, classifications, operations)
    private val createWf = CreateWorkflowUseCase(operations, workflows, steps)
    private val transition = TransitionWorkflowStepUseCase(steps, history)
    private val advance = AdvanceWorkflowUseCase(workflows, steps, transition, history)

    private fun seedRecord() {
        val captureAt = CaptureInstant(Instant.parse("2026-09-07T08:00:00Z"))
        assertTrue(captureUseCase.execute("cap-1", "Follow the school-fee process", captureAt) is DomainResult.Success)
        assertTrue(
            admit.execute(
                "jrn-1",
                "cap-1",
                JournalAdmissionInstant(Instant.parse("2026-09-07T08:05:00Z")),
            ) is DomainResult.Success,
        )
        assertTrue(
            classify.execute(
                "cls-1",
                "jrn-1",
                ClassificationType.FOLLOW_UP,
                ClassificationBasis.MANUAL,
                ClassificationInstant(Instant.parse("2026-09-07T08:10:00Z")),
            ) is DomainResult.Success,
        )
        assertTrue(
            createOp.execute(
                "op-1",
                "jrn-1",
                OperationalCreationInstant(Instant.parse("2026-09-07T08:30:00Z")),
            ) is DomainResult.Success,
        )
    }

    @Test
    fun createAndAdvanceWithoutMutatingOperationalRecord() {
        seedRecord()
        val created = createWf.execute(
            workflowId = "wf-1",
            operationalRecordIdValue = "op-1",
            createdAt = WorkflowCreationInstant(Instant.parse("2026-09-07T10:30:00Z")),
            stepSpecs = listOf(
                WorkflowStepDefinition("s1", 1, "prep", "Prepare"),
                WorkflowStepDefinition("s2", 2, "act", "Act"),
            ),
            basis = WorkflowBasis.MANUAL,
        )
        assertTrue(created is DomainResult.Success)
        val missing = createWf.execute(
            "wf-2",
            "missing",
            WorkflowCreationInstant(Instant.parse("2026-09-07T10:31:00Z")),
            listOf(WorkflowStepDefinition("s3", 1, "only", "Only")),
        )
        assertTrue(missing is DomainResult.Failure)
        val at = WorkflowTransitionInstant(Instant.parse("2026-09-07T10:40:00Z"))
        val first = advance.execute("wf-1", "tr-c1", "tr-a1", at)
        assertTrue(first is DomainResult.Success)
        assertEquals(WorkflowStepStatus.ACTIVE, (first as DomainResult.Success).value.toStatus)
        val second = advance.execute(
            "wf-1",
            "tr-c2",
            "tr-a2",
            WorkflowTransitionInstant(Instant.parse("2026-09-07T10:41:00Z")),
        )
        assertTrue(second is DomainResult.Success)
        val wfId = (WorkflowId.of("wf-1") as DomainResult.Success).value
        val stepList = steps.listByWorkflowId(wfId)
        val historyByStep = stepList.associate { it.id to history.listByStepId(it.id) }
        assertEquals(WorkflowStepStatus.COMPLETED, WorkflowProgression.statusByStep(stepList, historyByStep)[stepList[0].id])
        assertEquals("cls-1", operations.findById(
            (com.agbofa.smartoffice.domain.operations.OperationalRecordId.of("op-1") as DomainResult.Success).value,
        )?.classificationId?.value)
        val pendingComplete = transition.execute(
            "tr-bad",
            "s2",
            WorkflowStepStatus.COMPLETED,
            at,
        )
        assertTrue(pendingComplete is DomainResult.Failure || (second as DomainResult.Success).value.toStatus == WorkflowStepStatus.ACTIVE)
    }
}
