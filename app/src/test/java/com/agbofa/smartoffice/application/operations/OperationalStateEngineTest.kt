package com.agbofa.smartoffice.application.operations

import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.application.classification.ClassifyJournalEntryUseCase
import com.agbofa.smartoffice.application.journal.AdmitCaptureToJournalUseCase
import com.agbofa.smartoffice.data.capture.InMemoryCaptureRepository
import com.agbofa.smartoffice.data.classification.InMemoryClassificationRepository
import com.agbofa.smartoffice.data.journal.InMemoryJournalRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalRecordRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalStateRepository
import com.agbofa.smartoffice.domain.classification.ClassificationBasis
import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.domain.foundation.time.ClassificationInstant
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalTransitionInstant
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class OperationalStateEngineTest {
    private val captures = InMemoryCaptureRepository()
    private val journal = InMemoryJournalRepository()
    private val classifications = InMemoryClassificationRepository()
    private val operations = InMemoryOperationalRecordRepository()
    private val states = InMemoryOperationalStateRepository()
    private val captureUseCase = CaptureExpressionUseCase(captures)
    private val admit = AdmitCaptureToJournalUseCase(captures, journal)
    private val classify = ClassifyJournalEntryUseCase(journal, classifications)
    private val create = CreateOperationalRecordUseCase(journal, classifications, operations)
    private val transition = TransitionOperationalRecordStateUseCase(operations, states)
    private val getState = GetOperationalRecordStateUseCase(states)
    private val getHistory = GetOperationalStateHistoryUseCase(states)

    private val captureAt = CaptureInstant(Instant.parse("2026-09-07T08:00:00Z"))
    private val admittedAt = JournalAdmissionInstant(Instant.parse("2026-09-07T08:05:00Z"))
    private val classifiedAt = ClassificationInstant(Instant.parse("2026-09-07T08:10:00Z"))
    private val createdAt = OperationalCreationInstant(Instant.parse("2026-09-07T08:30:00Z"))
    private val t1 = OperationalTransitionInstant(Instant.parse("2026-09-07T09:00:00Z"))
    private val t2 = OperationalTransitionInstant(Instant.parse("2026-09-07T09:05:00Z"))

    private fun seedOperational(): OperationalRecordId {
        assertTrue(captureUseCase.execute("cap-1", "Koho said I should call him Tuesday at 8 AM", captureAt) is DomainResult.Success)
        assertTrue(admit.execute("jrn-1", "cap-1", admittedAt) is DomainResult.Success)
        assertTrue(classify.execute("cls-1", "jrn-1", ClassificationType.FOLLOW_UP, ClassificationBasis.MANUAL, classifiedAt) is DomainResult.Success)
        val created = create.execute("op-1", "jrn-1", createdAt)
        assertTrue(created is DomainResult.Success)
        return (created as DomainResult.Success).value.id
    }

    @Test
    fun st01NewRecordProjectsOpen() {
        val id = seedOperational()
        assertEquals(OperationalState.OPEN, getState.execute(id))
        assertTrue(getHistory.execute(id).isEmpty())
    }

    @Test
    fun st02OpenToActivePersisted() {
        val id = seedOperational()
        val result = transition.execute("st-1", id.value, OperationalState.ACTIVE, t1)
        assertTrue(result is DomainResult.Success)
        assertEquals(OperationalState.ACTIVE, getState.execute(id))
        assertEquals(OperationalState.OPEN, (result as DomainResult.Success).value.fromState)
    }

    @Test
    fun st06OpenToCompletedRejected() {
        val id = seedOperational()
        val result = transition.execute("st-1", id.value, OperationalState.COMPLETED, t1)
        assertTrue(result is DomainResult.Failure)
        assertEquals(OperationalState.OPEN, getState.execute(id))
    }

    @Test
    fun st12MissingRecordRejected() {
        val result = transition.execute("st-1", "missing", OperationalState.ACTIVE, t1)
        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).error is DomainError.InvalidState)
    }

    @Test
    fun st13DuplicateTransitionIdRejected() {
        val id = seedOperational()
        assertTrue(transition.execute("st-1", id.value, OperationalState.ACTIVE, t1) is DomainResult.Success)
        val second = transition.execute("st-1", id.value, OperationalState.COMPLETED, t2)
        assertTrue(second is DomainResult.Failure)
    }

    @Test
    fun st18CurrentStateDeterministicAfterTwoTransitions() {
        val id = seedOperational()
        transition.execute("st-1", id.value, OperationalState.ACTIVE, t1)
        transition.execute("st-2", id.value, OperationalState.COMPLETED, t2)
        assertEquals(OperationalState.COMPLETED, getState.execute(id))
        val history = getHistory.execute(id)
        assertEquals(listOf("st-1", "st-2"), history.map { it.id.value })
    }

    @Test
    fun st19TieBreakUsesTransitionId() {
        val id = seedOperational()
        val same = OperationalTransitionInstant(Instant.parse("2026-09-07T09:00:00Z"))
        transition.execute("st-b", id.value, OperationalState.ACTIVE, same)
        assertEquals(OperationalState.ACTIVE, getState.execute(id))
    }

    @Test
    fun st20ClassificationRevisionDoesNotAlterState() {
        val id = seedOperational()
        transition.execute("st-1", id.value, OperationalState.ACTIVE, t1)
        classify.execute(
            "cls-2",
            "jrn-1",
            ClassificationType.ACTION,
            ClassificationBasis.MANUAL,
            ClassificationInstant(Instant.parse("2026-09-07T10:00:00Z")),
        )
        assertEquals(OperationalState.ACTIVE, getState.execute(id))
        val record = operations.findById(id)
        assertEquals("cls-1", record?.classificationId?.value)
    }

    @Test
    fun st21CaptureUnchanged() {
        val raw = "  Koho said I should call him Tuesday at 8 AM  "
        assertTrue(captureUseCase.execute("cap-1", raw, captureAt) is DomainResult.Success)
        assertTrue(admit.execute("jrn-1", "cap-1", admittedAt) is DomainResult.Success)
        assertTrue(classify.execute("cls-1", "jrn-1", ClassificationType.FOLLOW_UP, ClassificationBasis.MANUAL, classifiedAt) is DomainResult.Success)
        create.execute("op-1", "jrn-1", createdAt)
        transition.execute("st-1", "op-1", OperationalState.ACTIVE, t1)
        val stored = captures.findById(
            (com.agbofa.smartoffice.domain.capture.CaptureId.of("cap-1") as DomainResult.Success).value,
        )
        assertEquals(raw, stored?.originalExpression?.value)
    }

    @Test
    fun st24OperationalProvenanceUnchanged() {
        val id = seedOperational()
        val before = operations.findById(id)
        transition.execute("st-1", id.value, OperationalState.ACTIVE, t1)
        assertEquals(before, operations.findById(id))
    }
}
