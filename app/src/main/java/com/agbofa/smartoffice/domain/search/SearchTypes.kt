package com.agbofa.smartoffice.domain.search

enum class SearchType {
    JOURNAL,
    RECORD,
    DECISION,
}

data class SearchIndexEntry(
    val id: String,
    val type: SearchType,
    val entityId: String,
    val content: String,
    val createdAtEpochMillis: Long,
)

data class SearchQuery(
    val term: String,
    val types: Set<SearchType> = SearchType.entries.toSet(),
)

data class SearchHit(
    val type: SearchType,
    val entityId: String,
    val content: String,
    val createdAtEpochMillis: Long,
)

object SearchNormalization {
    fun normalize(raw: String): String =
        raw.trim().lowercase(java.util.Locale.ROOT).replace(Regex("\\s+"), " ")

    fun indexId(type: SearchType, entityId: String): String = "${type.name}:$entityId"
}

object SearchOrdering {
    val INDEX = compareBy<SearchIndexEntry> { it.type.name }.thenBy { it.entityId }
    val RESULTS = compareByDescending<SearchHit> { it.createdAtEpochMillis }
        .thenBy { it.type.name }
        .thenBy { it.entityId }
}
