package com.agbofa.smartoffice.domain.operations

object OperationalTemporalProjection {
    val CHRONOLOGY = compareBy<OperationalTemporalRecord> { it.assignedAt.value }
        .thenBy { it.id.value }

    fun current(history: List<OperationalTemporalRecord>): OperationalTemporalRecord? =
        history.maxWithOrNull(CHRONOLOGY)

    fun ordered(history: List<OperationalTemporalRecord>): List<OperationalTemporalRecord> =
        history.sortedWith(CHRONOLOGY)
}
