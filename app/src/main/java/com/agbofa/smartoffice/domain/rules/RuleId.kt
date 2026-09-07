package com.agbofa.smartoffice.domain.rules

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

@JvmInline
value class RuleId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<RuleId> {
            val trimmed = value.trim()
            return if (trimmed.isEmpty()) {
                DomainResult.Failure(
                    DomainError.ValidationError("RuleId must not be blank", "RuleId"),
                )
            } else {
                DomainResult.Success(RuleId(trimmed))
            }
        }
    }
}
