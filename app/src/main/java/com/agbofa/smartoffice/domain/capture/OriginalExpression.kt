package com.agbofa.smartoffice.domain.capture

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

/**
 * Exact text the user entered.
 *
 * Validation decides whether the expression is meaningful.
 * Validation does not rewrite evidence: leading and trailing
 * spaces are preserved when the expression contains content.
 */
@JvmInline
value class OriginalExpression private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<OriginalExpression> {
            return if (value.isBlank()) {
                DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "Original expression must not be blank",
                        path = "originalExpression",
                    ),
                )
            } else {
                DomainResult.Success(OriginalExpression(value))
            }
        }
    }
}
