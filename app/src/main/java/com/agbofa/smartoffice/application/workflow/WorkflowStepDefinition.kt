package com.agbofa.smartoffice.application.workflow

data class WorkflowStepDefinition(
    val stepId: String,
    val ordinal: Int,
    val key: String,
    val label: String,
)
