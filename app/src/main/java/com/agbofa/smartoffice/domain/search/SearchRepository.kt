package com.agbofa.smartoffice.domain.search

import com.agbofa.smartoffice.domain.foundation.result.DomainResult

interface SearchIndexRepository {
    fun replaceAll(entries: List<SearchIndexEntry>): DomainResult<Int>
    fun listAll(): List<SearchIndexEntry>
}
