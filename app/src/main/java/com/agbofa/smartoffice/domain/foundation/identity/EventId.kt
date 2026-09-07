package com.agbofa.smartoffice.domain.foundation.identity

import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.error.DomainError

/**
 * Foundational event identity.
 *
 * Distinct from [EntityId] and [OperationId] so an event cannot be
 * passed where an entity is required.
 */
@JvmInline
value class EventId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<EventId> {
            val trimmed = value.trim()
            return if (trimmed.isEmpty()) {
                DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "EventId must not be blank",
                        path = "EventId",
                    ),
                )
            } else {
                DomainResult.Success(EventId(trimmed))
            }
        }
    }
}
