package com.agbofa.smartoffice.domain.decision

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

@JvmInline
value class DecisionId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<DecisionId> = typedId(value, "DecisionId") { DecisionId(it) }
    }
}

@JvmInline
value class DecisionTransitionId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<DecisionTransitionId> =
            typedId(value, "DecisionTransitionId") { DecisionTransitionId(it) }
    }
}

@JvmInline
value class AuthorizedActionRequestId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<AuthorizedActionRequestId> =
            typedId(value, "AuthorizedActionRequestId") { AuthorizedActionRequestId(it) }
    }
}

@JvmInline
value class AuthorizedActionExecutionId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<AuthorizedActionExecutionId> =
            typedId(value, "AuthorizedActionExecutionId") { AuthorizedActionExecutionId(it) }
    }
}

private fun <T> typedId(value: String, path: String, wrap: (String) -> T): DomainResult<T> {
    val trimmed = value.trim()
    return if (trimmed.isEmpty()) {
        DomainResult.Failure(DomainError.ValidationError("$path must not be blank", path))
    } else {
        DomainResult.Success(wrap(trimmed))
    }
}
