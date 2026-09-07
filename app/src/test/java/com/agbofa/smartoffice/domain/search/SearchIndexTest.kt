package com.agbofa.smartoffice.domain.search

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchIndexTest {
    private fun entry(type: SearchType, id: String, content: String, at: Long) =
        SearchIndexEntry(SearchNormalization.indexId(type, id), type, id, content, at)

    @Test
    fun emptyQueryMatchesNothing() {
        val hits = SearchIndex.search(
            listOf(entry(SearchType.JOURNAL, "j1", "School fees", 1)),
            SearchQuery("   "),
        )
        assertTrue(hits.isEmpty())
    }

    @Test
    fun normalizationIsLocaleRootAndCaseInsensitive() {
        val entries = listOf(entry(SearchType.JOURNAL, "j1", "School   FEES", 2))
        val hits = SearchIndex.search(entries, SearchQuery(" school fees "))
        assertEquals(1, hits.size)
        assertEquals("j1", hits[0].entityId)
    }

    @Test
    fun resultOrderIsCreatedThenTypeThenId() {
        val entries = listOf(
            entry(SearchType.RECORD, "r1", "alpha", 10),
            entry(SearchType.JOURNAL, "j1", "alpha", 10),
            entry(SearchType.DECISION, "d1", "alpha", 20),
        )
        val hits = SearchIndex.search(entries, SearchQuery("alpha"))
        assertEquals(listOf("d1", "j1", "r1"), hits.map { it.entityId })
    }

    @Test
    fun rebuildSortsDeterministicallyRegardlessOfInsertion() {
        val a = entry(SearchType.RECORD, "b", "x", 1)
        val b = entry(SearchType.JOURNAL, "a", "x", 1)
        assertEquals(
            SearchIndex.rebuildEntries(listOf(a, b)),
            SearchIndex.rebuildEntries(listOf(b, a)),
        )
    }

    @Test
    fun indexIdIsDeterministic() {
        assertEquals("DECISION:d-1", SearchNormalization.indexId(SearchType.DECISION, "d-1"))
    }

    @Test
    fun typeFilterExcludesOthers() {
        val entries = listOf(
            entry(SearchType.JOURNAL, "j1", "fee", 1),
            entry(SearchType.RECORD, "r1", "fee", 1),
        )
        val hits = SearchIndex.search(entries, SearchQuery("fee", setOf(SearchType.JOURNAL)))
        assertEquals(listOf("j1"), hits.map { it.entityId })
    }
}
