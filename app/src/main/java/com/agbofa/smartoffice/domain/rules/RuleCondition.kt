package com.agbofa.smartoffice.domain.rules

import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalState

/**
 * Typed deterministic predicate algebra.
 *
 * Empty [All] is true. Empty [Any] is false.
 */
sealed class RuleCondition {
    data class All(val conditions: List<RuleCondition>) : RuleCondition()
    data class Any(val conditions: List<RuleCondition>) : RuleCondition()
    data class Not(val condition: RuleCondition) : RuleCondition()
    data class OperationalStateIs(val state: OperationalState) : RuleCondition()
    data class DueStatusIs(val status: DueStatus) : RuleCondition()
    data object WorkflowComplete : RuleCondition()
    data object WorkflowCancelled : RuleCondition()
    data object WorkflowHasActiveStep : RuleCondition()
    data object HasDependencies : RuleCondition()
    data class ClassificationIs(val type: ClassificationType) : RuleCondition()
}
