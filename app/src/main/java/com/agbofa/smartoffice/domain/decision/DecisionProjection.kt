package com.agbofa.smartoffice.domain.decision

object DecisionProjection {
    val CHRONOLOGY = compareBy<DecisionTransition> { it.transitionedAt.value }
        .thenBy { it.id.value }

    fun current(history: List<DecisionTransition>): DecisionStatus {
        val latest = history.maxWithOrNull(CHRONOLOGY) ?: return DecisionStatus.PROPOSED
        return latest.toStatus
    }

    fun ordered(history: List<DecisionTransition>): List<DecisionTransition> =
        history.sortedWith(CHRONOLOGY)
}
