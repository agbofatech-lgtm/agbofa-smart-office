package com.agbofa.smartoffice.presentation.theme

import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.decision.DecisionStatus
import com.agbofa.smartoffice.domain.intelligence.AnomalyType
import com.agbofa.smartoffice.domain.intelligence.RecommendationType
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.search.SearchType

internal fun ClassificationType.toLabel(): String = when (this) {
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

internal fun OperationalState.toLabel(): String = when (this) {
    OperationalState.OPEN -> "Open"
    OperationalState.ACTIVE -> "Active"
    OperationalState.COMPLETED -> "Completed"
    OperationalState.CANCELLED -> "Cancelled"
}

internal fun DueStatus.toLabel(): String = when (this) {
    DueStatus.BEFORE_DUE -> "Upcoming"
    DueStatus.AT_DUE -> "Due now"
    DueStatus.PAST_DUE -> "Past due"
}

internal fun DecisionStatus.toLabel(): String = name.lowercase().replaceFirstChar { it.titlecase() }

internal fun SearchType.toLabel(): String = when (this) {
    SearchType.JOURNAL -> "Journal"
    SearchType.RECORD -> "Record"
    SearchType.DECISION -> "Decision"
}

internal fun RecommendationType.toLabel(): String = when (this) {
    RecommendationType.REVIEW_PAST_DUE -> "Review past due"
    RecommendationType.INVESTIGATE_INTEGRITY -> "Investigate integrity"
    RecommendationType.CONSIDER_WORKFLOW_ADVANCE -> "Consider workflow advance"
    RecommendationType.REVIEW_PROPOSED_DECISION -> "Review proposed decision"
    RecommendationType.REVIEW_DEPENDENCY_BLOCK -> "Review blocked dependency"
}

internal fun AnomalyType.toLabel(): String = when (this) {
    AnomalyType.INTEGRITY_ERROR -> "Integrity concern"
    AnomalyType.PAST_DUE_CONCENTRATION -> "Past-due concentration"
    AnomalyType.DEPENDENCY_BLOCK -> "Blocked dependency"
    AnomalyType.WORKFLOW_INCOMPLETE_WITH_NO_ACTIVE_STEP -> "Workflow idle"
}
