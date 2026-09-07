package com.agbofa.smartoffice.application.rules

import com.agbofa.smartoffice.domain.rules.Rule
import com.agbofa.smartoffice.domain.rules.RuleId
import com.agbofa.smartoffice.domain.rules.RuleRepository
import com.agbofa.smartoffice.domain.rules.RuleVersion

class GetRuleVersionUseCase(
    private val rules: RuleRepository,
) {
    fun execute(id: RuleId, version: RuleVersion): Rule? = rules.findByIdAndVersion(id, version)
}
