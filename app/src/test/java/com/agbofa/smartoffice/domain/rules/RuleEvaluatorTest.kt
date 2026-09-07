package com.agbofa.smartoffice.domain.rules

import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalState
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class RuleEvaluatorTest {
    private val input = RuleEvaluationInput(
        context = EvaluationContext(EvaluationInstant(Instant.parse("2026-09-07T10:00:00Z"))),
        operationalState = OperationalState.ACTIVE,
        dueStatus = DueStatus.PAST_DUE,
        workflowComplete = false,
        workflowCancelled = false,
        workflowHasActiveStep = true,
        hasDependencies = true,
        classificationType = ClassificationType.NOTE,
    )

    @Test
    fun emptyAllMatches() {
        assertEquals(RuleMatch.MATCH, RuleEvaluator.evaluate(RuleCondition.All(emptyList()), input))
    }

    @Test
    fun emptyAnyDoesNotMatch() {
        assertEquals(RuleMatch.NO_MATCH, RuleEvaluator.evaluate(RuleCondition.Any(emptyList()), input))
    }

    @Test
    fun statePredicateMatches() {
        assertEquals(
            RuleMatch.MATCH,
            RuleEvaluator.evaluate(RuleCondition.OperationalStateIs(OperationalState.ACTIVE), input),
        )
    }

    @Test
    fun statePredicateMisses() {
        assertEquals(
            RuleMatch.NO_MATCH,
            RuleEvaluator.evaluate(RuleCondition.OperationalStateIs(OperationalState.OPEN), input),
        )
    }

    @Test
    fun missingInputIsInapplicable() {
        val sparse = input.copy(dueStatus = null)
        assertEquals(
            RuleMatch.INAPPLICABLE,
            RuleEvaluator.evaluate(RuleCondition.DueStatusIs(DueStatus.PAST_DUE), sparse),
        )
    }

    @Test
    fun notInvertsMatch() {
        assertEquals(
            RuleMatch.NO_MATCH,
            RuleEvaluator.evaluate(RuleCondition.Not(RuleCondition.WorkflowHasActiveStep), input),
        )
    }

    @Test
    fun nestedAllAny() {
        val condition = RuleCondition.All(
            listOf(
                RuleCondition.OperationalStateIs(OperationalState.ACTIVE),
                RuleCondition.Any(
                    listOf(
                        RuleCondition.DueStatusIs(DueStatus.PAST_DUE),
                        RuleCondition.ClassificationIs(ClassificationType.NOTE),
                    ),
                ),
            ),
        )
        assertEquals(RuleMatch.MATCH, RuleEvaluator.evaluate(condition, input))
    }

    @Test
    fun identicalInputsIdenticalResults() {
        val condition = RuleCondition.HasDependencies
        assertEquals(RuleEvaluator.evaluate(condition, input), RuleEvaluator.evaluate(condition, input))
    }
}
