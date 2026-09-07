package com.agbofa.smartoffice.domain.journal

import com.agbofa.smartoffice.domain.capture.CaptureId
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class JournalEntryTest {
    private val admittedAt = JournalAdmissionInstant(Instant.parse("2026-09-07T08:05:00Z"))
    private val captureId = (CaptureId.of("cap-1") as DomainResult.Success).value

    @Test
    fun jt06ExplicitIdentityIsStored() {
        val entry = (JournalEntry.from("jrn-1", captureId, admittedAt) as DomainResult.Success).value
        assertEquals("jrn-1", entry.id.value)
        assertEquals("cap-1", entry.captureId.value)
    }

    @Test
    fun jt05ExplicitAdmissionTimeIsStored() {
        val entry = (JournalEntry.from("jrn-1", captureId, admittedAt) as DomainResult.Success).value
        assertEquals(Instant.parse("2026-09-07T08:05:00Z"), entry.admittedAt.value)
    }

    @Test
    fun blankJournalIdIsRejected() {
        val result = JournalEntry.from("   ", captureId, admittedAt)
        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).error is DomainError.ValidationError)
    }

    @Test
    fun identicalInputsYieldIdenticalEntry() {
        val first = JournalEntry.from("jrn-1", captureId, admittedAt)
        val second = JournalEntry.from("jrn-1", captureId, admittedAt)
        assertEquals(first, second)
    }

    @Test
    fun journalEntryDoesNotOwnExpressionOrInterpretation() {
        val names = JournalEntry::class.java.declaredFields.map { it.name }
        val forbidden = listOf(
            "originalExpression", "expression", "amount", "currency",
            "category", "priority", "dueDate", "schedule", "taskType",
            "classification", "confidence", "intent",
        )
        forbidden.forEach { token ->
            assertTrue(
                "unexpected $token in $names",
                names.none { it.equals(token, ignoreCase = true) },
            )
        }
    }

    @Test
    fun journalEntryReferencesCaptureByIdOnly() {
        val entry = (JournalEntry.from("jrn-1", captureId, admittedAt) as DomainResult.Success).value
        assertEquals(captureId, entry.captureId)
    }
}
