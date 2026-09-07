package com.agbofa.smartoffice.domain.foundation.error

/**
 * Foundational typed errors.
 *
 * Product-specific errors are not authorized in Phase 2.
 */
sealed class DomainError {
    abstract val message: String

    data class ValidationError(
        override val message: String,
        val path: String? = null,
    ) : DomainError()

    data class InvariantViolation(
        override val message: String,
    ) : DomainError()

    data class InvalidState(
        override val message: String,
    ) : DomainError()

    data class PersistenceFailure(
        override val message: String,
    ) : DomainError()
}
