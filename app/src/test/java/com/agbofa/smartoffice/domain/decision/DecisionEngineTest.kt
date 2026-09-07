package com.agbofa.smartoffice.domain.decision

import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.DecisionCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.DecisionTransitionInstant
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class DecisionEngineTest {
    private fun id(value: String) = (DecisionId.of(value) as DomainResult.Success).value
    private fun tid(value: String) = (DecisionTransitionId.of(value) as DomainResult.Success).value
    private fun oid(value: String) = (OperationalRecordId.of(value) as DomainResult.Success).value

    @Test
    fun emptyHistoryProjectsProposed() {
        assertEquals(DecisionStatus.PROPOSED, DecisionProjection.current(emptyList()))
    }

    @Test
    fun proposedMayApproveRejectWithdraw() {
        val decisionId = id("dec-1")
        val at = DecisionTransitionInstant(Instant.parse("2026-09-07T12:00:00Z"))
        assertTrue(DecisionTransition.of(tid("t1"), decisionId, DecisionStatus.PROPOSED, DecisionStatus.APPROVED, at) is DomainResult.Success)
        assertTrue(DecisionTransition.of(tid("t2"), decisionId, DecisionStatus.PROPOSED, DecisionStatus.REJECTED, at) is DomainResult.Success)
        assertTrue(DecisionTransition.of(tid("t3"), decisionId, DecisionStatus.PROPOSED, DecisionStatus.WITHDRAWN, at) is DomainResult.Success)
        assertTrue(DecisionTransition.of(tid("t4"), decisionId, DecisionStatus.APPROVED, DecisionStatus.REJECTED, at) is DomainResult.Failure)
        assertTrue(DecisionTransition.of(tid("t5"), decisionId, DecisionStatus.PROPOSED, DecisionStatus.PROPOSED, at) is DomainResult.Failure)
    }

    @Test
    fun tieBreakUsesTransitionId() {
        val decisionId = id("dec-1")
        val at = DecisionTransitionInstant(Instant.parse("2026-09-07T12:00:00Z"))
        val a = (DecisionTransition.of(tid("b-late"), decisionId, DecisionStatus.PROPOSED, DecisionStatus.REJECTED, at) as DomainResult.Success).value
        val b = (DecisionTransition.of(tid("a-early"), decisionId, DecisionStatus.PROPOSED, DecisionStatus.APPROVED, at) as DomainResult.Success).value
        assertEquals(DecisionStatus.APPROVED, DecisionProjection.current(listOf(a, b)))
        assertEquals(DecisionStatus.APPROVED, DecisionProjection.current(listOf(b, a)))
    }

    @Test
    fun decisionRequiresRationale() {
        val result = Decision.of(
            id("dec-1"),
            DecisionSubject.operationalRecord(oid("op-1")),
            AuthorizedActionType.TRANSITION_OPERATIONAL_STATE,
            "  ",
            DecisionCreationInstant(Instant.parse("2026-09-07T12:00:00Z")),
        )
        assertTrue(result is DomainResult.Failure)
    }

    @Test
    fun subjectMustMatchActionType() {
        val result = Decision.of(
            id("dec-2"),
            DecisionSubject.operationalRecord(oid("op-1")),
            AuthorizedActionType.ADVANCE_WORKFLOW,
            "Advance a record",
            DecisionCreationInstant(Instant.parse("2026-09-07T12:00:00Z")),
        )
        assertTrue(result is DomainResult.Failure)
    }

    @Test
    fun terminalRejectsResurrection() {
        val at = DecisionTransitionInstant(Instant.parse("2026-09-07T12:00:00Z"))
        val decisionId = id("dec-term")
        assertTrue(
            DecisionTransition.of(tid("t-rej"), decisionId, DecisionStatus.REJECTED, DecisionStatus.APPROVED, at)
                is DomainResult.Failure,
        )
        assertTrue(
            DecisionTransition.of(tid("t-app"), decisionId, DecisionStatus.APPROVED, DecisionStatus.WITHDRAWN, at)
                is DomainResult.Failure,
        )
    }

    @Test
    fun insertionOrderDoesNotChangeProjection() {
        val decisionId = id("dec-order")
        val at = DecisionTransitionInstant(Instant.parse("2026-09-07T12:00:00Z"))
        val first = (DecisionTransition.of(tid("t-b"), decisionId, DecisionStatus.PROPOSED, DecisionStatus.APPROVED, at) as DomainResult.Success).value
        val sameTime = (DecisionTransition.of(tid("t-a"), decisionId, DecisionStatus.PROPOSED, DecisionStatus.REJECTED, at) as DomainResult.Success).value
        assertEquals(DecisionStatus.APPROVED, DecisionProjection.current(listOf(first, sameTime)))
        assertEquals(DecisionStatus.APPROVED, DecisionProjection.current(listOf(sameTime, first)))
    }
}
