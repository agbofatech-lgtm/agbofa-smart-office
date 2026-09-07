package com.agbofa.smartoffice.domain.operations

import com.agbofa.smartoffice.domain.foundation.result.DomainResult

/**
 * Persistence port for immutable operational state transitions.
 *
 * Implementations must not rewrite Capture, Journal, Classification,
 * or OperationalRecord provenance.
 */
interface OperationalStateRepository {
    fun save(transition: OperationalStateTransition): DomainResult<OperationalStateTransition>
    fun findById(id: OperationalStateTransitionId): OperationalStateTransition?
    fun listByOperationalRecordId(operationalRecordId: OperationalRecordId): List<OperationalStateTransition>
}
