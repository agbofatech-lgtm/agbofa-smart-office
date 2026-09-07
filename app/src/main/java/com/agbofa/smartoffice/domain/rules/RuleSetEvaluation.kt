package com.agbofa.smartoffice.domain.rules

enum class RuleSetOutcome {
    NO_MATCH,
    MATCH,
    CONFLICT,
}

data class RuleSetEvaluation(
    val outcome: RuleSetOutcome,
    val results: List<RuleEvaluationResult>,
    val matches: List<RuleEvaluationResult>,
    val conflictDecisions: List<RuleDecision>,
)
