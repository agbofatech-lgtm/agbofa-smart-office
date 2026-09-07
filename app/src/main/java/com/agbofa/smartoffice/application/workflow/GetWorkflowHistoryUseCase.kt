package com.agbofa.smartoffice.application.workflow

import com.agbofa.smartoffice.domain.workflow.WorkflowId
import com.agbofa.smartoffice.domain.workflow.WorkflowStepProjection
import com.agbofa.smartoffice.domain.workflow.WorkflowStepRepository
import com.agbofa.smartoffice.domain.workflow.WorkflowStepTransition
import com.agbofa.smartoffice.domain.workflow.WorkflowStepTransitionRepository

class GetWorkflowHistoryUseCase(
    private val steps: WorkflowStepRepository,
    private val transitions: WorkflowStepTransitionRepository,
) {
    fun execute(workflowId: WorkflowId): List<WorkflowStepTransition> {
        val stepIds = steps.listByWorkflowId(workflowId).map { it.id }
        return WorkflowStepProjection.ordered(transitions.listByWorkflowStepIds(stepIds))
    }
}
