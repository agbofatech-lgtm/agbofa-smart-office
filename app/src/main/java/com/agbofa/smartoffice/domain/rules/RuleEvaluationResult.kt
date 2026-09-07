package com.agbofa.smartoffice.domain.rules

import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant

data class RuleEvaluationResult(
    val ruleId: RuleId,
    val version: RuleVersion,
    val key: String,
    val match: RuleMatch,
    val decision: RuleDecision,
    val evaluatedAt: EvaluationInstant,
)
