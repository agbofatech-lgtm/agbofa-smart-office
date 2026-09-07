package com.agbofa.smartoffice.presentation.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.agbofa.smartoffice.application.analytics.GetOperationalAnalyticsUseCase
import com.agbofa.smartoffice.application.projection.GetOperationalOverviewsUseCase
import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext

class DashboardViewModel(
    private val overviews: GetOperationalOverviewsUseCase,
    private val analytics: GetOperationalAnalyticsUseCase,
) : ViewModel() {
    var state by mutableStateOf(DashboardUiState(loading = true))
        private set

    fun refresh(context: EvaluationContext) {
        val list = overviews.execute(context)
        val report = analytics.execute(context)
        state = DashboardUiState(loading = false, empty = list.isEmpty(), analytics = report, overviewCount = list.size)
    }
}
