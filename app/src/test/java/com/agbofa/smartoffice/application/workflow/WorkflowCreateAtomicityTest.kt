package com.agbofa.smartoffice.application.workflow

import com.agbofa.smartoffice.data.operations.InMemoryOperationalRecordRepository
import com.agbofa.smartoffice.data.workflow.InMemoryWorkflowRepository
import com.agbofa.smartoffice.data.workflow.InMemoryWorkflowStepRepository
import com.agbofa.smartoffice.domain.classification.ClassificationId
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.OperationalCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.WorkflowCreationInstant
import com.agbofa.smartoffice.domain.journal.JournalEntryId
import com.agbofa.smartoffice.domain.operations.OperationalCreationBasis
import com.agbofa.smartoffice.domain.operations.OperationalRecord
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalRecordType
import com.agbofa.smartoffice.domain.workflow.WorkflowId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class WorkflowCreateAtomicityTest {
    @Test
    fun failedSecondStepDoesNotLeaveOrphanWorkflow() {
        val records = InMemoryOperationalRecordRepository()
        val workflows = InMemoryWorkflowRepository()
        val steps = InMemoryWorkflowStepRepository()
        workflows.companionSteps = steps

        val recordId = (OperationalRecordId.of("op-atom") as DomainResult.Success).value
        val journalId = (JournalEntryId.of("j-atom") as DomainResult.Success).value
        val classificationId = (ClassificationId.of("c-atom") as DomainResult.Success).value
        val record = (
            OperationalRecord.of(
                id = recordId,
                journalEntryId = journalId,
                classificationId = classificationId,
                type = OperationalRecordType.ACTION,
                createdAt = OperationalCreationInstant(Instant.parse("2026-01-01T00:00:00Z")),
                creationBasis = OperationalCreationBasis.MANUAL,
            ) as DomainResult.Success
        ).value
        records.save(record)

        val create = CreateWorkflowUseCase(records, workflows, steps)
        val result = create.execute(
            workflowId = "wf-atom",
            operationalRecordIdValue = "op-atom",
            createdAt = WorkflowCreationInstant(Instant.parse("2026-01-01T00:01:00Z")),
            stepSpecs = listOf(
                WorkflowStepDefinition("s1", 1, "one", "One"),
                WorkflowStepDefinition("s1", 2, "two", "Two"),
            ),
        )
        assertTrue(result is DomainResult.Failure)
        assertNull(
            workflows.findById((WorkflowId.of("wf-atom") as DomainResult.Success).value),
        )
        assertEquals(0, steps.listByWorkflowId((WorkflowId.of("wf-atom") as DomainResult.Success).value).size)
    }
}
