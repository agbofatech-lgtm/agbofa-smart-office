package com.agbofa.smartoffice.domain.decision

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.ActionRequestInstant

enum class AuthorizedActionExecutionResult {
    DISPATCHED,
    REJECTED_BY_OWNER,
    REJECTED_BY_AUTHORIZATION,
}

/**
 * Append-only record that an approved request was dispatched
 * to an owning-domain use case. Not a second lifecycle.
 */
data class AuthorizedActionExecution private constructor(
    val id: AuthorizedActionExecutionId,
    val requestId: AuthorizedActionRequestId,
    val decisionId: DecisionId,
    val executedAt: ActionRequestInstant,
    val result: AuthorizedActionExecutionResult,
    val detail: String,
) {
    companion object {
        fun of(
            id: AuthorizedActionExecutionId,
            requestId: AuthorizedActionRequestId,
            decisionId: DecisionId,
            executedAt: ActionRequestInstant,
            result: AuthorizedActionExecutionResult,
            detail: String,
        ): DomainResult<AuthorizedActionExecution> {
            val trimmed = detail.trim()
            if (trimmed.isEmpty()) {
                return DomainResult.Failure(
                    DomainError.ValidationError("Execution detail must not be blank", "detail"),
                )
            }
            return DomainResult.Success(
                AuthorizedActionExecution(id, requestId, decisionId, executedAt, result, trimmed),
            )
        }
    }
}
