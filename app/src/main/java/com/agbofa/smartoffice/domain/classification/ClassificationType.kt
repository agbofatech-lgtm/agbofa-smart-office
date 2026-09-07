package com.agbofa.smartoffice.domain.classification

/**
 * Constrained operational vocabulary.
 *
 * UNCLASSIFIED is a state of absence, not a stored row.
 * These labels do not create tasks, payments, or schedules.
 */
enum class ClassificationType {
    UNCLASSIFIED,
    INFORMATION,
    ACTION,
    FOLLOW_UP,
    FINANCIAL_OBLIGATION,
    FINANCIAL_RECORD,
    COMMITMENT,
    EVENT,
    NOTE,
}
