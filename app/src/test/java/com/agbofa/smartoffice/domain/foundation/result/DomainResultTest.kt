package com.agbofa.smartoffice.domain.foundation.result

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainResultTest {

    @Test
    fun successIsInspectable() {
        val result: DomainResult<Int> = DomainResult.Success(7)
        assertTrue(result.isSuccess)
        assertEquals(7, result.getOrNull())
        assertNull(result.errorOrNull())
    }

    @Test
    fun failurePreservesTypedError() {
        val error = DomainError.InvariantViolation("broken")
        val result: DomainResult<Int> = DomainResult.Failure(error)
        assertTrue(result.isFailure)
        assertEquals(error, result.errorOrNull())
        assertNull(result.getOrNull())
    }

    @Test
    fun identicalInputsYieldIdenticalResults() {
        val error = DomainError.InvalidState("no")
        assertEquals(DomainResult.Success("x"), DomainResult.Success("x"))
        assertEquals(DomainResult.Failure(error), DomainResult.Failure(error))
    }
}
