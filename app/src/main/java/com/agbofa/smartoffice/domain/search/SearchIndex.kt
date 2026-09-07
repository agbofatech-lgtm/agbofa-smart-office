package com.agbofa.smartoffice.domain.search

object SearchIndex {
    fun matches(entry: SearchIndexEntry, query: SearchQuery): Boolean {
        val term = SearchNormalization.normalize(query.term)
        if (term.isEmpty()) return false
        if (entry.type !in query.types) return false
        return SearchNormalization.normalize(entry.content).contains(term)
    }

    fun search(entries: List<SearchIndexEntry>, query: SearchQuery): List<SearchHit> =
        entries.filter { matches(it, query) }
            .map { SearchHit(it.type, it.entityId, it.content, it.createdAtEpochMillis) }
            .sortedWith(SearchOrdering.RESULTS)

    fun rebuildEntries(sources: List<SearchIndexEntry>): List<SearchIndexEntry> =
        sources.sortedWith(SearchOrdering.INDEX)
}
