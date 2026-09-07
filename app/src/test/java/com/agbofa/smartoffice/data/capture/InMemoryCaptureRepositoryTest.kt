package com.agbofa.smartoffice.data.capture

import com.agbofa.smartoffice.domain.capture.Capture
import com.agbofa.smartoffice.domain.capture.CaptureId
import com.agbofa.smartoffice.domain.capture.OriginalExpression
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class InMemoryCaptureRepositoryTest {

    @Test
    fun roundTripPreservesOriginalExpression() {
        val repository = InMemoryCaptureRepository()
        val id = (CaptureId.of("cap-9") as DomainResult.Success).value
        val expression = (OriginalExpression.of("  Buy cement tomorrow  ") as DomainResult.Success).value
        val capturedAt = CaptureInstant(Instant.parse("2026-09-07T08:00:00Z"))
        val capture = (Capture.of(id, expression, capturedAt) as DomainResult.Success).value
        repository.save(capture)
        val loaded = repository.findById(id)
        assertEquals("  Buy cement tomorrow  ", loaded?.originalExpression?.value)
        assertEquals(capturedAt, loaded?.capturedAt)
        assertEquals(id, loaded?.id)
    }
}
