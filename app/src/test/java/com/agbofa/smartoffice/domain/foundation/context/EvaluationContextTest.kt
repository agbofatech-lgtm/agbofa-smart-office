package com.agbofa.smartoffice.domain.foundation.context

import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class EvaluationContextTest {
    @Test
    fun identicalContextIsEqual() {
        val t = EvaluationInstant(Instant.parse("2026-09-07T08:00:00Z"))
        assertEquals(EvaluationContext(t), EvaluationContext(t))
    }
}
