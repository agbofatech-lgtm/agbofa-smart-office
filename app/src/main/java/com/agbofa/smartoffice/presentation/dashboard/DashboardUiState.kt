package com.agbofa.smartoffice.presentation.dashboard

import com.agbofa.smartoffice.domain.analytics.OperationalAnalyticsReport

data class DashboardUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val empty: Boolean = true,
    val analytics: OperationalAnalyticsReport? = null,
    val overviewCount: Int = 0,
)
