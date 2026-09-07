package com.agbofa.smartoffice.application.capture

import com.agbofa.smartoffice.data.capture.InMemoryCaptureRepository
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class CaptureExpressionUseCaseTest {

    private val instant = CaptureInstant(Instant.parse("2026-09-07T08:00:00Z"))

    @Test
    fun executePersistsExactExpression() {
        val repository = InMemoryCaptureRepository()
        val useCase = CaptureExpressionUseCase(repository)
        val result = useCase.execute(
            CaptureExpressionCommand(
                id = "cap-1",
                expression = "Ama gave me GH₵500 to pay school fees",
                capturedAt = instant,
            ),
        )
        assertTrue(result is DomainResult.Success)
        val capture = (result as DomainResult.Success).value
        val loaded = repository.findById(capture.id)
        assertEquals(capture, loaded)
        assertEquals("Ama gave me GH₵500 to pay school fees", loaded?.originalExpression?.value)
        assertEquals(instant, loaded?.capturedAt)
    }

    @Test
    fun executeRejectsBlankWithoutPersisting() {
        val repository = InMemoryCaptureRepository()
        val useCase = CaptureExpressionUseCase(repository)
        val result = useCase.execute(
            CaptureExpressionCommand(
                id = "cap-2",
                expression = "   ",
                capturedAt = instant,
            ),
        )
        assertTrue(result is DomainResult.Failure)
    }
}
