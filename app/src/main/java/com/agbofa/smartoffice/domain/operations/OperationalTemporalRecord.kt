package com.agbofa.smartoffice.domain.operations

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CivilTime
import com.agbofa.smartoffice.domain.foundation.time.DueInstant
import com.agbofa.smartoffice.domain.foundation.time.TemporalAssignmentInstant

/**
 * Temporal information attached to an OperationalRecord.
 *
 * Unresolved references preserve the original phrase. They are not DueInstants.
 * Resolved dues use DueInstant. This does not create a schedule or reminder.
 */
data class OperationalTemporalRecord private constructor(
    val id: OperationalTemporalId,
    val operationalRecordId: OperationalRecordId,
    val resolution: TemporalResolution,
    val referenceExpression: String?,
    val dueInstant: DueInstant?,
    val civilTime: CivilTime?,
    val assignedAt: TemporalAssignmentInstant,
    val basis: TemporalCreationBasis,
    val ruleVersion: String?,
) {
    fun asDueInstant(): DueInstant? =
        if (resolution == TemporalResolution.RESOLVED) dueInstant else null

    companion object {
        fun of(
            id: OperationalTemporalId,
            operationalRecordId: OperationalRecordId,
            resolution: TemporalResolution,
            referenceExpression: String?,
            dueInstant: DueInstant?,
            civilTime: CivilTime?,
            assignedAt: TemporalAssignmentInstant,
            basis: TemporalCreationBasis,
            ruleVersion: String? = null,
        ): DomainResult<OperationalTemporalRecord> {
            val expression = referenceExpression?.trim()?.takeIf { it.isNotEmpty() }
            if (resolution == TemporalResolution.UNRESOLVED) {
                if (expression == null) {
                    return DomainResult.Failure(
                        DomainError.ValidationError(
                            message = "Unresolved temporal reference requires an expression",
                            path = "referenceExpression",
                        ),
                    )
                }
                if (dueInstant != null) {
                    return DomainResult.Failure(
                        DomainError.InvalidState(
                            "Unresolved temporal reference cannot carry a DueInstant",
                        ),
                    )
                }
            }
            if (resolution == TemporalResolution.RESOLVED && dueInstant == null) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "Resolved temporal record requires DueInstant",
                        path = "dueInstant",
                    ),
                )
            }
            if (basis == TemporalCreationBasis.RULE && ruleVersion.isNullOrBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "RULE temporal assignment requires ruleVersion",
                        path = "ruleVersion",
                    ),
                )
            }
            if (basis == TemporalCreationBasis.MANUAL && !ruleVersion.isNullOrBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError(
                        message = "MANUAL temporal assignment must not carry ruleVersion",
                        path = "ruleVersion",
                    ),
                )
            }
            return DomainResult.Success(
                OperationalTemporalRecord(
                    id = id,
                    operationalRecordId = operationalRecordId,
                    resolution = resolution,
                    referenceExpression = expression,
                    dueInstant = dueInstant,
                    civilTime = civilTime,
                    assignedAt = assignedAt,
                    basis = basis,
                    ruleVersion = ruleVersion,
                ),
            )
        }
    }
}
