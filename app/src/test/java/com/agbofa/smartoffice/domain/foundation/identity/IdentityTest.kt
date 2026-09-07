package com.agbofa.smartoffice.domain.foundation.identity

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IdentityTest {

    @Test
    fun entityIdEqualityAndTrim() {
        val a = (EntityId.of("  abc  ") as DomainResult.Success).value
        val b = (EntityId.of("abc") as DomainResult.Success).value
        assertEquals(a, b)
        assertEquals("abc", a.value)
    }

    @Test
    fun blankIdIsValidationError() {
        val result = EventId.of("   ")
        assertTrue(result is DomainResult.Failure)
        val error = (result as DomainResult.Failure).error
        assertTrue(error is DomainError.ValidationError)
    }

    @Test
    fun differentRawValuesAreNotEqual() {
        val a = (OperationId.of("op-1") as DomainResult.Success).value
        val b = (OperationId.of("op-2") as DomainResult.Success).value
        assertNotEquals(a, b)
    }

    @Test
    fun identicalConstructionIsRepeatable() {
        assertEquals(EntityId.of("e-1"), EntityId.of("e-1"))
        assertEquals(EventId.of("ev-1"), EventId.of("ev-1"))
        assertEquals(OperationId.of("op-1"), OperationId.of("op-1"))
    }
}
