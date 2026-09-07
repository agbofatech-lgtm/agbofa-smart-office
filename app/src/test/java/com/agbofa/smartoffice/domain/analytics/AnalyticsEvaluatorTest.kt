package com.agbofa.smartoffice.domain.analytics

import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant
import com.agbofa.smartoffice.domain.integrity.IntegrityDomain
import com.agbofa.smartoffice.domain.integrity.IntegrityFinding
import com.agbofa.smartoffice.domain.integrity.IntegrityCode
import com.agbofa.smartoffice.domain.integrity.IntegrityOutcome
import com.agbofa.smartoffice.domain.integrity.IntegritySeverity
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.operations.TemporalResolution
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class AnalyticsEvaluatorTest {
    private val context = EvaluationContext(EvaluationInstant(Instant.parse("2026-09-07T15:00:00Z")))

    private fun record(
        id: String,
        state: OperationalState = OperationalState.OPEN,
        hasTemporal: Boolean = false,
        resolution: TemporalResolution? = null,
        due: DueStatus? = null,
        prereq: Int = 0,
        dependents: Int = 0,
        hasWorkflow: Boolean = false,
        complete: Boolean = false,
        active: Boolean = false,
        outcome: IntegrityOutcome = IntegrityOutcome.HEALTHY,
        findings: List<IntegrityFinding> = emptyList(),
    ) = AnalyticsRecord(
        recordId = id,
        currentState = state,
        hasTemporal = hasTemporal,
        temporalResolution = resolution,
        dueStatus = due,
        prerequisiteCount = prereq,
        dependentCount = dependents,
        hasWorkflow = hasWorkflow,
        workflowComplete = complete,
        workflowHasActiveStep = active,
        integrityOutcome = outcome,
        integrityFindings = findings,
    )

    @Test
    fun emptyInputIsZero() {
        val report = AnalyticsEvaluator.evaluate(AnalyticsInput(context = context))
        assertEquals(0, report.state.totalRecords)
        assertEquals(0, report.state.openCount)
        assertEquals(0, report.temporal.pastDueCount)
        assertEquals(0, report.workflow.recordsWithWorkflow)
        assertEquals(0, report.integrity.errorCount)
    }

    @Test
    fun insertionOrderDoesNotChangeCounts() {
        val a = record("a", state = OperationalState.ACTIVE, due = DueStatus.BEFORE_DUE, hasTemporal = true, resolution = TemporalResolution.RESOLVED)
        val b = record("b", state = OperationalState.OPEN)
        val first = AnalyticsEvaluator.evaluate(AnalyticsInput(records = listOf(a, b), context = context))
        val second = AnalyticsEvaluator.evaluate(AnalyticsInput(records = listOf(b, a), context = context))
        assertEquals(first, second)
        assertEquals(1, first.state.activeCount)
        assertEquals(1, first.state.openCount)
        assertEquals(1, first.temporal.beforeDueCount)
        assertEquals(1, first.temporal.recordsWithoutTemporalAssignment)
    }

    @Test
    fun unresolvedNeverCountsAsDue() {
        val report = AnalyticsEvaluator.evaluate(
            AnalyticsInput(
                records = listOf(
                    record("u", hasTemporal = true, resolution = TemporalResolution.UNRESOLVED, due = null),
                ),
                context = context,
            ),
        )
        assertEquals(1, report.temporal.unresolvedTemporalCount)
        assertEquals(0, report.temporal.beforeDueCount)
        assertEquals(0, report.temporal.atDueCount)
        assertEquals(0, report.temporal.pastDueCount)
    }

    @Test
    fun workflowAndDependencyCounts() {
        val report = AnalyticsEvaluator.evaluate(
            AnalyticsInput(
                records = listOf(
                    record("root", prereq = 0, dependents = 1, hasWorkflow = true, complete = true),
                    record("leaf", prereq = 1, dependents = 0, hasWorkflow = true, complete = false, active = true),
                ),
                context = context,
            ),
        )
        assertEquals(1, report.dependency.recordsWithPrerequisites)
        assertEquals(1, report.dependency.recordsWithDependents)
        assertEquals(1, report.dependency.totalDependencies)
        assertEquals(1, report.dependency.rootDependencyCount)
        assertEquals(1, report.dependency.leafDependencyCount)
        assertEquals(1, report.workflow.workflowCompleteCount)
        assertEquals(1, report.workflow.workflowIncompleteCount)
        assertEquals(1, report.workflow.workflowWithActiveStepCount)
    }

    @Test
    fun integrityAggregatesWithoutRewrite() {
        val finding = IntegrityFinding(
            code = IntegrityCode.MISSING_OPERATIONAL_RECORD_REFERENCE,
            severity = IntegritySeverity.ERROR,
            domain = IntegrityDomain.OPERATIONAL_RECORD,
            entityId = "x",
            description = "missing",
        )
        val report = AnalyticsEvaluator.evaluate(
            AnalyticsInput(
                records = listOf(record("x", outcome = IntegrityOutcome.ERRORS_PRESENT, findings = listOf(finding))),
                context = context,
            ),
        )
        assertEquals(1, report.integrity.errorCount)
        assertEquals(0, report.integrity.healthyCount)
        assertEquals(1, report.integrity.findingsBySeverity.getValue(IntegritySeverity.ERROR))
        assertEquals(finding, report.let { it.integrity }.let { finding })
        assertTrue(report.integrity.findingsByDomain.getValue(IntegrityDomain.OPERATIONAL_RECORD) == 1)
    }
}
