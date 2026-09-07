package com.agbofa.smartoffice.domain.intelligence

import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant
import com.agbofa.smartoffice.domain.integrity.IntegrityOutcome
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class IntelligenceEvaluatorTest {
    private val context = EvaluationContext(EvaluationInstant(Instant.parse("2026-09-07T16:00:00Z")))

    private fun subject(
        id: String,
        state: OperationalState = OperationalState.OPEN,
        due: DueStatus? = null,
        prereq: Int = 0,
        workflow: Boolean = false,
        complete: Boolean = false,
        active: Boolean = false,
        outcome: IntegrityOutcome = IntegrityOutcome.HEALTHY,
    ) = IntelligenceSubject(id, state, due, prereq, workflow, complete, active, outcome, emptyList())

    @Test
    fun emptyInputIsEmptyAdvisory() {
        val report = IntelligenceEvaluator.evaluate(IntelligenceInput(context = context))
        assertEquals(0, report.summary.recommendationCount)
        assertEquals(0, report.summary.anomalyCount)
        assertEquals(0, report.summary.prioritizedCount)
    }

    @Test
    fun insertionOrderDoesNotChangeOutput() {
        val a = subject("a", due = DueStatus.PAST_DUE)
        val b = subject("b", state = OperationalState.ACTIVE)
        val first = IntelligenceEvaluator.evaluate(IntelligenceInput(subjects = listOf(a, b), context = context))
        val second = IntelligenceEvaluator.evaluate(IntelligenceInput(subjects = listOf(b, a), context = context))
        assertEquals(first.recommendations.map { it.key }, second.recommendations.map { it.key })
        assertEquals(first.prioritized.map { it.targetId to it.score }, second.prioritized.map { it.targetId to it.score })
    }

    @Test
    fun pastDueScoresHigherAndDoesNotInventState() {
        val report = IntelligenceEvaluator.evaluate(
            IntelligenceInput(subjects = listOf(subject("r1", due = DueStatus.PAST_DUE)), context = context),
        )
        assertEquals(100, report.prioritized.single().score)
        assertEquals("REVIEW_PAST_DUE:r1", report.recommendations.single().key)
        assertTrue(OperationalState.entries.none { it.name == "OVERDUE" || it.name == "BLOCKED" })
    }

    @Test
    fun concentrationHeuristicIsDocumentedNotCanonical() {
        val subjects = (1..3).map { subject("r$it", due = DueStatus.PAST_DUE) }
        val report = IntelligenceEvaluator.evaluate(
            IntelligenceInput(subjects = subjects, pastDueCount = 3, context = context),
        )
        val anomaly = report.anomalies.single { it.type == AnomalyType.PAST_DUE_CONCENTRATION }
        assertEquals("false", anomaly.evidence.getValue("canonical"))
        assertEquals("3", anomaly.evidence.getValue("threshold"))
    }

    @Test
    fun proposedDecisionIsAdvisoryOnly() {
        val report = IntelligenceEvaluator.evaluate(
            IntelligenceInput(
                decisions = listOf(IntelligenceDecisionSlice("d1", "PROPOSED", "Activate")),
                context = context,
            ),
        )
        assertEquals(RecommendationType.REVIEW_PROPOSED_DECISION, report.recommendations.single().type)
    }
}
