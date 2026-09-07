package com.agbofa.smartoffice.domain.foundation

import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.domain.foundation.time.CivilTime
import com.agbofa.smartoffice.domain.foundation.time.DueInstant
import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class TimeAndContextTest {

    private val t0 = Instant.parse("2026-09-07T08:00:00Z")
    private val t1 = Instant.parse("2026-09-07T09:00:00Z")

    @Test
    fun instantRolesAreNotInterchangeableTypes() {
        val capture = CaptureInstant(t0)
        val evaluation = EvaluationInstant(t0)
        assertEquals(capture.value, evaluation.value)
        assertEquals(CaptureInstant::class.java, capture::class.java)
        assertEquals(EvaluationInstant::class.java, evaluation::class.java)
        assertNotEquals(capture::class.java, evaluation::class.java)
    }

    @Test
    fun evaluationAgainstDueIsDeterministic() {
        val due = DueInstant(t1)
        val before = EvaluationInstant(t0)
        val after = EvaluationInstant(t1)
        assertFalse(before.hasReached(due))
        assertTrue(after.hasReached(due))
        assertEquals(before.hasReached(due), EvaluationInstant(t0).hasReached(due))
    }

    @Test
    fun identicalContextYieldsIdenticalDecision() {
        val context = EvaluationContext(EvaluationInstant(t1))
        val due = DueInstant(t1)
        val first = context.evaluationTime.hasReached(due)
        val second = context.evaluationTime.hasReached(due)
        assertTrue(first)
        assertEquals(first, second)
    }

    @Test
    fun civilTimePreservesZoneAndConvertsExplicitly() {
        val civil = CivilTime(
            dateTime = LocalDateTime.of(2026, 9, 8, 8, 0),
            zone = ZoneOffset.UTC,
        )
        assertEquals(Instant.parse("2026-09-08T08:00:00Z"), civil.toInstant())
    }
}
