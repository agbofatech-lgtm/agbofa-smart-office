package com.agbofa.smartoffice.data.rules

import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.rules.RuleCondition
import com.agbofa.smartoffice.domain.rules.RuleDecision

/**
 * Deterministic text encoding of conditions and decisions.
 * Not a scripting language.
 */
object RuleConditionCodec {
    fun encode(condition: RuleCondition): String = when (condition) {
        is RuleCondition.All ->
            "ALL(" + condition.conditions.joinToString(",") { encode(it) } + ")"
        is RuleCondition.Any ->
            "ANY(" + condition.conditions.joinToString(",") { encode(it) } + ")"
        is RuleCondition.Not ->
            "NOT(" + encode(condition.condition) + ")"
        is RuleCondition.OperationalStateIs -> "STATE_IS:${condition.state.name}"
        is RuleCondition.DueStatusIs -> "DUE_IS:${condition.status.name}"
        RuleCondition.WorkflowComplete -> "WORKFLOW_COMPLETE"
        RuleCondition.WorkflowCancelled -> "WORKFLOW_CANCELLED"
        RuleCondition.WorkflowHasActiveStep -> "WORKFLOW_HAS_ACTIVE_STEP"
        RuleCondition.HasDependencies -> "HAS_DEPENDENCIES"
        is RuleCondition.ClassificationIs -> "CLASSIFICATION_IS:${condition.type.name}"
    }

    fun decode(raw: String): RuleCondition? = parse(raw.trim()).first

    fun encodeDecision(decision: RuleDecision): String = when (decision) {
        RuleDecision.NoAction -> "NO_ACTION"
        is RuleDecision.RecommendStateTransition -> "RECOMMEND_STATE_TRANSITION:${decision.target.name}"
        RuleDecision.RecommendWorkflowAdvancement -> "RECOMMEND_WORKFLOW_ADVANCEMENT"
    }

    fun decodeDecision(raw: String): RuleDecision? {
        val value = raw.trim()
        if (value == "NO_ACTION") return RuleDecision.NoAction
        if (value == "RECOMMEND_WORKFLOW_ADVANCEMENT") return RuleDecision.RecommendWorkflowAdvancement
        if (value.startsWith("RECOMMEND_STATE_TRANSITION:")) {
            val name = value.removePrefix("RECOMMEND_STATE_TRANSITION:")
            val state = runCatching { OperationalState.valueOf(name) }.getOrNull() ?: return null
            return RuleDecision.RecommendStateTransition(state)
        }
        return null
    }

    private fun parse(text: String): Pair<RuleCondition?, Int> {
        if (text.startsWith("ALL(")) return parseList(text, "ALL") { RuleCondition.All(it) }
        if (text.startsWith("ANY(")) return parseList(text, "ANY") { RuleCondition.Any(it) }
        if (text.startsWith("NOT(")) {
            val inner = parse(text.substring(4))
            val condition = inner.first ?: return null to 0
            val close = 4 + inner.second
            if (close >= text.length || text[close] != ')') return null to 0
            return RuleCondition.Not(condition) to close + 1
        }
        val token = text.takeWhile { it != ',' && it != ')' }
        val decoded = decodeLeaf(token) ?: return null to 0
        return decoded to token.length
    }

    private fun parseList(
        text: String,
        name: String,
        wrap: (List<RuleCondition>) -> RuleCondition,
    ): Pair<RuleCondition?, Int> {
        val start = name.length + 1
        if (start >= text.length) return null to 0
        if (text[start] == ')') return wrap(emptyList()) to start + 1
        val items = mutableListOf<RuleCondition>()
        var cursor = start
        while (cursor < text.length) {
            val parsed = parse(text.substring(cursor))
            val item = parsed.first ?: return null to 0
            items.add(item)
            cursor += parsed.second
            if (cursor >= text.length) return null to 0
            when (text[cursor]) {
                ',' -> cursor += 1
                ')' -> return wrap(items) to cursor + 1
                else -> return null to 0
            }
        }
        return null to 0
    }

    private fun decodeLeaf(token: String): RuleCondition? = when {
        token == "WORKFLOW_COMPLETE" -> RuleCondition.WorkflowComplete
        token == "WORKFLOW_CANCELLED" -> RuleCondition.WorkflowCancelled
        token == "WORKFLOW_HAS_ACTIVE_STEP" -> RuleCondition.WorkflowHasActiveStep
        token == "HAS_DEPENDENCIES" -> RuleCondition.HasDependencies
        token.startsWith("STATE_IS:") ->
            runCatching { RuleCondition.OperationalStateIs(OperationalState.valueOf(token.removePrefix("STATE_IS:"))) }.getOrNull()
        token.startsWith("DUE_IS:") ->
            runCatching { RuleCondition.DueStatusIs(DueStatus.valueOf(token.removePrefix("DUE_IS:"))) }.getOrNull()
        token.startsWith("CLASSIFICATION_IS:") ->
            runCatching { RuleCondition.ClassificationIs(ClassificationType.valueOf(token.removePrefix("CLASSIFICATION_IS:"))) }.getOrNull()
        else -> null
    }
}
