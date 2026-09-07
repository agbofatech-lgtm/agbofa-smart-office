package com.agbofa.smartoffice.application.rules

import com.agbofa.smartoffice.domain.rules.RuleEvaluationInput
import com.agbofa.smartoffice.domain.rules.RuleRepository
import com.agbofa.smartoffice.domain.rules.RuleSetEvaluation
import com.agbofa.smartoffice.domain.rules.RuleSetEvaluator

class EvaluateRuleSetUseCase(
    private val rules: RuleRepository,
) {
    fun execute(input: RuleEvaluationInput): RuleSetEvaluation =
        RuleSetEvaluator.evaluate(rules.listAll(), input)
}
