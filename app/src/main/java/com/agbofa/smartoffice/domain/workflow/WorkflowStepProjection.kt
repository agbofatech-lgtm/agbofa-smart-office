package com.agbofa.smartoffice.domain.workflow

object WorkflowStepProjection {
    val CHRONOLOGY = compareBy<WorkflowStepTransition> { it.transitionedAt.value }
        .thenBy { it.id.value }

    fun current(history: List<WorkflowStepTransition>): WorkflowStepStatus {
        val latest = history.maxWithOrNull(CHRONOLOGY) ?: return WorkflowStepStatus.PENDING
        return latest.toStatus
    }

    fun ordered(history: List<WorkflowStepTransition>): List<WorkflowStepTransition> =
        history.sortedWith(CHRONOLOGY)
}
