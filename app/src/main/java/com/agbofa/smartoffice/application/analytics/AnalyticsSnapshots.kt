package com.agbofa.smartoffice.application.analytics

import com.agbofa.smartoffice.application.projection.OperationalOverview
import com.agbofa.smartoffice.domain.analytics.AnalyticsRecordSnapshot

fun OperationalOverview.toAnalyticsSnapshot(): AnalyticsRecordSnapshot =
    AnalyticsRecordSnapshot(
        recordId = operationalRecord.id.value,
        state = currentState,
        hasTemporal = temporal != null,
        temporalResolution = temporal?.resolution,
        dueStatus = dueStatus,
        prerequisiteCount = prerequisites.size,
        dependentCount = dependents.size,
        hasWorkflow = workflow != null,
        workflowComplete = workflow?.isComplete == true,
        workflowHasActiveStep = workflow?.activeStep != null,
        integrityOutcome = integrity.outcome,
    )
