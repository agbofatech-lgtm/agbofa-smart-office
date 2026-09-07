package com.agbofa.smartoffice.domain.rules

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

@JvmInline
value class RuleVersion private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<RuleVersion> {
            val trimmed = value.trim()
            return if (trimmed.isEmpty()) {
                DomainResult.Failure(
                    DomainError.ValidationError("RuleVersion must not be blank", "RuleVersion"),
                )
            } else {
                DomainResult.Success(RuleVersion(trimmed))
            }
        }
    }
}
