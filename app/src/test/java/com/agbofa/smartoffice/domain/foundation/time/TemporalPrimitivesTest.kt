package com.agbofa.smartoffice.domain.foundation.time

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class TemporalPrimitivesTest {
    private val t0 = Instant.parse("2026-09-07T08:00:00Z")
    private val t1 = Instant.parse("2026-09-07T09:00:00Z")

    @Test
    fun evaluationInstantEqualityIsByValue() {
        assertEquals(EvaluationInstant(t0), EvaluationInstant(t0))
        assertNotEquals(EvaluationInstant(t0), EvaluationInstant(t1))
    }

    @Test
    fun roleWrappersPreserveTheSameInstantWithoutCollapsingTypes() {
        val capture = CaptureInstant(t0)
        val due = DueInstant(t0)
        assertEquals(capture.value, due.value)
    }

    @Test
    fun civilTimeToInstantIsDeterministic() {
        val civil = CivilTime(
            dateTime = LocalDateTime.of(2026, 9, 8, 8, 0),
            zone = ZoneOffset.UTC,
        )
        assertEquals(Instant.parse("2026-09-08T08:00:00Z"), civil.toInstant())
        assertEquals(civil.toInstant(), civil.toInstant())
    }

    @Test
    fun overdueAssessmentIsDeterministicGivenEvaluationInstant() {
        val due = DueInstant(t0)
        val later = EvaluationInstant(t1)
        val atDue = EvaluationInstant(t0)
        assertEquals(true, later.hasReached(due))
        assertEquals(true, later.hasReached(due))
        assertEquals(true, atDue.hasReached(due))
        assertEquals(
            false,
            EvaluationInstant(Instant.parse("2026-09-07T07:59:59Z")).hasReached(due),
        )
    }
}
