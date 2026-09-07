package com.agbofa.smartoffice.domain.decision

object DecisionPolicy {
    private val graph = mapOf(
        DecisionStatus.PROPOSED to setOf(
            DecisionStatus.APPROVED,
            DecisionStatus.REJECTED,
            DecisionStatus.WITHDRAWN,
        ),
        DecisionStatus.APPROVED to emptySet(),
        DecisionStatus.REJECTED to emptySet(),
        DecisionStatus.WITHDRAWN to emptySet(),
    )

    fun allows(from: DecisionStatus, to: DecisionStatus): Boolean =
        graph[from].orEmpty().contains(to)

    fun successors(from: DecisionStatus): Set<DecisionStatus> = graph[from].orEmpty()

    fun isTerminal(status: DecisionStatus): Boolean = successors(status).isEmpty()
}
