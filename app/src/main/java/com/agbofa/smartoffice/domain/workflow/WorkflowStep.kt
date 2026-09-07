package com.agbofa.smartoffice.domain.workflow

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

/**
 * Process stage inside a workflow.
 *
 * Not a task: no priority, assignee, due date, reminder, or checklist.
 */
data class WorkflowStep private constructor(
    val id: WorkflowStepId,
    val workflowId: WorkflowId,
    val ordinal: Int,
    val key: String,
    val label: String,
) {
    companion object {
        fun of(
            id: WorkflowStepId,
            workflowId: WorkflowId,
            ordinal: Int,
            key: String,
            label: String,
        ): DomainResult<WorkflowStep> {
            if (ordinal < 1) {
                return DomainResult.Failure(
                    DomainError.ValidationError("Workflow step ordinal must be >= 1", "ordinal"),
                )
            }
            val trimmedKey = key.trim()
            val trimmedLabel = label.trim()
            if (trimmedKey.isEmpty()) {
                return DomainResult.Failure(
                    DomainError.ValidationError("Workflow step key must not be blank", "key"),
                )
            }
            if (trimmedLabel.isEmpty()) {
                return DomainResult.Failure(
                    DomainError.ValidationError("Workflow step label must not be blank", "label"),
                )
            }
            return DomainResult.Success(
                WorkflowStep(id, workflowId, ordinal, trimmedKey, trimmedLabel),
            )
        }
    }
}
