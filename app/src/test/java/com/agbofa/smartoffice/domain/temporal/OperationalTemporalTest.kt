package com.agbofa.smartoffice.domain.temporal

import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CivilTime
import com.agbofa.smartoffice.domain.foundation.time.DueInstant
import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant
import com.agbofa.smartoffice.domain.foundation.time.TemporalAssignmentInstant
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalTemporalId
import com.agbofa.smartoffice.domain.operations.OperationalTemporalProjection
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRecord
import com.agbofa.smartoffice.domain.operations.TemporalCreationBasis
import com.agbofa.smartoffice.domain.operations.TemporalResolution
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class OperationalTemporalTest {
    private val recordId = (OperationalRecordId.of("op-1") as DomainResult.Success).value
    private val assignedAt = TemporalAssignmentInstant(Instant.parse("2026-09-07T09:40:00Z"))
    private val due = DueInstant(Instant.parse("2026-09-10T08:00:00Z"))

    private fun resolved(
        rawId: String = "tmp-1",
        dueInstant: DueInstant = due,
        basis: TemporalCreationBasis = TemporalCreationBasis.MANUAL,
        ruleVersion: String? = null,
        expression: String? = "Tuesday at 8 AM",
        assigned: TemporalAssignmentInstant = assignedAt,
    ) = OperationalTemporalRecord.of(
        id = (OperationalTemporalId.of(rawId) as DomainResult.Success).value,
        operationalRecordId = recordId,
        resolution = TemporalResolution.RESOLVED,
        referenceExpression = expression,
        dueInstant = dueInstant,
        civilTime = CivilTime(LocalDateTime.parse("2026-09-10T08:00:00"), ZoneId.of("Africa/Accra")),
        assignedAt = assigned,
        basis = basis,
        ruleVersion = ruleVersion,
    )

    private fun unresolved(expression: String? = "Tuesday at 8 AM") = OperationalTemporalRecord.of(
        id = (OperationalTemporalId.of("tmp-u") as DomainResult.Success).value,
        operationalRecordId = recordId,
        resolution = TemporalResolution.UNRESOLVED,
        referenceExpression = expression,
        dueInstant = null,
        civilTime = null,
        assignedAt = assignedAt,
        basis = TemporalCreationBasis.MANUAL,
    )

    @Test
    fun ph8T01ValidExactAccepted() {
        assertTrue(resolved() is DomainResult.Success)
    }

    @Test
    fun ph8T02BlankIdRejected() {
        assertTrue(OperationalTemporalId.of("  ") is DomainResult.Failure)
    }

    @Test
    fun ph8T03AndT04InstantAndZonePreserved() {
        val value = (resolved() as DomainResult.Success).value
        assertEquals(Instant.parse("2026-09-10T08:00:00Z"), value.dueInstant?.value)
        assertEquals(ZoneId.of("Africa/Accra"), value.civilTime?.zone)
    }

    @Test
    fun ph8T05AndT06UnresolvedStaysUnresolved() {
        val value = (unresolved() as DomainResult.Success).value
        assertEquals(TemporalResolution.UNRESOLVED, value.resolution)
        assertNull(value.asDueInstant())
        assertEquals("Tuesday at 8 AM", value.referenceExpression)
        assertTrue(unresolved(expression = "   ") is DomainResult.Failure)
        val withDue = OperationalTemporalRecord.of(
            id = (OperationalTemporalId.of("tmp-bad") as DomainResult.Success).value,
            operationalRecordId = recordId,
            resolution = TemporalResolution.UNRESOLVED,
            referenceExpression = "Tuesday",
            dueInstant = due,
            civilTime = null,
            assignedAt = assignedAt,
            basis = TemporalCreationBasis.MANUAL,
        )
        assertTrue(withDue is DomainResult.Failure)
    }

    @Test
    fun ph8T07T08T09Provenance() {
        assertTrue(resolved(ruleVersion = "r1") is DomainResult.Failure)
        assertTrue(resolved(basis = TemporalCreationBasis.RULE) is DomainResult.Failure)
        assertTrue(
            resolved(basis = TemporalCreationBasis.RULE, ruleVersion = "phase8-ready")
                is DomainResult.Success,
        )
    }

    @Test
    fun ph8T10T11T12DueEvaluation() {
        val evaluationBefore = EvaluationInstant(Instant.parse("2026-09-09T08:00:00Z"))
        val evaluationAt = EvaluationInstant(Instant.parse("2026-09-10T08:00:00Z"))
        val evaluationAfter = EvaluationInstant(Instant.parse("2026-09-11T08:00:00Z"))
        assertEquals(DueStatus.BEFORE_DUE, DueStatus.evaluate(due, evaluationBefore))
        assertEquals(DueStatus.AT_DUE, DueStatus.evaluate(due, evaluationAt))
        assertEquals(DueStatus.PAST_DUE, DueStatus.evaluate(due, evaluationAfter))
    }

    @Test
    fun ph8T19T20ProjectionTieBreak() {
        val earlier = TemporalAssignmentInstant(Instant.parse("2026-09-07T09:00:00Z"))
        val later = TemporalAssignmentInstant(Instant.parse("2026-09-07T10:00:00Z"))
        val a = (unresolved() as DomainResult.Success).value
        val early = (OperationalTemporalRecord.of(
            id = (OperationalTemporalId.of("tmp-b") as DomainResult.Success).value,
            operationalRecordId = recordId,
            resolution = TemporalResolution.UNRESOLVED,
            referenceExpression = "tomorrow",
            dueInstant = null,
            civilTime = null,
            assignedAt = earlier,
            basis = TemporalCreationBasis.MANUAL,
        ) as DomainResult.Success).value
        val late = (resolved(rawId = "tmp-a", assigned = later) as DomainResult.Success).value
        assertEquals(late.id, OperationalTemporalProjection.current(listOf(early, late))?.id)
        val tieA = (resolved(rawId = "tmp-2", assigned = earlier) as DomainResult.Success).value
        val tieB = (resolved(rawId = "tmp-1", assigned = earlier) as DomainResult.Success).value
        assertEquals("tmp-2", OperationalTemporalProjection.current(listOf(tieB, tieA))?.id?.value)
    }
}
