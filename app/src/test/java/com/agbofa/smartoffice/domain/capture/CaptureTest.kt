package com.agbofa.smartoffice.domain.capture

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class CaptureTest {

    private val instant = CaptureInstant(Instant.parse("2026-09-07T08:00:00Z"))

    private fun id(raw: String = "cap-1"): CaptureId =
        (CaptureId.of(raw) as DomainResult.Success).value

    @Test
    fun validExpressionIsCaptured() {
        val expression = (OriginalExpression.of("Ama gave me GH₵500 to pay school fees") as DomainResult.Success).value
        val result = Capture.of(id(), expression, instant)
        assertTrue(result is DomainResult.Success)
        val capture = (result as DomainResult.Success).value
        assertEquals("Ama gave me GH₵500 to pay school fees", capture.originalExpression.value)
        assertEquals(instant, capture.capturedAt)
        assertEquals(CaptureSource.TEXT, capture.source)
    }

    @Test
    fun emptyExpressionIsRejected() {
        val result = OriginalExpression.of("")
        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).error is DomainError.ValidationError)
    }

    @Test
    fun whitespaceOnlyExpressionIsRejected() {
        val result = OriginalExpression.of("     ")
        assertTrue(result is DomainResult.Failure)
    }

    @Test
    fun leadingAndTrailingSpacesArePreserved() {
        val raw = "  Buy cement tomorrow  "
        val expression = (OriginalExpression.of(raw) as DomainResult.Success).value
        assertEquals(raw, expression.value)
        val capture = (Capture.of(id(), expression, instant) as DomainResult.Success).value
        assertEquals(raw, capture.originalExpression.value)
    }

    @Test
    fun suppliedInstantIsUsed() {
        val expression = (OriginalExpression.of("Meeting with Kwame about the project") as DomainResult.Success).value
        val capture = (Capture.of(id(), expression, instant) as DomainResult.Success).value
        assertEquals(Instant.parse("2026-09-07T08:00:00Z"), capture.capturedAt.value)
    }

    @Test
    fun suppliedIdentityIsUsed() {
        val expression = (OriginalExpression.of("I paid GH₵200 for transport") as DomainResult.Success).value
        val capture = (Capture.of(id("explicit-id"), expression, instant) as DomainResult.Success).value
        assertEquals("explicit-id", capture.id.value)
    }

    @Test
    fun identicalInputsYieldIdenticalCapture() {
        val expression = (OriginalExpression.of("Koho said I should call him Tuesday at 8 AM") as DomainResult.Success).value
        val first = Capture.of(id("same"), expression, instant)
        val second = Capture.of(id("same"), expression, instant)
        assertEquals(first, second)
    }

    @Test
    fun captureHasNoInterpretationFields() {
        val names = Capture::class.java.declaredFields.map { it.name }
        val leaked = listOf(
            "amount", "currency", "category", "priority", "dueDate",
            "schedule", "status", "taskType", "classification", "confidence", "aiResult",
        )
        assertEquals(emptyList<String>(), leaked.filter { needle ->
            names.any { it.equals(needle, ignoreCase = true) }
        })
    }
}
