package com.agbofa.smartoffice.application.integrity

import com.agbofa.smartoffice.domain.capture.CaptureRepository
import com.agbofa.smartoffice.domain.classification.ClassificationRepository
import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.integrity.IntegrityEvaluationInput
import com.agbofa.smartoffice.domain.integrity.IntegrityEvaluator
import com.agbofa.smartoffice.domain.integrity.IntegrityReport
import com.agbofa.smartoffice.domain.journal.JournalRepository
import com.agbofa.smartoffice.domain.operations.OperationalDependencyRepository
import com.agbofa.smartoffice.domain.operations.OperationalRecordRepository
import com.agbofa.smartoffice.domain.operations.OperationalStateRepository
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRepository
import com.agbofa.smartoffice.domain.rules.RuleRepository
import com.agbofa.smartoffice.domain.workflow.WorkflowRepository
import com.agbofa.smartoffice.domain.workflow.WorkflowStepRepository
import com.agbofa.smartoffice.domain.workflow.WorkflowStepTransitionRepository

/**
 * Assembles a read-only snapshot and evaluates integrity.
 *
 * Does not call any save method.
 */
class EvaluateIntegrityUseCase(
    private val captures: CaptureRepository,
    private val journal: JournalRepository,
    private val classifications: ClassificationRepository,
    private val records: OperationalRecordRepository,
    private val states: OperationalStateRepository,
    private val temporals: OperationalTemporalRepository,
    private val dependencies: OperationalDependencyRepository,
    private val workflows: WorkflowRepository,
    private val steps: WorkflowStepRepository,
    private val workflowTransitions: WorkflowStepTransitionRepository,
    private val rules: RuleRepository,
) {
    fun execute(context: EvaluationContext): IntegrityReport {
        val entries = journal.listAll()
        val captureSnapshot = entries.mapNotNull { captures.findById(it.captureId) }
        val classificationSnapshot = entries.flatMap { classifications.listByJournalEntryId(it.id) }
        val recordSnapshot = records.listAll()
        val stateSnapshot = recordSnapshot.flatMap { states.listByOperationalRecordId(it.id) }
        val temporalSnapshot = recordSnapshot.flatMap { temporals.listByOperationalRecordId(it.id) }
        val workflowSnapshot = recordSnapshot.mapNotNull { workflows.findByOperationalRecordId(it.id) }
        val stepSnapshot = workflowSnapshot.flatMap { steps.listByWorkflowId(it.id) }
        val transitionSnapshot = if (stepSnapshot.isEmpty()) {
            emptyList()
        } else {
            workflowTransitions.listByWorkflowStepIds(stepSnapshot.map { it.id })
        }
        val input = IntegrityEvaluationInput(
            captures = captureSnapshot,
            journalEntries = entries,
            classifications = classificationSnapshot,
            operationalRecords = recordSnapshot,
            stateTransitions = stateSnapshot,
            temporalRecords = temporalSnapshot,
            dependencies = dependencies.listAll(),
            workflows = workflowSnapshot,
            workflowSteps = stepSnapshot,
            workflowTransitions = transitionSnapshot,
            rules = rules.listAll(),
        )
        return IntegrityEvaluator.evaluate(input, context)
    }

    fun execute(input: IntegrityEvaluationInput, context: EvaluationContext): IntegrityReport =
        IntegrityEvaluator.evaluate(input, context)
}
