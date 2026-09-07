package com.agbofa.smartoffice.domain.workflow

object WorkflowStepPolicy {
    private val allowed: Map<WorkflowStepStatus, Set<WorkflowStepStatus>> = mapOf(
        WorkflowStepStatus.PENDING to setOf(WorkflowStepStatus.ACTIVE, WorkflowStepStatus.CANCELLED),
        WorkflowStepStatus.ACTIVE to setOf(WorkflowStepStatus.COMPLETED, WorkflowStepStatus.CANCELLED),
        WorkflowStepStatus.COMPLETED to emptySet(),
        WorkflowStepStatus.CANCELLED to emptySet(),
    )

    fun permitted(from: WorkflowStepStatus, to: WorkflowStepStatus): Boolean {
        if (from == to) return false
        return allowed[from]?.contains(to) == true
    }

    fun successors(from: WorkflowStepStatus): Set<WorkflowStepStatus> = allowed[from].orEmpty()

    fun isTerminal(state: WorkflowStepStatus): Boolean = allowed[state].isNullOrEmpty()
}
