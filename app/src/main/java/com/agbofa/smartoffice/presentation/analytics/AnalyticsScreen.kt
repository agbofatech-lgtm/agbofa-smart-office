package com.agbofa.smartoffice.presentation.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AnalyticsScreen(
    state: AnalyticsUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Analytics", style = MaterialTheme.typography.headlineSmall)
        Text("Renders OperationalAnalyticsReport. Does not recompute aggregates.")
        Button(onClick = onRefresh, modifier = Modifier.fillMaxWidth()) { Text("Refresh") }
        when {
            state.loading -> Text("Loading")
            state.error != null -> Text(state.error)
            state.empty -> Text("No records to aggregate")
            state.report != null -> {
                val report = state.report
                Text("State OPEN ${report.state.openCount} / ${report.state.totalRecords}")
                if (report.state.totalRecords > 0) {
                    LinearProgressIndicator(
                        progress = { report.state.openCount.toFloat() / report.state.totalRecords },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Text("Temporal resolved ${report.temporal.resolvedTemporalCount} unresolved ${report.temporal.unresolvedTemporalCount}")
                Text("Due BEFORE ${report.temporal.beforeDueCount} AT ${report.temporal.atDueCount} PAST ${report.temporal.pastDueCount}")
                Text("Dependencies ${report.dependency.totalDependencies}")
                Text("Workflows ${report.workflow.recordsWithWorkflow} complete ${report.workflow.workflowCompleteCount}")
                Text("Integrity errors ${report.integrity.errorCount} warnings ${report.integrity.warningCount}")
            }
        }
    }
}
