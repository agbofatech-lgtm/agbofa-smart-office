package com.agbofa.smartoffice.domain.operations

/**
 * Deterministic directed-cycle detection.
 *
 * Neighbors are visited in prerequisiteId order, then dependency id.
 * Complexity: O(V + E) over the reachable subgraph from the proposed dependent.
 */
object OperationalDependencyCycleDetector {
    fun wouldCreateCycle(
        existing: List<OperationalDependency>,
        dependent: OperationalRecordId,
        prerequisite: OperationalRecordId,
    ): Boolean {
        if (dependent == prerequisite) return true
        val adjacency = LinkedHashMap<String, MutableList<String>>()
        existing.sortedWith(
            compareBy<OperationalDependency> { it.dependentOperationalRecordId.value }
                .thenBy { it.prerequisiteOperationalRecordId.value }
                .thenBy { it.id.value },
        ).forEach { edge ->
            adjacency
                .getOrPut(edge.dependentOperationalRecordId.value) { mutableListOf() }
                .add(edge.prerequisiteOperationalRecordId.value)
        }
        adjacency.getOrPut(dependent.value) { mutableListOf() }.add(prerequisite.value)
        adjacency.values.forEach { neighbors -> neighbors.sort() }

        val visiting = HashSet<String>()
        val visited = HashSet<String>()

        fun dfs(node: String): Boolean {
            if (node in visiting) return true
            if (node in visited) return false
            visiting.add(node)
            val neighbors = adjacency[node].orEmpty()
            for (next in neighbors) {
                if (dfs(next)) return true
            }
            visiting.remove(node)
            visited.add(node)
            return false
        }

        return dfs(dependent.value)
    }
}
