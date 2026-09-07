package com.agbofa.smartoffice.application.operations

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalTemporalProjection
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRepository
import com.agbofa.smartoffice.domain.operations.TemporalResolution

class EvaluateDueStatusUseCase(
    private val temporals: OperationalTemporalRepository,
) {
    fun execute(
        operationalRecordId: OperationalRecordId,
        evaluation: EvaluationInstant,
    ): DomainResult<DueStatus> {
        val current = OperationalTemporalProjection.current(
            temporals.listByOperationalRecordId(operationalRecordId),
        ) ?: return DomainResult.Failure(DomainError.InvalidState("No temporal assignment"))
        if (current.resolution != TemporalResolution.RESOLVED || current.dueInstant == null) {
            return DomainResult.Failure(
                DomainError.InvalidState("Temporal assignment is not a resolved due instant"),
            )
        }
        return DomainResult.Success(DueStatus.evaluate(current.dueInstant, evaluation))
    }
}
