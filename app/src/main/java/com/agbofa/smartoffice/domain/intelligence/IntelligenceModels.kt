package com.agbofa.smartoffice.domain.intelligence

import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.integrity.IntegrityFinding
import com.agbofa.smartoffice.domain.integrity.IntegrityOutcome
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalState

enum class AdvisorySeverity { LOW, MEDIUM, HIGH, CRITICAL }

enum class RecommendationType {
    REVIEW_PAST_DUE,
    INVESTIGATE_INTEGRITY,
    CONSIDER_WORKFLOW_ADVANCE,
    REVIEW_PROPOSED_DECISION,
    REVIEW_DEPENDENCY_BLOCK,
}

enum class RecommendationSource {
    TEMPORAL,
    INTEGRITY,
    WORKFLOW,
    DECISION,
    DEPENDENCY,
}

enum class AnomalyType {
    INTEGRITY_ERROR,
    PAST_DUE_CONCENTRATION,
    DEPENDENCY_BLOCK,
    WORKFLOW_INCOMPLETE_WITH_NO_ACTIVE_STEP,
}

data class IntelligenceSubject(
    val recordId: String,
    val state: OperationalState,
    val dueStatus: DueStatus?,
    val prerequisiteCount: Int,
    val hasWorkflow: Boolean,
    val workflowComplete: Boolean,
    val workflowHasActiveStep: Boolean,
    val integrityOutcome: IntegrityOutcome,
    val integrityFindings: List<IntegrityFinding>,
)

data class IntelligenceDecisionSlice(
    val decisionId: String,
    val statusName: String,
    val rationale: String,
)

data class IntelligenceInput(
    val subjects: List<IntelligenceSubject> = emptyList(),
    val decisions: List<IntelligenceDecisionSlice> = emptyList(),
    val findings: List<IntegrityFinding> = emptyList(),
    val pastDueCount: Int = 0,
    val context: EvaluationContext,
)

data class Recommendation(
    val key: String,
    val type: RecommendationType,
    val targetId: String?,
    val source: RecommendationSource,
    val reason: String,
    val severity: AdvisorySeverity,
)

data class Anomaly(
    val key: String,
    val type: AnomalyType,
    val severity: AdvisorySeverity,
    val targetId: String?,
    val description: String,
    val evidence: Map<String, String>,
)

data class PrioritizedItem(
    val targetId: String,
    val score: Int,
    val factors: Map<String, Int>,
    val explanation: String,
)

data class IntelligenceSummary(
    val recommendationCount: Int,
    val anomalyCount: Int,
    val criticalCount: Int,
    val prioritizedCount: Int,
)

data class IntelligenceReport(
    val context: EvaluationContext,
    val summary: IntelligenceSummary,
    val recommendations: List<Recommendation>,
    val anomalies: List<Anomaly>,
    val prioritized: List<PrioritizedItem>,
)
