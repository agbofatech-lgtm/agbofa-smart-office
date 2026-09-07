package com.agbofa.smartoffice.domain.rules

import com.agbofa.smartoffice.domain.foundation.result.DomainResult

interface RuleRepository {
    fun save(rule: Rule): DomainResult<Rule>
    fun findByIdAndVersion(id: RuleId, version: RuleVersion): Rule?
    fun listById(id: RuleId): List<Rule>
    fun listAll(): List<Rule>
}
