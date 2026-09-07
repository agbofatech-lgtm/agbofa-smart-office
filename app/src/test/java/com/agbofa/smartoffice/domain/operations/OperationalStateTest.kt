package com.agbofa.smartoffice.domain.operations

import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.OperationalTransitionInstant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class OperationalStateTest {
    private val recordId = (OperationalRecordId.of("op-1") as DomainResult.Success).value
    private val at = OperationalTransitionInstant(Instant.parse("2026-09-07T09:00:00Z"))

    private fun transition(
        id: String,
        from: OperationalState,
        to: OperationalState,
        instant: OperationalTransitionInstant = at,
    ): DomainResult<OperationalStateTransition> {
        val transitionId = (OperationalStateTransitionId.of(id) as DomainResult.Success).value
        return OperationalStateTransition.of(
            id = transitionId,
            operationalRecordId = recordId,
            fromState = from,
            toState = to,
            transitionedAt = instant,
            basis = OperationalTransitionBasis.MANUAL,
        )
    }

    @Test
    fun st01EmptyHistoryIsOpen() {
        assertEquals(OperationalState.OPEN, OperationalStateProjection.current(emptyList()))
    }

    @Test
    fun st02OpenToActiveAllowed() {
        assertTrue(OperationalStatePolicy.allowed(OperationalState.OPEN, OperationalState.ACTIVE))
        assertTrue(transition("st-1", OperationalState.OPEN, OperationalState.ACTIVE) is DomainResult.Success)
    }

    @Test
    fun st03OpenToCancelledAllowed() {
        assertTrue(transition("st-1", OperationalState.OPEN, OperationalState.CANCELLED) is DomainResult.Success)
    }

    @Test
    fun st04ActiveToCompletedAllowed() {
        assertTrue(transition("st-1", OperationalState.ACTIVE, OperationalState.COMPLETED) is DomainResult.Success)
    }

    @Test
    fun st05ActiveToCancelledAllowed() {
        assertTrue(transition("st-1", OperationalState.ACTIVE, OperationalState.CANCELLED) is DomainResult.Success)
    }

    @Test
    fun st06OpenToCompletedRejected() {
        assertTrue(transition("st-1", OperationalState.OPEN, OperationalState.COMPLETED) is DomainResult.Failure)
    }

    @Test
    fun st07CompletedToActiveRejected() {
        assertFalse(OperationalStatePolicy.allowed(OperationalState.COMPLETED, OperationalState.ACTIVE))
        assertTrue(transition("st-1", OperationalState.COMPLETED, OperationalState.ACTIVE) is DomainResult.Failure)
    }

    @Test
    fun st08CompletedToCancelledRejected() {
        assertTrue(transition("st-1", OperationalState.COMPLETED, OperationalState.CANCELLED) is DomainResult.Failure)
    }

    @Test
    fun st09CancelledToActiveRejected() {
        assertTrue(transition("st-1", OperationalState.CANCELLED, OperationalState.ACTIVE) is DomainResult.Failure)
    }

    @Test
    fun st10CancelledToCompletedRejected() {
        assertTrue(transition("st-1", OperationalState.CANCELLED, OperationalState.COMPLETED) is DomainResult.Failure)
    }

    @Test
    fun st11SameStateRejected() {
        assertFalse(OperationalStatePolicy.allowed(OperationalState.OPEN, OperationalState.OPEN))
        assertTrue(transition("st-1", OperationalState.OPEN, OperationalState.OPEN) is DomainResult.Failure)
    }

    @Test
    fun st19TimestampTieUsesId() {
        val same = OperationalTransitionInstant(Instant.parse("2026-09-07T09:00:00Z"))
        val first = (transition("st-a", OperationalState.OPEN, OperationalState.ACTIVE, same) as DomainResult.Success).value
        val second = (transition("st-b", OperationalState.ACTIVE, OperationalState.COMPLETED, same) as DomainResult.Success).value
        assertEquals(OperationalState.COMPLETED, OperationalStateProjection.current(listOf(second, first)))
        assertEquals(listOf(first, second), OperationalStateProjection.ordered(listOf(second, first)))
    }
}
