package com.agbofa.smartoffice.presentation.analytics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.agbofa.smartoffice.application.analytics.GetOperationalAnalyticsUseCase
import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext

class AnalyticsViewModel(private val analytics: GetOperationalAnalyticsUseCase) : ViewModel() {
    var state by mutableStateOf(AnalyticsUiState(loading = true))
        private set

    fun refresh(context: EvaluationContext) {
        val report = analytics.execute(context)
        state = AnalyticsUiState(loading = false, empty = report.state.totalRecords == 0, report = report)
    }
}
