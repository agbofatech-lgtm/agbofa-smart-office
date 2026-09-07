package com.agbofa.smartoffice.domain.operations

import com.agbofa.smartoffice.domain.classification.ClassificationId
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.OperationalCreationInstant
import com.agbofa.smartoffice.domain.journal.JournalEntryId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class OperationalRecordTest {
    private val createdAt = OperationalCreationInstant(Instant.parse("2026-09-07T08:30:00Z"))
    private val journalId = (JournalEntryId.of("jrn-1") as DomainResult.Success).value
    private val classificationId = (ClassificationId.of("cls-1") as DomainResult.Success).value

    @Test
    fun or01ValidRecordCreated() {
        val result = OperationalRecord.from(
            id = "op-1",
            journalEntryId = journalId,
            classificationId = classificationId,
            type = OperationalRecordType.FOLLOW_UP,
            createdAt = createdAt,
            creationBasis = OperationalCreationBasis.MANUAL,
        )
        assertTrue(result is DomainResult.Success)
        val record = (result as DomainResult.Success).value
        assertEquals("op-1", record.id.value)
        assertEquals(classificationId, record.classificationId)
        assertEquals(createdAt, record.createdAt)
    }

    @Test
    fun or02BlankIdRejected() {
        val result = OperationalRecordId.of("  ")
        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).error is DomainError.ValidationError)
    }

    @Test
    fun or03ExplicitTimePreserved() {
        val record = (OperationalRecord.from(
            "op-1", journalId, classificationId, OperationalRecordType.NOTE,
            createdAt, OperationalCreationBasis.MANUAL,
        ) as DomainResult.Success).value
        assertEquals(Instant.parse("2026-09-07T08:30:00Z"), record.createdAt.value)
    }

    @Test
    fun or08NoOriginalExpressionField() {
        val names = OperationalRecord::class.java.declaredFields.map { it.name }
        val forbidden = listOf(
            "originalExpression", "amount", "currency", "dueDate", "status",
            "priority", "schedule", "reminder", "workflowStage",
        )
        forbidden.forEach { token ->
            assertTrue(names.none { it.equals(token, ignoreCase = true) })
        }
    }
}
