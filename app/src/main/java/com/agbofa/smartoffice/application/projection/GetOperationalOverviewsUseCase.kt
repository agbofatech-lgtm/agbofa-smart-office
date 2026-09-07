package com.agbofa.smartoffice.application.projection

import com.agbofa.smartoffice.application.integrity.EvaluateIntegrityUseCase
import com.agbofa.smartoffice.domain.capture.CaptureRepository
import com.agbofa.smartoffice.domain.classification.ClassificationRepository
import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.journal.JournalRepository
import com.agbofa.smartoffice.domain.operations.OperationalDependencyRepository
import com.agbofa.smartoffice.domain.operations.OperationalRecordRepository
import com.agbofa.smartoffice.domain.operations.OperationalStateRepository
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRepository
import com.agbofa.smartoffice.domain.workflow.WorkflowRepository
import com.agbofa.smartoffice.domain.workflow.WorkflowStepRepository
import com.agbofa.smartoffice.domain.workflow.WorkflowStepTransitionRepository

class GetOperationalOverviewsUseCase(
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
    fun execute(context: EvaluationContext): List<OperationalOverview> {
        val integrity = evaluateIntegrity.execute(context)
        return records.listAll()
            .sortedWith(OperationalOverviewAssembler.RECORD_ORDER)
            .map { record ->
                val journalEntry = journal.findById(record.journalEntryId)
                val capture = journalEntry?.let { captures.findById(it.captureId) }
                val classification = classifications.findById(record.classificationId)
                val workflow = workflows.findByOperationalRecordId(record.id)
                val workflowSteps = workflow?.let { steps.listByWorkflowId(it.id) }.orEmpty()
                val workflowHistory = if (workflowSteps.isEmpty()) {
                    emptyList()
                } else {
                    workflowTransitions.listByWorkflowStepIds(workflowSteps.map { it.id })
                }
                OperationalOverviewAssembler.assemble(
                    record = record,
                    capture = capture,
                    journalEntry = journalEntry,
                    classification = classification,
                    stateHistory = states.listByOperationalRecordId(record.id),
                    temporalHistory = temporals.listByOperationalRecordId(record.id),
                    prerequisites = dependencies.listByDependent(record.id),
                    dependents = dependencies.listByPrerequisite(record.id),
                    workflow = workflow,
                    workflowSteps = workflowSteps,
                    workflowTransitions = workflowHistory,
                    integrity = integrity,
                    context = context,
                )
            }
    }
}
