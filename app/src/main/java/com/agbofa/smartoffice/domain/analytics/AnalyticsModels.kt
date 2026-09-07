package com.agbofa.smartoffice.domain.analytics

import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.integrity.IntegrityDomain
import com.agbofa.smartoffice.domain.integrity.IntegrityFinding
import com.agbofa.smartoffice.domain.integrity.IntegrityOutcome
import com.agbofa.smartoffice.domain.integrity.IntegritySeverity
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.operations.TemporalResolution

/**
 * Minimal derived facts for aggregation.
 * Not a second OperationalOverview and not a persistence type.
 */
data class AnalyticsRecordSnapshot(
    val recordId: String,
    val state: OperationalState,
    val hasTemporal: Boolean,
    val temporalResolution: TemporalResolution?,
    val dueStatus: DueStatus?,
    val prerequisiteCount: Int,
    val dependentCount: Int,
    val hasWorkflow: Boolean,
    val workflowComplete: Boolean,
    val workflowHasActiveStep: Boolean,
    val integrityOutcome: IntegrityOutcome,
)

data class AnalyticsInput(
    val records: List<AnalyticsRecordSnapshot> = emptyList(),
    val findings: List<IntegrityFinding> = emptyList(),
    val context: EvaluationContext,
)

data class StateAnalytics(
    val totalRecords: Int,
    val recordsByState: Map<OperationalState, Int>,
    val openCount: Int,
    val activeCount: Int,
    val completedCount: Int,
    val cancelledCount: Int,
)

data class TemporalAnalytics(
    val recordsWithTemporalAssignment: Int,
    val resolvedTemporalCount: Int,
    val unresolvedTemporalCount: Int,
    val recordsWithoutTemporalAssignment: Int,
    val beforeDueCount: Int,
    val atDueCount: Int,
    val pastDueCount: Int,
)

data class DependencyAnalytics(
    val recordsWithPrerequisites: Int,
    val recordsWithDependents: Int,
    val totalDependencies: Int,
    val rootDependencyCount: Int,
    val leafDependencyCount: Int,
)

data class WorkflowAnalytics(
    val recordsWithWorkflow: Int,
    val workflowCompleteCount: Int,
    val workflowIncompleteCount: Int,
    val workflowWithActiveStepCount: Int,
    val workflowWithoutActiveStepCount: Int,
)

data class IntegrityAnalytics(
    val healthyCount: Int,
    val warningCount: Int,
    val errorCount: Int,
    val findingsBySeverity: Map<IntegritySeverity, Int>,
    val findingsByDomain: Map<IntegrityDomain, Int>,
)

data class OperationalAnalyticsReport(
    val context: EvaluationContext,
    val state: StateAnalytics,
    val temporal: TemporalAnalytics,
    val dependency: DependencyAnalytics,
    val workflow: WorkflowAnalytics,
    val integrity: IntegrityAnalytics,
)
