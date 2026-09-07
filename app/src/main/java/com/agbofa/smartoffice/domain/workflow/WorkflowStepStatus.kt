package com.agbofa.smartoffice.domain.workflow

/**
 * Lifecycle of a workflow step.
 *
 * Distinct from OperationalState. This is process-stage status,
 * not operational-record lifecycle.
 */
enum class WorkflowStepStatus {
    PENDING,
    ACTIVE,
    COMPLETED,
    CANCELLED,
}
