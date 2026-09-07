package com.agbofa.smartoffice.domain.foundation.event

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.identity.EventId
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.EventInstant

/**
 * Foundational domain event contract.
 *
 * This is not event sourcing, not an event bus, and not persistence.
 * Product events such as JournalCaptured or PaymentRecorded are forbidden
 * in Phase 2.
 */
data class DomainEvent private constructor(
    val id: EventId,
    val occurredAt: EventInstant,
    val type: String,
    val source: String?,
) {
    companion object {
        fun of(
            id: EventId,
            occurredAt: EventInstant,
            type: String,
            source: String? = null,
        ): DomainResult<DomainEvent> {
            val trimmedType = type.trim()
            if (trimmedType.isEmpty()) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "DomainEvent type must not be blank",
                        path = "DomainEvent.type",
                    ),
                )
            }
            val trimmedSource = source?.trim()?.takeIf { it.isNotEmpty() }
            return DomainResult.Success(
                DomainEvent(
                    id = id,
                    occurredAt = occurredAt,
                    type = trimmedType,
                    source = trimmedSource,
                ),
            )
        }
    }
}
