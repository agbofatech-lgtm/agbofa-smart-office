package com.agbofa.smartoffice.application.search

import com.agbofa.smartoffice.application.decision.GetDecisionsUseCase
import com.agbofa.smartoffice.application.journal.GetJournalTimelineUseCase
import com.agbofa.smartoffice.application.projection.GetOperationalOverviewsUseCase
import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.search.SearchIndex
import com.agbofa.smartoffice.domain.search.SearchIndexEntry
import com.agbofa.smartoffice.domain.search.SearchIndexRepository
import com.agbofa.smartoffice.domain.search.SearchNormalization
import com.agbofa.smartoffice.domain.search.SearchType

class RebuildSearchIndexUseCase(
    private val journal: GetJournalTimelineUseCase,
    private val overviews: GetOperationalOverviewsUseCase,
    private val decisions: GetDecisionsUseCase,
    private val index: SearchIndexRepository,
) {
    fun execute(context: EvaluationContext): DomainResult<Int> {
        val entries = mutableListOf<SearchIndexEntry>()
        journal.execute().forEach { record ->
            val content = listOf(
                record.entryId.value,
                record.originalExpression.value,
                record.classificationType.name,
            ).joinToString(" ")
            entries += SearchIndexEntry(
                id = SearchNormalization.indexId(SearchType.JOURNAL, record.entryId.value),
                type = SearchType.JOURNAL,
                entityId = record.entryId.value,
                content = content,
                createdAtEpochMillis = record.admittedAt.value.toEpochMilli(),
            )
        }
        overviews.execute(context).forEach { overview ->
            val content = listOf(
                overview.operationalRecord.id.value,
                overview.operationalRecord.type.name,
                overview.currentState.name,
                overview.classification?.type?.name.orEmpty(),
                overview.workflow?.workflow?.id?.value.orEmpty(),
            ).joinToString(" ")
            entries += SearchIndexEntry(
                id = SearchNormalization.indexId(SearchType.RECORD, overview.operationalRecord.id.value),
                type = SearchType.RECORD,
                entityId = overview.operationalRecord.id.value,
                content = content,
                createdAtEpochMillis = overview.operationalRecord.createdAt.value.toEpochMilli(),
            )
        }
        decisions.execute().forEach { item ->
            val content = listOf(
                item.decision.id.value,
                item.decision.rationale,
                item.decision.actionType.name,
                item.status.name,
            ).joinToString(" ")
            entries += SearchIndexEntry(
                id = SearchNormalization.indexId(SearchType.DECISION, item.decision.id.value),
                type = SearchType.DECISION,
                entityId = item.decision.id.value,
                content = content,
                createdAtEpochMillis = item.decision.createdAt.value.toEpochMilli(),
            )
        }
        return index.replaceAll(SearchIndex.rebuildEntries(entries))
    }
}
