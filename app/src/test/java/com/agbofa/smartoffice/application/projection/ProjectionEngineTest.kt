package com.agbofa.smartoffice.application.projection

import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.application.classification.ClassifyJournalEntryUseCase
import com.agbofa.smartoffice.application.integrity.EvaluateIntegrityUseCase
import com.agbofa.smartoffice.application.journal.AdmitCaptureToJournalUseCase
import com.agbofa.smartoffice.application.operations.AssignOperationalTemporalUseCase
import com.agbofa.smartoffice.application.operations.CreateOperationalDependencyUseCase
import com.agbofa.smartoffice.application.operations.CreateOperationalRecordUseCase
import com.agbofa.smartoffice.application.operations.TransitionOperationalRecordStateUseCase
import com.agbofa.smartoffice.application.workflow.CreateWorkflowUseCase
import com.agbofa.smartoffice.application.workflow.WorkflowStepDefinition
import com.agbofa.smartoffice.data.capture.InMemoryCaptureRepository
import com.agbofa.smartoffice.data.classification.InMemoryClassificationRepository
import com.agbofa.smartoffice.data.journal.InMemoryJournalRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalDependencyRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalRecordRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalStateRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalTemporalRepository
import com.agbofa.smartoffice.data.rules.InMemoryRuleRepository
import com.agbofa.smartoffice.data.workflow.InMemoryWorkflowRepository
import com.agbofa.smartoffice.data.workflow.InMemoryWorkflowStepRepository
import com.agbofa.smartoffice.data.workflow.InMemoryWorkflowStepTransitionRepository
import com.agbofa.smartoffice.domain.classification.ClassificationBasis
import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.domain.foundation.time.ClassificationInstant
import com.agbofa.smartoffice.domain.foundation.time.DueInstant
import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalDependencyCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalTransitionInstant
import com.agbofa.smartoffice.domain.foundation.time.TemporalAssignmentInstant
import com.agbofa.smartoffice.domain.foundation.time.WorkflowCreationInstant
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.operations.TemporalResolution
import com.agbofa.smartoffice.domain.workflow.WorkflowStepStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class ProjectionEngineTest {
    private val captures = InMemoryCaptureRepository()
    private val journal = InMemoryJournalRepository()
    private val classifications = InMemoryClassificationRepository()
    private val records = InMemoryOperationalRecordRepository()
    private val states = InMemoryOperationalStateRepository()
    private val temporals = InMemoryOperationalTemporalRepository()
    private val dependencies = InMemoryOperationalDependencyRepository()
    private val workflows = InMemoryWorkflowRepository()
    private val steps = InMemoryWorkflowStepRepository()
        init {
            workflows.companionSteps = steps
        }
    private val workflowTransitions = InMemoryWorkflowStepTransitionRepository()
    private val rules = InMemoryRuleRepository()
    private val captureUseCase = CaptureExpressionUseCase(captures)
    private val admit = AdmitCaptureToJournalUseCase(captures, journal)
    private val classify = ClassifyJournalEntryUseCase(journal, classifications)
    private val create = CreateOperationalRecordUseCase(journal, classifications, records)
    private val transition = TransitionOperationalRecordStateUseCase(records, states)
    private val assignTemporal = AssignOperationalTemporalUseCase(records, temporals)
    private val createDep = CreateOperationalDependencyUseCase(records, dependencies)
    private val createWorkflow = CreateWorkflowUseCase(records, workflows, steps)
    private val evaluateIntegrity = EvaluateIntegrityUseCase(
        captures, journal, classifications, records, states, temporals,
        dependencies, workflows, steps, workflowTransitions, rules,
    )
    private val getOverview = GetOperationalOverviewUseCase(
        captures, journal, classifications, records, states, temporals,
        dependencies, workflows, steps, workflowTransitions, evaluateIntegrity,
    )
    private val getOverviews = GetOperationalOverviewsUseCase(
        captures, journal, classifications, records, states, temporals,
        dependencies, workflows, steps, workflowTransitions, evaluateIntegrity,
    )

    private fun seed(suffix: String): OperationalRecordId {
        val capAt = CaptureInstant(Instant.parse("2026-09-07T08:00:00Z"))
        assertTrue(captureUseCase.execute("cap-$suffix", "Call Ama $suffix", capAt) is DomainResult.Success)
        assertTrue(
            admit.execute(
                "jrn-$suffix",
                "cap-$suffix",
                JournalAdmissionInstant(Instant.parse("2026-09-07T08:05:00Z")),
            ) is DomainResult.Success,
        )
        assertTrue(
            classify.execute(
                "cls-$suffix",
                "jrn-$suffix",
                ClassificationType.FOLLOW_UP,
                ClassificationBasis.MANUAL,
                ClassificationInstant(Instant.parse("2026-09-07T08:10:00Z")),
            ) is DomainResult.Success,
        )
        assertTrue(
            create.execute(
                "op-$suffix",
                "jrn-$suffix",
                OperationalCreationInstant(Instant.parse("2026-09-07T08:30:00Z")),
            ) is DomainResult.Success,
        )
        return (OperationalRecordId.of("op-$suffix") as DomainResult.Success).value
    }

    private fun ctx(instant: String) =
        EvaluationContext(EvaluationInstant(Instant.parse(instant)))

    @Test
    fun pt12_01EmptyHistoryProjectsOpen() {
        val id = seed("a")
        val overview = (getOverview.execute(id, ctx("2026-09-07T09:00:00Z")) as DomainResult.Success).value
        assertEquals(OperationalState.OPEN, overview.currentState)
        assertNull(overview.temporal)
        assertNull(overview.dueStatus)
        assertNull(overview.workflow)
        assertEquals("cls-a", overview.classification?.id?.value)
        assertEquals("Call Ama a", overview.capture?.originalExpression?.value)
    }

    @Test
    fun pt12_02StateTransitionProjected() {
        val id = seed("a")
        assertTrue(
            transition.execute(
                "tr-1", id.value, OperationalState.ACTIVE,
                OperationalTransitionInstant(Instant.parse("2026-09-07T09:00:00Z")),
            ) is DomainResult.Success,
        )
        val overview = (getOverview.execute(id, ctx("2026-09-07T09:05:00Z")) as DomainResult.Success).value
        assertEquals(OperationalState.ACTIVE, overview.currentState)
    }

    @Test
    fun pt12_05UnresolvedHasNoDueStatus() {
        val id = seed("a")
        assertTrue(
            assignTemporal.execute(
                temporalId = "tmp-1",
                operationalRecordIdValue = id.value,
                resolution = TemporalResolution.UNRESOLVED,
                assignedAt = TemporalAssignmentInstant(Instant.parse("2026-09-07T09:40:00Z")),
                referenceExpression = "Tuesday at 8 AM",
            ) is DomainResult.Success,
        )
        val overview = (getOverview.execute(id, ctx("2026-09-08T08:00:00Z")) as DomainResult.Success).value
        assertEquals(TemporalResolution.UNRESOLVED, overview.temporal?.resolution)
        assertNull(overview.dueStatus)
    }

    @Test
    fun pt12_06To08DueEvaluationUsesContext() {
        val id = seed("a")
        assertTrue(
            assignTemporal.execute(
                "tmp-1", id.value, TemporalResolution.RESOLVED,
                TemporalAssignmentInstant(Instant.parse("2026-09-07T09:40:00Z")),
                dueInstant = DueInstant(Instant.parse("2026-09-10T08:00:00Z")),
            ) is DomainResult.Success,
        )
        assertEquals(
            DueStatus.BEFORE_DUE,
            (getOverview.execute(id, ctx("2026-09-09T08:00:00Z")) as DomainResult.Success).value.dueStatus,
        )
        assertEquals(
            DueStatus.AT_DUE,
            (getOverview.execute(id, ctx("2026-09-10T08:00:00Z")) as DomainResult.Success).value.dueStatus,
        )
        assertEquals(
            DueStatus.PAST_DUE,
            (getOverview.execute(id, ctx("2026-09-11T08:00:00Z")) as DomainResult.Success).value.dueStatus,
        )
    }

    @Test
    fun pt12_09DependencyDirection() {
        val a = seed("a")
        val b = seed("b")
        val at = OperationalDependencyCreationInstant(Instant.parse("2026-09-07T09:45:00Z"))
        assertTrue(createDep.execute("dep-1", a.value, b.value, at) is DomainResult.Success)
        val overviewA = (getOverview.execute(a, ctx("2026-09-07T10:00:00Z")) as DomainResult.Success).value
        val overviewB = (getOverview.execute(b, ctx("2026-09-07T10:00:00Z")) as DomainResult.Success).value
        assertEquals(b, overviewA.prerequisites.single().prerequisiteOperationalRecordId)
        assertEquals(a, overviewB.dependents.single().dependentOperationalRecordId)
    }

    @Test
    fun pt12_10WorkflowSummary() {
        val id = seed("a")
        assertTrue(
            createWorkflow.execute(
                workflowId = "wf-1",
                operationalRecordIdValue = id.value,
                createdAt = WorkflowCreationInstant(Instant.parse("2026-09-07T10:00:00Z")),
                stepSpecs = listOf(
                    WorkflowStepDefinition("st-1", 1, "intake", "Intake"),
                    WorkflowStepDefinition("st-2", 2, "close", "Close"),
                ),
            ) is DomainResult.Success,
        )
        val overview = (getOverview.execute(id, ctx("2026-09-07T10:05:00Z")) as DomainResult.Success).value
        assertEquals(2, overview.workflow?.steps?.size)
        assertEquals(WorkflowStepStatus.PENDING, overview.workflow?.steps?.first()?.status)
        assertFalse(overview.workflow?.isComplete == true)
    }

    @Test
    fun pt12_14NoMutation() {
        val id = seed("a")
        val before = records.findById(id)
        getOverview.execute(id, ctx("2026-09-07T11:00:00Z"))
        assertEquals(before, records.findById(id))
        assertTrue(states.listByOperationalRecordId(id).isEmpty())
        assertTrue(temporals.listByOperationalRecordId(id).isEmpty())
        assertTrue(dependencies.listAll().isEmpty())
    }

    @Test
    fun pt12_15Determinism() {
        val id = seed("a")
        val context = ctx("2026-09-07T11:00:00Z")
        val first = (getOverview.execute(id, context) as DomainResult.Success).value
        val second = (getOverview.execute(id, context) as DomainResult.Success).value
        assertEquals(first, second)
    }

    @Test
    fun pt12_22MissingRecordFails() {
        val missing = (OperationalRecordId.of("op-missing") as DomainResult.Success).value
        assertTrue(getOverview.execute(missing, ctx("2026-09-07T11:00:00Z")) is DomainResult.Failure)
    }

    @Test
    fun pt12_24PinnedClassificationPreserved() {
        val id = seed("a")
        val overview = (getOverview.execute(id, ctx("2026-09-07T11:00:00Z")) as DomainResult.Success).value
        assertEquals(overview.operationalRecord.classificationId, overview.classification?.id)
        val listed = getOverviews.execute(ctx("2026-09-07T11:00:00Z"))
        assertEquals(1, listed.size)
        assertEquals(id, listed.single().operationalRecord.id)
    }
}
