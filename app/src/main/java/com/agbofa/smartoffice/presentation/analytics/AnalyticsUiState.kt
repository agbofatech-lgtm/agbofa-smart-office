package com.agbofa.smartoffice.presentation.analytics

import com.agbofa.smartoffice.domain.analytics.OperationalAnalyticsReport

data class AnalyticsUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val empty: Boolean = true,
    val report: OperationalAnalyticsReport? = null,
)
