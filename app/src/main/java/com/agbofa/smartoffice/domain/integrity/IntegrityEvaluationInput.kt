package com.agbofa.smartoffice.domain.integrity

import com.agbofa.smartoffice.domain.capture.Capture
import com.agbofa.smartoffice.domain.classification.Classification
import com.agbofa.smartoffice.domain.journal.JournalEntry
import com.agbofa.smartoffice.domain.operations.OperationalDependency
import com.agbofa.smartoffice.domain.operations.OperationalRecord
import com.agbofa.smartoffice.domain.operations.OperationalStateTransition
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRecord
import com.agbofa.smartoffice.domain.rules.Rule
import com.agbofa.smartoffice.domain.workflow.Workflow
import com.agbofa.smartoffice.domain.workflow.WorkflowStep
import com.agbofa.smartoffice.domain.workflow.WorkflowStepTransition

/**
 * Read-only snapshot of canonical facts.
 *
 * The evaluator must not reach into Room. Insertion order of these
 * lists must not change the report.
 */
data class IntegrityEvaluationInput(
    val captures: List<Capture> = emptyList(),
    val journalEntries: List<JournalEntry> = emptyList(),
    val classifications: List<Classification> = emptyList(),
    val operationalRecords: List<OperationalRecord> = emptyList(),
    val stateTransitions: List<OperationalStateTransition> = emptyList(),
    val temporalRecords: List<OperationalTemporalRecord> = emptyList(),
    val dependencies: List<OperationalDependency> = emptyList(),
    val workflows: List<Workflow> = emptyList(),
    val workflowSteps: List<WorkflowStep> = emptyList(),
    val workflowTransitions: List<WorkflowStepTransition> = emptyList(),
    val rules: List<Rule> = emptyList(),
)
