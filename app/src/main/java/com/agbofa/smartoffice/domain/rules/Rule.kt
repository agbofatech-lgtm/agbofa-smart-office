package com.agbofa.smartoffice.domain.rules

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.RuleCreationInstant

/**
 * Immutable rule version.
 *
 * Changing meaning requires a new [version], not an in-place rewrite.
 */
data class Rule private constructor(
    val id: RuleId,
    val version: RuleVersion,
    val key: String,
    val condition: RuleCondition,
    val decision: RuleDecision,
    val createdAt: RuleCreationInstant,
    val basis: RuleDefinitionBasis,
) {
    companion object {
        fun of(
            id: RuleId,
            version: RuleVersion,
            key: String,
            condition: RuleCondition,
            decision: RuleDecision,
            createdAt: RuleCreationInstant,
            basis: RuleDefinitionBasis = RuleDefinitionBasis.MANUAL,
        ): DomainResult<Rule> {
            val trimmedKey = key.trim()
            if (trimmedKey.isEmpty()) {
                return DomainResult.Failure(
                    DomainError.ValidationError("Rule key must not be blank", "key"),
                )
            }
            return DomainResult.Success(
                Rule(id, version, trimmedKey, condition, decision, createdAt, basis),
            )
        }
    }
}
