package com.agbofa.smartoffice.application.rules

import com.agbofa.smartoffice.data.operations.InMemoryOperationalRecordRepository
import com.agbofa.smartoffice.data.rules.InMemoryRuleRepository
import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant
import com.agbofa.smartoffice.domain.foundation.time.RuleCreationInstant
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.rules.RuleCondition
import com.agbofa.smartoffice.domain.rules.RuleDecision
import com.agbofa.smartoffice.domain.rules.RuleEvaluationInput
import com.agbofa.smartoffice.domain.rules.RuleId
import com.agbofa.smartoffice.domain.rules.RuleMatch
import com.agbofa.smartoffice.domain.rules.RuleSetOutcome
import com.agbofa.smartoffice.domain.rules.RuleVersion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class RulesEngineTest {
    private val rules = InMemoryRuleRepository()
    private val records = InMemoryOperationalRecordRepository()
    private val create = CreateRuleUseCase(rules)
    private val evaluate = EvaluateRuleUseCase(rules)
    private val evaluateSet = EvaluateRuleSetUseCase(rules)
    private val createdAt = RuleCreationInstant(Instant.parse("2026-09-07T10:00:00Z"))
    private val input = RuleEvaluationInput(
        context = EvaluationContext(EvaluationInstant(Instant.parse("2026-09-07T11:00:00Z"))),
        operationalState = OperationalState.ACTIVE,
    )

    private fun createRule(
        id: String,
        version: String,
        key: String,
        condition: RuleCondition,
        decision: RuleDecision,
    ) {
        val result = create.execute(id, version, key, condition, decision, createdAt)
        assertTrue(result is DomainResult.Success)
    }

    @Test
    fun createAndEvaluateMatch() {
        createRule(
            "r-1", "1", "active-check",
            RuleCondition.OperationalStateIs(OperationalState.ACTIVE),
            RuleDecision.NoAction,
        )
        val id = (RuleId.of("r-1") as DomainResult.Success).value
        val version = (RuleVersion.of("1") as DomainResult.Success).value
        val result = evaluate.execute(id, version, input)
        assertTrue(result is DomainResult.Success)
        assertEquals(RuleMatch.MATCH, (result as DomainResult.Success).value.match)
        assertEquals("1", result.value.version.value)
    }

    @Test
    fun duplicateVersionRejected() {
        createRule("r-dup", "1", "k", RuleCondition.HasDependencies, RuleDecision.NoAction)
        val second = create.execute(
            "r-dup", "1", "k",
            RuleCondition.HasDependencies,
            RuleDecision.NoAction,
            createdAt,
        )
        assertTrue(second is DomainResult.Failure)
    }

    @Test
    fun missingRuleRejected() {
        val id = (RuleId.of("missing") as DomainResult.Success).value
        val version = (RuleVersion.of("1") as DomainResult.Success).value
        assertTrue(evaluate.execute(id, version, input) is DomainResult.Failure)
    }

    @Test
    fun evaluationDoesNotWriteOperationalStateOrWorkflow() {
        createRule(
            "r-2", "1", "advance",
            RuleCondition.OperationalStateIs(OperationalState.ACTIVE),
            RuleDecision.RecommendWorkflowAdvancement,
        )
        val id = (RuleId.of("r-2") as DomainResult.Success).value
        val version = (RuleVersion.of("1") as DomainResult.Success).value
        assertTrue(evaluate.execute(id, version, input) is DomainResult.Success)
        assertTrue(records.listAll().isEmpty())
    }

    @Test
    fun ruleSetConflictIsDeterministic() {
        createRule(
            "r-a", "1", "a-key",
            RuleCondition.OperationalStateIs(OperationalState.ACTIVE),
            RuleDecision.RecommendWorkflowAdvancement,
        )
        createRule(
            "r-b", "1", "b-key",
            RuleCondition.OperationalStateIs(OperationalState.ACTIVE),
            RuleDecision.RecommendStateTransition(OperationalState.COMPLETED),
        )
        val first = evaluateSet.execute(input)
        val second = evaluateSet.execute(input)
        assertEquals(RuleSetOutcome.CONFLICT, first.outcome)
        assertEquals(first.outcome, second.outcome)
        assertEquals(first.matches.map { it.ruleId.value }, second.matches.map { it.ruleId.value })
    }

    @Test
    fun ruleSetOrderIgnoresInsertion() {
        createRule("r-z", "1", "z-key", RuleCondition.OperationalStateIs(OperationalState.ACTIVE), RuleDecision.NoAction)
        createRule("r-a", "1", "a-key", RuleCondition.OperationalStateIs(OperationalState.ACTIVE), RuleDecision.NoAction)
        val keys = evaluateSet.execute(input).results.map { it.key }
        assertEquals(listOf("a-key", "z-key"), keys)
    }
}
