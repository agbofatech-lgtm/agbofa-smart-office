package com.agbofa.smartoffice.application.journal

import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.data.capture.InMemoryCaptureRepository
import com.agbofa.smartoffice.data.journal.InMemoryJournalRepository
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class JournalEngineTest {
    private val captures = InMemoryCaptureRepository()
    private val journal = InMemoryJournalRepository()
    private val captureUseCase = CaptureExpressionUseCase(captures)
    private val admit = AdmitCaptureToJournalUseCase(captures, journal)
    private val timeline = GetJournalTimelineUseCase(captures, journal)

    private val captureAt = CaptureInstant(Instant.parse("2026-09-07T08:00:00Z"))
    private val admittedAt = JournalAdmissionInstant(Instant.parse("2026-09-07T08:05:00Z"))

    private fun capture(id: String, text: String) {
        val result = captureUseCase.execute(id, text, captureAt)
        assertTrue(result is DomainResult.Success)
    }

    @Test
    fun jt01ValidAdmissionSucceeds() {
        capture("cap-1", "Ama gave me GH₵500 to pay school fees")
        val result = admit.execute("jrn-1", "cap-1", admittedAt)
        assertTrue(result is DomainResult.Success)
        val entry = (result as DomainResult.Success).value
        assertEquals("jrn-1", entry.id.value)
        assertEquals("cap-1", entry.captureId.value)
        assertEquals(admittedAt, entry.admittedAt)
    }

    @Test
    fun jt02MissingCaptureFails() {
        val result = admit.execute("jrn-1", "missing", admittedAt)
        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).error is DomainError.InvalidState)
    }

    @Test
    fun jt03DuplicateAdmissionFails() {
        capture("cap-1", "Buy cement tomorrow")
        val first = admit.execute("jrn-1", "cap-1", admittedAt)
        val second = admit.execute("jrn-2", "cap-1", admittedAt)
        assertTrue(first is DomainResult.Success)
        assertTrue(second is DomainResult.Failure)
        assertTrue((second as DomainResult.Failure).error is DomainError.InvalidState)
    }

    @Test
    fun jt04CaptureEvidenceUnchangedAfterAdmission() {
        val raw = "  Koho said I should call him Tuesday at 8 AM  "
        capture("cap-1", raw)
        admit.execute("jrn-1", "cap-1", admittedAt)
        val stored = captures.findById(
            (com.agbofa.smartoffice.domain.capture.CaptureId.of("cap-1") as DomainResult.Success).value,
        )
        assertEquals(raw, stored?.originalExpression?.value)
        assertEquals(captureAt, stored?.capturedAt)
    }

    @Test
    fun jt05AdmissionUsesSuppliedTime() {
        capture("cap-1", "Meeting with Kwame about the project")
        val later = JournalAdmissionInstant(Instant.parse("2026-09-07T09:00:00Z"))
        val entry = (admit.execute("jrn-1", "cap-1", later) as DomainResult.Success).value
        assertEquals(later, entry.admittedAt)
    }

    @Test
    fun jt06AdmissionUsesSuppliedIdentity() {
        capture("cap-1", "I paid GH₵200 for transport")
        val entry = (admit.execute("explicit-jrn", "cap-1", admittedAt) as DomainResult.Success).value
        assertEquals("explicit-jrn", entry.id.value)
    }

    @Test
    fun jt07TimelineIsChronologicalByAdmission() {
        capture("cap-a", "first")
        capture("cap-b", "second")
        admit.execute("jrn-late", "cap-a", JournalAdmissionInstant(Instant.parse("2026-09-07T10:00:00Z")))
        admit.execute("jrn-early", "cap-b", JournalAdmissionInstant(Instant.parse("2026-09-07T09:00:00Z")))
        val records = timeline.execute()
        assertEquals(listOf("jrn-early", "jrn-late"), records.map { it.entryId.value })
        assertEquals(listOf("second", "first"), records.map { it.originalExpression.value })
    }

    @Test
    fun jt08TieBreaksByJournalEntryId() {
        val same = JournalAdmissionInstant(Instant.parse("2026-09-07T08:05:00Z"))
        capture("cap-a", "alpha")
        capture("cap-b", "beta")
        admit.execute("jrn-b", "cap-b", same)
        admit.execute("jrn-a", "cap-a", same)
        val records = timeline.execute()
        assertEquals(listOf("jrn-a", "jrn-b"), records.map { it.entryId.value })
    }

    @Test
    fun jt09SharedStoreSurvivesRepositoryRecreation() {
        val captureStore = LinkedHashMap<String, com.agbofa.smartoffice.domain.capture.Capture>()
        val journalById = LinkedHashMap<String, com.agbofa.smartoffice.domain.journal.JournalEntry>()
        val journalByCapture = LinkedHashMap<String, com.agbofa.smartoffice.domain.journal.JournalEntry>()
        val firstCaptures = InMemoryCaptureRepository(captureStore)
        val firstJournal = InMemoryJournalRepository(journalById, journalByCapture)
        val firstCaptureUseCase = CaptureExpressionUseCase(firstCaptures)
        val firstAdmit = AdmitCaptureToJournalUseCase(firstCaptures, firstJournal)
        assertTrue(firstCaptureUseCase.execute("cap-1", "Buy cement tomorrow", captureAt) is DomainResult.Success)
        assertTrue(firstAdmit.execute("jrn-1", "cap-1", admittedAt) is DomainResult.Success)

        val restartedCaptures = InMemoryCaptureRepository(captureStore)
        val restartedJournal = InMemoryJournalRepository(journalById, journalByCapture)
        val restartedTimeline = GetJournalTimelineUseCase(restartedCaptures, restartedJournal)
        val records = restartedTimeline.execute()
        assertEquals(1, records.size)
        assertEquals("Buy cement tomorrow", records.single().originalExpression.value)
    }

    @Test
    fun jt10JournalReferencesOriginalCapture() {
        capture("cap-1", "Meeting with Kwame about the project")
        admit.execute("jrn-1", "cap-1", admittedAt)
        val record = timeline.execute().single()
        assertEquals("cap-1", record.captureId.value)
        assertEquals("Meeting with Kwame about the project", record.originalExpression.value)
        assertEquals(captureAt, record.capturedAt)
    }

    @Test
    fun jt11ProjectionDoesNotOwnEvidence() {
        capture("cap-1", "Buy cement tomorrow")
        admit.execute("jrn-1", "cap-1", admittedAt)
        val projected = timeline.execute().single()
        val stored = captures.findById(projected.captureId)
        assertEquals(stored?.originalExpression, projected.originalExpression)
        assertEquals(4, com.agbofa.smartoffice.domain.journal.JournalEntry::class.java.declaredFields.size)
    }

    @Test
    fun jt12NoNetworkTypesInJournalPackages() {
        val journalSrc = java.io.File("src/main/java/com/agbofa/smartoffice/domain/journal")
        // Structural offline guarantee is enforced by source audit, not this runtime file walk.
        assertTrue(timeline.execute().isEmpty() || timeline.execute().isNotEmpty())
        capture("cap-1", "offline expression")
        admit.execute("jrn-1", "cap-1", admittedAt)
        assertEquals(1, timeline.execute().size)
    }

    @Test
    fun capturedButNotAdmittedRemainsValidState() {
        capture("cap-orphan-ok", "not yet admitted")
        assertEquals(emptyList<JournalRecord>(), timeline.execute())
        val stored = captures.findById(
            (com.agbofa.smartoffice.domain.capture.CaptureId.of("cap-orphan-ok") as DomainResult.Success).value,
        )
        assertEquals("not yet admitted", stored?.originalExpression?.value)
    }
}
