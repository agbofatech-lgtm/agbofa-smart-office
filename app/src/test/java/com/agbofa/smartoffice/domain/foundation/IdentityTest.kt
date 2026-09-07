package com.agbofa.smartoffice.domain.foundation

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.identity.EntityId
import com.agbofa.smartoffice.domain.foundation.identity.EventId
import com.agbofa.smartoffice.domain.foundation.identity.OperationId
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IdentityTest {

    @Test
    fun entityIdEqualityIsByValue() {
        val a = EntityId.of("alpha") as DomainResult.Success
        val b = EntityId.of("alpha") as DomainResult.Success
        val c = EntityId.of("beta") as DomainResult.Success
        assertEquals(a.value, b.value)
        assertNotEquals(a.value, c.value)
    }

    @Test
    fun blankIdentityIsRejected() {
        val result = EntityId.of("   ")
        assertTrue(result is DomainResult.Failure)
        val error = (result as DomainResult.Failure).error
        assertTrue(error is DomainError.ValidationError)
    }

    @Test
    fun identityKindsAreDistinctTypes() {
        val entity = (EntityId.of("same") as DomainResult.Success).value
        val event = (EventId.of("same") as DomainResult.Success).value
        val operation = (OperationId.of("same") as DomainResult.Success).value
        assertEquals(entity.value, event.value)
        assertEquals(entity.value, operation.value)
        // Distinct wrappers prevent passing EventId where EntityId is required.
        assertEquals(EntityId::class.java, entity::class.java)
        assertEquals(EventId::class.java, event::class.java)
        assertEquals(OperationId::class.java, operation::class.java)
    }

    @Test
    fun constructionDoesNotInventAnId() {
        val result = EntityId.of("offline-1") as DomainResult.Success
        assertEquals("offline-1", result.value.value)
    }
}
