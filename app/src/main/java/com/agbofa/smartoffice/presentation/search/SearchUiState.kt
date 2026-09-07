package com.agbofa.smartoffice.presentation.search

import com.agbofa.smartoffice.domain.search.SearchHit

data class SearchUiState(
    val loading: Boolean = false,
    val query: String = "",
    val results: List<SearchHit> = emptyList(),
    val emptyResults: Boolean = false,
    val message: String = "",
)
