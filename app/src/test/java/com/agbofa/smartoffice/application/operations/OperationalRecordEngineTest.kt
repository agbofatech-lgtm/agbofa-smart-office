package com.agbofa.smartoffice.application.operations

import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.application.classification.ClassifyJournalEntryUseCase
import com.agbofa.smartoffice.data.capture.InMemoryCaptureRepository
import com.agbofa.smartoffice.data.classification.InMemoryClassificationRepository
import com.agbofa.smartoffice.data.journal.InMemoryJournalRepository
import com.agbofa.smartoffice.data.operations.InMemoryOperationalRecordRepository
import com.agbofa.smartoffice.application.journal.AdmitCaptureToJournalUseCase
import com.agbofa.smartoffice.domain.classification.ClassificationBasis
import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.domain.foundation.time.ClassificationInstant
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalCreationInstant
import com.agbofa.smartoffice.domain.journal.JournalEntryId
import com.agbofa.smartoffice.domain.operations.OperationalRecordType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class OperationalRecordEngineTest {
    private val captures = InMemoryCaptureRepository()
    private val journal = InMemoryJournalRepository()
    private val classifications = InMemoryClassificationRepository()
    private val operations = InMemoryOperationalRecordRepository()
    private val captureUseCase = CaptureExpressionUseCase(captures)
    private val admit = AdmitCaptureToJournalUseCase(captures, journal)
    private val classify = ClassifyJournalEntryUseCase(journal, classifications)
    private val create = CreateOperationalRecordUseCase(journal, classifications, operations)
    private val getForJournal = GetOperationalRecordForJournalEntryUseCase(operations)

    private val captureAt = CaptureInstant(Instant.parse("2026-09-07T08:00:00Z"))
    private val admittedAt = JournalAdmissionInstant(Instant.parse("2026-09-07T08:05:00Z"))
    private val classifiedAt = ClassificationInstant(Instant.parse("2026-09-07T08:10:00Z"))
    private val createdAt = OperationalCreationInstant(Instant.parse("2026-09-07T08:30:00Z"))

    private fun admitAndClassify(
        captureId: String = "cap-1",
        journalId: String = "jrn-1",
        text: String = "Koho said I should call him Tuesday at 8 AM",
        type: ClassificationType = ClassificationType.FOLLOW_UP,
        classificationId: String = "cls-1",
    ) {
        assertTrue(captureUseCase.execute(captureId, text, captureAt) is DomainResult.Success)
        assertTrue(admit.execute(journalId, captureId, admittedAt) is DomainResult.Success)
        assertTrue(
            classify.execute(
                classificationId,
                journalId,
                type,
                ClassificationBasis.MANUAL,
                classifiedAt,
            ) is DomainResult.Success,
        )
    }

    @Test
    fun orTest01ValidCreation() {
        admitAndClassify()
        val result = create.execute("op-1", "jrn-1", createdAt)
        assertTrue(result is DomainResult.Success)
        val record = (result as DomainResult.Success).value
        assertEquals("op-1", record.id.value)
        assertEquals("cls-1", record.classificationId.value)
        assertEquals(OperationalRecordType.FOLLOW_UP, record.type)
        assertEquals(createdAt, record.createdAt)
    }

    @Test
    fun orTest06MissingJournalRejected() {
        val result = create.execute("op-1", "missing", createdAt)
        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).error is DomainError.InvalidState)
    }

    @Test
    fun orTest07UnclassifiedRejected() {
        assertTrue(captureUseCase.execute("cap-1", "Buy cement tomorrow", captureAt) is DomainResult.Success)
        assertTrue(admit.execute("jrn-1", "cap-1", admittedAt) is DomainResult.Success)
        val result = create.execute("op-1", "jrn-1", createdAt)
        assertTrue(result is DomainResult.Failure)
        assertEquals("Journal entry is unclassified", (result as DomainResult.Failure).error.message)
    }

    @Test
    fun orTest09And10DuplicateRejected() {
        admitAndClassify()
        assertTrue(create.execute("op-1", "jrn-1", createdAt) is DomainResult.Success)
        val second = create.execute("op-2", "jrn-1", createdAt)
        assertTrue(second is DomainResult.Failure)
    }

    @Test
    fun orTest12RevisionDoesNotRewriteRecord() {
        admitAndClassify()
        val created = (create.execute("op-1", "jrn-1", createdAt) as DomainResult.Success).value
        assertTrue(
            classify.execute(
                "cls-2",
                "jrn-1",
                ClassificationType.ACTION,
                ClassificationBasis.MANUAL,
                ClassificationInstant(Instant.parse("2026-09-07T09:00:00Z")),
            ) is DomainResult.Success,
        )
        val journalId = (JournalEntryId.of("jrn-1") as DomainResult.Success).value
        val loaded = getForJournal.execute(journalId)
        assertEquals("cls-1", loaded?.classificationId?.value)
        assertEquals(OperationalRecordType.FOLLOW_UP, loaded?.type)
        assertNotEquals("cls-2", loaded?.classificationId?.value)
        assertEquals(created.classificationId, loaded?.classificationId)
    }

    @Test
    fun orTest13To15SourceDomainsUnchanged() {
        val raw = "  Ama gave me GH₵500 to pay school fees  "
        admitAndClassify("cap-1", "jrn-1", raw, ClassificationType.FINANCIAL_OBLIGATION, "cls-1")
        create.execute("op-1", "jrn-1", createdAt)
        val capture = captures.findById(
            (com.agbofa.smartoffice.domain.capture.CaptureId.of("cap-1") as DomainResult.Success).value,
        )
        val entry = journal.findById((JournalEntryId.of("jrn-1") as DomainResult.Success).value)
        val classification = classifications.findById(
            (com.agbofa.smartoffice.domain.classification.ClassificationId.of("cls-1") as DomainResult.Success).value,
        )
        assertEquals(raw, capture?.originalExpression?.value)
        assertEquals(admittedAt, entry?.admittedAt)
        assertEquals(ClassificationType.FINANCIAL_OBLIGATION, classification?.type)
    }

    @Test
    fun orTest16NoExpressionOnOperationalRecord() {
        admitAndClassify()
        val record = (create.execute("op-1", "jrn-1", createdAt) as DomainResult.Success).value
        val names = record.javaClass.declaredFields.map { it.name }
        assertTrue(names.none { it.contains("expression", ignoreCase = true) })
    }
}
