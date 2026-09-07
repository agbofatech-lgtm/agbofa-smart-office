package com.agbofa.smartoffice.domain.foundation.result

import com.agbofa.smartoffice.domain.foundation.error.DomainError

/**
 * Explicit domain result.
 *
 * Kotlin's [Result] stores failure as [Throwable]. Domain rules need
 * inspectable, typed [DomainError] values without using exceptions as
 * control flow.
 */
sealed class DomainResult<out T> {
    data class Success<T>(val value: T) : DomainResult<T>()
    data class Failure(val error: DomainError) : DomainResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    fun getOrNull(): T? = (this as? Success)?.value
    fun errorOrNull(): DomainError? = (this as? Failure)?.error
}
