package com.agbofa.smartoffice.presentation.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.agbofa.smartoffice.application.search.RebuildSearchIndexUseCase
import com.agbofa.smartoffice.application.search.SearchUseCase
import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.search.SearchQuery

class SearchViewModel(
    private val search: SearchUseCase,
    private val rebuild: RebuildSearchIndexUseCase,
) : ViewModel() {
    var state by mutableStateOf(SearchUiState())
        private set

    fun onQueryChange(value: String) {
        state = state.copy(query = value, emptyResults = false)
    }

    fun search() {
        val hits = search.execute(SearchQuery(state.query))
        state = state.copy(
            loading = false,
            results = hits,
            emptyResults = hits.isEmpty() && state.query.isNotBlank(),
            message = "",
        )
    }

    fun rebuild(context: EvaluationContext) {
        state = state.copy(loading = true, message = "")
        state = when (val result = rebuild.execute(context)) {
            is DomainResult.Success -> state.copy(loading = false, message = "Index rebuilt: ${result.value}")
            is DomainResult.Failure -> state.copy(loading = false, message = result.error.message)
        }
    }
}
