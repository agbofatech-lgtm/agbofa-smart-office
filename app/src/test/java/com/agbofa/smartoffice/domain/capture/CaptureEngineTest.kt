package com.agbofa.smartoffice.domain.capture

import com.agbofa.smartoffice.application.capture.CaptureExpressionCommand
import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.data.capture.InMemoryCaptureRepository
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class CaptureEngineTest {

    private val t0 = CaptureInstant(Instant.parse("2026-09-07T08:00:00Z"))

    @Test
    fun ct01_validCaptureSucceeds() {
        val result = Capture.from("c-1", "Buy cement tomorrow", t0)
        assertTrue(result is DomainResult.Success)
        val capture = (result as DomainResult.Success).value
        assertEquals("Buy cement tomorrow", capture.originalExpression.value)
        assertEquals(t0, capture.capturedAt)
        assertEquals(CaptureSource.TEXT, capture.source)
    }

    @Test
    fun ct02_emptyExpressionRejected() {
        val result = Capture.from("c-1", "", t0)
        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).error is DomainError.ValidationError)
    }

    @Test
    fun ct03_whitespaceOnlyRejected() {
        val result = Capture.from("c-1", "     ", t0)
        assertTrue(result is DomainResult.Failure)
    }

    @Test
    fun ct04_originalExpressionPreservedExactly() {
        val raw = "  Ama gave me GH₵500 to pay school fees  "
        val capture = (Capture.from("c-1", raw, t0) as DomainResult.Success).value
        assertEquals(raw, capture.originalExpression.value)
    }

    @Test
    fun ct05_explicitTimeIsStored() {
        val other = CaptureInstant(Instant.parse("2026-01-01T00:00:00Z"))
        val capture = (Capture.from("c-1", "Meeting with Kwame", other) as DomainResult.Success).value
        assertEquals(other, capture.capturedAt)
    }

    @Test
    fun ct06_explicitIdentityIsStored() {
        val capture = (Capture.from("supplied-id", "Call Koho", t0) as DomainResult.Success).value
        assertEquals("supplied-id", capture.id.value)
    }

    @Test
    fun ct07_noPublicMutationPath() {
        val methods = Capture::class.java.methods.map { it.name }
        assertTrue(methods.none { it.startsWith("setOriginal") || it == "setOriginalExpression" })
    }

    @Test
    fun ct08_sameInputsAreDeterministic() {
        val first = Capture.from("c-1", "Buy cement tomorrow", t0)
        val second = Capture.from("c-1", "Buy cement tomorrow", t0)
        assertEquals(first, second)
    }

    @Test
    fun ct09_failureIsTyped() {
        val error = (Capture.from("c-1", "", t0) as DomainResult.Failure).error
        assertTrue(error is DomainError.ValidationError)
        assertEquals("originalExpression", (error as DomainError.ValidationError).path)
    }

    @Test
    fun ct10_noInterpretationFields() {
        val fields = Capture::class.java.declaredFields.map { it.name }
        val forbidden = listOf(
            "amount", "currency", "task", "category", "priority",
            "dueDate", "schedule", "status", "classification", "confidence", "aiResult",
        )
        forbidden.forEach { name ->
            assertTrue("$name must not exist on Capture", fields.none { it.equals(name, ignoreCase = true) })
        }
    }

    @Test
    fun ct11_persistenceRoundTripPreservesEvidence() {
        val repository = InMemoryCaptureRepository()
        val useCase = CaptureExpressionUseCase(repository)
        val raw = "I paid GH₵200 for transport"
        val saved = (useCase.execute(CaptureExpressionCommand("c-9", raw, t0)) as DomainResult.Success).value
        val loaded = repository.findById(saved.id)
        assertNotNull(loaded)
        assertEquals(raw, loaded!!.originalExpression.value)
        assertEquals(t0, loaded.capturedAt)
        assertEquals(saved.id, loaded.id)
    }

    @Test
    fun ct12_unknownIdIsAbsent() {
        val repository = InMemoryCaptureRepository()
        val missing = (CaptureId.of("missing") as DomainResult.Success).value
        assertNull(repository.findById(missing))
    }
}
