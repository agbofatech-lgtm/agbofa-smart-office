package com.agbofa.smartoffice.domain.operations

/**
 * Deterministic allowed transitions.
 *
 * Compose must not own this graph.
 */
object OperationalStatePolicy {
    private val allowed: Map<OperationalState, Set<OperationalState>> = mapOf(
        OperationalState.OPEN to setOf(OperationalState.ACTIVE, OperationalState.CANCELLED),
        OperationalState.ACTIVE to setOf(OperationalState.COMPLETED, OperationalState.CANCELLED),
        OperationalState.COMPLETED to emptySet(),
        OperationalState.CANCELLED to emptySet(),
    )

    fun permitted(from: OperationalState, to: OperationalState): Boolean {
        if (from == to) return false
        return allowed[from]?.contains(to) == true
    }

    fun successors(from: OperationalState): Set<OperationalState> =
        allowed[from].orEmpty()

    fun isTerminal(state: OperationalState): Boolean = allowed[state].isNullOrEmpty()
}
