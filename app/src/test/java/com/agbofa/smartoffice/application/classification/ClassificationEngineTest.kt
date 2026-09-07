package com.agbofa.smartoffice.application.classification

import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.application.journal.AdmitCaptureToJournalUseCase
import com.agbofa.smartoffice.data.capture.InMemoryCaptureRepository
import com.agbofa.smartoffice.data.classification.InMemoryClassificationRepository
import com.agbofa.smartoffice.data.journal.InMemoryJournalRepository
import com.agbofa.smartoffice.domain.classification.ClassificationBasis
import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.domain.foundation.time.ClassificationInstant
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class ClassificationEngineTest {
    private val captures = InMemoryCaptureRepository()
    private val journal = InMemoryJournalRepository()
    private val classifications = InMemoryClassificationRepository()
    private val captureUseCase = CaptureExpressionUseCase(captures)
    private val admit = AdmitCaptureToJournalUseCase(captures, journal)
    private val classify = ClassifyJournalEntryUseCase(journal, classifications)
    private val getActive = GetActiveClassificationUseCase(classifications)
    private val getUnclassified = GetUnclassifiedJournalEntriesUseCase(journal, classifications)

    private val captureAt = CaptureInstant(Instant.parse("2026-09-07T08:00:00Z"))
    private val admittedAt = JournalAdmissionInstant(Instant.parse("2026-09-07T08:05:00Z"))
    private val classifiedAt = ClassificationInstant(Instant.parse("2026-09-07T08:10:00Z"))

    private fun admitOne(captureId: String, journalId: String, text: String) {
        assertTrue(captureUseCase.execute(captureId, text, captureAt) is DomainResult.Success)
        assertTrue(admit.execute(journalId, captureId, admittedAt) is DomainResult.Success)
    }

    @Test
    fun ct09MissingJournalEntryRejected() {
        val result = classify.execute(
            classificationId = "cls-1",
            journalEntryIdValue = "missing",
            type = ClassificationType.NOTE,
            basis = ClassificationBasis.MANUAL,
            classifiedAt = classifiedAt,
        )
        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).error is DomainError.InvalidState)
    }

    @Test
    fun ct10ClassificationPersisted() {
        admitOne("cap-1", "jrn-1", "Koho said I should call him Tuesday at 8 AM")
        val result = classify.execute(
            "cls-1", "jrn-1", ClassificationType.FOLLOW_UP, ClassificationBasis.MANUAL, classifiedAt,
        )
        assertTrue(result is DomainResult.Success)
        val stored = classifications.findById((result as DomainResult.Success).value.id)
        assertEquals(ClassificationType.FOLLOW_UP, stored?.type)
    }

    @Test
    fun ct11ActiveClassificationRetrieved() {
        admitOne("cap-1", "jrn-1", "Meeting with Kwame about the project")
        classify.execute("cls-1", "jrn-1", ClassificationType.EVENT, ClassificationBasis.MANUAL, classifiedAt)
        val journalId = (com.agbofa.smartoffice.domain.journal.JournalEntryId.of("jrn-1") as DomainResult.Success).value
        assertEquals(ClassificationType.EVENT, getActive.execute(journalId)?.type)
    }

    @Test
    fun ct12UnclassifiedEntriesRetrieved() {
        admitOne("cap-1", "jrn-1", "Buy cement tomorrow")
        admitOne("cap-2", "jrn-2", "Meeting with Kwame about the project")
        classify.execute("cls-1", "jrn-2", ClassificationType.EVENT, ClassificationBasis.MANUAL, classifiedAt)
        val unclassified = getUnclassified.execute()
        assertEquals(listOf("jrn-1"), unclassified.map { it.id.value })
    }

    @Test
    fun ct13AlreadyClassifiedRevisesInsteadOfDuplicatingActive() {
        admitOne("cap-1", "jrn-1", "Ama gave me GH₵500 to pay school fees")
        classify.execute("cls-1", "jrn-1", ClassificationType.NOTE, ClassificationBasis.MANUAL, classifiedAt)
        classify.execute(
            "cls-2",
            "jrn-1",
            ClassificationType.FINANCIAL_OBLIGATION,
            ClassificationBasis.MANUAL,
            ClassificationInstant(Instant.parse("2026-09-07T08:20:00Z")),
        )
        val journalId = (com.agbofa.smartoffice.domain.journal.JournalEntryId.of("jrn-1") as DomainResult.Success).value
        val active = getActive.execute(journalId)
        assertEquals(ClassificationType.FINANCIAL_OBLIGATION, active?.type)
        assertEquals(2, active?.revision)
        assertEquals(2, classifications.listByJournalEntryId(journalId).size)
    }

    @Test
    fun ct14ReclassificationKeepsPreviousRevision() {
        admitOne("cap-1", "jrn-1", "Ama gave me GH₵500 to pay school fees")
        classify.execute("cls-1", "jrn-1", ClassificationType.NOTE, ClassificationBasis.MANUAL, classifiedAt)
        classify.execute(
            "cls-2",
            "jrn-1",
            ClassificationType.FINANCIAL_OBLIGATION,
            ClassificationBasis.MANUAL,
            ClassificationInstant(Instant.parse("2026-09-07T08:20:00Z")),
        )
        val journalId = (com.agbofa.smartoffice.domain.journal.JournalEntryId.of("jrn-1") as DomainResult.Success).value
        val history = classifications.listByJournalEntryId(journalId)
        assertEquals(ClassificationType.NOTE, history[0].type)
        assertEquals(ClassificationType.FINANCIAL_OBLIGATION, history[1].type)
        assertEquals(history[0].id, history[1].supersedesId)
    }

    @Test
    fun ct15ClassificationDoesNotMutateCapture() {
        val raw = "  Ama gave me GH₵500 to pay school fees  "
        admitOne("cap-1", "jrn-1", raw)
        classify.execute("cls-1", "jrn-1", ClassificationType.FINANCIAL_OBLIGATION, ClassificationBasis.MANUAL, classifiedAt)
        val stored = captures.findById(
            (com.agbofa.smartoffice.domain.capture.CaptureId.of("cap-1") as DomainResult.Success).value,
        )
        assertEquals(raw, stored?.originalExpression?.value)
        assertEquals(captureAt, stored?.capturedAt)
    }

    @Test
    fun ct16ClassificationDoesNotMutateJournal() {
        admitOne("cap-1", "jrn-1", "Buy cement tomorrow")
        val before = journal.findById(
            (com.agbofa.smartoffice.domain.journal.JournalEntryId.of("jrn-1") as DomainResult.Success).value,
        )
        classify.execute("cls-1", "jrn-1", ClassificationType.ACTION, ClassificationBasis.MANUAL, classifiedAt)
        val after = journal.findById(before!!.id)
        assertEquals(before, after)
    }

    @Test
    fun journalAdmissionStillWorksWithoutClassification() {
        admitOne("cap-1", "jrn-1", "Buy cement tomorrow")
        assertEquals(1, getUnclassified.execute().size)
        assertNull(
            getActive.execute(
                (com.agbofa.smartoffice.domain.journal.JournalEntryId.of("jrn-1") as DomainResult.Success).value,
            ),
        )
    }
}
