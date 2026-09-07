package com.agbofa.smartoffice.application.workflow

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.WorkflowTransitionInstant
import com.agbofa.smartoffice.domain.workflow.WorkflowId
import com.agbofa.smartoffice.domain.workflow.WorkflowProgression
import com.agbofa.smartoffice.domain.workflow.WorkflowRepository
import com.agbofa.smartoffice.domain.workflow.WorkflowStepRepository
import com.agbofa.smartoffice.domain.workflow.WorkflowStepStatus
import com.agbofa.smartoffice.domain.workflow.WorkflowStepTransition
import com.agbofa.smartoffice.domain.workflow.WorkflowStepTransitionRepository

class AdvanceWorkflowUseCase(
    private val workflows: WorkflowRepository,
    private val steps: WorkflowStepRepository,
    private val transitionStep: TransitionWorkflowStepUseCase,
    private val transitions: WorkflowStepTransitionRepository,
) {
    fun execute(
        workflowIdValue: String,
        completeTransitionId: String,
        activateTransitionId: String,
        transitionedAt: WorkflowTransitionInstant,
    ): DomainResult<WorkflowStepTransition> {
        val workflowId = when (val result = WorkflowId.of(workflowIdValue)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        if (workflows.findById(workflowId) == null) {
            return DomainResult.Failure(DomainError.InvalidState("Workflow does not exist"))
        }
        val stepList = WorkflowProgression.orderedSteps(steps.listByWorkflowId(workflowId))
        if (stepList.isEmpty()) {
            return DomainResult.Failure(DomainError.InvalidState("Workflow has no steps"))
        }
        val historyByStep = stepList.associate { step ->
            step.id to transitions.listByStepId(step.id)
        }
        if (WorkflowProgression.isComplete(stepList, historyByStep)) {
            return DomainResult.Failure(DomainError.InvalidState("Completed workflow cannot advance"))
        }
        if (WorkflowProgression.isCancelled(stepList, historyByStep)) {
            return DomainResult.Failure(DomainError.InvalidState("Cancelled workflow cannot advance"))
        }
        val active = WorkflowProgression.activeStep(stepList, historyByStep)
        if (active != null) {
            val completed = transitionStep.execute(
                transitionId = completeTransitionId,
                workflowStepIdValue = active.id.value,
                toStatus = WorkflowStepStatus.COMPLETED,
                transitionedAt = transitionedAt,
            )
            if (completed is DomainResult.Failure) return completed
            val remaining = historyByStep.toMutableMap()
            remaining[active.id] = remaining[active.id].orEmpty() +
                listOf((completed as DomainResult.Success).value)
            val next = WorkflowProgression.nextPending(stepList, remaining)
            if (next == null) return completed
            return transitionStep.execute(
                transitionId = activateTransitionId,
                workflowStepIdValue = next.id.value,
                toStatus = WorkflowStepStatus.ACTIVE,
                transitionedAt = transitionedAt,
            )
        }
        val first = WorkflowProgression.nextPending(stepList, historyByStep)
            ?: return DomainResult.Failure(DomainError.InvalidState("No pending workflow step to activate"))
        return transitionStep.execute(
            transitionId = activateTransitionId,
            workflowStepIdValue = first.id.value,
            toStatus = WorkflowStepStatus.ACTIVE,
            transitionedAt = transitionedAt,
        )
    }
}
