package com.agbofa.smartoffice.application.operations

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CivilTime
import com.agbofa.smartoffice.domain.foundation.time.DueInstant
import com.agbofa.smartoffice.domain.foundation.time.TemporalAssignmentInstant
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalRecordRepository
import com.agbofa.smartoffice.domain.operations.OperationalTemporalId
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRecord
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRepository
import com.agbofa.smartoffice.domain.operations.TemporalCreationBasis
import com.agbofa.smartoffice.domain.operations.TemporalResolution

class AssignOperationalTemporalUseCase(
    private val records: OperationalRecordRepository,
    private val temporals: OperationalTemporalRepository,
) {
    fun execute(
        temporalId: String,
        operationalRecordIdValue: String,
        resolution: TemporalResolution,
        assignedAt: TemporalAssignmentInstant,
        referenceExpression: String? = null,
        dueInstant: DueInstant? = null,
        civilTime: CivilTime? = null,
        basis: TemporalCreationBasis = TemporalCreationBasis.MANUAL,
        ruleVersion: String? = null,
    ): DomainResult<OperationalTemporalRecord> {
        val recordId = when (val result = OperationalRecordId.of(operationalRecordIdValue)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        if (records.findById(recordId) == null) {
            return DomainResult.Failure(DomainError.InvalidState("Operational record does not exist"))
        }
        val id = when (val result = OperationalTemporalId.of(temporalId)) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        if (temporals.findById(id) != null) {
            return DomainResult.Failure(DomainError.InvalidState("Temporal record id already exists"))
        }
        val record = when (
            val result = OperationalTemporalRecord.of(
                id = id,
                operationalRecordId = recordId,
                resolution = resolution,
                referenceExpression = referenceExpression,
                dueInstant = dueInstant,
                civilTime = civilTime,
                assignedAt = assignedAt,
                basis = basis,
                ruleVersion = ruleVersion,
            )
        ) {
            is DomainResult.Failure -> return result
            is DomainResult.Success -> result.value
        }
        return temporals.save(record)
    }
}
