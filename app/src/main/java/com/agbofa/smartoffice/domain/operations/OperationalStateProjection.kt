package com.agbofa.smartoffice.domain.operations

/**
 * Deterministic current-state projection.
 *
 * No history → OPEN.
 * Otherwise latest toState ordered by transitionedAt, then transition id.
 */
object OperationalStateProjection {
    val CHRONOLOGY = compareBy<OperationalStateTransition> { it.transitionedAt.value }
        .thenBy { it.id.value }

    fun current(history: List<OperationalStateTransition>): OperationalState {
        val latest = history.maxWithOrNull(CHRONOLOGY) ?: return OperationalState.OPEN
        return latest.toState
    }

    fun ordered(history: List<OperationalStateTransition>): List<OperationalStateTransition> =
        history.sortedWith(CHRONOLOGY)
}
