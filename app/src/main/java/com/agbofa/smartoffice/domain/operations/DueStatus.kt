package com.agbofa.smartoffice.domain.operations

import com.agbofa.smartoffice.domain.foundation.time.DueInstant
import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant

/**
 * Result of comparing a DueInstant to an explicit EvaluationInstant.
 *
 * Does not read the clock. Does not change OperationalState.
 * Does not create a reminder.
 */
enum class DueStatus {
    BEFORE_DUE,
    AT_DUE,
    PAST_DUE,
    ;

    companion object {
        fun evaluate(due: DueInstant, evaluation: EvaluationInstant): DueStatus {
            return when {
                evaluation.value.isBefore(due.value) -> BEFORE_DUE
                evaluation.value == due.value -> AT_DUE
                else -> PAST_DUE
            }
        }
    }
}
