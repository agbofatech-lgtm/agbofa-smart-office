package com.agbofa.smartoffice.domain.rules

import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant

/**
 * Deterministic multi-rule evaluation.
 *
 * Order: key, then id, then version.
 * MATCH results with more than one distinct decision → CONFLICT.
 */
object RuleSetEvaluator {
    val ORDER = compareBy<Rule> { it.key }
        .thenBy { it.id.value }
        .thenBy { it.version.value }

    fun ordered(rules: List<Rule>): List<Rule> = rules.sortedWith(ORDER)

    fun evaluate(rules: List<Rule>, input: RuleEvaluationInput): RuleSetEvaluation {
        val results = ordered(rules).map { rule ->
            RuleEvaluationResult(
                ruleId = rule.id,
                version = rule.version,
                key = rule.key,
                match = RuleEvaluator.evaluate(rule.condition, input),
                decision = rule.decision,
                evaluatedAt = input.context.evaluationTime,
            )
        }
        val matches = results.filter { it.match == RuleMatch.MATCH }
        val decisions = matches.map { it.decision }.distinct()
        val outcome = when {
            matches.isEmpty() -> RuleSetOutcome.NO_MATCH
            decisions.size > 1 -> RuleSetOutcome.CONFLICT
            else -> RuleSetOutcome.MATCH
        }
        return RuleSetEvaluation(
            outcome = outcome,
            results = results,
            matches = matches,
            conflictDecisions = if (outcome == RuleSetOutcome.CONFLICT) decisions else emptyList(),
        )
    }
}
