package com.agbofa.smartoffice.presentation.labels

import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.decision.DecisionStatus
import com.agbofa.smartoffice.domain.intelligence.AdvisorySeverity
import com.agbofa.smartoffice.domain.intelligence.AnomalyType
import com.agbofa.smartoffice.domain.intelligence.RecommendationType
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.search.SearchType

fun ClassificationType.asLabel(): String = when (this) {
    ClassificationType.UNCLASSIFIED -> "Unclassified"
    ClassificationType.INFORMATION -> "Information"
    ClassificationType.ACTION -> "Action"
    ClassificationType.FOLLOW_UP -> "Follow-up"
    ClassificationType.FINANCIAL_OBLIGATION -> "Financial obligation"
    ClassificationType.FINANCIAL_RECORD -> "Financial record"
    ClassificationType.COMMITMENT -> "Commitment"
    ClassificationType.EVENT -> "Event"
    ClassificationType.NOTE -> "Note"
}

fun OperationalState.asLabel(): String = when (this) {
    OperationalState.OPEN -> "Open"
    OperationalState.ACTIVE -> "Active"
    OperationalState.COMPLETED -> "Completed"
    OperationalState.CANCELLED -> "Cancelled"
}

fun DueStatus.asLabel(): String = when (this) {
    DueStatus.BEFORE_DUE -> "Upcoming"
    DueStatus.AT_DUE -> "Due now"
    DueStatus.PAST_DUE -> "Past due"
}

fun DecisionStatus.asLabel(): String = when (this) {
    DecisionStatus.PROPOSED -> "Needs your decision"
    DecisionStatus.APPROVED -> "Approved"
    DecisionStatus.REJECTED -> "Declined"
    DecisionStatus.WITHDRAWN -> "Withdrawn"
}

fun SearchType.asLabel(): String = when (this) {
    SearchType.JOURNAL -> "Journal"
    SearchType.RECORD -> "Record"
    SearchType.DECISION -> "Decision"
}

fun RecommendationType.asLabel(): String = when (this) {
    RecommendationType.REVIEW_PAST_DUE -> "Review past due item"
    RecommendationType.INVESTIGATE_INTEGRITY -> "Review office health"
    RecommendationType.CONSIDER_WORKFLOW_ADVANCE -> "Advance a workflow"
    RecommendationType.REVIEW_PROPOSED_DECISION -> "Decision waiting"
    RecommendationType.REVIEW_DEPENDENCY_BLOCK -> "Blocked by a prerequisite"
}

fun AnomalyType.asLabel(): String = when (this) {
    AnomalyType.INTEGRITY_ERROR -> "Health issue"
    AnomalyType.PAST_DUE_CONCENTRATION -> "Several items are past due"
    AnomalyType.DEPENDENCY_BLOCK -> "Waiting on a prerequisite"
    AnomalyType.WORKFLOW_INCOMPLETE_WITH_NO_ACTIVE_STEP -> "Workflow paused"
}

fun AdvisorySeverity.asLabel(): String = when (this) {
    AdvisorySeverity.LOW -> "Low"
    AdvisorySeverity.MEDIUM -> "Medium"
    AdvisorySeverity.HIGH -> "High"
    AdvisorySeverity.CRITICAL -> "Critical"
}
