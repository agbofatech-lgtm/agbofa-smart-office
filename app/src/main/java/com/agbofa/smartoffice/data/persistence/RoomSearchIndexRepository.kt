package com.agbofa.smartoffice.data.persistence

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.search.SearchIndexEntry
import com.agbofa.smartoffice.domain.search.SearchIndexRepository
import com.agbofa.smartoffice.domain.search.SearchOrdering
import com.agbofa.smartoffice.domain.search.SearchType

class RoomSearchIndexRepository(
    private val database: SmartOfficeDatabase,
) : SearchIndexRepository {
    private val dao: SearchIndexDao get() = database.searchIndexDao()

    override fun replaceAll(entries: List<SearchIndexEntry>): DomainResult<Int> {
        return try {
            database.runInTransaction {
                dao.clear()
                val rows = entries.sortedWith(SearchOrdering.INDEX).map { it.toEntity() }
                if (rows.isNotEmpty()) dao.insertAll(rows)
            }
            DomainResult.Success(entries.size)
        } catch (error: Exception) {
            DomainResult.Failure(DomainError.PersistenceFailure(error.message ?: "Search rebuild failed"))
        }
    }

    override fun listAll(): List<SearchIndexEntry> =
        dao.list().mapNotNull { it.toDomain() }.sortedWith(SearchOrdering.INDEX)
}

private fun SearchIndexEntry.toEntity() = SearchIndexEntity(
    id = id,
    type = type.name,
    entityId = entityId,
    content = content,
    createdAt = createdAtEpochMillis,
)

private fun SearchIndexEntity.toDomain(): SearchIndexEntry? {
    val parsed = runCatching { SearchType.valueOf(type) }.getOrNull() ?: return null
    return SearchIndexEntry(id, parsed, entityId, content, createdAt)
}
