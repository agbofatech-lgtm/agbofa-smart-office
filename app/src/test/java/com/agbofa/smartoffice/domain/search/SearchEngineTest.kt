package com.agbofa.smartoffice.domain.search

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchEngineTest {
    private fun entry(type: SearchType, id: String, content: String, at: Long) =
        SearchIndexEntry(SearchNormalization.indexId(type, id), type, id, content, at)

    @Test
    fun emptyQueryReturnsNothing() {
        val index = listOf(entry(SearchType.JOURNAL, "j1", "Pay fees", 1))
        assertTrue(SearchIndex.search(index, SearchQuery("")).isEmpty())
    }

    @Test
    fun caseAndWhitespaceNormalize() {
        val index = listOf(entry(SearchType.JOURNAL, "j1", "Pay   FEES", 2))
        val hits = SearchIndex.search(index, SearchQuery("  pay fees "))
        assertEquals(1, hits.size)
        assertEquals("j1", hits[0].entityId)
    }

    @Test
    fun insertionOrderDoesNotChangeResults() {
        val a = entry(SearchType.RECORD, "r1", "alpha", 10)
        val b = entry(SearchType.DECISION, "d1", "alpha", 20)
        val q = SearchQuery("alpha")
        assertEquals(SearchIndex.search(listOf(a, b), q), SearchIndex.search(listOf(b, a), q))
        assertEquals("d1", SearchIndex.search(listOf(a, b), q)[0].entityId)
    }

    @Test
    fun indexIdsAreDeterministic() {
        assertEquals("JOURNAL:j1", SearchNormalization.indexId(SearchType.JOURNAL, "j1"))
    }

    @Test
    fun typeFilter() {
        val index = listOf(
            entry(SearchType.JOURNAL, "j1", "fee", 1),
            entry(SearchType.RECORD, "r1", "fee", 1),
        )
        val hits = SearchIndex.search(index, SearchQuery("fee", setOf(SearchType.RECORD)))
        assertEquals(listOf("r1"), hits.map { it.entityId })
    }
}
