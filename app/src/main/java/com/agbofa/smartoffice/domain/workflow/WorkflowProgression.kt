package com.agbofa.smartoffice.domain.workflow

/**
 * Deterministic view of a workflow's process position.
 *
 * Steps are considered in ordinal order, then step id.
 */
object WorkflowProgression {
    val STEP_ORDER = compareBy<WorkflowStep> { it.ordinal }.thenBy { it.id.value }

    fun orderedSteps(steps: List<WorkflowStep>): List<WorkflowStep> = steps.sortedWith(STEP_ORDER)

    fun statusByStep(
        steps: List<WorkflowStep>,
        historyByStep: Map<WorkflowStepId, List<WorkflowStepTransition>>,
    ): Map<WorkflowStepId, WorkflowStepStatus> =
        steps.associate { step ->
            step.id to WorkflowStepProjection.current(historyByStep[step.id].orEmpty())
        }

    fun isComplete(
        steps: List<WorkflowStep>,
        historyByStep: Map<WorkflowStepId, List<WorkflowStepTransition>>,
    ): Boolean {
        if (steps.isEmpty()) return false
        val statuses = statusByStep(steps, historyByStep)
        return steps.all { statuses[it.id] == WorkflowStepStatus.COMPLETED }
    }

    fun isCancelled(
        steps: List<WorkflowStep>,
        historyByStep: Map<WorkflowStepId, List<WorkflowStepTransition>>,
    ): Boolean {
        val statuses = statusByStep(steps, historyByStep)
        return steps.any { statuses[it.id] == WorkflowStepStatus.CANCELLED }
    }

    fun activeStep(
        steps: List<WorkflowStep>,
        historyByStep: Map<WorkflowStepId, List<WorkflowStepTransition>>,
    ): WorkflowStep? {
        val statuses = statusByStep(steps, historyByStep)
        return orderedSteps(steps).firstOrNull { statuses[it.id] == WorkflowStepStatus.ACTIVE }
    }

    fun nextPending(
        steps: List<WorkflowStep>,
        historyByStep: Map<WorkflowStepId, List<WorkflowStepTransition>>,
    ): WorkflowStep? {
        val statuses = statusByStep(steps, historyByStep)
        return orderedSteps(steps).firstOrNull { statuses[it.id] == WorkflowStepStatus.PENDING }
    }
}
