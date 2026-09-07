package com.agbofa.smartoffice.application.projection

import com.agbofa.smartoffice.domain.capture.Capture
import com.agbofa.smartoffice.domain.classification.Classification
import com.agbofa.smartoffice.domain.integrity.IntegrityReport
import com.agbofa.smartoffice.domain.journal.JournalEntry
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalDependency
import com.agbofa.smartoffice.domain.operations.OperationalRecord
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRecord

/**
 * Ephemeral composed read model.
 *
 * Owns no operational facts. Every field is derived from an upstream authority.
 * Safe to discard.
 */
data class OperationalOverview(
    val operationalRecord: OperationalRecord,
    val capture: Capture?,
    val journalEntry: JournalEntry?,
    val classification: Classification?,
    val currentState: OperationalState,
    val temporal: OperationalTemporalRecord?,
    val dueStatus: DueStatus?,
    val prerequisites: List<OperationalDependency>,
    val dependents: List<OperationalDependency>,
    val workflow: WorkflowOverview?,
    val integrity: IntegrityReport,
)
