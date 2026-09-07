package com.agbofa.smartoffice.application.operations

import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.application.classification.ClassifyJournalEntryUseCase
import com.agbofa.smartoffice.application.journal.AdmitCaptureToJournalUseCase
import com.agbofa.smartoffice.data.capture.InMemoryCaptureRepository
import com.agbofa.smartoffice.data.classification.InMemoryClassificationRepository
import com.agbofa.smartoffice.data.journal.InMemoryJournalRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalDependencyRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalRecordRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalTemporalRepository
import com.agbofa.smartoffice.domain.classification.ClassificationBasis
import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.domain.foundation.time.ClassificationInstant
import com.agbofa.smartoffice.domain.foundation.time.DueInstant
import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalDependencyCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.TemporalAssignmentInstant
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.TemporalCreationBasis
import com.agbofa.smartoffice.domain.operations.TemporalResolution
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class TemporalDependencyEngineTest {
    private val captures = InMemoryCaptureRepository()
    private val journal = InMemoryJournalRepository()
    private val classifications = InMemoryClassificationRepository()
    private val operations = InMemoryOperationalRecordRepository()
    private val temporals = InMemoryOperationalTemporalRepository()
    private val dependencies = InMemoryOperationalDependencyRepository()
    private val captureUseCase = CaptureExpressionUseCase(captures)
    private val admit = AdmitCaptureToJournalUseCase(captures, journal)
    private val classify = ClassifyJournalEntryUseCase(journal, classifications)
    private val create = CreateOperationalRecordUseCase(journal, classifications, operations)
    private val assignTemporal = AssignOperationalTemporalUseCase(operations, temporals)
    private val evaluateDue = EvaluateDueStatusUseCase(temporals)
    private val createDep = CreateOperationalDependencyUseCase(operations, dependencies)

    private fun seed(captureId: String, journalId: String, opId: String, text: String) {
        val capAt = CaptureInstant(Instant.parse("2026-09-07T08:00:00Z"))
        assertTrue(captureUseCase.execute(captureId, text, capAt) is DomainResult.Success)
        assertTrue(
            admit.execute(journalId, captureId, JournalAdmissionInstant(Instant.parse("2026-09-07T08:05:00Z")))
                is DomainResult.Success,
        )
        assertTrue(
            classify.execute(
                "cls-$journalId", journalId, ClassificationType.FOLLOW_UP,
                ClassificationBasis.MANUAL, ClassificationInstant(Instant.parse("2026-09-07T08:10:00Z")),
            ) is DomainResult.Success,
        )
        assertTrue(
            create.execute(opId, journalId, OperationalCreationInstant(Instant.parse("2026-09-07T08:30:00Z")))
                is DomainResult.Success,
        )
    }

    @Test
    fun ph8t05UnresolvedRemainsUnresolved() {
        seed("cap-1", "jrn-1", "op-1", "Call Ama Tuesday at 8 AM")
        val result = assignTemporal.execute(
            temporalId = "tmp-1",
            operationalRecordIdValue = "op-1",
            resolution = TemporalResolution.UNRESOLVED,
            assignedAt = TemporalAssignmentInstant(Instant.parse("2026-09-07T09:40:00Z")),
            referenceExpression = "Tuesday at 8 AM",
            basis = TemporalCreationBasis.MANUAL,
        )
        assertTrue(result is DomainResult.Success)
        val value = (result as DomainResult.Success).value
        assertEquals(TemporalResolution.UNRESOLVED, value.resolution)
        assertNull(value.dueInstant)
        assertEquals("Tuesday at 8 AM", value.referenceExpression)
        val opId = (OperationalRecordId.of("op-1") as DomainResult.Success).value
        assertTrue(evaluateDue.execute(opId, EvaluationInstant(Instant.parse("2026-09-08T08:00:00Z"))) is DomainResult.Failure)
    }

    @Test
    fun ph8t10To12DueEvaluation() {
        seed("cap-1", "jrn-1", "op-1", "Pay school fees")
        val due = DueInstant(Instant.parse("2026-09-10T08:00:00Z"))
        assertTrue(
            assignTemporal.execute(
                "tmp-1", "op-1", TemporalResolution.RESOLVED,
                TemporalAssignmentInstant(Instant.parse("2026-09-07T09:40:00Z")),
                dueInstant = due,
            ) is DomainResult.Success,
        )
        val opId = (OperationalRecordId.of("op-1") as DomainResult.Success).value
        assertEquals(
            DueStatus.BEFORE_DUE,
            (evaluateDue.execute(opId, EvaluationInstant(Instant.parse("2026-09-09T08:00:00Z"))) as DomainResult.Success).value,
        )
        assertEquals(
            DueStatus.AT_DUE,
            (evaluateDue.execute(opId, EvaluationInstant(Instant.parse("2026-09-10T08:00:00Z"))) as DomainResult.Success).value,
        )
        assertEquals(
            DueStatus.PAST_DUE,
            (evaluateDue.execute(opId, EvaluationInstant(Instant.parse("2026-09-11T08:00:00Z"))) as DomainResult.Success).value,
        )
        assertEquals("cls-jrn-1", operations.findById(opId)?.classificationId?.value)
    }

    @Test
    fun ph8d01And12DependencyAndCycle() {
        seed("cap-1", "jrn-1", "op-a", "Call Ama")
        seed("cap-2", "jrn-2", "op-b", "Buy cement")
        val createdAt = OperationalDependencyCreationInstant(Instant.parse("2026-09-07T09:45:00Z"))
        assertTrue(createDep.execute("dep-1", "op-a", "op-b", createdAt) is DomainResult.Success)
        val reverse = createDep.execute("dep-2", "op-b", "op-a", createdAt)
        assertTrue(reverse is DomainResult.Failure)
        assertEquals("cls-jrn-1", operations.findById((OperationalRecordId.of("op-a") as DomainResult.Success).value)?.classificationId?.value)
    }
}
