package com.agbofa.smartoffice.domain.integrity

import com.agbofa.smartoffice.domain.capture.Capture
import com.agbofa.smartoffice.domain.capture.CaptureId
import com.agbofa.smartoffice.domain.capture.OriginalExpression
import com.agbofa.smartoffice.domain.classification.Classification
import com.agbofa.smartoffice.domain.classification.ClassificationBasis
import com.agbofa.smartoffice.domain.classification.ClassificationId
import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.domain.foundation.time.ClassificationInstant
import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalTransitionInstant
import com.agbofa.smartoffice.domain.journal.JournalEntry
import com.agbofa.smartoffice.domain.journal.JournalEntryId
import com.agbofa.smartoffice.domain.operations.OperationalCreationBasis
import com.agbofa.smartoffice.domain.operations.OperationalRecord
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalRecordType
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.operations.OperationalStateTransition
import com.agbofa.smartoffice.domain.operations.OperationalStateTransitionId
import com.agbofa.smartoffice.domain.operations.OperationalTransitionBasis
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class IntegrityEvaluatorTest {
    private val context = EvaluationContext(EvaluationInstant(Instant.parse("2026-09-07T12:00:00Z")))

    private fun capture(id: String = "cap-1"): Capture = (
        Capture.of(
            (CaptureId.of(id) as DomainResult.Success).value,
            (OriginalExpression.of("Call Tuesday") as DomainResult.Success).value,
            CaptureInstant(Instant.parse("2026-09-07T08:00:00Z")),
        ) as DomainResult.Success
    ).value

    private fun journal(id: String = "jrn-1", captureId: String = "cap-1"): JournalEntry = (
        JournalEntry.of(
            (JournalEntryId.of(id) as DomainResult.Success).value,
            (CaptureId.of(captureId) as DomainResult.Success).value,
            JournalAdmissionInstant(Instant.parse("2026-09-07T08:05:00Z")),
        ) as DomainResult.Success
    ).value

    private fun classification(
        id: String = "cls-1",
        journalId: String = "jrn-1",
    ): Classification = (
        Classification.of(
            id = (ClassificationId.of(id) as DomainResult.Success).value,
            journalEntryId = (JournalEntryId.of(journalId) as DomainResult.Success).value,
            type = ClassificationType.FOLLOW_UP,
            basis = ClassificationBasis.MANUAL,
            classifiedAt = ClassificationInstant(Instant.parse("2026-09-07T08:10:00Z")),
            revision = 1,
        ) as DomainResult.Success
    ).value

    private fun record(
        id: String = "op-1",
        journalId: String = "jrn-1",
        classificationId: String = "cls-1",
    ): OperationalRecord = (
        OperationalRecord.of(
            id = (OperationalRecordId.of(id) as DomainResult.Success).value,
            journalEntryId = (JournalEntryId.of(journalId) as DomainResult.Success).value,
            classificationId = (ClassificationId.of(classificationId) as DomainResult.Success).value,
            type = OperationalRecordType.FOLLOW_UP,
            createdAt = OperationalCreationInstant(Instant.parse("2026-09-07T08:30:00Z")),
            creationBasis = OperationalCreationBasis.MANUAL,
        ) as DomainResult.Success
    ).value

    private fun transition(
        id: String,
        recordId: String,
        from: OperationalState,
        to: OperationalState,
        at: String,
    ): OperationalStateTransition = (
        OperationalStateTransition.of(
            id = (OperationalStateTransitionId.of(id) as DomainResult.Success).value,
            operationalRecordId = (OperationalRecordId.of(recordId) as DomainResult.Success).value,
            fromState = from,
            toState = to,
            transitionedAt = OperationalTransitionInstant(Instant.parse(at)),
            basis = OperationalTransitionBasis.MANUAL,
        ) as DomainResult.Success
    ).value

    @Test
    fun healthyInputHasNoErrors() {
        val report = IntegrityEvaluator.evaluate(
            IntegrityEvaluationInput(
                captures = listOf(capture()),
                journalEntries = listOf(journal()),
                classifications = listOf(classification()),
                operationalRecords = listOf(record()),
            ),
            context,
        )
        assertEquals(IntegrityOutcome.HEALTHY, report.outcome)
        assertEquals(0, report.errorCount)
        assertTrue(report.findings.isEmpty())
    }

    @Test
    fun missingCaptureIsError() {
        val report = IntegrityEvaluator.evaluate(
            IntegrityEvaluationInput(journalEntries = listOf(journal())),
            context,
        )
        assertEquals(IntegrityOutcome.ERRORS_PRESENT, report.outcome)
        assertEquals(IntegrityCode.MISSING_CAPTURE_REFERENCE, report.findings.single().code)
    }

    @Test
    fun missingPinnedClassificationIsError() {
        val report = IntegrityEvaluator.evaluate(
            IntegrityEvaluationInput(
                captures = listOf(capture()),
                journalEntries = listOf(journal()),
                operationalRecords = listOf(record()),
            ),
            context,
        )
        assertTrue(report.findings.any { it.code == IntegrityCode.MISSING_CLASSIFICATION_REFERENCE })
    }

    @Test
    fun stateTransitionWithoutRecordIsError() {
        val report = IntegrityEvaluator.evaluate(
            IntegrityEvaluationInput(
                captures = listOf(capture()),
                journalEntries = listOf(journal()),
                classifications = listOf(classification()),
                operationalRecords = listOf(record()),
                stateTransitions = listOf(
                    transition("st-1", "op-missing", OperationalState.OPEN, OperationalState.ACTIVE, "2026-09-07T09:00:00Z"),
                ),
            ),
            context,
        )
        assertTrue(report.findings.any { it.code == IntegrityCode.MISSING_OPERATIONAL_RECORD_REFERENCE })
    }

    @Test
    fun insertionOrderDoesNotChangeReport() {
        val a = IntegrityEvaluationInput(
            captures = listOf(capture("cap-2"), capture("cap-1")),
            journalEntries = listOf(journal("jrn-2", "cap-2"), journal("jrn-1", "cap-1")),
        )
        val b = IntegrityEvaluationInput(
            captures = listOf(capture("cap-1"), capture("cap-2")),
            journalEntries = listOf(journal("jrn-1", "cap-1"), journal("jrn-2", "cap-2")),
        )
        val first = IntegrityEvaluator.evaluate(a, context)
        val second = IntegrityEvaluator.evaluate(b, context)
        assertEquals(first.outcome, second.outcome)
        assertEquals(first.findings.map { it.findingKey }, second.findings.map { it.findingKey })
    }
}
