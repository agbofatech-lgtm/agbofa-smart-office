package com.agbofa.smartoffice.data.workflow

import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.WorkflowCreationInstant
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.workflow.Workflow
import com.agbofa.smartoffice.domain.workflow.WorkflowBasis
import com.agbofa.smartoffice.domain.workflow.WorkflowId
import com.agbofa.smartoffice.domain.workflow.WorkflowStep
import com.agbofa.smartoffice.domain.workflow.WorkflowStepId
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InMemoryWorkflowAtomicityTest {
    private val workflows = InMemoryWorkflowRepository()
    private val steps = InMemoryWorkflowStepRepository()

    @Before
    fun attach() {
        workflows.companionSteps = steps
    }

    @Test
    fun saveWithSteps_rollsBackWhenAStepFails() {
        val existing = step("step-dup", "wf-atomic", 9, "existing")
        assertTrue(steps.save(existing) is DomainResult.Success)

        val workflow = workflow("wf-atomic", "op-atomic")
        val result = workflows.saveWithSteps(
            workflow,
            listOf(
                step("step-ok", "wf-atomic", 1, "ok"),
                step("step-dup", "wf-atomic", 2, "dup"),
            ),
        )
        assertTrue(result is DomainResult.Failure)
        assertEquals(0, workflows.listAll().size)
        assertEquals(1, steps.listByWorkflowId(id("wf-atomic")).size)
    }

    private fun id(value: String) =
        (WorkflowId.of(value) as DomainResult.Success).value

    private fun workflow(wid: String, oid: String): Workflow {
        val result = Workflow.of(
            id(wid),
            (OperationalRecordId.of(oid) as DomainResult.Success).value,
            WorkflowCreationInstant(Instant.parse("2026-04-01T00:00:00Z")),
            WorkflowBasis.MANUAL,
            null,
        )
        return (result as DomainResult.Success).value
    }

    private fun step(sid: String, wid: String, ordinal: Int, key: String): WorkflowStep {
        val result = WorkflowStep.of(
            (WorkflowStepId.of(sid) as DomainResult.Success).value,
            id(wid),
            ordinal,
            key,
            key,
        )
        return (result as DomainResult.Success).value
    }
}
