package com.agbofa.smartoffice.application.search

import com.agbofa.smartoffice.domain.search.SearchHit
import com.agbofa.smartoffice.domain.search.SearchIndex
import com.agbofa.smartoffice.domain.search.SearchIndexRepository
import com.agbofa.smartoffice.domain.search.SearchQuery

class SearchUseCase(
    private val index: SearchIndexRepository,
) {
    fun execute(query: SearchQuery): List<SearchHit> =
        SearchIndex.search(index.listAll(), query)
}
