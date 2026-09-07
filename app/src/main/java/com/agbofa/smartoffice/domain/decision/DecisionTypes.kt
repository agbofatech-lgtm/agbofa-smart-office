package com.agbofa.smartoffice.domain.decision

import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.workflow.WorkflowId

enum class DecisionStatus {
    PROPOSED,
    APPROVED,
    REJECTED,
    WITHDRAWN,
}

enum class DecisionBasis {
    MANUAL,
}

enum class DecisionSubjectKind {
    OPERATIONAL_RECORD,
    WORKFLOW,
}

data class DecisionSubject(
    val kind: DecisionSubjectKind,
    val targetId: String,
) {
    companion object {
        fun operationalRecord(id: OperationalRecordId) =
            DecisionSubject(DecisionSubjectKind.OPERATIONAL_RECORD, id.value)

        fun workflow(id: WorkflowId) =
            DecisionSubject(DecisionSubjectKind.WORKFLOW, id.value)
    }
}

enum class AuthorizedActionType {
    TRANSITION_OPERATIONAL_STATE,
    ADVANCE_WORKFLOW,
}

data class TransitionStateParameters(
    val operationalRecordId: OperationalRecordId,
    val toState: OperationalState,
)

data class AdvanceWorkflowParameters(
    val workflowId: WorkflowId,
    val completeTransitionId: String,
    val activateTransitionId: String,
)
