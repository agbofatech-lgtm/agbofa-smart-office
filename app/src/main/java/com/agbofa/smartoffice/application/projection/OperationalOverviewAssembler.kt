package com.agbofa.smartoffice.application.projection

import com.agbofa.smartoffice.domain.capture.Capture
import com.agbofa.smartoffice.domain.classification.Classification
import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.integrity.IntegrityFinding
import com.agbofa.smartoffice.domain.integrity.IntegrityOutcome
import com.agbofa.smartoffice.domain.integrity.IntegrityReport
import com.agbofa.smartoffice.domain.integrity.IntegritySeverity
import com.agbofa.smartoffice.domain.journal.JournalEntry
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalDependency
import com.agbofa.smartoffice.domain.operations.OperationalRecord
import com.agbofa.smartoffice.domain.operations.OperationalStateProjection
import com.agbofa.smartoffice.domain.operations.OperationalStateTransition
import com.agbofa.smartoffice.domain.operations.OperationalTemporalProjection
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRecord
import com.agbofa.smartoffice.domain.operations.TemporalResolution
import com.agbofa.smartoffice.domain.workflow.Workflow
import com.agbofa.smartoffice.domain.workflow.WorkflowProgression
import com.agbofa.smartoffice.domain.workflow.WorkflowStep
import com.agbofa.smartoffice.domain.workflow.WorkflowStepProjection
import com.agbofa.smartoffice.domain.workflow.WorkflowStepTransition

object OperationalOverviewAssembler {
    val RECORD_ORDER = compareBy<OperationalRecord> { it.createdAt.value }
        .thenBy { it.id.value }

    val DEPENDENCY_ORDER = compareBy<OperationalDependency> { it.createdAt.value }
        .thenBy { it.id.value }

    fun assemble(
        record: OperationalRecord,
        capture: Capture?,
        journalEntry: JournalEntry?,
        classification: Classification?,
        stateHistory: List<OperationalStateTransition>,
        temporalHistory: List<OperationalTemporalRecord>,
        prerequisites: List<OperationalDependency>,
        dependents: List<OperationalDependency>,
        workflow: Workflow?,
        workflowSteps: List<WorkflowStep>,
        workflowTransitions: List<WorkflowStepTransition>,
        integrity: IntegrityReport,
        context: EvaluationContext,
    ): OperationalOverview {
        val currentState = OperationalStateProjection.current(stateHistory)
        val temporal = OperationalTemporalProjection.current(temporalHistory)
        val dueStatus = temporal?.asDueInstant()?.let { due ->
            DueStatus.evaluate(due, context.evaluationTime)
        }
        val workflowOverview = workflow?.let {
            projectWorkflow(it, workflowSteps, workflowTransitions)
        }
        return OperationalOverview(
            operationalRecord = record,
            capture = capture,
            journalEntry = journalEntry,
            classification = classification,
            currentState = currentState,
            temporal = temporal,
            dueStatus = dueStatus,
            prerequisites = prerequisites.sortedWith(DEPENDENCY_ORDER),
            dependents = dependents.sortedWith(DEPENDENCY_ORDER),
            workflow = workflowOverview,
            integrity = scopeIntegrity(integrity, record),
        )
    }

    fun projectWorkflow(
        workflow: Workflow,
        steps: List<WorkflowStep>,
        transitions: List<WorkflowStepTransition>,
    ): WorkflowOverview {
        val ordered = WorkflowProgression.orderedSteps(steps)
        val historyByStep = ordered.associate { step ->
            step.id to transitions.filter { it.workflowStepId == step.id }
        }
        val stepOverviews = ordered.map { step ->
            WorkflowStepOverview(
                step = step,
                status = WorkflowStepProjection.current(historyByStep[step.id].orEmpty()),
            )
        }
        val active = WorkflowProgression.activeStep(ordered, historyByStep)
        return WorkflowOverview(
            workflow = workflow,
            steps = stepOverviews,
            isComplete = WorkflowProgression.isComplete(ordered, historyByStep),
            isCancelled = WorkflowProgression.isCancelled(ordered, historyByStep),
            activeStep = active?.let { step ->
                stepOverviews.firstOrNull { it.step.id == step.id }
            },
        )
    }

    fun scopeIntegrity(report: IntegrityReport, record: OperationalRecord): IntegrityReport {
        val related = relatedIds(record, report)
        val scoped = report.findings.filter { finding ->
            finding.entityId in related || finding.entityId == record.id.value
        }
        return reportFrom(report, scoped)
    }

    private fun relatedIds(record: OperationalRecord, report: IntegrityReport): Set<String> {
        val ids = mutableSetOf(
            record.id.value,
            record.journalEntryId.value,
            record.classificationId.value,
        )
        report.findings.forEach { finding ->
            if (finding.evidence.contains(record.id.value) ||
                finding.entityId == record.id.value
            ) {
                ids.add(finding.entityId)
            }
        }
        return ids
    }

    private fun reportFrom(source: IntegrityReport, findings: List<IntegrityFinding>): IntegrityReport {
        val errors = findings.count { it.severity == IntegritySeverity.ERROR }
        val warnings = findings.count { it.severity == IntegritySeverity.WARNING }
        val infos = findings.count { it.severity == IntegritySeverity.INFO }
        val outcome = when {
            errors > 0 -> IntegrityOutcome.ERRORS_PRESENT
            warnings > 0 -> IntegrityOutcome.WARNINGS_PRESENT
            else -> IntegrityOutcome.HEALTHY
        }
        return IntegrityReport(
            context = source.context,
            outcome = outcome,
            findings = findings,
            errorCount = errors,
            warningCount = warnings,
            infoCount = infos,
        )
    }
}
