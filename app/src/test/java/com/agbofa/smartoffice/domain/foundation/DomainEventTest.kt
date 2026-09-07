package com.agbofa.smartoffice.domain.foundation

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.event.DomainEvent
import com.agbofa.smartoffice.domain.foundation.identity.EventId
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.EventInstant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class DomainEventTest {

    private val fixed = Instant.parse("2026-01-15T08:00:00Z")

    private fun eventId(raw: String = "evt-1"): EventId =
        (EventId.of(raw) as DomainResult.Success).value

    @Test
    fun createsImmutableEventFromExplicitTime() {
        val result = DomainEvent.of(
            id = eventId(),
            occurredAt = EventInstant(fixed),
            type = "foundation.recorded",
            source = "phase-2-test",
        )
        assertTrue(result is DomainResult.Success)
        val event = (result as DomainResult.Success).value
        assertEquals("evt-1", event.id.value)
        assertEquals(fixed, event.occurredAt.value)
        assertEquals("foundation.recorded", event.type)
        assertEquals("phase-2-test", event.source)
    }

    @Test
    fun rejectsBlankType() {
        val result = DomainEvent.of(
            id = eventId(),
            occurredAt = EventInstant(fixed),
            type = "   ",
        )
        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).error is DomainError.ValidationError)
    }

    @Test
    fun blankSourceBecomesNull() {
        val event = (DomainEvent.of(
            id = eventId(),
            occurredAt = EventInstant(fixed),
            type = "foundation.recorded",
            source = "  ",
        ) as DomainResult.Success).value
        assertNull(event.source)
    }

    @Test
    fun identicalInputYieldsIdenticalEvent() {
        val first = DomainEvent.of(eventId("same"), EventInstant(fixed), "foundation.recorded")
        val second = DomainEvent.of(eventId("same"), EventInstant(fixed), "foundation.recorded")
        assertEquals(first, second)
    }
}
