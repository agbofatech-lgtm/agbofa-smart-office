package com.agbofa.smartoffice.domain.decision

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.DecisionCreationInstant

/**
 * Immutable human-intent record.
 * Not operational state, not a workflow step, not a task.
 */
data class Decision private constructor(
    val id: DecisionId,
    val subject: DecisionSubject,
    val actionType: AuthorizedActionType,
    val rationale: String,
    val createdAt: DecisionCreationInstant,
    val basis: DecisionBasis,
) {
    companion object {
        fun of(
            id: DecisionId,
            subject: DecisionSubject,
            actionType: AuthorizedActionType,
            rationale: String,
            createdAt: DecisionCreationInstant,
            basis: DecisionBasis = DecisionBasis.MANUAL,
        ): DomainResult<Decision> {
            if (rationale.isBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError("Decision rationale must not be blank", "rationale"),
                )
            }
            if (subject.targetId.isBlank()) {
                return DomainResult.Failure(
                    DomainError.ValidationError("Decision subject target must not be blank", "subject"),
                )
            }
            val consistent = when (actionType) {
                AuthorizedActionType.TRANSITION_OPERATIONAL_STATE ->
                    subject.kind == DecisionSubjectKind.OPERATIONAL_RECORD
                AuthorizedActionType.ADVANCE_WORKFLOW ->
                    subject.kind == DecisionSubjectKind.WORKFLOW
            }
            if (!consistent) {
                return DomainResult.Failure(
                    DomainError.InvalidState("Decision subject kind does not match action type"),
                )
            }
            return DomainResult.Success(
                Decision(id, subject, actionType, rationale.trim(), createdAt, basis),
            )
        }
    }
}
