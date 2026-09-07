package com.agbofa.smartoffice.presentation.decision

import com.agbofa.smartoffice.application.decision.DecisionListItem

data class DecisionUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val empty: Boolean = true,
    val items: List<DecisionListItem> = emptyList(),
    val message: String = "",
)
