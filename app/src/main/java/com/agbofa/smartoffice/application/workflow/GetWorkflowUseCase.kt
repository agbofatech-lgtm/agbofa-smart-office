package com.agbofa.smartoffice.application.workflow

import com.agbofa.smartoffice.domain.workflow.Workflow
import com.agbofa.smartoffice.domain.workflow.WorkflowId
import com.agbofa.smartoffice.domain.workflow.WorkflowRepository

class GetWorkflowUseCase(
    private val workflows: WorkflowRepository,
) {
    fun execute(id: WorkflowId): Workflow? = workflows.findById(id)
}
