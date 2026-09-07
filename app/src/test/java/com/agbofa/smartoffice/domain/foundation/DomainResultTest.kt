package com.agbofa.smartoffice.domain.foundation

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainResultTest {

    @Test
    fun successHoldsValue() {
        val result: DomainResult<Int> = DomainResult.Success(7)
        assertTrue(result.isSuccess)
        assertEquals(7, result.getOrNull())
        assertNull(result.errorOrNull())
    }

    @Test
    fun failureHoldsTypedError() {
        val error = DomainError.InvalidState("not allowed")
        val result: DomainResult<Int> = DomainResult.Failure(error)
        assertTrue(result.isFailure)
        assertEquals(error, result.errorOrNull())
        assertNull(result.getOrNull())
    }
}
