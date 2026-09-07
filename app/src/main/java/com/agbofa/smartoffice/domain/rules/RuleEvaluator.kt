package com.agbofa.smartoffice.domain.rules

object RuleEvaluator {
    fun evaluate(condition: RuleCondition, input: RuleEvaluationInput): RuleMatch =
        when (condition) {
            is RuleCondition.All -> evaluateAll(condition.conditions, input)
            is RuleCondition.Any -> evaluateAny(condition.conditions, input)
            is RuleCondition.Not -> evaluateNot(condition.condition, input)
            is RuleCondition.OperationalStateIs ->
                compare(input.operationalState) { it == condition.state }
            is RuleCondition.DueStatusIs ->
                compare(input.dueStatus) { it == condition.status }
            is RuleCondition.WorkflowComplete ->
                compare(input.workflowComplete) { it }
            is RuleCondition.WorkflowCancelled ->
                compare(input.workflowCancelled) { it }
            is RuleCondition.WorkflowHasActiveStep ->
                compare(input.workflowHasActiveStep) { it }
            is RuleCondition.HasDependencies ->
                compare(input.hasDependencies) { it }
            is RuleCondition.ClassificationIs ->
                compare(input.classificationType) { it == condition.type }
        }

    private fun evaluateAll(conditions: List<RuleCondition>, input: RuleEvaluationInput): RuleMatch {
        if (conditions.isEmpty()) return RuleMatch.MATCH
        var sawInapplicable = false
        for (child in conditions) {
            when (evaluate(child, input)) {
                RuleMatch.NO_MATCH -> return RuleMatch.NO_MATCH
                RuleMatch.INAPPLICABLE -> sawInapplicable = true
                RuleMatch.MATCH -> Unit
            }
        }
        return if (sawInapplicable) RuleMatch.INAPPLICABLE else RuleMatch.MATCH
    }

    private fun evaluateAny(conditions: List<RuleCondition>, input: RuleEvaluationInput): RuleMatch {
        if (conditions.isEmpty()) return RuleMatch.NO_MATCH
        var sawInapplicable = false
        for (child in conditions) {
            when (evaluate(child, input)) {
                RuleMatch.MATCH -> return RuleMatch.MATCH
                RuleMatch.INAPPLICABLE -> sawInapplicable = true
                RuleMatch.NO_MATCH -> Unit
            }
        }
        return if (sawInapplicable) RuleMatch.INAPPLICABLE else RuleMatch.NO_MATCH
    }

    private fun evaluateNot(condition: RuleCondition, input: RuleEvaluationInput): RuleMatch =
        when (evaluate(condition, input)) {
            RuleMatch.MATCH -> RuleMatch.NO_MATCH
            RuleMatch.NO_MATCH -> RuleMatch.MATCH
            RuleMatch.INAPPLICABLE -> RuleMatch.INAPPLICABLE
        }

    private fun <T> compare(value: T?, predicate: (T) -> Boolean): RuleMatch {
        if (value == null) return RuleMatch.INAPPLICABLE
        return if (predicate(value)) RuleMatch.MATCH else RuleMatch.NO_MATCH
    }
}
