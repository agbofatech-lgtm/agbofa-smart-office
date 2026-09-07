package com.agbofa.smartoffice.domain.rules

import com.agbofa.smartoffice.domain.operations.OperationalState

/**
 * Recommendation only. Evaluation must not execute these decisions.
 */
sealed class RuleDecision {
    data object NoAction : RuleDecision()

    data class RecommendStateTransition(
        val target: OperationalState,
    ) : RuleDecision()

    data object RecommendWorkflowAdvancement : RuleDecision()
}
