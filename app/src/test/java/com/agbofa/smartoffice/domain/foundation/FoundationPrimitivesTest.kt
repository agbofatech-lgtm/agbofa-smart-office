package com.agbofa.smartoffice.domain.foundation

import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.event.DomainEvent
import com.agbofa.smartoffice.domain.foundation.identity.EntityId
import com.agbofa.smartoffice.domain.foundation.identity.EventId
import com.agbofa.smartoffice.domain.foundation.identity.OperationId
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.domain.foundation.time.CivilTime
import com.agbofa.smartoffice.domain.foundation.time.DueInstant
import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant
import com.agbofa.smartoffice.domain.foundation.time.EventInstant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class FoundationPrimitivesTest {

    private val t0 = Instant.parse("2026-09-07T08:00:00Z")
    private val t1 = Instant.parse("2026-09-07T09:00:00Z")

    @Test
    fun entityIdEqualityIsByValue() {
        val left = (EntityId.of("entity-1") as DomainResult.Success).value
        val right = (EntityId.of("entity-1") as DomainResult.Success).value
        assertEquals(left, right)
        assertEquals(left.hashCode(), right.hashCode())
    }

    @Test
    fun entityIdRejectsBlank() {
        val result = EntityId.of("   ")
        assertTrue(result is DomainResult.Failure)
        val error = (result as DomainResult.Failure).error
        assertTrue(error is DomainError.ValidationError)
    }

    @Test
    fun typedIdsAreNotInterchangeable() {
        val entity = (EntityId.of("same") as DomainResult.Success).value
        val event = (EventId.of("same") as DomainResult.Success).value
        val operation = (OperationId.of("same") as DomainResult.Success).value
        assertEquals(entity.value, event.value)
        assertEquals(entity.value, operation.value)
        assertNotEquals(entity::class, event::class)
        assertNotEquals(entity::class, operation::class)
    }

    @Test
    fun resultSuccessAndFailureAreInspectable() {
        val ok: DomainResult<Int> = DomainResult.Success(4)
        val bad: DomainResult<Int> = DomainResult.Failure(
            DomainError.InvariantViolation("broken"),
        )
        assertEquals(4, ok.getOrNull())
        assertEquals("broken", bad.errorOrNull()?.message)
        assertTrue(ok.isSuccess)
        assertTrue(bad.isFailure)
    }

    @Test
    fun domainEventRejectsBlankType() {
        val id = (EventId.of("evt-1") as DomainResult.Success).value
        val result = DomainEvent.of(id, EventInstant(t0), "  ")
        assertTrue(result is DomainResult.Failure)
    }

    @Test
    fun domainEventPreservesFields() {
        val id = (EventId.of("evt-1") as DomainResult.Success).value
        val event = (
            DomainEvent.of(id, EventInstant(t0), "foundation.seed", "test")
                as DomainResult.Success
            ).value
        assertEquals("foundation.seed", event.type)
        assertEquals(EventInstant(t0), event.occurredAt)
        assertEquals("test", event.source)
        assertEquals(event, event.copy())
    }

    @Test
    fun evaluationIsDeterministicForIdenticalContext() {
        val context = EvaluationContext(EvaluationInstant(t0))
        val first = classifyDue(DueInstant(t1), context)
        val second = classifyDue(DueInstant(t1), context)
        assertEquals(first, second)
        assertEquals("pending", first)
    }

    @Test
    fun evaluationChangesOnlyWhenContextChanges() {
        val due = DueInstant(t1)
        val before = classifyDue(due, EvaluationContext(EvaluationInstant(t0)))
        val after = classifyDue(
            due,
            EvaluationContext(EvaluationInstant(Instant.parse("2026-09-07T10:00:00Z"))),
        )
        assertEquals("pending", before)
        assertEquals("reached", after)
    }

    @Test
    fun captureAndDueInstantsAreDistinctTypes() {
        val capture = CaptureInstant(t0)
        val due = DueInstant(t0)
        assertEquals(capture.value, due.value)
        assertNotEquals(capture::class, due::class)
    }

    @Test
    fun civilTimeConvertsWithExplicitZone() {
        val civil = CivilTime(
            dateTime = LocalDateTime.parse("2026-09-08T08:00:00"),
            zone = ZoneOffset.UTC,
        )
        assertEquals(Instant.parse("2026-09-08T08:00:00Z"), civil.toInstant())
    }

    /**
     * Tiny deterministic helper used only to prove evaluation context
     * is an input. Not a scheduling engine.
     */
    private fun classifyDue(due: DueInstant, context: EvaluationContext): String {
        return if (context.evaluationTime.value < due.value) "pending" else "reached"
    }
}
