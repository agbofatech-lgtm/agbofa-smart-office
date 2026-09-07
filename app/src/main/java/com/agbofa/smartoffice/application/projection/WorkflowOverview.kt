package com.agbofa.smartoffice.application.projection

import com.agbofa.smartoffice.domain.workflow.Workflow
import com.agbofa.smartoffice.domain.workflow.WorkflowStep
import com.agbofa.smartoffice.domain.workflow.WorkflowStepStatus

data class WorkflowStepOverview(
    val step: WorkflowStep,
    val status: WorkflowStepStatus,
)

data class WorkflowOverview(
    val workflow: Workflow,
    val steps: List<WorkflowStepOverview>,
    val isComplete: Boolean,
    val isCancelled: Boolean,
    val activeStep: WorkflowStepOverview?,
)
