package com.agbofa.smartoffice.domain.operations

import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.OperationalDependencyCreationInstant
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class OperationalDependencyTest {
    private val at = OperationalDependencyCreationInstant(Instant.parse("2026-09-07T09:40:00Z"))
    private val a = (OperationalRecordId.of("op-a") as DomainResult.Success).value
    private val b = (OperationalRecordId.of("op-b") as DomainResult.Success).value
    private val c = (OperationalRecordId.of("op-c") as DomainResult.Success).value

    private fun edge(id: String, dependent: OperationalRecordId, prerequisite: OperationalRecordId) =
        (OperationalDependency.of(
            id = (OperationalDependencyId.of(id) as DomainResult.Success).value,
            dependentOperationalRecordId = dependent,
            prerequisiteOperationalRecordId = prerequisite,
            createdAt = at,
            basis = OperationalDependencyBasis.MANUAL,
        ) as DomainResult.Success).value

    @Test
    fun ph8d04SelfDependencyRejected() {
        val result = OperationalDependency.of(
            id = (OperationalDependencyId.of("dep-1") as DomainResult.Success).value,
            dependentOperationalRecordId = a,
            prerequisiteOperationalRecordId = a,
            createdAt = at,
            basis = OperationalDependencyBasis.MANUAL,
        )
        assertTrue(result is DomainResult.Failure)
    }

    @Test
    fun ph8d10SimpleEdgeAccepted() {
        val result = OperationalDependency.of(
            id = (OperationalDependencyId.of("dep-1") as DomainResult.Success).value,
            dependentOperationalRecordId = a,
            prerequisiteOperationalRecordId = b,
            createdAt = at,
            basis = OperationalDependencyBasis.MANUAL,
        )
        assertTrue(result is DomainResult.Success)
    }

    @Test
    fun ph8d12TwoNodeCycleRejected() {
        val existing = listOf(edge("dep-1", a, b))
        assertTrue(OperationalDependencyCycleDetector.wouldCreateCycle(existing, b, a))
        assertFalse(OperationalDependencyCycleDetector.wouldCreateCycle(existing, a, c))
    }

    @Test
    fun ph8d13ThreeNodeCycleRejected() {
        val existing = listOf(edge("dep-1", a, b), edge("dep-2", b, c))
        assertTrue(OperationalDependencyCycleDetector.wouldCreateCycle(existing, c, a))
        assertFalse(OperationalDependencyCycleDetector.wouldCreateCycle(existing, a, c))
    }
}
