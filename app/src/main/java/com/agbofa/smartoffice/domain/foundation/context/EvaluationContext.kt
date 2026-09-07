package com.agbofa.smartoffice.domain.foundation.context

import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant

/**
 * Explicit inputs for a deterministic domain evaluation.
 *
 * Domain decision functions receive this. They must not call Instant.now().
 */
data class EvaluationContext(
    val evaluationTime: EvaluationInstant,
)
