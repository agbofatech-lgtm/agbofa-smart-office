package com.agbofa.smartoffice.data.rules

import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.rules.RuleDecision

object RuleDecisionCodec {
    fun encode(decision: RuleDecision): String = when (decision) {
        RuleDecision.NoAction -> "NO_ACTION"
        is RuleDecision.RecommendStateTransition -> "STATE:${decision.target.name}"
        RuleDecision.RecommendWorkflowAdvancement -> "WORKFLOW_ADVANCE"
    }

    fun decode(raw: String): RuleDecision? = when {
        raw == "NO_ACTION" -> RuleDecision.NoAction
        raw == "WORKFLOW_ADVANCE" -> RuleDecision.RecommendWorkflowAdvancement
        raw.startsWith("STATE:") ->
            runCatching {
                RuleDecision.RecommendStateTransition(OperationalState.valueOf(raw.removePrefix("STATE:")))
            }.getOrNull()
        else -> null
    }
}
