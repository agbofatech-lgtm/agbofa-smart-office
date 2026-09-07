package com.agbofa.smartoffice.domain.workflow

import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.WorkflowCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.WorkflowTransitionInstant
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class WorkflowEngineDomainTest {
    private val recordId = (OperationalRecordId.of("op-1") as DomainResult.Success).value
    private val workflowId = (WorkflowId.of("wf-1") as DomainResult.Success).value
    private val createdAt = WorkflowCreationInstant(Instant.parse("2026-09-07T10:30:00Z"))

    @Test
    fun validWorkflowSucceeds() {
        val result = Workflow.of(workflowId, recordId, createdAt, WorkflowBasis.MANUAL)
        assertTrue(result is DomainResult.Success)
    }

    @Test
    fun manualForbidsRuleVersion() {
        assertTrue(
            Workflow.of(workflowId, recordId, createdAt, WorkflowBasis.MANUAL, "r1")
                is DomainResult.Failure,
        )
    }

    @Test
    fun blankStepKeyRejected() {
        val id = (WorkflowStepId.of("s1") as DomainResult.Success).value
        assertTrue(
            WorkflowStep.of(id, workflowId, 1, "  ", "Prepare") is DomainResult.Failure,
        )
    }

    @Test
    fun pendingToActiveSucceeds() {
        val transition = transition(WorkflowStepStatus.PENDING, WorkflowStepStatus.ACTIVE)
        assertTrue(transition is DomainResult.Success)
    }

    @Test
    fun pendingToCompletedRejected() {
        assertTrue(
            transition(WorkflowStepStatus.PENDING, WorkflowStepStatus.COMPLETED)
                is DomainResult.Failure,
        )
    }

    @Test
    fun completedIsTerminal() {
        assertTrue(
            transition(WorkflowStepStatus.COMPLETED, WorkflowStepStatus.ACTIVE)
                is DomainResult.Failure,
        )
    }

    @Test
    fun emptyHistoryProjectsPending() {
        assertEquals(WorkflowStepStatus.PENDING, WorkflowStepProjection.current(emptyList()))
    }

    @Test
    fun workflowCompleteWhenAllStepsCompleted() {
        val s1 = step("s1", 1, "prep")
        val s2 = step("s2", 2, "act")
        val t1 = (transition(WorkflowStepStatus.PENDING, WorkflowStepStatus.ACTIVE, "tr-1", "s1")
            as DomainResult.Success).value
        val t2 = (transition(WorkflowStepStatus.ACTIVE, WorkflowStepStatus.COMPLETED, "tr-2", "s1")
            as DomainResult.Success).value
        val t3 = (transition(WorkflowStepStatus.PENDING, WorkflowStepStatus.ACTIVE, "tr-3", "s2")
            as DomainResult.Success).value
        val t4 = (transition(WorkflowStepStatus.ACTIVE, WorkflowStepStatus.COMPLETED, "tr-4", "s2")
            as DomainResult.Success).value
        val history = mapOf(s1.id to listOf(t1, t2), s2.id to listOf(t3, t4))
        assertTrue(WorkflowProgression.isComplete(listOf(s1, s2), history))
        assertEquals(s1, WorkflowProgression.orderedSteps(listOf(s2, s1)).first())
    }

    @Test
    fun sameStateRejected() {
        assertFalse(WorkflowStepPolicy.permitted(WorkflowStepStatus.ACTIVE, WorkflowStepStatus.ACTIVE))
    }

    private fun step(rawId: String, ordinal: Int, key: String): WorkflowStep {
        val id = (WorkflowStepId.of(rawId) as DomainResult.Success).value
        return (WorkflowStep.of(id, workflowId, ordinal, key, key) as DomainResult.Success).value
    }

    private fun transition(
        from: WorkflowStepStatus,
        to: WorkflowStepStatus,
        rawId: String = "tr-1",
        stepRaw: String = "s1",
    ): DomainResult<WorkflowStepTransition> {
        val id = (WorkflowStepTransitionId.of(rawId) as DomainResult.Success).value
        val stepId = (WorkflowStepId.of(stepRaw) as DomainResult.Success).value
        return WorkflowStepTransition.of(
            id = id,
            workflowStepId = stepId,
            fromStatus = from,
            toStatus = to,
            transitionedAt = WorkflowTransitionInstant(Instant.parse("2026-09-07T10:40:00Z")),
            basis = WorkflowBasis.MANUAL,
        )
    }
}
