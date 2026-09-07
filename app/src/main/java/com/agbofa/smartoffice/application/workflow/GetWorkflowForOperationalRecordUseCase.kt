package com.agbofa.smartoffice.application.workflow

import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.workflow.Workflow
import com.agbofa.smartoffice.domain.workflow.WorkflowRepository

class GetWorkflowForOperationalRecordUseCase(
    private val workflows: WorkflowRepository,
) {
    fun execute(operationalRecordId: OperationalRecordId): Workflow? =
        workflows.findByOperationalRecordId(operationalRecordId)
}
