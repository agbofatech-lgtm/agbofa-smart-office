package com.agbofa.smartoffice.domain.intelligence

import com.agbofa.smartoffice.domain.integrity.IntegrityOutcome
import com.agbofa.smartoffice.domain.integrity.IntegritySeverity
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalState

/**
 * Pure advisory derivation. Does not query or save.
 *
 * Overdue-concentration heuristic: pastDueCount >= 3 (documented, not canonical).
 */
object IntelligenceEvaluator {
    const val PAST_DUE_CONCENTRATION_THRESHOLD = 3

    private val recommendationOrder = compareByDescending<Recommendation> { it.severity }
        .thenBy { it.type.name }
        .thenBy { it.targetId.orEmpty() }
        .thenBy { it.key }
    private val anomalyOrder = compareByDescending<Anomaly> { it.severity }
        .thenBy { it.type.name }
        .thenBy { it.targetId.orEmpty() }
        .thenBy { it.key }
    private val priorityOrder = compareByDescending<PrioritizedItem> { it.score }
        .thenBy { it.targetId }

    fun evaluate(input: IntelligenceInput): IntelligenceReport {
        val subjects = input.subjects.sortedBy { it.recordId }
        val recommendations = mutableListOf<Recommendation>()
        val anomalies = mutableListOf<Anomaly>()
        val prioritized = mutableListOf<PrioritizedItem>()

        subjects.forEach { subject ->
            if (subject.dueStatus == DueStatus.PAST_DUE) {
                recommendations += Recommendation(
                    key = "${RecommendationType.REVIEW_PAST_DUE}:${subject.recordId}",
                    type = RecommendationType.REVIEW_PAST_DUE,
                    targetId = subject.recordId,
                    source = RecommendationSource.TEMPORAL,
                    reason = "Record is PAST_DUE under the supplied evaluation instant",
                    severity = AdvisorySeverity.HIGH,
                )
            }
            if (subject.integrityOutcome == IntegrityOutcome.ERRORS_PRESENT ||
                subject.integrityFindings.any { it.severity == IntegritySeverity.ERROR }
            ) {
                recommendations += Recommendation(
                    key = "${RecommendationType.INVESTIGATE_INTEGRITY}:${subject.recordId}",
                    type = RecommendationType.INVESTIGATE_INTEGRITY,
                    targetId = subject.recordId,
                    source = RecommendationSource.INTEGRITY,
                    reason = "Integrity evaluator reported errors on this record",
                    severity = AdvisorySeverity.CRITICAL,
                )
                anomalies += Anomaly(
                    key = "${AnomalyType.INTEGRITY_ERROR}:${subject.recordId}",
                    type = AnomalyType.INTEGRITY_ERROR,
                    severity = AdvisorySeverity.CRITICAL,
                    targetId = subject.recordId,
                    description = "Integrity errors present",
                    evidence = mapOf(
                        "outcome" to subject.integrityOutcome.name,
                        "errors" to subject.integrityFindings.count { it.severity == IntegritySeverity.ERROR }.toString(),
                    ),
                )
            }
            if (subject.hasWorkflow && !subject.workflowComplete) {
                recommendations += Recommendation(
                    key = "${RecommendationType.CONSIDER_WORKFLOW_ADVANCE}:${subject.recordId}",
                    type = RecommendationType.CONSIDER_WORKFLOW_ADVANCE,
                    targetId = subject.recordId,
                    source = RecommendationSource.WORKFLOW,
                    reason = "Workflow is incomplete; human may consider authorized advancement",
                    severity = AdvisorySeverity.MEDIUM,
                )
                if (!subject.workflowHasActiveStep) {
                    anomalies += Anomaly(
                        key = "${AnomalyType.WORKFLOW_INCOMPLETE_WITH_NO_ACTIVE_STEP}:${subject.recordId}",
                        type = AnomalyType.WORKFLOW_INCOMPLETE_WITH_NO_ACTIVE_STEP,
                        severity = AdvisorySeverity.MEDIUM,
                        targetId = subject.recordId,
                        description = "Incomplete workflow has no active step",
                        evidence = mapOf("workflowComplete" to "false", "activeStep" to "none"),
                    )
                }
            }
            if (subject.prerequisiteCount > 0 &&
                subject.state != OperationalState.COMPLETED &&
                subject.state != OperationalState.CANCELLED
            ) {
                recommendations += Recommendation(
                    key = "${RecommendationType.REVIEW_DEPENDENCY_BLOCK}:${subject.recordId}",
                    type = RecommendationType.REVIEW_DEPENDENCY_BLOCK,
                    targetId = subject.recordId,
                    source = RecommendationSource.DEPENDENCY,
                    reason = "Record has prerequisites; treat blockage as dependency evidence, not a new state",
                    severity = AdvisorySeverity.MEDIUM,
                )
                anomalies += Anomaly(
                    key = "${AnomalyType.DEPENDENCY_BLOCK}:${subject.recordId}",
                    type = AnomalyType.DEPENDENCY_BLOCK,
                    severity = AdvisorySeverity.MEDIUM,
                    targetId = subject.recordId,
                    description = "Open/active record has prerequisites",
                    evidence = mapOf(
                        "prerequisites" to subject.prerequisiteCount.toString(),
                        "state" to subject.state.name,
                    ),
                )
            }
            prioritized += score(subject)
        }

        input.decisions.sortedBy { it.decisionId }.forEach { decision ->
            if (decision.statusName == "PROPOSED") {
                recommendations += Recommendation(
                    key = "${RecommendationType.REVIEW_PROPOSED_DECISION}:${decision.decisionId}",
                    type = RecommendationType.REVIEW_PROPOSED_DECISION,
                    targetId = decision.decisionId,
                    source = RecommendationSource.DECISION,
                    reason = "Decision is PROPOSED and awaits explicit human authorization",
                    severity = AdvisorySeverity.MEDIUM,
                )
            }
        }

        if (input.pastDueCount >= PAST_DUE_CONCENTRATION_THRESHOLD) {
            anomalies += Anomaly(
                key = "${AnomalyType.PAST_DUE_CONCENTRATION}:global",
                type = AnomalyType.PAST_DUE_CONCENTRATION,
                severity = AdvisorySeverity.HIGH,
                targetId = null,
                description = "Advisory heuristic: past-due count meets threshold $PAST_DUE_CONCENTRATION_THRESHOLD",
                evidence = mapOf(
                    "pastDueCount" to input.pastDueCount.toString(),
                    "threshold" to PAST_DUE_CONCENTRATION_THRESHOLD.toString(),
                    "canonical" to "false",
                ),
            )
        }

        val recs = recommendations.sortedWith(recommendationOrder)
        val anoms = anomalies.sortedWith(anomalyOrder)
        val prios = prioritized.filter { it.score > 0 }.sortedWith(priorityOrder)
        return IntelligenceReport(
            context = input.context,
            summary = IntelligenceSummary(
                recommendationCount = recs.size,
                anomalyCount = anoms.size,
                criticalCount = recs.count { it.severity == AdvisorySeverity.CRITICAL } +
                    anoms.count { it.severity == AdvisorySeverity.CRITICAL },
                prioritizedCount = prios.size,
            ),
            recommendations = recs,
            anomalies = anoms,
            prioritized = prios,
        )
    }

    private fun score(subject: IntelligenceSubject): PrioritizedItem {
        val factors = linkedMapOf<String, Int>()
        if (subject.dueStatus == DueStatus.PAST_DUE) factors["pastDue"] = 100
        if (subject.dueStatus == DueStatus.AT_DUE) factors["atDue"] = 60
        if (subject.integrityOutcome == IntegrityOutcome.ERRORS_PRESENT) factors["integrityError"] = 80
        if (subject.integrityOutcome == IntegrityOutcome.WARNINGS_PRESENT) factors["integrityWarning"] = 40
        if (subject.state == OperationalState.ACTIVE) factors["active"] = 20
        if (subject.state == OperationalState.OPEN) factors["open"] = 10
        if (subject.prerequisiteCount > 0) factors["prerequisites"] = 30
        if (subject.hasWorkflow && !subject.workflowComplete) factors["incompleteWorkflow"] = 15
        val score = factors.values.sum()
        return PrioritizedItem(
            targetId = subject.recordId,
            score = score,
            factors = factors,
            explanation = if (factors.isEmpty()) {
                "No advisory factors"
            } else {
                factors.entries.joinToString { "${it.key}=${it.value}" }
            },
        )
    }
}
