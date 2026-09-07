package com.agbofa.smartoffice.application.rules

import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.RuleCreationInstant
import com.agbofa.smartoffice.domain.rules.Rule
import com.agbofa.smartoffice.domain.rules.RuleCondition
import com.agbofa.smartoffice.domain.rules.RuleDecision
import com.agbofa.smartoffice.domain.rules.RuleDefinitionBasis
import com.agbofa.smartoffice.domain.rules.RuleId
import com.agbofa.smartoffice.domain.rules.RuleRepository
import com.agbofa.smartoffice.domain.rules.RuleVersion

class CreateRuleUseCase(
    private val rules: RuleRepository,
) {
    fun execute(
        ruleId: String,
        version: String,
        key: String,
        condition: RuleCondition,
        decision: RuleDecision,
        createdAt: RuleCreationInstant,
        basis: RuleDefinitionBasis = RuleDefinitionBasis.MANUAL,
    ): DomainResult<Rule> {
        val id = when (val result = RuleId.of(ruleId)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        val ver = when (val result = RuleVersion.of(version)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        val rule = when (
            val result = Rule.of(
                id = id,
                version = ver,
                key = key,
                condition = condition,
                decision = decision,
                createdAt = createdAt,
                basis = basis,
            )
        ) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        return rules.save(rule)
    }
}
