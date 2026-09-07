package com.agbofa.smartoffice.presentation.intelligence

import com.agbofa.smartoffice.domain.intelligence.IntelligenceReport

data class IntelligenceUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val empty: Boolean = true,
    val report: IntelligenceReport? = null,
)
