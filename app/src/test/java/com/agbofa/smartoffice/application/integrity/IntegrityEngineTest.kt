package com.agbofa.smartoffice.application.integrity

import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.application.classification.ClassifyJournalEntryUseCase
import com.agbofa.smartoffice.application.journal.AdmitCaptureToJournalUseCase
import com.agbofa.smartoffice.application.operations.CreateOperationalRecordUseCase
import com.agbofa.smartoffice.data.capture.InMemoryCaptureRepository
import com.agbofa.smartoffice.data.classification.InMemoryClassificationRepository
import com.agbofa.smartoffice.data.journal.InMemoryJournalRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalDependencyRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalDependencyRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalRecordRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalStateRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalTemporalRepository
import com.agbofa.smartoffice.data.rules.InMemoryRuleRepository
import com.agbofa.smartoffice.data.workflow.InMemoryWorkflowRepository
import com.agbofa.smartoffice.data.workflow.InMemoryWorkflowStepRepository
import com.agbofa.smartoffice.data.workflow.InMemoryWorkflowStepTransitionRepository
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
import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalCreationInstant
import com.agbofa.smartoffice.domain.integrity.IntegrityOutcome
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class IntegrityEngineTest {
    private val captures = InMemoryCaptureRepository()
    private val journal = InMemoryJournalRepository()
    private val classifications = InMemoryClassificationRepository()
    private val operations = InMemoryOperationalRecordRepository()
    private val states = InMemoryOperationalStateRepository()
    private val temporals = InMemoryOperationalTemporalRepository()
    private val dependencies = InMemoryOperationalDependencyRepository()
    private val workflows = InMemoryWorkflowRepository()
    private val steps = InMemoryWorkflowStepRepository()
        init {
            workflows.companionSteps = steps
        }
    private val transitions = InMemoryWorkflowStepTransitionRepository()
    private val rules = InMemoryRuleRepository()
    private val evaluate = EvaluateIntegrityUseCase(
        captures, journal, classifications, operations, states, temporals,
        dependencies, workflows, steps, transitions, rules,
    )
    private val context = EvaluationContext(EvaluationInstant(Instant.parse("2026-09-07T12:00:00Z")))

    @Test
    fun healthyChainDoesNotMutateRecords() {
        assertTrue(
            CaptureExpressionUseCase(captures).execute(
                "cap-1", "Pay school fees", CaptureInstant(Instant.parse("2026-09-07T08:00:00Z")),
            ) is DomainResult.Success,
        )
        assertTrue(
            AdmitCaptureToJournalUseCase(captures, journal).execute(
                "jrn-1", "cap-1", JournalAdmissionInstant(Instant.parse("2026-09-07T08:05:00Z")),
            ) is DomainResult.Success,
        )
        assertTrue(
            ClassifyJournalEntryUseCase(journal, classifications).execute(
                "cls-1", "jrn-1", ClassificationType.FOLLOW_UP,
                ClassificationBasis.MANUAL, ClassificationInstant(Instant.parse("2026-09-07T08:10:00Z")),
            ) is DomainResult.Success,
        )
        assertTrue(
            CreateOperationalRecordUseCase(journal, classifications, operations).execute(
                "op-1", "jrn-1", OperationalCreationInstant(Instant.parse("2026-09-07T08:30:00Z")),
            ) is DomainResult.Success,
        )
        val before = operations.findById((OperationalRecordId.of("op-1") as DomainResult.Success).value)
        val first = evaluate.execute(context)
        val second = evaluate.execute(context)
        val after = operations.findById((OperationalRecordId.of("op-1") as DomainResult.Success).value)
        assertEquals(IntegrityOutcome.HEALTHY, first.outcome)
        assertEquals(first.findings, second.findings)
        assertEquals(before, after)
        assertEquals(1, operations.listAll().size)
        assertEquals(0, states.listByOperationalRecordId(before!!.id).size)
    }
}
