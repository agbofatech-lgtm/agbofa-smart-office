package com.agbofa.smartoffice.domain.analytics

import com.agbofa.smartoffice.domain.integrity.IntegrityDomain
import com.agbofa.smartoffice.domain.integrity.IntegrityOutcome
import com.agbofa.smartoffice.domain.integrity.IntegritySeverity
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.operations.TemporalResolution

/**
 * Pure aggregation. Receives explicit snapshots. Does not query or save.
 */
object AnalyticsEvaluator {
    fun evaluate(input: AnalyticsInput): OperationalAnalyticsReport {
        val records = input.records.sortedBy { it.recordId }
        val stateCounts = linkedMapOf<OperationalState, Int>().apply {
            OperationalState.entries.forEach { state ->
                put(state, records.count { it.state == state })
            }
        }
        val dueCounts = linkedMapOf<DueStatus, Int>().apply {
            DueStatus.entries.forEach { status ->
                put(status, records.count { it.dueStatus == status })
            }
        }
        val bySeverity = linkedMapOf<IntegritySeverity, Int>().apply {
            IntegritySeverity.entries.forEach { severity ->
                put(severity, input.findings.count { it.severity == severity })
            }
        }
        val byDomain = linkedMapOf<IntegrityDomain, Int>().apply {
            IntegrityDomain.entries.forEach { domain ->
                put(domain, input.findings.count { it.domain == domain })
            }
        }
        return OperationalAnalyticsReport(
            context = input.context,
            state = StateAnalytics(
                totalRecords = records.size,
                recordsByState = stateCounts,
                openCount = stateCounts.getValue(OperationalState.OPEN),
                activeCount = stateCounts.getValue(OperationalState.ACTIVE),
                completedCount = stateCounts.getValue(OperationalState.COMPLETED),
                cancelledCount = stateCounts.getValue(OperationalState.CANCELLED),
            ),
            temporal = TemporalAnalytics(
                recordsWithTemporalAssignment = records.count { it.hasTemporal },
                resolvedTemporalCount = records.count { it.temporalResolution == TemporalResolution.RESOLVED },
                unresolvedTemporalCount = records.count { it.temporalResolution == TemporalResolution.UNRESOLVED },
                recordsWithoutTemporalAssignment = records.count { !it.hasTemporal },
                beforeDueCount = dueCounts.getValue(DueStatus.BEFORE_DUE),
                atDueCount = dueCounts.getValue(DueStatus.AT_DUE),
                pastDueCount = dueCounts.getValue(DueStatus.PAST_DUE),
            ),
            dependency = DependencyAnalytics(
                recordsWithPrerequisites = records.count { it.prerequisiteCount > 0 },
                recordsWithDependents = records.count { it.dependentCount > 0 },
                totalDependencies = records.sumOf { it.prerequisiteCount },
                rootDependencyCount = records.count { it.prerequisiteCount == 0 },
                leafDependencyCount = records.count { it.dependentCount == 0 },
            ),
            workflow = WorkflowAnalytics(
                recordsWithWorkflow = records.count { it.hasWorkflow },
                workflowCompleteCount = records.count { it.hasWorkflow && it.workflowComplete },
                workflowIncompleteCount = records.count { it.hasWorkflow && !it.workflowComplete },
                workflowWithActiveStepCount = records.count { it.hasWorkflow && it.workflowHasActiveStep },
                workflowWithoutActiveStepCount = records.count { it.hasWorkflow && !it.workflowHasActiveStep },
            ),
            integrity = IntegrityAnalytics(
                healthyCount = records.count { it.integrityOutcome == IntegrityOutcome.HEALTHY },
                warningCount = records.count { it.integrityOutcome == IntegrityOutcome.WARNINGS_PRESENT },
                errorCount = records.count { it.integrityOutcome == IntegrityOutcome.ERRORS_PRESENT },
                findingsBySeverity = bySeverity,
                findingsByDomain = byDomain,
            ),
        )
    }
}
