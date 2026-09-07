package com.agbofa.smartoffice.domain.rules

import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalState

/**
 * Explicit facts supplied to a rule evaluation.
 *
 * Null means the fact was not provided. A leaf that needs that fact
 * evaluates to INAPPLICABLE rather than guessing false.
 */
data class RuleEvaluationInput(
    val context: EvaluationContext,
    val operationalState: OperationalState? = null,
    val dueStatus: DueStatus? = null,
    val workflowComplete: Boolean? = null,
    val workflowCancelled: Boolean? = null,
    val workflowHasActiveStep: Boolean? = null,
    val hasDependencies: Boolean? = null,
    val classificationType: ClassificationType? = null,
)
