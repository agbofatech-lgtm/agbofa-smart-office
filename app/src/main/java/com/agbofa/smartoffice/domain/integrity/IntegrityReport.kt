package com.agbofa.smartoffice.domain.integrity

import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext

data class IntegrityReport(
    val context: EvaluationContext,
    val outcome: IntegrityOutcome,
    val findings: List<IntegrityFinding>,
    val errorCount: Int,
    val warningCount: Int,
    val infoCount: Int,
) {
    val summary: IntegritySummary
        get() = IntegritySummary(errorCount, warningCount, infoCount, findings.size)
}

data class IntegritySummary(
    val errorCount: Int,
    val warningCount: Int,
    val infoCount: Int,
    val findingCount: Int,
)
