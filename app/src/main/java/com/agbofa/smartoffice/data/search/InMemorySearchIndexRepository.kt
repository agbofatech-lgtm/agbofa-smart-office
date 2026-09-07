package com.agbofa.smartoffice.data.search

import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.search.SearchIndexEntry
import com.agbofa.smartoffice.domain.search.SearchIndexRepository
import com.agbofa.smartoffice.domain.search.SearchOrdering

class InMemorySearchIndexRepository : SearchIndexRepository {
    private val entries = LinkedHashMap<String, SearchIndexEntry>()

    override fun replaceAll(incoming: List<SearchIndexEntry>): DomainResult<Int> {
        entries.clear()
        incoming.sortedWith(SearchOrdering.INDEX).forEach { entries[it.id] = it }
        return DomainResult.Success(entries.size)
    }

    override fun listAll(): List<SearchIndexEntry> = entries.values.sortedWith(SearchOrdering.INDEX)
}
