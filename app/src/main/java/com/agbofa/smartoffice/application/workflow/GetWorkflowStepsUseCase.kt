package com.agbofa.smartoffice.application.workflow

import com.agbofa.smartoffice.domain.workflow.WorkflowId
import com.agbofa.smartoffice.domain.workflow.WorkflowProgression
import com.agbofa.smartoffice.domain.workflow.WorkflowStep
import com.agbofa.smartoffice.domain.workflow.WorkflowStepRepository

class GetWorkflowStepsUseCase(
    private val steps: WorkflowStepRepository,
) {
    fun execute(workflowId: WorkflowId): List<WorkflowStep> =
        WorkflowProgression.orderedSteps(steps.listByWorkflowId(workflowId))
}
