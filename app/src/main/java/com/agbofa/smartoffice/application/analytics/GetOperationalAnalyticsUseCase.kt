package com.agbofa.smartoffice.application.analytics

import com.agbofa.smartoffice.application.integrity.EvaluateIntegrityUseCase
import com.agbofa.smartoffice.application.projection.GetOperationalOverviewsUseCase
import com.agbofa.smartoffice.domain.analytics.AnalyticsEvaluator
import com.agbofa.smartoffice.domain.analytics.AnalyticsInput
import com.agbofa.smartoffice.domain.analytics.OperationalAnalyticsReport
import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext

/**
 * Assembles existing overviews and integrity findings, then aggregates.
 * Does not write any repository.
 */
class GetOperationalAnalyticsUseCase(
    private val overviews: GetOperationalOverviewsUseCase,
    private val integrity: EvaluateIntegrityUseCase,
) {
    fun execute(context: EvaluationContext): OperationalAnalyticsReport {
        val snapshot = overviews.execute(context)
        val report = integrity.execute(context)
        return AnalyticsEvaluator.evaluate(
            AnalyticsInput(
                records = snapshot.map { it.toAnalyticsSnapshot() },
                findings = report.findings,
                context = context,
            ),
        )
    }
}
