package com.agbofa.smartoffice.domain.foundation.identity

import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.error.DomainError

/**
 * Foundational entity identity.
 *
 * Offline-capable: the caller supplies the raw value.
 * This type does not read a clock or a network, and it does not
 * generate identifiers internally.
 */
@JvmInline
value class EntityId private constructor(val value: String) {
    companion object {
        fun of(value: String): DomainResult<EntityId> {
            val trimmed = value.trim()
            return if (trimmed.isEmpty()) {
                DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "EntityId must not be blank",
                        path = "EntityId",
                    ),
                )
            } else {
                DomainResult.Success(EntityId(trimmed))
            }
        }
    }
}
