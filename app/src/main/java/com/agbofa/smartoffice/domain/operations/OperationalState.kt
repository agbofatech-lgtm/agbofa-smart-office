package com.agbofa.smartoffice.domain.operations

/**
 * Lifecycle of an OperationalRecord.
 *
 * Not a task status board. Not a workflow engine.
 * Initial projected state with no history is [OPEN].
 */
enum class OperationalState {
    OPEN,
    ACTIVE,
    COMPLETED,
    CANCELLED,
}
