package com.agbofa.smartoffice.application.projection

import com.agbofa.smartoffice.application.integrity.EvaluateIntegrityUseCase
import com.agbofa.smartoffice.domain.capture.CaptureRepository
import com.agbofa.smartoffice.domain.classification.ClassificationRepository
import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.journal.JournalRepository
import com.agbofa.smartoffice.domain.operations.OperationalDependencyRepository
import com.agbofa.smartoffice.domain.operations.OperationalRecordId
import com.agbofa.smartoffice.domain.operations.OperationalRecordRepository
import com.agbofa.smartoffice.domain.operations.OperationalStateRepository
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRepository
import com.agbofa.smartoffice.domain.workflow.WorkflowRepository
import com.agbofa.smartoffice.domain.workflow.WorkflowStepRepository
import com.agbofa.smartoffice.domain.workflow.WorkflowStepTransitionRepository

/**
 * Loads canonical facts and composes an ephemeral OperationalOverview.
 * Does not write any repository.
 */
class GetOperationalOverviewUseCase(
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
    private val evaluateIntegrity: EvaluateIntegrityUseCase,
) {
    fun execute(
        operationalRecordId: OperationalRecordId,
        context: EvaluationContext,
    ): DomainResult<OperationalOverview> {
        val record = records.findById(operationalRecordId)
            ?: return DomainResult.Failure(
                DomainError.InvalidState("Operational record does not exist"),
            )
        val journalEntry = journal.findById(record.journalEntryId)
        val capture = journalEntry?.let { captures.findById(it.captureId) }
        val classification = classifications.findById(record.classificationId)
        val stateHistory = states.listByOperationalRecordId(record.id)
        val temporalHistory = temporals.listByOperationalRecordId(record.id)
        val prerequisites = dependencies.listByDependent(record.id)
        val dependents = dependencies.listByPrerequisite(record.id)
        val workflow = workflows.findByOperationalRecordId(record.id)
        val workflowSteps = workflow?.let { steps.listByWorkflowId(it.id) }.orEmpty()
        val workflowHistory = if (workflowSteps.isEmpty()) {
            emptyList()
        } else {
            workflowTransitions.listByWorkflowStepIds(workflowSteps.map { it.id })
        }
        val integrity = evaluateIntegrity.execute(context)
        return DomainResult.Success(
            OperationalOverviewAssembler.assemble(
                record = record,
                capture = capture,
                journalEntry = journalEntry,
                classification = classification,
                stateHistory = stateHistory,
                temporalHistory = temporalHistory,
                prerequisites = prerequisites,
                dependents = dependents,
                workflow = workflow,
                workflowSteps = workflowSteps,
                workflowTransitions = workflowHistory,
                integrity = integrity,
                context = context,
            ),
        )
    }
}
