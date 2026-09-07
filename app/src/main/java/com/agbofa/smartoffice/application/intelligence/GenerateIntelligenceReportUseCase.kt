package com.agbofa.smartoffice.application.intelligence

import com.agbofa.smartoffice.application.decision.GetDecisionsUseCase
import com.agbofa.smartoffice.application.projection.GetOperationalOverviewsUseCase
import com.agbofa.smartoffice.application.projection.OperationalOverview
import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.intelligence.IntelligenceDecisionSlice
import com.agbofa.smartoffice.domain.intelligence.IntelligenceEvaluator
import com.agbofa.smartoffice.domain.intelligence.IntelligenceInput
import com.agbofa.smartoffice.domain.intelligence.IntelligenceReport
import com.agbofa.smartoffice.domain.intelligence.IntelligenceSubject
import com.agbofa.smartoffice.domain.operations.DueStatus

/**
 * Assembles existing projections into an ephemeral advisory report.
 * Does not save, approve, or execute.
 */
class GenerateIntelligenceReportUseCase(
    private val overviews: GetOperationalOverviewsUseCase,
    private val decisions: GetDecisionsUseCase,
) {
    fun execute(context: EvaluationContext): IntelligenceReport {
        val list = overviews.execute(context)
        val slices = decisions.execute().map {
            IntelligenceDecisionSlice(it.decision.id.value, it.status.name, it.decision.rationale)
        }
        return IntelligenceEvaluator.evaluate(
            IntelligenceInput(
                subjects = list.map { it.toSubject() },
                decisions = slices,
                findings = list.flatMap { it.integrity.findings },
                pastDueCount = list.count { it.dueStatus == DueStatus.PAST_DUE },
                context = context,
            ),
        )
    }
}

private fun OperationalOverview.toSubject(): IntelligenceSubject =
    IntelligenceSubject(
        recordId = operationalRecord.id.value,
        state = currentState,
        dueStatus = dueStatus,
        prerequisiteCount = prerequisites.size,
        hasWorkflow = workflow != null,
        workflowComplete = workflow?.isComplete == true,
        workflowHasActiveStep = workflow?.activeStep != null,
        integrityOutcome = integrity.outcome,
        integrityFindings = integrity.findings,
    )
