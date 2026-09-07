package com.agbofa.smartoffice.application.search

import com.agbofa.smartoffice.data.search.InMemorySearchIndexRepository
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.search.SearchQuery
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchRebuildTest {
    @Test
    fun replaceAllIsDeterministicAndDoesNotCreateFacts() {
        val repo = InMemorySearchIndexRepository()
        val first = listOf(
            com.agbofa.smartoffice.domain.search.SearchIndexEntry("JOURNAL:j1", com.agbofa.smartoffice.domain.search.SearchType.JOURNAL, "j1", "hello", 1),
        )
        val second = first + com.agbofa.smartoffice.domain.search.SearchIndexEntry(
            "DECISION:d1", com.agbofa.smartoffice.domain.search.SearchType.DECISION, "d1", "hello world", 2,
        )
        assertTrue(repo.replaceAll(second.reversed()) is DomainResult.Success)
        val once = repo.listAll().map { it.id }
        assertTrue(repo.replaceAll(second) is DomainResult.Success)
        assertEquals(once, repo.listAll().map { it.id })
        val hits = SearchUseCase(repo).execute(SearchQuery("HELLO"))
        assertEquals(2, hits.size)
        assertEquals(listOf("d1", "j1"), hits.map { it.entityId })
    }
}
