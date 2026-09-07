package com.agbofa.smartoffice.domain.integrity

import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.operations.OperationalDependencyCycleDetector
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.operations.OperationalStatePolicy
import com.agbofa.smartoffice.domain.operations.TemporalResolution
import com.agbofa.smartoffice.domain.workflow.WorkflowStepPolicy
import com.agbofa.smartoffice.domain.workflow.WorkflowStepProjection
import com.agbofa.smartoffice.domain.workflow.WorkflowStepStatus

object IntegrityEvaluator {
    private val SEVERITY_ORDER = mapOf(
        IntegritySeverity.ERROR to 0,
        IntegritySeverity.WARNING to 1,
        IntegritySeverity.INFO to 2,
    )

    private val FINDING_ORDER = compareBy<IntegrityFinding> { SEVERITY_ORDER.getValue(it.severity) }
        .thenBy { it.code.name }
        .thenBy { it.domain.name }
        .thenBy { it.entityId }
        .thenBy { it.findingKey }

    fun evaluate(input: IntegrityEvaluationInput, context: EvaluationContext): IntegrityReport {
        val findings = mutableListOf<IntegrityFinding>()
        checkJournal(input, findings)
        checkClassification(input, findings)
        checkOperationalRecords(input, findings)
        checkState(input, findings)
        checkTemporal(input, findings)
        checkDependencies(input, findings)
        checkWorkflow(input, findings)
        checkRules(input, findings)
        val ordered = findings.sortedWith(FINDING_ORDER)
        val errors = ordered.count { it.severity == IntegritySeverity.ERROR }
        val warnings = ordered.count { it.severity == IntegritySeverity.WARNING }
        val infos = ordered.count { it.severity == IntegritySeverity.INFO }
        val outcome = when {
            errors > 0 -> IntegrityOutcome.ERRORS_PRESENT
            warnings > 0 -> IntegrityOutcome.WARNINGS_PRESENT
            else -> IntegrityOutcome.HEALTHY
        }
        return IntegrityReport(
            context = context,
            outcome = outcome,
            findings = ordered,
            errorCount = errors,
            warningCount = warnings,
            infoCount = infos,
        )
    }

    private fun add(
        findings: MutableList<IntegrityFinding>,
        code: IntegrityCode,
        domain: IntegrityDomain,
        entityId: String,
        description: String,
        evidence: String,
        severity: IntegritySeverity = IntegritySeverity.ERROR,
    ) {
        findings += IntegrityFinding(code, severity, domain, entityId, description, evidence)
    }

    private fun checkJournal(input: IntegrityEvaluationInput, findings: MutableList<IntegrityFinding>) {
        val captureIds = input.captures.map { it.id }.toSet()
        val seenCapture = HashSet<String>()
        for (entry in input.journalEntries) {
            if (entry.captureId !in captureIds) {
                add(
                    findings, IntegrityCode.MISSING_CAPTURE_REFERENCE, IntegrityDomain.JOURNAL,
                    entry.id.value, "Journal entry references a missing capture",
                    entry.captureId.value,
                )
            }
            if (!seenCapture.add(entry.captureId.value)) {
                add(
                    findings, IntegrityCode.DUPLICATE_JOURNAL_CAPTURE, IntegrityDomain.JOURNAL,
                    entry.id.value, "Capture admitted more than once",
                    entry.captureId.value,
                )
            }
        }
    }

    private fun checkClassification(input: IntegrityEvaluationInput, findings: MutableList<IntegrityFinding>) {
        val journalIds = input.journalEntries.map { it.id }.toSet()
        for (classification in input.classifications) {
            if (classification.journalEntryId !in journalIds) {
                add(
                    findings, IntegrityCode.MISSING_JOURNAL_REFERENCE, IntegrityDomain.CLASSIFICATION,
                    classification.id.value, "Classification references a missing journal entry",
                    classification.journalEntryId.value,
                )
            }
            if (classification.basis.name == "RULE" && classification.ruleVersion.isNullOrBlank()) {
                add(
                    findings, IntegrityCode.INVALID_RULE_PROVENANCE, IntegrityDomain.CLASSIFICATION,
                    classification.id.value, "RULE classification missing ruleVersion",
                    classification.id.value,
                )
            }
        }
    }

    private fun checkOperationalRecords(input: IntegrityEvaluationInput, findings: MutableList<IntegrityFinding>) {
        val journalIds = input.journalEntries.map { it.id }.toSet()
        val classificationIds = input.classifications.map { it.id }.toSet()
        val seenJournal = HashSet<String>()
        for (record in input.operationalRecords) {
            if (record.journalEntryId !in journalIds) {
                add(
                    findings, IntegrityCode.MISSING_JOURNAL_REFERENCE, IntegrityDomain.OPERATIONAL_RECORD,
                    record.id.value, "Operational record references a missing journal entry",
                    record.journalEntryId.value,
                )
            }
            if (record.classificationId !in classificationIds) {
                add(
                    findings, IntegrityCode.MISSING_CLASSIFICATION_REFERENCE, IntegrityDomain.OPERATIONAL_RECORD,
                    record.id.value, "Operational record pins a missing classification",
                    record.classificationId.value,
                )
            }
            if (!seenJournal.add(record.journalEntryId.value)) {
                add(
                    findings, IntegrityCode.DUPLICATE_OPERATIONAL_RECORD_FOR_JOURNAL, IntegrityDomain.OPERATIONAL_RECORD,
                    record.id.value, "More than one operational record for a journal entry",
                    record.journalEntryId.value,
                )
            }
            if (record.creationBasis.name == "RULE" && record.ruleVersion.isNullOrBlank()) {
                add(
                    findings, IntegrityCode.INVALID_RULE_PROVENANCE, IntegrityDomain.OPERATIONAL_RECORD,
                    record.id.value, "RULE operational record missing ruleVersion",
                    record.id.value,
                )
            }
        }
    }

    private fun checkState(input: IntegrityEvaluationInput, findings: MutableList<IntegrityFinding>) {
        val recordIds = input.operationalRecords.map { it.id }.toSet()
        val seenIds = HashSet<String>()
        val byRecord = input.stateTransitions.groupBy { it.operationalRecordId }
        for (transition in input.stateTransitions) {
            if (transition.operationalRecordId !in recordIds) {
                add(
                    findings, IntegrityCode.MISSING_OPERATIONAL_RECORD_REFERENCE, IntegrityDomain.OPERATIONAL_STATE,
                    transition.id.value, "State transition references a missing operational record",
                    transition.operationalRecordId.value,
                )
            }
            if (!seenIds.add(transition.id.value)) {
                add(
                    findings, IntegrityCode.DUPLICATE_STATE_TRANSITION_ID, IntegrityDomain.OPERATIONAL_STATE,
                    transition.id.value, "Duplicate state transition id",
                    transition.id.value,
                )
            }
            if (!OperationalStatePolicy.permitted(transition.fromState, transition.toState)) {
                add(
                    findings, IntegrityCode.INVALID_STATE_TRANSITION, IntegrityDomain.OPERATIONAL_STATE,
                    transition.id.value,
                    "Impossible state transition ${transition.fromState} → ${transition.toState}",
                    "${transition.fromState}:${transition.toState}",
                )
            }
        }
        for ((recordId, history) in byRecord) {
            val ordered = history.sortedWith(
                compareBy({ it.transitionedAt.value }, { it.id.value }),
            )
            var current = OperationalState.OPEN
            for (transition in ordered) {
                if (transition.fromState != current) {
                    add(
                        findings, IntegrityCode.INVALID_STATE_TRANSITION, IntegrityDomain.OPERATIONAL_STATE,
                        transition.id.value,
                        "Transition source does not match projected state $current",
                        "${transition.fromState}:$current",
                    )
                }
                current = transition.toState
            }
        }
    }

    private fun checkTemporal(input: IntegrityEvaluationInput, findings: MutableList<IntegrityFinding>) {
        val recordIds = input.operationalRecords.map { it.id }.toSet()
        for (temporal in input.temporalRecords) {
            if (temporal.operationalRecordId !in recordIds) {
                add(
                    findings, IntegrityCode.MISSING_OPERATIONAL_RECORD_REFERENCE, IntegrityDomain.TEMPORAL,
                    temporal.id.value, "Temporal record references a missing operational record",
                    temporal.operationalRecordId.value,
                )
            }
            val unresolvedBad = temporal.resolution == TemporalResolution.UNRESOLVED &&
                (temporal.dueInstant != null || temporal.referenceExpression.isNullOrBlank())
            val resolvedBad = temporal.resolution == TemporalResolution.RESOLVED && temporal.dueInstant == null
            if (unresolvedBad || resolvedBad) {
                add(
                    findings, IntegrityCode.INVALID_TEMPORAL_RECORD, IntegrityDomain.TEMPORAL,
                    temporal.id.value, "Temporal resolution does not match payload",
                    temporal.resolution.name,
                )
            }
        }
    }

    private fun checkDependencies(input: IntegrityEvaluationInput, findings: MutableList<IntegrityFinding>) {
        val recordIds = input.operationalRecords.map { it.id }.toSet()
        val seenIds = HashSet<String>()
        val seenPairs = HashSet<String>()
        for (dependency in input.dependencies) {
            if (dependency.dependentOperationalRecordId !in recordIds ||
                dependency.prerequisiteOperationalRecordId !in recordIds
            ) {
                add(
                    findings, IntegrityCode.INVALID_DEPENDENCY_REFERENCE, IntegrityDomain.DEPENDENCY,
                    dependency.id.value, "Dependency references a missing operational record",
                    "${dependency.dependentOperationalRecordId.value}->${dependency.prerequisiteOperationalRecordId.value}",
                )
            }
            if (dependency.dependentOperationalRecordId == dependency.prerequisiteOperationalRecordId) {
                add(
                    findings, IntegrityCode.SELF_DEPENDENCY, IntegrityDomain.DEPENDENCY,
                    dependency.id.value, "Operational record depends on itself",
                    dependency.dependentOperationalRecordId.value,
                )
            }
            if (!seenIds.add(dependency.id.value)) {
                add(
                    findings, IntegrityCode.DUPLICATE_DEPENDENCY, IntegrityDomain.DEPENDENCY,
                    dependency.id.value, "Duplicate dependency id",
                    dependency.id.value,
                )
            }
            val pair = "${dependency.dependentOperationalRecordId.value}->${dependency.prerequisiteOperationalRecordId.value}"
            if (!seenPairs.add(pair)) {
                add(
                    findings, IntegrityCode.DUPLICATE_DEPENDENCY, IntegrityDomain.DEPENDENCY,
                    dependency.id.value, "Duplicate dependency pair",
                    pair,
                )
            }
        }
        val remaining = input.dependencies.toMutableList()
        for (dependency in input.dependencies.sortedWith(
            compareBy({ it.dependentOperationalRecordId.value }, { it.prerequisiteOperationalRecordId.value }, { it.id.value }),
        )) {
            remaining.remove(dependency)
            if (OperationalDependencyCycleDetector.wouldCreateCycle(
                    remaining,
                    dependency.dependentOperationalRecordId,
                    dependency.prerequisiteOperationalRecordId,
                )
            ) {
                add(
                    findings, IntegrityCode.DEPENDENCY_CYCLE, IntegrityDomain.DEPENDENCY,
                    dependency.id.value, "Dependency graph contains a cycle",
                    "${dependency.dependentOperationalRecordId.value}->${dependency.prerequisiteOperationalRecordId.value}",
                )
                break
            }
            remaining.add(dependency)
        }
    }

    private fun checkWorkflow(input: IntegrityEvaluationInput, findings: MutableList<IntegrityFinding>) {
        val recordIds = input.operationalRecords.map { it.id }.toSet()
        val workflowIds = input.workflows.map { it.id }.toSet()
        val seenRecord = HashSet<String>()
        for (workflow in input.workflows) {
            if (workflow.operationalRecordId !in recordIds) {
                add(
                    findings, IntegrityCode.INVALID_WORKFLOW_REFERENCE, IntegrityDomain.WORKFLOW,
                    workflow.id.value, "Workflow references a missing operational record",
                    workflow.operationalRecordId.value,
                )
            }
            if (!seenRecord.add(workflow.operationalRecordId.value)) {
                add(
                    findings, IntegrityCode.DUPLICATE_WORKFLOW_FOR_RECORD, IntegrityDomain.WORKFLOW,
                    workflow.id.value, "More than one workflow for an operational record",
                    workflow.operationalRecordId.value,
                )
            }
        }
        val stepsByWorkflow = input.workflowSteps.groupBy { it.workflowId }
        for (step in input.workflowSteps) {
            if (step.workflowId !in workflowIds) {
                add(
                    findings, IntegrityCode.INVALID_WORKFLOW_REFERENCE, IntegrityDomain.WORKFLOW,
                    step.id.value, "Workflow step references a missing workflow",
                    step.workflowId.value,
                )
            }
        }
        for ((workflowId, steps) in stepsByWorkflow) {
            val ordinals = HashSet<Int>()
            val keys = HashSet<String>()
            for (step in steps) {
                if (step.ordinal < 1 || !ordinals.add(step.ordinal)) {
                    add(
                        findings, IntegrityCode.INVALID_WORKFLOW_STEP_ORDER, IntegrityDomain.WORKFLOW,
                        step.id.value, "Workflow step ordinal is invalid or duplicated",
                        "${workflowId.value}:${step.ordinal}",
                    )
                }
                if (!keys.add(step.key)) {
                    add(
                        findings, IntegrityCode.DUPLICATE_WORKFLOW_STEP_KEY, IntegrityDomain.WORKFLOW,
                        step.id.value, "Workflow step key is duplicated",
                        "${workflowId.value}:${step.key}",
                    )
                }
            }
        }
        val stepIds = input.workflowSteps.map { it.id }.toSet()
        val seenTransitionIds = HashSet<String>()
        val byStep = input.workflowTransitions.groupBy { it.workflowStepId }
        for (transition in input.workflowTransitions) {
            if (transition.workflowStepId !in stepIds) {
                add(
                    findings, IntegrityCode.INVALID_WORKFLOW_HISTORY, IntegrityDomain.WORKFLOW,
                    transition.id.value, "Workflow transition references a missing step",
                    transition.workflowStepId.value,
                )
            }
            if (!seenTransitionIds.add(transition.id.value)) {
                add(
                    findings, IntegrityCode.DUPLICATE_WORKFLOW_TRANSITION_ID, IntegrityDomain.WORKFLOW,
                    transition.id.value, "Duplicate workflow transition id",
                    transition.id.value,
                )
            }
            if (!WorkflowStepPolicy.permitted(transition.fromStatus, transition.toStatus)) {
                add(
                    findings, IntegrityCode.INVALID_WORKFLOW_HISTORY, IntegrityDomain.WORKFLOW,
                    transition.id.value,
                    "Impossible workflow step transition ${transition.fromStatus} → ${transition.toStatus}",
                    "${transition.fromStatus}:${transition.toStatus}",
                )
            }
        }
        for ((stepId, history) in byStep) {
            val ordered = history.sortedWith(
                compareBy({ it.transitionedAt.value }, { it.id.value }),
            )
            var current = WorkflowStepStatus.PENDING
            for (transition in ordered) {
                if (transition.fromStatus != current) {
                    add(
                        findings, IntegrityCode.INVALID_WORKFLOW_HISTORY, IntegrityDomain.WORKFLOW,
                        transition.id.value,
                        "Workflow transition source does not match projected status $current",
                        "${transition.fromStatus}:$current",
                    )
                }
                current = transition.toStatus
            }
            WorkflowStepProjection.current(history)
        }
    }

    private fun checkRules(input: IntegrityEvaluationInput, findings: MutableList<IntegrityFinding>) {
        val seen = HashSet<String>()
        for (rule in input.rules) {
            val key = "${rule.id.value}:${rule.version.value}"
            if (!seen.add(key)) {
                add(
                    findings, IntegrityCode.DUPLICATE_RULE_VERSION, IntegrityDomain.RULES,
                    key, "Duplicate rule id and version",
                    key,
                )
            }
        }
    }
}
