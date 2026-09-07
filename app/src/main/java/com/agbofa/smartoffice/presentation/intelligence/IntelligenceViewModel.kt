package com.agbofa.smartoffice.presentation.intelligence

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.agbofa.smartoffice.application.intelligence.GenerateIntelligenceReportUseCase
import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext

class IntelligenceViewModel(
    private val generate: GenerateIntelligenceReportUseCase,
) : ViewModel() {
    var state by mutableStateOf(IntelligenceUiState(loading = true))
        private set

    fun refresh(context: EvaluationContext) {
        val report = generate.execute(context)
        state = IntelligenceUiState(
            loading = false,
            empty = report.summary.recommendationCount == 0 &&
                report.summary.anomalyCount == 0 &&
                report.summary.prioritizedCount == 0,
            report = report,
        )
    }
}
