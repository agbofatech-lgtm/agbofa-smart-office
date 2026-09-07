package com.agbofa.smartoffice.application.rules

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.rules.RuleEvaluationInput
import com.agbofa.smartoffice.domain.rules.RuleEvaluationResult
import com.agbofa.smartoffice.domain.rules.RuleEvaluator
import com.agbofa.smartoffice.domain.rules.RuleId
import com.agbofa.smartoffice.domain.rules.RuleRepository
import com.agbofa.smartoffice.domain.rules.RuleVersion

class EvaluateRuleUseCase(
    private val rules: RuleRepository,
) {
    fun execute(
        ruleId: RuleId,
        version: RuleVersion,
        input: RuleEvaluationInput,
    ): DomainResult<RuleEvaluationResult> {
        val rule = rules.findByIdAndVersion(ruleId, version)
            ?: return DomainResult.Failure(DomainError.InvalidState("Rule version does not exist"))
        return DomainResult.Success(
            RuleEvaluationResult(
                ruleId = rule.id,
                version = rule.version,
                key = rule.key,
                match = RuleEvaluator.evaluate(rule.condition, input),
                decision = rule.decision,
                evaluatedAt = input.context.evaluationTime,
            ),
        )
    }
}
