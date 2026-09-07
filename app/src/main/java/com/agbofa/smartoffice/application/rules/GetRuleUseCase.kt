package com.agbofa.smartoffice.application.rules

import com.agbofa.smartoffice.domain.rules.Rule
import com.agbofa.smartoffice.domain.rules.RuleId
import com.agbofa.smartoffice.domain.rules.RuleRepository

class GetRuleUseCase(
    private val rules: RuleRepository,
) {
    fun execute(id: RuleId): List<Rule> = rules.listById(id)
}
