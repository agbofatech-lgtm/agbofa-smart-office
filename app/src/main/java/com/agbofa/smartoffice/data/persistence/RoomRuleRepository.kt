package com.agbofa.smartoffice.data.persistence

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.rules.Rule
import com.agbofa.smartoffice.domain.rules.RuleId
import com.agbofa.smartoffice.domain.rules.RuleRepository
import com.agbofa.smartoffice.domain.rules.RuleSetEvaluator
import com.agbofa.smartoffice.domain.rules.RuleVersion

class RoomRuleRepository(
    private val dao: RuleDao,
) : RuleRepository {
    override fun save(rule: Rule): DomainResult<Rule> {
        if (dao.find(rule.id.value, rule.version.value) != null) {
            return DomainResult.Failure(DomainError.InvalidState("Rule version already exists"))
        }
        return try {
            dao.insert(rule.toEntity())
            DomainResult.Success(rule)
        } catch (error: Exception) {
            DomainResult.Failure(
                DomainError.PersistenceFailure(error.message ?: "Failed to persist rule"),
            )
        }
    }

    override fun findByIdAndVersion(id: RuleId, version: RuleVersion): Rule? =
        dao.find(id.value, version.value)?.toDomain()

    override fun listById(id: RuleId): List<Rule> =
        RuleSetEvaluator.ordered(dao.listById(id.value).mapNotNull { it.toDomain() })

    override fun listAll(): List<Rule> =
        RuleSetEvaluator.ordered(dao.list().mapNotNull { it.toDomain() })
}
